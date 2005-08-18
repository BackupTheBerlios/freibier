/* Erzeugt am 13.08.2005 von tbayen
 * $Id: Konto.java,v 1.7 2005/08/18 17:04:24 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;

/**
 * Klasse für ein einzelnes Konto unserer Buchhaltung. Ein Konto kann 
 * Buchunggsätze enthalten. Es hat einen Saldo.
 * 
 * @author tbayen
 */
public class Konto extends AbstractObject implements Comparable {
	private static Log log = LogFactory.getLog(Konto.class);
	private Table table;
	private Record record;

	/**
	 * Dieser Konstruktor liest ein vorhandenes Konto aus der Tabelle
	 * 
	 * @param table
	 * @param id
	 * @throws DatabaseException
	 */
	protected Konto(Table table, Long id) throws DatabaseException {
		this.table = table;
		record = table.getRecordByPrimaryKey(id.toString());
	}

	/**
	 * Dieser Konstruktor liest das Konto mit der angegebenen buchhalterischen
	 * Kontonummer aus der Datenbank.
	 */
	protected Konto(Table table, String ktonr) throws DatabaseException {
		this.table = table;
		Record rec = table.getRecordByValue("Kontonummer", ktonr);
		record = table.getRecordByPrimaryKey(rec.getPrimaryKey());
	}

	/**
	 * Dieser Konstruktor erzeugt ein neues Konto
	 * 
	 * @param table
	 * @throws DatabaseException 
	 */
	protected Konto(Table table) throws DatabaseException {
		this.table = table;
		record = table.getEmptyRecord();
		//		record.setField("MwSt","1");
	}

	/**
	 * Änderungen am Konto werden erst hiermit endgültig in die Datenbank
	 * geschrieben.
	 * <p>
	 * (Dies gilt nicht für Buchungen, die ja in eigenen Tabellen verwaltet 
	 * werden.)
	 * </p>
	 * 
	 * @throws DatabaseException
	 */
	public void write() throws DatabaseException {
		DataObject id = table.setRecordAndReturnID(record);
		record = table.getRecordByPrimaryKey(id);
	}

	public Long getID() throws DatabaseException {
		return (Long) record.getField("id").getValue();
	}

	public String getKontonummer() throws DatabaseException {
		return record.getFormatted("Kontonummer");
	}

	public String getBezeichnung() throws DatabaseException {
		return record.getFormatted("Bezeichnung");
	}

	/**
	 * ergibt den Mehrwertsteuersatz als String, also z.B. "16.0"
	 * @return MwSt-Satz
	 * @throws DatabaseException
	 */
	public String getMwSt() throws DatabaseException {
		DataObject field = record.getField("MwSt");
		return field.getForeignResultColumn(table.getDatabase());
	}

	/**
	 * ergibt die buchhalterische Kontonummer des Oberkontos (nicht die ID).
	 * @return String, der die buchhalterische Kontonummer enthält
	 * @throws DatabaseException
	 */
	public String getOberkontoNummer() throws DatabaseException {
		DataObject field = record.getField("Oberkonto");
		return field.getForeignResultColumn(table.getDatabase());
	}

	public Konto getOberkonto() throws DatabaseException {
		Object field = record.getField("Oberkonto").getValue();
		Long ktoid = ((Long) ((ForeignKey) field).getKey());
		if (ktoid == null)
			return null;
		return new Konto(table, ktoid);
	}

	public int getGewicht() throws DatabaseException {
		return Integer.parseInt(record.getFormatted("Gewicht"));
	}

	public void setKontonummer(String ktonr) throws DatabaseException {
		record.setField("Kontonummer", ktonr);
	}

	public void setBezeichnung(String bezeichnung) throws DatabaseException {
		record.setField("Bezeichnung", bezeichnung);
	}

	/**
	 * Hier wird ein ID-Wert als Parameter angegeben (kein MwSt-Satz oder sowas).
	 * @param mwst
	 * @throws DatabaseException
	 */
	public void setMwSt(Long mwst) throws DatabaseException {
		record.setField("MwSt", mwst);
	}

