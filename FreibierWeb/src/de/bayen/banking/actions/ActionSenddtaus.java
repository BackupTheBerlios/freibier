/* Erzeugt am 02.04.2005 von tbayen
 * $Id: ActionSenddtaus.java,v 1.3 2005/04/19 17:17:04 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import de.bayen.webframework.ServletDatabase;
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
			Map root, WebDBDatabase db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
		logger.debug("ActionSenddtaus");
		Table tab = db.getTable("Pool");
		String recordid = (String) ((Map) root.get("uri")).get("id");
		Record record = tab.getRecordByPrimaryKey(tab.getRecordDefinition()
				.getFieldDef("id").parse(recordid));
		BLOB dtausblob = ((BLOB) record.getField("DTAUS").getValue());
		// Konto-Datensatz suchen
		Table k_tab = db.getTable("Konten");
		QueryCondition cond;
		String lastorgut;
		try {
			DTAUSReader dtaus = new DTAUSReader(dtausblob.toByteArray());
			cond = k_tab.new QueryCondition("Kontonummer",
					Table.QueryCondition.EQUAL, dtaus.getA_Kontonummer());
			cond.and(k_tab.new QueryCondition("BLZ",
					Table.QueryCondition.EQUAL, dtaus.getA_BLZ()));
			lastorgut=dtaus.getA_LastOrGut();
		} catch (Exception e) {
			throw new ServletException("Fehler beim Lesen der DTAUS-Datei",e);
		}
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
				passport = BankingUtils.makePassport(map, callback, db, servlet);
				hbciHandle = BankingUtils.makeHandle(passport);
				// TODO: Callback in verschiedenen Threads gleichzeitig
				// Was ich hier mit dem Callback mache, ist nicht 
				// multithread-fähig (In einer Webapplikation nicht so gut...).
				// Hier hole ich den wirklich verwendeten Callback:
				callback = (HBCICallbackWebinterface) HBCIUtilsInternal
						.getCallback();
				// Job zum Versand der DTAUS-Datei erzeugen
				HBCIJob auszug;
				if (lastorgut.equals("L")) {
					auszug = hbciHandle.newJob("MultiLast");
				} else {
					auszug = hbciHandle.newJob("MultiUeb");
				}
				String kontonummer = kontorecord.getFormatted("Kontonummer");
				// Hier wird die PIN und TAN gesetzt, wenn es eine gibt
				callback.setPin(servlet.getProperty(kontonummer + ".pin"));
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
		ad.executeAction("show", req, root, db, servlet);
	}
}
/*
 * $Log: ActionSenddtaus.java,v $
 * Revision 1.3  2005/04/19 17:17:04  tbayen
 * DTAUS-Dateien wieder einlesen in die Datenbank
 *
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */