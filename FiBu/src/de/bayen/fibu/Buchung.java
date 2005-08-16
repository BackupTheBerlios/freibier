/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Buchung.java,v 1.1 2005/08/16 08:52:32 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.Date;
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
		write();
		log.info("Buchung neu angelegt");
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

	/**
	 * Ausgabe in einen String
	 */
	public String toString() {
		String erg;
		try {
			erg = "<" + getJournal().getJournalnummer() + "/"
					+ getBelegnummer() +"> - " + getBuchungstext();
		} catch (DatabaseException e) {
			log.error("Fehler in toString()", e);
			erg = "EXCEPTION: " + e.getMessage();
		}
		return erg;
	}
}
/*
 * $Log: Buchung.java,v $
 * Revision 1.1  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 */