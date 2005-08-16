/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Buchungszeile.java,v 1.1 2005/08/16 12:22:09 tbayen Exp $
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
public class Buchungszeile {
	private static Log log = LogFactory.getLog(Buchungszeile.class);
	private Table table;
	private Record record;
	private Buchung buchung;

	/**
	 * erzeugt eine neue Buchungszeile. Diese wird noch nicht in die Datenbank
	 * geschrieben. Das sollte erst veranlasst werden, wenn die Buchung komplett
	 * ist.
	 * 
	 * @param table
	 * @throws DatabaseException 
	 */
	public Buchungszeile(Table table, Buchung buchung) throws DatabaseException {
		this.table = table;
		this.buchung=buchung;
		record = table.getEmptyRecord();
		setBetrag(new Betrag(new BigDecimal("0.00"),"S"));
		setBuchung(buchung);
		log.debug("neue Buchungszeile erzeugt");
	}

	/**
	 * Änderungen werden erst hiermit endgültig in die Datenbank
	 * geschrieben. die Methode ist nicht public, da das Schreiben von
	 * Buchungszeilen nur in ganzen Buchungen erfolgen darf.
	 * 
	 * @throws DatabaseException
	 */
	protected void write() throws DatabaseException {
		DataObject id = table.setRecordAndReturnID(record);
		record = table.getRecordByPrimaryKey(id);
	}

	public int getID() throws DatabaseException{
		return ((Long)record.getField("id").getValue()).intValue();
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
		record.setField("Buchung", new Long(buchung.getID()));
		this.buchung=buchung;
	}

	public Konto getKonto() throws DatabaseException {
		return new Konto(table.getDatabase().getTable("Konten"),
				((Long) ((ForeignKey) record.getField("Konto").getValue())
						.getKey()).intValue());
	}

	public void setKonto(Konto konto) throws DatabaseException {
		record.setField("Konto", new Long(konto.getID()));
	}
		
	/**
	 * Ausgabe in einen String.
	 */
	public String toString(){
		try {
			return "<"+getKonto().getKontonummer() + "> -> " + getBetrag();
		} catch (Exception e) {
			log.error("Fehler in toString()", e);
			return "EXCEPTION: " + e.getMessage();
		}
	}
}
/*
 * $Log: Buchungszeile.java,v $
 * Revision 1.1  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 */