/* Erzeugt am 15.08.2005 von tbayen
 * $Id: Journal.java,v 1.10 2005/08/21 17:08:54 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.exceptions.ImpossibleException;

/**
 * Ein Journal entspricht dem Grundbuch der Buchhaltung. Im Grundbuch stehen
 * alle Buchungsvorfälle chronologisch (Im Gegensatz zum sog. "Hauptbuch",
 * das nach Konten unterteilt ist).
 * 
 * @author tbayen
 */
public class Journal extends AbstractObject implements Comparable {
	private static Log log = LogFactory.getLog(Journal.class);
	private Table table;

	protected Journal(Table table, String jahr, String periode)
			throws ParseErrorDBException, SQL_DBException {
		this.table = table;
		record = table.getEmptyRecord();
		record.setField("Buchungsjahr", jahr);
		record.setField("Buchungsperiode", periode);
		record.setField("Startdatum", new DataObject(new Date(), record
				.getFieldDef("Startdatum")));
		List list = table.getMultipleRecords(0, table.getNumberOfRecords(),
				"Journalnummer", false);
		int value;
		if (list.size() == 0) {
			value = 1;
		} else {
			value = ((Long) ((Record) list.get(0)).getField("Journalnummer")
					.getValue()).intValue() + 1;
		}
		record.setField("Journalnummer", new DataObject(new Long(value), record
				.getFieldDef("Journalnummer")));
		write();
		log.info("Anlegen eines neuen Journals <"
				+ record.getField("Journalnummer").format() + ">");
	}

	/**
	 * liest ein vorhandenes Journal aus der Datenbank.
	 * 
	 * @param table
	 * @param nummer
	 * @throws SQL_DBException 
	 * @throws RecordNotExistsDBException 
	 */
	protected Journal(Table table, Long nummer) throws SQL_DBException,
			RecordNotExistsDBException {
		this.table = table;
		Record rec;
		try {
			rec = table.getRecordByValue("Journalnummer", nummer.toString());
			record = table.getRecordByPrimaryKey(rec.getPrimaryKey());
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Änderungen werden erst hiermit endgültig in die Datenbank
	 * geschrieben.
	 * @throws SQL_DBException 
	 */
	public void write() throws SQL_DBException {
		try {
			record = table.setRecordAndGetRecord(record);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Long getJournalnummer() {
		return (Long) record.getField("Journalnummer").getValue();
	}

	public String getStartdatum() {
		try {
			return record.getField("Startdatum").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public String getBuchungsjahr() {
		try {
			return record.getField("Buchungsjahr").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public String getBuchungsperiode() {
		try {
			return record.getField("Buchungsperiode").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Stellt fest, ob dieses Journal bereits absummiert ist. In einem
	 * absummierten Journal kann nicht mehr weitergebucht werden.
	 * 
	 * @return boolean
	 */
	public boolean isAbsummiert() {
		try {
			return record.getField("absummiert").format().equals("");
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Hiermit wird ein Journal absummiert. Danach kann in dieses Journal
	 * nicht mehr gebucht werden. Dieser Vorgang kann nicht mehr
	 * rückgängig gemacht werden. Der Vorgang wird direkt in der Datenbank
	 * vermerkt, braucht also nicht mehr mit write() bestätigt zu werden.
	 * @throws SQL_DBException 
	 */
	public void absummieren() throws SQL_DBException {
		try {
			record.setField("absummiert", new Boolean(true));
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
		write();
	}

	// Umgang mit Buchungen
	/**
	 * Erzeugt eine ganz neue Buchung
	 * @throws SQL_DBException 
	 */
	public Buchung createBuchung() throws SQL_DBException {
		if (isAbsummiert()) {
			return null;
		} else {
			try {
				return new Buchung(table.getDatabase().getTable("Buchungen"),
						this);
			} catch (IllegalDefaultValueDBException e) {
				throw new ImpossibleException(e, log);
			} catch (ParseErrorDBException e) {
				throw new ImpossibleException(e, log);
			}
		}
	}

	/**
	 * Ergibt eine Liste der Buchungen in diesem Journal. Die Liste ist 
	 * nach Erfassungsdatum sortiert.
	 * @throws SQL_DBException 
	 */
	public List getBuchungen() throws SQL_DBException {
		List buchungen = new ArrayList();
		Table table;
		try {
			table = this.table.getDatabase().getTable("Buchungen");
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
		List records;
		try {
			records = table.getRecordsFromQuery(table.new QueryCondition(
					"Journal", QueryCondition.EQUAL, getID()),
					"Erfassungsdatum,id", true);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
		for (Iterator iter = records.iterator(); iter.hasNext();) {
			Record rec = (Record) iter.next();
			try {
				buchungen.add(new Buchung(table, (Long) rec.getPrimaryKey()
						.getValue()));
			} catch (RecordNotExistsDBException e) {
				throw new ImpossibleException(e, log);
			}
		}
		return buchungen;
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
			Journal journal = (Journal) o;
			int cmp = getJournalnummer().compareTo(journal.getJournalnummer());
			return cmp;
		} catch (Exception e) {
			// in compareTo() darf keine "fangbare" Exception geworfen werden
			throw new RuntimeException("Fehler beim Vergleich von Objekten", e);
		}
	}

	// Ausgabefunktionen
	/**
	 * Ausgabe des Journals in Textform
	 */
	public String toString() {
		String erg;
		try {
			erg = "Journal <" + getJournalnummer() + ">";
			erg += " vom " + getStartdatum();
			erg += " - Periode: " + getBuchungsperiode() + "/"
					+ getBuchungsjahr();
			if (isAbsummiert())
				erg += " --Absummiert--";
			List buchungen = getBuchungen();
			for (Iterator iter = buchungen.iterator(); iter.hasNext();) {
				Buchung buch = (Buchung) iter.next();
				erg += "\n" + buch;
			}
		} catch (Exception e) {
			log.error("Fehler in toString()", e);
			erg = "EXCEPTION: " + e.getMessage();
		}
		return erg;
	}
}
/*
 * $Log: Journal.java,v $
 * Revision 1.10  2005/08/21 17:08:54  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.9  2005/08/18 17:04:23  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.8  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.7  2005/08/17 21:33:29  tbayen
 * Journal.getBuchungen() neu und alles, was ich dazu benötigt habe
 *
 * Revision 1.6  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 * Revision 1.5  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
 * Revision 1.4  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 * Revision 1.3  2005/08/16 07:02:33  tbayen
 * Journal-Klasse steht als Grundgerüst
 *
 * Revision 1.2  2005/08/16 00:06:42  tbayen
 * grundlegende Journal-Eigenschaften implementiert
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */