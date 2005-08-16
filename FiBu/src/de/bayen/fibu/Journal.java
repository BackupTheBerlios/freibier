/* Erzeugt am 15.08.2005 von tbayen
 * $Id: Journal.java,v 1.2 2005/08/16 00:06:42 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;

/**
 * Ein Journal entspricht dem Grundbuch der Buchhaltung. Im Grundbuch stehen
 * alle Buchungsvorfälle chronologisch (Im Gegensatz zum sog. "Hauptbuch",
 * das nach Konten unterteilt ist).
 * 
 * @author tbayen
 */
//CREATE TABLE `Journale` (
//		  `id` int(10) unsigned NOT NULL auto_increment,
//		  `Journalnummer` int(10) unsigned NOT NULL default 0,
//		  `Startdatum` date NOT NULL default '0000-00-00',
//		  `Buchungsjahr` char(4) NOT NULL default '',
//		  `Buchungsperiode` char(2) NOT NULL default '',
//		  `absummiert` tinyint(1) NOT NULL default '0',
//		  PRIMARY KEY  (`id`)
//) TYPE=InnoDB AUTO_INCREMENT=1 ;
public class Journal {
	private static Log log = LogFactory.getLog(Journal.class);
	private Table table;
	private Record record;

	protected Journal(Table table, String jahr, String periode)
			throws DatabaseException {
		this.table = table;
		record = table.getEmptyRecord();
		record.setField("Buchungsjahr", jahr);
		record.setField("Buchungsperiode", periode);
		record.setField("Startdatum", new DataObject(new Date(), record
				.getRecordDefinition().getFieldDef("Startdatum")));
		List list = table.getMultipleRecords(0, table.getNumberOfRecords(),
				"Journalnummer", false);
		Object value;
		if (list.size() == 0) {
			value = new Long(1);
		} else {
			value = ((Record) list.get(0)).getField("Journalnummer").getValue();
		}
		record.setField("Journalnummer", new DataObject(value, record
				.getRecordDefinition().getFieldDef("Journalnummer")));
		write();
		log.info("Anlegen eines neuen Journals <"
				+ record.getField("Journalnummer") + ">");
	}

	protected Journal(Table table, String nummer) throws DatabaseException {
		this.table = table;
		Record rec = table.getRecordByValue("Journalnummer", nummer);
		record = table.getRecordByPrimaryKey(rec.getPrimaryKey());
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

	public int getJournalnummer() throws DatabaseException {
		return ((Long) record.getField("Journalnummer").getValue()).intValue();
	}

	public String getStartdatum() throws DatabaseException {
		return record.getField("Startdatum").format();
	}

	public String getBuchungsjahr() throws DatabaseException {
		return record.getField("Buchungsjahr").format();
	}

	public String getBuchungsperiode() throws DatabaseException {
		return record.getField("Buchungsperiode").format();
	}

	/**
	 * Ausgabe des Journals in Textform
	 */
	public String toString() {
		String erg;
		try {
			erg = "Journal <" + getJournalnummer() + ">";
			erg += " vom " + getStartdatum();
			erg += " - Periode: " + getBuchungsperiode() + "/"
					+ getBuchungsjahr() + "\n";
		} catch (Exception e) {
			erg = "EXCEPTION: " + e.getMessage();
		}
		return erg;
	}
}
/*
 * $Log: Journal.java,v $
 * Revision 1.2  2005/08/16 00:06:42  tbayen
 * grundlegende Journal-Eigenschaften implementiert
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */