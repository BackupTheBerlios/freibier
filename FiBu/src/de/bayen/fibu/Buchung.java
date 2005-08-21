/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Buchung.java,v 1.8 2005/08/21 17:08:55 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.SQL_getTableDBException;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.exceptions.FiBuRuntimeException;
import de.bayen.fibu.exceptions.ImpossibleException;

/**
 * Eine Buchung ist ein atomarer Eintrag in die Buchhaltung. Eine Buchung
 * besteht aus mehreren Buchungszeilen. Deren Werte zusammen müssen immer
 * 0,00 ergeben. Eine Buchung wird in ein Journal eingetragen (bzw. über 
 * dieses erzeugt) und ist damit Teil der Buchhaltung, d.h. die 
 * Buchungszeilen erscheinen auch auf den Konten, auf die gebucht wurde. 
 * 
 * @author tbayen
 */
public class Buchung extends AbstractObject implements Comparable {
	private static Log log = LogFactory.getLog(Buchung.class);
	private Table table;
	private Record record;
	private List zeilen = new ArrayList();

	/**
	 * Erzeugt eine ganz neue Buchung.
	 * 
	 * @param table
	 * @throws ParseErrorDBException 
	 * @throws SQL_DBException 
	 */
	protected Buchung(Table table, Journal journal) throws SQL_DBException {
		this.table = table;
		record = table.getEmptyRecord();
		setErfassungsdatum(new Date());
		setValutadatum(new Date());
		setJournal(journal);
		try {
			write();
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
		read();
		log.info("Buchung neu angelegt");
	}

	/**
	 * liest eine vorhandene Buchung anhand der ID aus der Datenbank.
	 * 
	 * @param table
	 * @param nummer
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	protected Buchung(Table table, Long nummer) throws SQL_DBException,
			RecordNotExistsDBException {
		this.table = table;
		try {
			record = table.getRecordByPrimaryKey(nummer);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	private void read() throws SQL_DBException {
		Table zeilentab;
		try {
			zeilentab = table.getDatabase().getTable("Buchungszeilen");
			List liste = zeilentab
					.getRecordsFromQuery(zeilentab.new QueryCondition(
							"Buchung", Table.QueryCondition.EQUAL, String
									.valueOf(getID())), null, true);
			for (Iterator iter = liste.iterator(); iter.hasNext();) {
				Record rec = (Record) iter.next();
				Buchungszeile bz;
				try {
					bz = new Buchungszeile(table.getDatabase().getTable(
							"Buchungszeilen"), this, (Long) rec.getPrimaryKey()
							.getValue());
				} catch (RecordNotExistsDBException e) {
					throw new ImpossibleException(e, log);
				}
				zeilen.add(bz);
			}
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Änderungen werden erst hiermit endgültig in die Datenbank
	 * geschrieben.
	 * @throws SQL_DBException 
	 * @throws ParseErrorDBException 
	 */
	public void write() throws SQL_DBException, ParseErrorDBException {
		setErfassungsdatum(new Date());
		try {
			record = table.setRecordAndGetRecord(record);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
		if (isSaldoNull()) {
			// TODO das hier ist ein Fall für eine Transaktion
			// erstmal alle vorhandenen Buchungszeilen löschen:
			Table zeilentab = table.getDatabase().getTable("Buchungszeilen");
			List liste;
			try {
				liste = zeilentab.getRecordsFromQuery(
						zeilentab.new QueryCondition("Buchung",
								Table.QueryCondition.EQUAL, String
										.valueOf(getID())), null, true);
			} catch (TypeNotSupportedDBException e1) {
				throw new ImpossibleException(e1, log);
			} catch (ParseErrorDBException e1) {
				throw new ImpossibleException(e1, log);
			}
			for (Iterator iter = liste.iterator(); iter.hasNext();) {
				Record rec = (Record) iter.next();
				try {
					table.deleteRecord(rec);
				} catch (RecordNotExistsDBException e) {
					throw new ImpossibleException(e, log);
				} catch (TypeNotSupportedDBException e) {
					throw new ImpossibleException(e, log);
				}
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

	public Long getID() {
		return (Long) record.getField("id").getValue();
	}

	public Journal getJournal() throws SQL_DBException {
		try {
			return new Journal(table.getDatabase().getTable("Journale"),
					(Long) ((ForeignKey) record.getField("Journal").getValue())
							.getKey());
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (RecordNotExistsDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setJournal(Journal journal) {
		try {
			record.setField("Journal", journal.getID());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public String getBelegnummer() {
		try {
			return record.getField("Belegnummer").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setBelegnummer(String nummer) throws ParseErrorDBException {
		record.setField("Belegnummer", nummer);
	}

	public String getBuchungstext() {
		try {
			return record.getField("Buchungstext").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setBuchungstext(String text) throws ParseErrorDBException {
		record.setField("Buchungstext", text);
	}

	public Date getValutadatum() {
		return (Date) record.getField("Valutadatum").getValue();
	}

	public void setValutadatum(Date datum) {
		try {
			record.setField("Valutadatum", datum);
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Date getErfassungsdatum() {
		return (Date) record.getField("Erfassungsdatum").getValue();
	}

	private void setErfassungsdatum(Date datum) {
		try {
			record.setField("Erfassungsdatum", datum);
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Buchungszeile createZeile() throws SQL_getTableDBException {
		Buchungszeile bz;
		try {
			bz = new Buchungszeile(table.getDatabase().getTable(
					"Buchungszeilen"), this);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
		zeilen.add(bz);
		return bz;
	}

	/**
	 * Ergibt die Liste der Buchungszeilen.
	 * 
	 * @return List von Buchungszeilen-Objekten
	 */
	public List getBuchungszeilen() {
		return new ArrayList(zeilen);
	}

	/**
	 * ergibt den Saldo aller Buchungszeilen. Bei einer abgeschlossenen
	 * Buchung muss das immer "0.00" sein.
	 */
	public Betrag getSaldo() {
		Betrag saldo = new Betrag();
		for (Iterator iter = zeilen.iterator(); iter.hasNext();) {
			Buchungszeile zeile = (Buchungszeile) iter.next();
			saldo = saldo.add(zeile.getBetrag());
		}
		return saldo;
	}

	public boolean isSaldoNull() {
		return getSaldo().equals(new Betrag());
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
			Buchung buchung = (Buchung) o;
			int cmp = getErfassungsdatum().compareTo(
					buchung.getErfassungsdatum());
			if (cmp != 0)
				return cmp;
			return getID().compareTo(buchung.getID());
		} catch (Exception e) {
			// in compareTo() darf keine "fangbare" Exception geworfen werden
			throw new FiBuRuntimeException(
					"Fehler beim Vergleich von Objekten", e);
		}
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
 * Revision 1.8  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.7  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.6  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.5  2005/08/17 21:33:29  tbayen
 * Journal.getBuchungen() neu und alles, was ich dazu benötigt habe
 *
 * Revision 1.4  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
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