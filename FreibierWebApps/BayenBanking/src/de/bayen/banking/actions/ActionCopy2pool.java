/* Erzeugt am 02.04.2005 von tbayen
 * $Id: ActionCopy2pool.java,v 1.2 2006/01/28 14:19:17 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci.swift.DTAUS;
import org.kapott.hbci.swift.DTAUS.Transaction;
import de.bayen.banking.hbci.HBCICallbackWebinterface;
import de.bayen.database.DataObject;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * Alle Transaktionen, die in einem Ausgangskorb sind, werden zusammengenommen
 * und in eine DTAUS-Datei umgewandelt. Diese DTAUS-Datei wird sodann in die
 * Pool-Tabelle gespeichert und die Transaktionen werden gel�scht.
 * 
 * @author tbayen
 */
public class ActionCopy2pool implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		// Transaktionen aus der Datenbank holen
		Table tab = db.getTable("Ausgangskoerbe");
		String recordid = (String) ((Map) root.get("uri")).get("id");
		Record ausgangskorb = tab.getRecordByPrimaryKey(tab
				.getRecordDefinition().getFieldDef("id").parse(recordid));
		Table t_tab = db.getTable("Transaktionen");
		QueryCondition cond = t_tab.new QueryCondition("Ausgangskorb",
				Table.QueryCondition.EQUAL, t_tab.getRecordDefinition()
						.getFieldDef("Ausgangskorb").parse(recordid));
		List transaktionen = t_tab.getRecordsFromQuery(cond, t_tab
				.getRecordDefinition().getFieldDef(0).getName(), true);
		Record konto = db.getTable("Konten").getRecordByPrimaryKey(
				ausgangskorb.getField("Konto"));
		Table zahlungsarten = db.getTable("Zahlungsarten");
		Record zahlungsart = zahlungsarten.getRecordByPrimaryKey(ausgangskorb
				.getField("Zahlungsart"));
		// Datenstruktur aufbauen, die HBCI4Java ben�tigt:
		Konto kto = new Konto(konto.getFormatted("BLZ"), konto
				.getFormatted("Kontonummer"));
		kto.name = konto.getFormatted("Inhaber");
		int typ = DTAUS.TYPE_CREDIT;
		String textschl = zahlungsart.getFormatted("Textschluessel");
		if (textschl.equals("04") || textschl.equals("05")) {
			typ = DTAUS.TYPE_DEBIT;
		}
		DTAUS dtaus = new DTAUS(kto, typ);
		BigDecimal summe = new BigDecimal(0);
		for (int i = 0; i < transaktionen.size(); i++) {
			Record ta = (Record) transaktionen.get(i);
			Transaction hbcita = dtaus.new Transaction();
			hbcita.otherAccount = new Konto(ta.getFormatted("BLZ"), ta
					.getFormatted("Kontonummer"));
			hbcita.otherAccount.name = ta.getFormatted("Empfaenger");
			hbcita.key = zahlungsarten.getRecordByPrimaryKey(
					ta.getField("Zahlungsart")).getFormatted("Textschluessel");
			String betrag = ta.getField("Betrag").getValue().toString();
			summe = summe.add(new BigDecimal(betrag));
			hbcita.value = new Value(betrag);
			String[] names = {
					"Vwz1", "Vwz2", "Vwz3", "Vwz4"
			};
			for (int j = 0; j < names.length; j++) {
				String vwz = ta.getFormatted(names[j]);
				if ((vwz != null) && (!vwz.equals(""))) {
					hbcita.addUsage(vwz);
				}
			}
			dtaus.addEntry(hbcita);
			if (ausgangskorb.getFormatted("Dauerauftraege").equals("false")) {
				t_tab.deleteRecord(ta);
			}
		}
		// Bevor ich HBCI4Java-Funktionen aufrufe, muss mit den folgenden
		// zwei Befehlen die Bibliothek initialisiert werden:
		// TODO: HBCI4Java sollte zentral initialisiert werden
		HBCICallbackWebinterface callback = new HBCICallbackWebinterface(
				new HashMap());
		HBCIUtils.init(null, null, callback);
		// und jetzt den Fisch in den Pool schmeissen:
		Table pool = db.getTable("Pool");
		Record fisch = pool.getEmptyRecord();
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String now = df.format(new Date());
		fisch.setField("Bezeichnung", ausgangskorb.getFormatted("Bezeichnung")
				+ " (" + summe + ") vom " + now);
		fisch.setField("Dateiname", "dtaus.txt");
		// hiermit ignoriere ich Zeichensatz-Fehler, weil z.B. der Doppelpunkt
		// in DTAUS sonst nicht vorkommen d�rfte...
		HBCIUtils.setParam("client.errors.ignoreWrongDataSyntaxErrors", "yes");
		fisch
				.setField("DTAUS", new DataObject(new BLOB(dtaus.toString()
						.getBytes()), fisch.getRecordDefinition().getFieldDef(
						"DTAUS")));
		pool.setRecord(fisch);
		// Umleitung auf andere Action
		ad.executeAction("show", req, root, db, servlet);
	}
}
/*
 * $Log: ActionCopy2pool.java,v $
 * Revision 1.2  2006/01/28 14:19:17  tbayen
 * Zahlungsart in Transaktionen erm�glicht, Abbuch. und Lastschr. zu mischen
 *
 * Revision 1.1  2006/01/24 00:26:01  tbayen
 * Erste eigenst�ndige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
 * Revision 1.5  2005/08/07 16:56:15  tbayen
 * Produktionsversion 1.5
 *
 * Revision 1.4  2005/05/03 11:21:15  tbayen
 * l�schen von Daten, die weiterbearbeitet wurden
 *
 * Revision 1.3  2005/04/18 13:11:53  tbayen
 * Sonderzeichen wie ":" im Verwendungszweck erlaubt
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