	/**
	 * Hier wird ein ID-Wert als Parameter angegeben (keine Kontonummer).
	 * 
	 * @throws DatabaseException
	 */
	public void setOberkonto(Long kto) throws DatabaseException {
		record.setField("Oberkonto", kto);
	}

	/**
	 * Hier wird ein Konto-Objekt angegeben, um das Oberkonto festzulegen.
	 */
	public void setOberkonto(Konto kto) throws DatabaseException {
		record.setField("Oberkonto", kto.getID());
	}

	/**
	 * Das Oberkonto wird anhand der buchhalterischen Kontonummer gesetzt.
	 * 
	 * @throws DatabaseException
	 */
	public void setOberkontoNummer(String ktonr) throws DatabaseException {
		Record rec = table.getRecordByValue("Kontonummer", ktonr);
		record.setField("Oberkonto", rec.getPrimaryKey());
	}

	public void setGewicht(int gew) throws DatabaseException {
		record.setField("Gewicht", String.valueOf(gew));
	}

	/**
	 * Ergibt eine Liste mit Konto-Objekten. Die aufgelisteten Konten sind die,
	 * die dieses Konto als Oberkonto angegeben haben.
	 * @throws DatabaseException 
	 *
	 */
	public List getUnterkonten() throws DatabaseException {
		List konten = new ArrayList();
		List records = table.getRecordsFromQuery(table.new QueryCondition(
				"Oberkonto", QueryCondition.EQUAL, getID()), null, true);
		for (Iterator iter = records.iterator(); iter.hasNext();) {
			Record rec = (Record) iter.next();
			konten.add(new Konto(table, (Long) rec.getPrimaryKey().getValue()));
		}
		return konten;
	}

	/**
	 * Ergibt alle Buchungszeilen auf diesem Konto.
	 * @throws DatabaseException 
	 */
	public List getBuchungszeilen() throws DatabaseException {
		List zeilen = new ArrayList();
		Table table = this.table.getDatabase().getTable("Buchungszeilen");
		List records = table.getRecordsFromQuery(table.new QueryCondition(
				"Konto", QueryCondition.EQUAL, getID()), null, true);
		for (Iterator iter = records.iterator(); iter.hasNext();) {
			Record zeile = (Record) iter.next();
			zeilen.add(new Buchungszeile(table, (Long) zeile.getPrimaryKey()
					.getValue()));
		}
		// Besser wäre, die Sortierung dem SQL-Server zu überlassen, aber bei
		// dieser Datenstruktur ist das mit FreibierDB nicht so einfach. 
		// Dies hier ist aufwendiger, aber einstweilen funktioniert es:
		Collections.sort(zeilen);
		return zeilen;
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
			Konto konto = (Konto) o;
			int cmp = getKontonummer().compareTo(konto.getKontonummer());
			return cmp;
		} catch (Exception e) {
			// in compareTo() darf keine "fangbare" Exception geworfen werden
			throw new RuntimeException("Fehler beim Vergleich von Objekten", e);
		}
	}

	/**
	 * Ausgabe des Kontos in Textform
	 */
	public String toString() {
		String erg;
		try {
			erg = "Konto <" + getKontonummer() + ">:";
			erg += " '" + getBezeichnung() + "'";
			erg += "\n";
			erg += "(";
			erg += "Oberkonto <" + getOberkontoNummer() + ">";
			erg += "; Gewicht: " + getGewicht();
			erg += ")";
			for (Iterator iter = getBuchungszeilen().iterator(); iter.hasNext();) {
				Buchungszeile zeile = (Buchungszeile) iter.next();
				erg += "\n" + zeile;
			}
		} catch (Exception e) {
			log.error("Fehler in toString()", e);
			erg = "EXCEPTION: " + e.getMessage();
		}
		return erg;
	}
}
/*
 * $Log: Konto.java,v $
 * Revision 1.7  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.6  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.5  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 * Revision 1.4  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
 * Revision 1.3  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 * Revision 1.2  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */