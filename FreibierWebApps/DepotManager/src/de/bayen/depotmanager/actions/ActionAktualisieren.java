/* Erzeugt am 15.01.2006 von tbayen
 * $Id: ActionAktualisieren.java,v 1.3 2007/11/12 14:56:02 tbayen Exp $
 */
package de.bayen.depotmanager.actions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
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
			CCAPI.DataRetrieval.YahooRetriever retriever = new CCAPI.DataRetrieval.YahooRetriever();
			// fr�her ging isin+"DE" f�r Xetra-Kurse
			Candle candle = retriever.getCurrentQuote(isin2TickerSymbol(isin));
			gelesen++;
			if (saveCandle(papier.getPrimaryKey(), db, candle))
				geschrieben++;
		}
		root.put("gelesen", String.valueOf(gelesen));
		root.put("geschrieben", String.valueOf(geschrieben));
	}

	/**
	 * Diese Funktion holt die History einer Aktie von der Cortal Consors Seite.
	 * Diese enth�lt f�r mehrere vergangene Jahre den Schlusskurs jedes B�rsentages.
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
	 * @param isin
	 * @return Tickersymbol
	 * @throws RuntimeException
	 */
	private static String isin2TickerSymbol(String isin)
			throws RuntimeException {
		if(!isin.startsWith("DE")) // TODO Das kann eigentlich weg
			return isin;
		String webseite;
		try {
			URL url = new URL("http://de.finance.yahoo.com/q?s=" + isin);
			Reader reader = new InputStreamReader(url.openStream());
			char[] buffer = new char[100000];
			int offset = 0;
			while ((offset += reader.read(buffer, offset, 100000 - offset)) >0) {/**/}
			webseite = new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		final String suchBegriff = "quotes.csv?s=";
		int suchindex = webseite.indexOf(suchBegriff);
		int endindex = webseite.indexOf('&', suchindex);
		String tickerSymbol = webseite.substring(suchindex
				+ suchBegriff.length(), endindex);
		return tickerSymbol;
	}

	/**
	 * Diese Funktion speichert eine durch die CCAPI-Bibliothek gelesene "Candle",
	 * d.h. einen einzelnen Kurswert eines Wertpapiers, in meiner Datenbank ab.
	 * 
	 * @param papierid
	 * @param db
	 * @param candle
	 * @return false, wenn schon ein Wert f�r diesen Tag vorlag
	 * @throws SysDBEx
	 */
	private boolean saveCandle(DataObject papierid, Database db,
			Candle candle) throws SysDBEx {
		logger.debug("Candle gelesen:"+candle);
		if(candle==null)
			return false;
		Table table = db.getTable("Kursdaten");
		// die Daten von Consors haben immer das richtige Datum, aber die Zeit 
		// von "Jetzt", also einen ung�ltigen Wert. Deshalb setze ich die Uhrzeit 
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
	 * herumspielen zu k�nnen.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		CCAPI.DataRetrieval.YahooRetriever retriever = new CCAPI.DataRetrieval.YahooRetriever();//YahooRetriever();
		Candle candle = retriever.getCurrentQuote("DE0005752000");
		//Candle candle = retriever.getCurrentQuote("BAY.DE");
		System.out.println(candle.toString());
		candle = retriever.getCurrentQuote(isin2TickerSymbol("DE0005752000"));
		System.out.println(candle.toString());
	}
}
/*
 * $Log: ActionAktualisieren.java,v $
 * Revision 1.3  2007/11/12 14:56:02  tbayen
 * Version 1.2 auf neuem Server und Tomcat 5.5
 *
 * Revision 1.2  2006/01/22 20:07:35  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unn�tig, wurde gel�scht
 *
 * Revision 1.1  2006/01/21 23:20:50  tbayen
 * Erste Version 1.0 des DepotManagers
 * erste FreibierWeb-Applikation im eigenen Paket
 *
 */