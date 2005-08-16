/* Erzeugt am 13.08.2005 von tbayen
 * $Id: Konto.java,v 1.2 2005/08/16 08:52:32 tbayen Exp $
 */
package de.bayen.fibu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;

/**
 * Klasse für ein einzelnes Konto unserer Buchhaltung. Ein Konto kann 
 * Buchunggsätze enthalten. Es hat einen Saldo.
 * 
 * @author tbayen
 */
public class Konto {
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
	protected Konto(Table table, int id) throws DatabaseException {
		this.table = table;
		record = table.getRecordByPrimaryKey(String.valueOf(id));
	}

	/**
	 * Dieser Konstruktor liest das Konto mit der angegebenen buchhalterischen
	 * Kontonummer aus der Datenbank.
	 */
	protected Konto(Table table, String ktonr) throws DatabaseException{
		this.table=table;
		Record rec = table.getRecordByValue("Kontonummer",ktonr);
		record=table.getRecordByPrimaryKey(rec.getPrimaryKey());
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
		DataObject id=table.setRecordAndReturnID(record);
		record=table.getRecordByPrimaryKey(id);
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
		Long ktoid = ((Long)((ForeignKey) field).getKey());
		if (ktoid == null)
			return null;
		return new Konto(table, ktoid.intValue());
	}

	public int getGewicht() throws DatabaseException {
		return Integer.parseInt(record.getFormatted("Gewicht"));
	}
	
	public void setKontonummer(String ktonr) throws DatabaseException{
		record.setField("Kontonummer", ktonr);
	}

	public void setBezeichnung(String bezeichnung) throws DatabaseException{
		record.setField("Bezeichnung", bezeichnung);
	}

	/**
	 * Hier wird ein ID-Wert als Parameter angegeben (kein MwSt-Satz oder sowas).
	 * @param mwst
	 * @throws DatabaseException
	 */
	public void setMwSt(int mwst) throws DatabaseException{
		record.setField("MwSt", String.valueOf(mwst));
	}

	/**
	 * Hier wird ein ID-Wert als Parameter angegeben (keine Kontonummer).
	 * 
	 * @throws DatabaseException
	 */
	public void setOberkonto(int kto) throws DatabaseException{
		record.setField("Oberkonto", String.valueOf(kto));
	}

	/**
	 * Das Oberkonto wird anhand der buchhalterischen Kontonummer gesetzt.
	 * 
	 * @throws DatabaseException
	 */
	public void setOberkontoNummer(String ktonr) throws DatabaseException{
		Record rec = table.getRecordByValue("Kontonummer",ktonr);
		record.setField("Oberkonto", rec.getPrimaryKey());
	}

	public void setGewicht(int gew) throws DatabaseException{
		record.setField("Gewicht", String.valueOf(gew));
	}

	/**
	 * Ausgabe des Kontos in Textform
	 */
	public String toString(){
		String erg;
		try {
			erg = "Konto <" + getKontonummer() + ">:";
			erg += " '" + getBezeichnung() + "'";
			erg+="\n";
			erg+="(";
			erg+="Oberkonto <"+getOberkontoNummer()+">";
			erg+="; Gewicht: "+getGewicht();
			erg+=")\n";
		} catch (Exception e) {
			log.error("Fehler in toString()",e);
			erg="EXCEPTION: "+e.getMessage();
		}
		return erg;
	}
}
/*
 * $Log: Konto.java,v $
 * Revision 1.2  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */