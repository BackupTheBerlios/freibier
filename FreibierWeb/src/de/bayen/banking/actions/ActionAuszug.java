/* Erzeugt am 24.03.2005 von tbayen
 * $Id: ActionAuszug.java,v 1.2 2005/04/18 10:57:55 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import de.bayen.banking.hbci.BankingUtils;
import de.bayen.banking.hbci.HBCICallbackWebinterface;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Holt anhand der in "auszugform" eingegebenen Parameter einen Kontoauszug
 * und stellt diesen im Model zur Verfügung.
 * 
 * @author tbayen
 */
public class ActionAuszug implements Action {
	static Logger logger = Logger.getLogger(ActionAuszug.class.getName());

	/*
	 * @see de.bayen.webframework.Action#executeAction(de.bayen.webframework.ActionDispatcher, javax.servlet.http.HttpServletRequest, java.util.Map, de.bayen.webframework.WebDBDatabase)
	 */
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
		logger.debug("ActionAuszug");
		// HBCI4Java initialisieren
		Map map = new HashMap();
		map.put("passportpassword", req.getParameter("_passportpassword"));
		map.put("startdate", req.getParameter("_startdate"));
		map.put("enddate", req.getParameter("_enddate"));
		map.put("konto", req.getParameter("_konto"));
		root.put("startdate", map.get("startdate"));
		root.put("enddate", map.get("enddate"));
		HBCICallbackWebinterface callback;
		HBCIPassport passport = null;
		HBCIHandler hbciHandle = null;
		try {
			callback = new HBCICallbackWebinterface(map);
			passport = BankingUtils.makePassport(map, callback, db,servlet);
			hbciHandle = BankingUtils.makeHandle(passport);
			// TODO: Callback-Nutzung nicht multithreading-fähig
			// Hier hole ich den wirklich verwendeten Callback:
			callback = (HBCICallbackWebinterface) HBCIUtilsInternal
					.getCallback();
			// Job zur Abholung der Kontoauszüge erzeugen
			HBCIJob auszug = hbciHandle.newJob("KUmsAll");
			Integer konto = Integer.valueOf((String) map.get("konto"));
			if (konto != null) {
				Record record = db.getTable("Konten").getRecordByPrimaryKey(
						konto);
				if (record != null) {
					String kontonummer = record.getFormatted("Kontonummer");
					// Hier wird die PIN gesetzt, wenn es eine gibt
					callback.setPin(servlet.getProperty(kontonummer + ".pin"));
					Konto kontoobj = new Konto();
					kontoobj.number = kontonummer;
					kontoobj.blz = record.getFormatted("BLZ");
					kontoobj.country = "DE";
					auszug.setParam("my", kontoobj);
				}
			}
			try {
				DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.GERMANY);
				auszug.setParam("startdate", df.parse((String) map
						.get("startdate")));
				auszug.setParam("enddate", df
						.parse((String) map.get("enddate")));
			} catch (ParseException e) {
				throw new ServletException("Fehler beim parsen des Jobs", e);
			}
			hbciHandle.addJob(auszug);
			// alle Jobs in der Job-Warteschlange ausführen
			HBCIExecStatus ret = hbciHandle.execute();
			GVRKUms result = (GVRKUms) auszug.getJobResult();
			if (!result.isOK()) {
				// Fehlermeldungen ausgeben
				root.put("globerror", ret.getErrorString());
				root.put("joberror", result.getJobStatus().getErrorString());
				logger.error("Job-Error: "
						+ result.getJobStatus().getErrorString());
				logger.error("Global Error: " + ret.getErrorString());
			} else {
				// Auswertung
				Auszug2Model(result, root);
				root.put("globerror", result.getGlobStatus().getErrorString());
				root.put("joberror", result.getJobStatus().getErrorString());
			}
			root.put("auszugtext", result.toString());
			root.put("hbcilog", callback.getLog());
			root.put("globstatus", result.getGlobStatus().toString());
			root.put("jobstatus", result.getJobStatus().toString());
		} catch (Throwable e1) {
			logger.error("Exception während HBCI-Ausführung");
			OutputStream os = new ByteArrayOutputStream();
			e1.printStackTrace(new PrintStream(os));
			logger.error("Exception während HBCI-Ausführung: " + os.toString());
		} finally {
			if (hbciHandle != null) {
				hbciHandle.close();
			} else if (passport != null) {
				passport.close();
			}
		}
		logger.debug("Auszug-Action abgeschlossen");
	}

	/**
	 * @param result
	 * @param root
	 */
	private void Auszug2Model(GVRKUms result, Map root) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM,
				Locale.GERMANY);
		Perl5Util regex = new Perl5Util();
		List auszug = new ArrayList();
		root.put("auszug", auszug);
		GVRKUms.BTag btag[] = result.getDataPerDay();
		for (int i = 0; i < btag.length; i++) {
			Map tag = new HashMap();
			auszug.add(tag);
			String datum;
			if (btag[i].lines.length > 0) {
				datum = df.format(btag[i].lines[0].bdate);
			} else {
				datum = (String) root.get("startdate");
			}
			tag.put("buchungsdatum", datum);
			tag.put("kontonummer", btag[i].my.number);
			tag.put("blz", btag[i].my.blz);
			tag.put("bank", HBCIUtils.getNameForBLZ(btag[i].my.blz));
			tag.put("startsaldo", btag[i].start.value + " "
					+ (btag[i].start.cd.equals("C") ? "H" : "S"));
			tag.put("endsaldo", btag[i].end.value + " "
					+ (btag[i].start.cd.equals("C") ? "H" : "S"));
			List zeilen = new ArrayList();
			tag.put("zeilen", zeilen);
			UmsLine[] lines = btag[i].lines;
			for (int j = 0; j < lines.length; j++) {
				Map buchung = new HashMap();
				zeilen.add(buchung);
				buchung.put("datum", df.format(lines[j].bdate));
				buchung.put("valuta", df.format(lines[j].valuta));
				buchung.put("betrag", lines[j].value + " "
						+ (lines[j].cd.equals("C") ? "H" : "S"));
				buchung.put("betragsh", lines[j].cd.equals("C") ? "H" : "S");
				buchung.put("saldo", lines[j].saldo.value + " "
						+ (lines[j].saldo.cd.equals("C") ? "H" : "S"));
				List vwz = new ArrayList();
				buchung.put("pappenheimer", new Integer(0));
				buchung.put("vwz", vwz);
				if (lines[j].other != null && lines[j].other.name != null
						&& lines[j].other.name.length() > 0) {
					vwz.add(lines[j].other.name);
				}
				if (lines[j].other != null && lines[j].other.name2 != null
						&& lines[j].other.name2.length() > 0) {
					vwz.add(lines[j].other.name2);
				}
				if (lines[j].text != null && lines[j].text.length() > 0) {
					vwz.add(lines[j].text);
				}
				String usage[] = lines[j].usage;
				for (int k = 0; k < usage.length; k++) {
					vwz.add(usage[k]);
					if (regex.match("/(^VORGELEGT AM)/", usage[k])) {
						buchung.put("pappenheimer", new Integer(1));
					}
				}
			}
		}
	}
}
/*
 * $Log: ActionAuszug.java,v $
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.6  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.5  2005/03/30 19:01:18  tbayen
 * Anpassung des Kontoauszugs
 *
 * Revision 1.4  2005/03/26 23:07:29  tbayen
 * Auswahl mehrerer Konten für Auszug
 *
 * Revision 1.3  2005/03/26 18:52:57  tbayen
 * Kontoauszug per PinTan abgeholt
 *
 * Revision 1.2  2005/03/26 03:10:44  tbayen
 * Banking-Applikation kann per Chipkarte
 * Auszüge abholen und anzeigen
 *
 * Revision 1.1  2005/03/25 00:17:00  tbayen
 * Log4J konfiguriert und Logging eingerichtet
 * HBCI4Java eingebunden
 * erster Anfang der Banking-Applikation
 *
 */