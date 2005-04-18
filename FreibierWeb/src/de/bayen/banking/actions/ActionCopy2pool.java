/* Erzeugt am 02.04.2005 von tbayen
 * $Id: ActionCopy2pool.java,v 1.2 2005/04/18 10:57:55 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci.swift.DTAUS;
import org.kapott.hbci.swift.DTAUS.Transaction;
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
public class ActionCopy2pool implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
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
		Record zahlungsart = db.getTable("Zahlungsarten")
				.getRecordByPrimaryKey(
						ausgangskorb.getField("Zahlungsart"));
		// Datenstruktur aufbauen, die HBCI4Java benötigt:
		Konto kto = new Konto(konto.getFormatted("BLZ"), konto
				.getFormatted("Kontonummer"));
		kto.name = konto.getFormatted("Inhaber");
		int typ = DTAUS.TYPE_CREDIT;
		String textschl = zahlungsart.getFormatted("Textschluessel");
		if (textschl.equals("04") || textschl.equals("05")) {
			typ = DTAUS.TYPE_DEBIT;
		}
		DTAUS dtaus = new DTAUS(kto, typ);
		BigDecimal summe=new BigDecimal(0);
		for (int i = 0; i < transaktionen.size(); i++) {
			Record ta = (Record) transaktionen.get(i);
			Transaction hbcita = dtaus.new Transaction();
			hbcita.otherAccount = new Konto(ta.getFormatted("BLZ"), ta
					.getFormatted("Kontonummer"));
			hbcita.otherAccount.name = ta.getFormatted("Empfaenger");
			hbcita.key = textschl;
			String betrag=ta.getField("Betrag").getValue().toString();
			summe=summe.add(new BigDecimal(betrag));
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
			// TODO Records werden noch nicht gelöscht
			//t_tab.deleteRecord(ta);
		}
		// und jetzt den Fisch in den Pool schmeissen:
		Table pool = db.getTable("Pool");
		Record fisch = pool.getEmptyRecord();
		DateFormat df=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String now=df.format(new Date());
		fisch.setField("Bezeichnung", ausgangskorb.getFormatted("Bezeichnung")+" ("+summe+") vom "+now);
		fisch.setField("Dateiname", "dtaus.txt");
		fisch
				.setField("DTAUS", new DataObject(new BLOB(dtaus.toString()
						.getBytes()), fisch.getRecordDefinition().getFieldDef(
						"DTAUS")));
		pool.setRecord(fisch);
		// Umleitung auf andere Action
		Map uri = (Map) root.get("uri");
		uri.put("action", "show");
		ad.executeAction("show", req, root, db, servlet);
	}
}
/*
 * $Log: ActionCopy2pool.java,v $
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