/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Buchung.java,v 1.3 2005/08/16 21:11:47 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;

/**
 * Eine Buchung ist ein atomarer Eintrag in die Buchhaltung. Eine Buchung
 * besteht aus mehreren Buchungszeilen. Deren Werte zusammen müssen immer
 * 0,00 ergeben. Eine Buchung wird in ein Journal eingetragen (bzw. über 
 * dieses erzeugt) und ist damit Teil der Buchhaltung, d.h. die 
 * Buchungszeilen erscheinen auch auf den Konten, auf die gebucht wurde. 
 * 
 * @author tbayen
 */
public class Buchung {
	private static Log log = LogFactory.getLog(Buchung.class);
	private Table table;
	private Record record;
	private List zeilen = new ArrayList();

	/**
	 * Erzeugt eine ganz neue Buchung.
	 * 
	 * @param table
	 * @throws DatabaseException 
	 */
	public Buchung(Table table, Journal journal) throws DatabaseException {
		this.table = table;
		record = table.getEmptyRecord();
		setErfassungsdatum(new Date());
		setValutadatum(new Date());
		setJournal(journal);
		write();
		read();
		log.info("Buchung neu angelegt");
	}

	private void read() throws DatabaseException {
		Table zeilentab = table.getDatabase().getTable("Buchungszeilen");
		List liste = zeilentab.getRecordsFromQuery(
				zeilentab.new QueryCondition("Buchung",
						Table.QueryCondition.EQUAL, String.valueOf(getID())),
				null, true);
		for (Iterator iter = liste.iterator(); iter.hasNext();) {
			Record rec = (Record) iter.next();
			Buchungszeile bz = new Buchungszeile(table.getDatabase().getTable(
					"Buchungszeilen"), this, (Long) rec.getPrimaryKey()
					.getValue());
			zeilen.add(bz);
		}
	}

	/**
	 * Änderungen werden erst hiermit endgültig in die Datenbank
	 * geschrieben.
	 * 
	 * @throws DatabaseException
	 */
	public void write() throws DatabaseException {
		DataObject id = table.setRecordAndReturnID(record);
		record = table.getRecordByPrimaryKey(id);
		if (isSaldoNull()) {
			// erstmal alle vorhandenen Buchungszeilen löschen
			Table zeilentab = table.getDatabase().getTable("Buchungszeilen");
			List liste = zeilentab
					.getRecordsFromQuery(zeilentab.new QueryCondition(
							"Buchung", Table.QueryCondition.EQUAL, String
									.valueOf(getID())), null, true);
			for (Iterator iter = liste.iterator(); iter.hasNext();) {
				Record rec = (Record) iter.next();
				table.deleteRecord(rec);
			}
			// und dann die neuen speichern.
			// (Die, die ich ggf. am Anfang gelesen habe, haben schon eine ID,
			// die wird dann praktischerweise wiederbenutzt)
			for (Iterator iter = zeilen.iterator(); iter.hasNext();) {
				Buchungszeile zeile = (Buchungszeile) iter.next();
				zeile.write();
			}
		}
	}

	public int getID() throws DatabaseException {
		return ((Long) record.getField("id").getValue()).intValue();
	}

	public Journal getJournal() throws DatabaseException {
		return new Journal(table.getDatabase().getTable("Journale"),
				((Long) ((ForeignKey) record.getField("Journal").getValue())
						.getKey()).intValue());
	}

	public void setJournal(Journal journal) throws DatabaseException {
		record.setField("Journal", new Long(journal.getID()));
	}

	public String getBelegnummer() throws DatabaseException {
		return record.getField("Belegnummer").format();
	}

	public void setBelegnummer(String nummer) throws DatabaseException {
		record.setField("Belegnummer", nummer);
	}

	public String getBuchungstext() throws DatabaseException {
		return record.getField("Buchungstext").format();
	}

	public void setBuchungstext(String text) throws DatabaseException {
		record.setField("Buchungstext", text);
	}

	public Date getValutadatum() throws DatabaseException {
		return (Date) record.getField("Valutadatum").getValue();
	}

	public void setValutadatum(Date datum) throws DatabaseException {
		record.setField("Valutadatum", datum);
	}

	public Date getErfassungsdatum() throws DatabaseException {
		return (Date) record.getField("Erfassungsdatum").getValue();
	}

	public void setErfassungsdatum(Date datum) throws DatabaseException {
		record.setField("Erfassungsdatum", datum);
	}

	public Buchungszeile createZeile() throws DatabaseException {
		Buchungszeile bz = new Buchungszeile(table.getDatabase().getTable(
				"Buchungszeilen"), this);
		zeilen.add(bz);
		return bz;
	}

	/**
	 * ergibt den Saldo aller Buchungszeilen. Bei einer abgeschlossenen
	 * Buchung muss das immer "0.00" sein.
	 * @throws DatabaseException 
	 */
	public Betrag getSaldo() throws DatabaseException {
		Betrag saldo = new Betrag();
		for (Iterator iter = zeilen.iterator(); iter.hasNext();) {
			Buchungszeile zeile = (Buchungszeile) iter.next();
			saldo = saldo.add(zeile.getBetrag());
		}
		return saldo;
	}

	public boolean isSaldoNull() throws DatabaseException {
		return getSaldo().equals(new Betrag());
	}

	/**
	 * Ausgabe in einen String
	 */
	public String toString() {
		String erg;
		try {
			erg = "<" + getJournal().getJournalnummer() + "/"
					+ getBelegnummer() + "> - " + getBuchungstext();
			for (Iterator iter = zeilen.iterator(); iter.hasNext();) {
				Buchungszeile zeile = (Buchungszeile) iter.next();
				erg += "\n......" + zeile;
			}
			erg += "\n......Saldo:" + getSaldo();
		} catch (DatabaseException e) {
			log.error("Fehler in toString()", e);
			erg = "EXCEPTION: " + e.getMessage();
		}
		return erg;
	}
}
/*
 * $Log: Buchung.java,v $
 * Revision 1.3  2005/08/16 21:11:47  tbayen
 * Buchungszeilen werden gespeichert
 *
 * Revision 1.2  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 * Revision 1.1  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 */