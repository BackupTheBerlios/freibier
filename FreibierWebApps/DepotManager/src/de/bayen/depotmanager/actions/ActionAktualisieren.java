/* Erzeugt am 15.01.2006 von tbayen
 * $Id: ActionAktualisieren.java,v 1.2 2006/01/22 20:07:35 tbayen Exp $
 */
package de.bayen.depotmanager.actions;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import CCAPI.Candle;
import CCAPI.DataRetrieval.ConsorsHistoryRetriever;
import de.bayen.database.DataObject;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx;
import de.bayen.database.exception.UserDBEx;
import de.bayen.depotmanager.DataRetrieval.YahooRetriever;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

public class ActionAktualisieren implements Action {
	static Logger logger = Logger
			.getLogger(ActionAktualisieren.class.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		//readConsorsHistory(root, db);
		readYahoo(root, db);
	}

	/**
	 * Diese Methode besorgt die aktuellen Kurswerte von Yahoo und speichert 
	 * sie in meiner Datenbank.
	 * 
	 * @param root
	 * @param db
	 * @throws SysDBEx
	 * @throws UserDBEx
	 */
	protected void readYahoo(Map root, Database db) throws SysDBEx,
			UserDBEx {
		Table allepapiere = db.getTable("Wertpapiere");
		int gelesen = 0, geschrieben = 0;
		for (int p = 0; p < allepapiere.getNumberOfRecords(); p++) {
			Record papier = allepapiere.getRecordByNumber(p);
			String isin = papier.getField("ISIN").format();
			YahooRetriever retriever = new YahooRetriever();
			Candle candle = retriever.getCurrentQuote(isin+".DE");  // "DE" für Xetra-Kurse
			gelesen++;
			if (saveCandle(papier.getPrimaryKey(), db, candle))
				geschrieben++;
		}
		root.put("gelesen", String.valueOf(gelesen));
		root.put("geschrieben", String.valueOf(geschrieben));
	}

	/**
	 * Diese Funktion holt die History einer Aktie von der Cortal Consors Seite.
	 * Diese enthält für mehrere vergangene Jahre den Schlusskurs jedes Börsentages.
	 * 
	 * @param root
	 * @param db
	 */
	protected void readConsorsHistory(Map root, Database db)
			throws SysDBEx, UserDBEx {
		Table allepapiere = db.getTable("Wertpapiere");
		int gelesen = 0, geschrieben = 0;
		for (int p = 0; p < allepapiere.getNumberOfRecords(); p++) {
			Record papier = allepapiere.getRecordByNumber(p);
			String isin = papier.getField("ISIN").format();
			ConsorsHistoryRetriever retriever = new ConsorsHistoryRetriever();
			String consorsid = retriever.search(isin);
			Vector v = retriever.getHistory(consorsid);
			for (int i = 0; i < v.size(); i++) {
				gelesen++;
				Candle candle = (Candle) v.elementAt(i);
				if (saveCandle(papier.getPrimaryKey(), db, candle))
					geschrieben++;
			}
		}
		root.put("gelesen", String.valueOf(gelesen));
		root.put("geschrieben", String.valueOf(geschrieben));
	}

	/**
	 * Diese Funktion speichert eine durch die CCAPI-Bibliothek gelesene "Candle",
	 * d.h. einen einzelnen Kurswert eines Wertpapiers, in meiner Datenbank ab.
	 * 
	 * @param papierid
	 * @param db
	 * @param candle
	 * @return false, wenn schon ein Wert für diesen Tag vorlag
	 * @throws SysDBEx
	 */
	private boolean saveCandle(DataObject papierid, Database db,
			Candle candle) throws SysDBEx {
		logger.debug("Candle gelesen:"+candle);
		if(candle==null)
			return false;
		Table table = db.getTable("Kursdaten");
		// die Daten von Consors haben immer das richtige Datum, aber die Zeit 
		// von "Jetzt", also einen ungültigen Wert. Deshalb setze ich die Uhrzeit 
		//hier auf 00:00:00.0000.
		Calendar cal = new GregorianCalendar();
		cal.setTime(candle.date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		// erstmal suchen, ob es diesen Eintrag schon gibt
		QueryCondition cond = table.new QueryCondition("Datum",
				QueryCondition.EQUAL, cal.getTime());
		cond.and(table.new QueryCondition("Wertpapier", QueryCondition.EQUAL,
				papierid.getValue()));
		List vorhanden = table.getRecordsFromQuery(cond, "Datum", false);
		if (vorhanden.size() == 0) {
			Record rec = table.getEmptyRecord();
			rec.setField("Wertpapier", papierid.getValue());
			rec.setField("Datum", cal.getTime());
			rec.setField("Schlusskurs", String.valueOf(candle.close));
			table.setRecord(rec);
			return true;
		}
		return false;
	}

	/**
	 * Diese Methode ist hier nur zu Testzwecken, um mit den Retrievern 
	 * herumspielen zu können.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		YahooRetriever retriever = new YahooRetriever();
		Candle candle = retriever.getCurrentQuote("DE0005752000");
		//Candle candle = retriever.getCurrentQuote("BAY.DE");
		System.out.println(candle.toString());
	}
}
/*
 * $Log: ActionAktualisieren.java,v $
 * Revision 1.2  2006/01/22 20:07:35  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.1  2006/01/21 23:20:50  tbayen
 * Erste Version 1.0 des DepotManagers
 * erste FreibierWeb-Applikation im eigenen Paket
 *
 */