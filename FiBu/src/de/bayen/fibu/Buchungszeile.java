/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Buchungszeile.java,v 1.10 2005/08/30 21:05:53 tbayen Exp $
 */
package de.bayen.fibu;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.ForeignKey;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.exceptions.FiBuRuntimeException;
import de.bayen.fibu.exceptions.ImpossibleException;
import de.bayen.fibu.util.Drucktabelle;
import de.bayen.fibu.util.StringUtil;

/**
 * 
 * Eine Buchungszeile ist eine einzelne Buchung auf ein einzelnes Konto.
 * Mehrere Buchungszeilen, deren Saldo 0.00 ist, ergeben zusammen eine 
 * Buchung.
 * 
 * @author tbayen
 */
public class Buchungszeile extends AbstractObject implements Comparable {
	private static Log log = LogFactory.getLog(Buchungszeile.class);
	private Table table;
	private Buchung buchung;

	/**
	 * erzeugt eine neue Buchungszeile. Diese wird noch nicht in die Datenbank
	 * geschrieben. Das sollte erst veranlasst werden, wenn die Buchung komplett
	 * ist. Buchungszeilen können nicht direkt, sondern nur über 
	 * Buchung.createZeile() erzeugt werden.
	 * 
	 * @param table
	 * @throws DatabaseException 
	 */
	protected Buchungszeile(Table table, Buchung buchung) {
		this.table = table;
		this.buchung = buchung;
		record = table.getEmptyRecord();
		try {
			setBetrag(new Betrag(new BigDecimal("0.00"), "S"));
			setBuchung(buchung);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
		log.debug("neue Buchungszeile erzeugt");
	}

	/**
	 * Liest eine vorhandene Buchungszeile aus der Datenbank und verknüpft sie
	 * mit einem bereits geladenen Buchungs-Objekt.
	 * 
	 * @param table
	 * @param buchung
	 * @param nummer
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	protected Buchungszeile(Table table, Buchung buchung, Long nummer)
			throws SQL_DBException, RecordNotExistsDBException {
		this.table = table;
		this.buchung = buchung;
		try {
			record = table.getRecordByPrimaryKey(nummer);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Liest eine vorhandene Buchungszeile aus der Datenbank.
	 * 
	 * @param table
	 * @param nummer
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 * @throws DatabaseException
	 */
	protected Buchungszeile(Table table, Long nummer) throws SQL_DBException,
			RecordNotExistsDBException {
		this.table = table;
		try {
			record = table.getRecordByPrimaryKey(nummer);
			this.buchung = new Buchung(table.getDatabase()
					.getTable("Buchungen"), ((Long) ((ForeignKey) record
					.getField("Buchung").getValue()).getKey()));
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Änderungen werden erst hiermit endgültig in die Datenbank
	 * geschrieben. die Methode ist nicht public, da das Schreiben von
	 * Buchungszeilen nur bei kompletten Buchungen erfolgen darf.
	 * @throws ParseErrorDBException 
	 * @throws SQL_DBException 
	 */
	protected void write() throws SQL_DBException, ParseErrorDBException {
		try {
			record = table.setRecordAndGetRecord(record);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Long getID() {
		return (Long) record.getField("id").getValue();
	}

	public Betrag getBetrag() {
		try {
			return new Betrag(
					(BigDecimal) record.getField("Betrag").getValue(), record
							.getField("SH").format());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setBetrag(Betrag betrag) throws ParseErrorDBException {
		try {
			record.setField("Betrag", betrag.getWert());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
		record.setField("SH", String.valueOf(betrag.getSollHaben()));
	}

	public Buchung getBuchung() {
		// Ich extrahiere die Buchung nicht aus dem Record, weil ich dann eine
		// neue Buchung erzeugen würde. Ich will aber die identische haben, da
		// diese ja evtl. andere Werte enthält als in der Datenbank stehen.
		return buchung;
	}

	public void setBuchung(Buchung buchung) throws ParseErrorDBException {
		try {
			record.setField("Buchung", buchung.getID());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
		this.buchung = buchung;
	}

	public Konto getKonto() throws SQL_DBException {
		try {
			return new Konto(table.getDatabase().getTable("Konten"),
					(Long) ((ForeignKey) record.getField("Konto").getValue())
							.getKey());
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (RecordNotExistsDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setKonto(Konto konto) {
		try {
			record.setField("Konto", konto.getID());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * vergleicht zwei Objekte miteinander. Diese Methode implementiert
	 * das Comparable-Interface. Sie erlaubt, Listen dieser Klasse zu 
	 * sortieren.
	 * 
	 * @param o
	 * @return -1: this<o; 1: this>o; 0:this=o
	 * @throws Exception 
	 */
	public int compareTo(Object o) {
		try {
			Buchungszeile buchungszeile = (Buchungszeile) o;
			int cmp = getBuchung().compareTo(buchungszeile.getBuchung());
			if (cmp != 0)
				return cmp;
			return getID().compareTo(buchungszeile.getID());
		} catch (Exception e) {
			// in compareTo() darf keine "fangbare" Exception geworfen werden
			throw new FiBuRuntimeException(
					"Fehler beim Vergleich von Objekten", e);
		}
	}

	/**
	 * Ausgabe in einen String.
	 */
	public String toString() {
		try {
			return "<" + getKonto().getKontonummer() + "> -> " + getBetrag();
		} catch (Exception e) {
			log.error("Fehler in toString()", e);
			return "EXCEPTION: " + e.getMessage();
		}
	}

	public String ausgabe(Drucktabelle tab, int ebene) {
		Map hash = new HashMap();
		String bezeichnung = StringUtil.times("  ", ebene)
				+ getBuchung().getBuchungstext();
		hash.put("Bezeichnung", bezeichnung);
		Betrag betrag = getBetrag();
		if (betrag.isSoll()) {
			hash.put("Soll", StringUtil.formatNumber(betrag.getWert()));
		} else {
			hash.put("Haben", StringUtil.formatNumber(betrag.getWert()));
		}
		return tab.printZeile(hash);
	}
}
/*
 * $Log: Buchungszeile.java,v $
 * Revision 1.10  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 * Revision 1.9  2005/08/21 17:35:22  tbayen
 * kleinere Warnung beseitigt
 *
 * Revision 1.8  2005/08/21 17:26:12  tbayen
 * doppelte Variable in fast allen von AbstractObject abgeleiteten Klassen
 *
 * Revision 1.7  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.6  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.5  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.4  2005/08/17 21:33:29  tbayen
 * Journal.getBuchungen() neu und alles, was ich dazu benötigt habe
 *
 * Revision 1.3  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
 * Revision 1.2  2005/08/16 21:11:47  tbayen
 * Buchungszeilen werden gespeichert
 *
 * Revision 1.1  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 */