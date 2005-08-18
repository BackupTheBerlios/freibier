/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Buchungszeile.java,v 1.5 2005/08/18 14:14:04 tbayen Exp $
 */
package de.bayen.fibu;

import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;

/**
 * 
 * Eine Buchungszeile ist eine einzelne Buchung auf ein einzelnes Konto.
 * Mehrere Buchungszeilen, deren Saldo 0.00 ist, ergeben zusammen eine 
 * Buchung.
 * 
 * @author tbayen
 */
public class Buchungszeile implements Comparable {
	private static Log log = LogFactory.getLog(Buchungszeile.class);
	private Table table;
	private Record record;
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
	protected Buchungszeile(Table table, Buchung buchung)
			throws DatabaseException {
		this.table = table;
		this.buchung = buchung;
		record = table.getEmptyRecord();
		setBetrag(new Betrag(new BigDecimal("0.00"), "S"));
		setBuchung(buchung);
		log.debug("neue Buchungszeile erzeugt");
	}

	/**
	 * Liest eine vorhandene Buchungszeile aus der Datenbank und verknüpft sie
	 * mit einem bereits geladenen Buchungs-Objekt.
	 * 
	 * @param table
	 * @param buchung
	 * @param nummer
	 * @throws DatabaseException
	 */
	protected Buchungszeile(Table table, Buchung buchung, Long nummer)
			throws DatabaseException {
		this.table = table;
		this.buchung = buchung;
		record = table.getRecordByPrimaryKey(nummer);
	}

	/**
	 * Liest eine vorhandene Buchungszeile aus der Datenbank.
	 * 
	 * @param table
	 * @param nummer
	 * @throws DatabaseException
	 */
	protected Buchungszeile(Table table, Long nummer) throws DatabaseException {
		this.table = table;
		record = table.getRecordByPrimaryKey(nummer);
		this.buchung = new Buchung(table.getDatabase().getTable("Buchungen"),
				((Long) ((ForeignKey) record.getField("Buchung").getValue())
						.getKey()));
	}

	/**
	 * Änderungen werden erst hiermit endgültig in die Datenbank
	 * geschrieben. die Methode ist nicht public, da das Schreiben von
	 * Buchungszeilen nur bei kompletten Buchungen erfolgen darf.
	 * 
	 * @throws DatabaseException
	 */
	protected void write() throws DatabaseException {
		DataObject id = table.setRecordAndReturnID(record);
		record = table.getRecordByPrimaryKey(id);
	}

	public Long getID() throws DatabaseException {
		return (Long) record.getField("id").getValue();
	}

	public Betrag getBetrag() throws DatabaseException {
		return new Betrag((BigDecimal) record.getField("Betrag").getValue(),
				record.getField("SH").format());
	}

	public void setBetrag(Betrag betrag) throws DatabaseException {
		record.setField("Betrag", betrag.getWert());
		record.setField("SH", String.valueOf(betrag.getSollHaben()));
	}

	public Buchung getBuchung() throws DatabaseException {
		// Ich extrahiere die Buchung nicht aus dem Record, weil ich dann eine
		// neue Buchung erzeugen würde. Ich will aber die identische haben, da
		// diese ja evtl. andere Werte enthält als in der Datenbank stehen.
		return buchung;
	}

	public void setBuchung(Buchung buchung) throws DatabaseException {
		record.setField("Buchung", buchung.getID());
		this.buchung = buchung;
	}

	public Konto getKonto() throws DatabaseException {
		return new Konto(table.getDatabase().getTable("Konten"),
				(Long) ((ForeignKey) record.getField("Konto").getValue())
						.getKey());
	}

	public void setKonto(Konto konto) throws DatabaseException {
		record.setField("Konto", konto.getID());
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
	public int compareTo(Object o){
		try {
			Buchungszeile buchungszeile = (Buchungszeile) o;
			int cmp = getBuchung().compareTo(buchungszeile.getBuchung());
			if (cmp != 0)
				return cmp;
			return getID().compareTo(buchungszeile.getID());
		} catch (Exception e) {
			// in compareTo() darf keine "fangbare" Exception geworfen werden
			throw new RuntimeException("Fehler beim Vergleich von Objekten", e);
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
}
/*
 * $Log: Buchungszeile.java,v $
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