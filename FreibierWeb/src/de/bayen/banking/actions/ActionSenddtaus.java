/* Erzeugt am 02.04.2005 von tbayen
 * $Id: ActionSenddtaus.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import de.bayen.banking.hbci.BankingUtils;
import de.bayen.banking.hbci.DTAUSReader;
import de.bayen.banking.hbci.HBCICallbackWebinterface;
import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * Alle Transaktionen, die in einem Ausgangskorb sind, werden zusammengenommen
 * und in eine DTAUS-Datei umgewandelt. Diese DTAUS-Datei wird sodann in die
 * Pool-Tabelle gespeichert und die Transaktionen werden gelöscht.
 * 
 * @author tbayen
 */
public class ActionSenddtaus implements Action {
	static Logger logger = Logger.getLogger(ActionSenddtaus.class.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db) throws DatabaseException,
			ServletException {
		logger.debug("ActionSenddtaus");
		Table tab = db.getTable("Pool");
		String recordid = (String) ((Map) root.get("uri")).get("id");
		Record record = tab.getRecordByPrimaryKey(tab.getRecordDefinition()
				.getFieldDef("id").parse(recordid));
		BLOB dtausblob = ((BLOB) record.getField("DTAUS").getValue());
		DTAUSReader dtaus = new DTAUSReader(dtausblob.toByteArray());
		// Konto-Datensatz suchen
		Table k_tab = db.getTable("Konten");
		QueryCondition cond = k_tab.new QueryCondition("Kontonummer",
				Table.QueryCondition.EQUAL, dtaus.getKontonummer());
		cond.and(k_tab.new QueryCondition("BLZ", Table.QueryCondition.EQUAL,
				dtaus.getBLZ()));
		List records = k_tab.getRecordsFromQuery(cond, k_tab
				.getRecordDefinition().getFieldDef(0).getName(), true);
		if (records.size() == 1) { //Konto gefunden?
			Record kontorecord = (Record) records.get(0);
			// HBCI4Java initialisieren
			Map map = new HashMap();
			map.put("passportpassword", "banking"); // kein richtiges Geheimnis
			map.put("konto", kontorecord.getFormatted("id"));
			HBCICallbackWebinterface callback;
			HBCIPassport passport = null;
			HBCIHandler hbciHandle = null;
			try {
				callback = new HBCICallbackWebinterface(map);
				Properties prop = new Properties();
				prop.load(new FileInputStream(
						"/etc/webdatabase/banking.properties"));
				passport = BankingUtils.makePassport(map, callback, db);
				hbciHandle = BankingUtils.makeHandle(passport);
				// TODO: Callback in verschiedenen Threads gleichzeitig
				// Was ich hier mit dem Callback mache, ist nicht 
				// multithread-fähig (In einer Webapplikation nicht so gut...).
				// Hier hole ich den wirklich verwendeten Callback:
				callback = (HBCICallbackWebinterface) HBCIUtilsInternal
						.getCallback();
				// Job zum Versand der DTAUS-Datei erzeugen
				HBCIJob auszug;
				if (dtaus.getLastOrGut().equals("L")) {
					auszug = hbciHandle.newJob("MultiLast");
				} else {
					auszug = hbciHandle.newJob("MultiUeb");
				}
				String kontonummer = kontorecord.getFormatted("Kontonummer");
				// Hier wird die PIN und TAN gesetzt, wenn es eine gibt
				callback.setPin(prop.getProperty(kontonummer + ".pin"));
				callback.setTan(req.getParameter("tan"));
				Konto kto = new Konto(kontorecord.getFormatted("BLZ"),
						kontonummer);
				auszug.setParam("my", kto);
				auszug.setParam("data", new String(dtausblob.toByteArray()));
				hbciHandle.addJob(auszug);
				// alle Jobs in der Job-Warteschlange ausführen
				HBCIExecStatus ret = hbciHandle.execute();
				HBCIJobResult result = auszug.getJobResult();
				// Ergebnisse  speichern
				String ergebnis = ret.getErrorString() + "-----"
						+ result.getJobStatus().getErrorString() + "+++++"
						+ result.getJobStatus();
				logger.debug("Ergebniscode: " + ergebnis);
				record.setField("Ergebniscode", ergebnis);
				if (result.isOK()) {
					record.setField("Versandzeit", new DataObject(new Date(),
							record.getRecordDefinition().getFieldDef(
									"Versandzeit")));
				}
				tab.setRecord(record);
			} catch (Throwable e1) {
				logger.error("Exception während HBCI-Ausführung");
				OutputStream os = new ByteArrayOutputStream();
				e1.printStackTrace(new PrintStream(os));
				logger.error("Exception während HBCI-Ausführung: "
						+ os.toString());
			} finally {
				if (hbciHandle != null) {
					hbciHandle.close();
				} else if (passport != null) {
					passport.close();
				}
			}
		}
		logger.debug("Senddtaus-Action abgeschlossen");
		// Umleitung auf andere Action
		Map uri = (Map) root.get("uri");
		uri.put("action", "show");
		ad.executeAction("show", req, root, db);
	}
}
/*
 * $Log: ActionSenddtaus.java,v $
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */