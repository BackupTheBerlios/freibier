/* Erzeugt am 12.08.2005 von tbayen
 * $Id: Buchhaltung.java,v 1.9 2005/08/21 17:08:55 tbayen Exp $
 */
package de.bayen.fibu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.UserDBEx;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.SQL_getTableDBException;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.database.exception.UserDBEx.UserSQL_DBException;
import de.bayen.fibu.exceptions.FiBuException;
import de.bayen.fibu.exceptions.FiBuRuntimeException;
import de.bayen.fibu.exceptions.ImpossibleException;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;

/**
 * Diese Klasse ist die Hauptklasse, die eine komplette Buchhaltung beschreibt.
 * Sie erzeugt eine Verbindung zur Datenbank und sie kann dann benutzt werden,
 * um andere Klassenobjekte aus der Datenbank zu holen.
 * <p>
 * Beim Erzeugen der Buchhaltung kann eine bestimmte Datenbank angegeben 
 * werden.
 * 
 * @author tbayen
 */
public class Buchhaltung extends AbstractObject {
	private static Log log = LogFactory.getLog(Buchhaltung.class);
	private Database db = null;
	private Record record = null;

	/**
	 * Dies main-Methode ist hier nur zu Testzwecken w�hrend der Entwicklung
	 * der Klasse.
	 */
	public static void main(String[] args) {
		log.info("Buchhaltung.main()");
	}

	/**
	 * Der Konstruktor �ffnet die Datenbankverbindung und l�sst so �berhaupt erst
	 * das Arbeiten mit der Klasse Buchhaltung zu.
	 * 
	 * @param name
	 * @param server
	 * @param user
	 * @param password
	 * @throws UserSQL_DBException 
	 */
	public Buchhaltung(String name, String server, String user, String password)
			throws UserSQL_DBException {
		db = new Database(name, server, user, password);
		db.setPropertyPath(getClass().getPackage().getName() + ".dbdefinition");
	}

	/**
	 * Dieser Konstruktor �ffnet die Datenbank mit Standard-Parametern
	 * @throws UserSQL_DBException 
	 * @throws DatabaseException 
	 *
	 */
	public Buchhaltung() throws UserSQL_DBException {
		// diese Parameter sind in einem Standard-Debian m�glich:
		db = new Database("test", "localhost", "test", null);
		db.setPropertyPath(getClass().getPackage().getName() + ".dbdefinition");
	}

	public void close() throws SQL_DBException {
		db.close();
		db = null;
	}

	/**
	 * Diese Methode bietet den direkten Zugriff auf die Datenbank. Sie sollte
	 * normalerweise nicht n�tig sein. Bisher wird sie nur beim Debugging,
	 * also bei Tests, benutzt. Falls direkter DB-Zugriff f�r ein Spezialproblem
	 * n�tig erscheint, sollten besser die FiBu-Klassen erweitert werden.
	 */
	public Database getDatabase() {
		return db;
	}

	/**
	 * Hiermit kann gepr�ft werden, ob die Buchhaltung initialisiert ist,
	 * die Datenbankverbindung steht, etc., ob ich also mit der Klasse
	 * arbeiten kann.
	 * @return boolean
	 */
	public boolean ok() {
		if (db == null)
			return false;
		if (db.ok() == false)
			return false;
		return true;
	}

	/**
	 * Arbeitet wie ok(), wirft aber eine Exception, falls etwas nicht ok ist.
	 * @throws NotInitializedException 
	 * @throws UserDBEx 
	 */
	public void assertOk() throws NotInitializedException {
		if (ok())
			return;
		throw new FiBuException.NotInitializedException(
				"Buchhaltung nicht initialisiert (kein Zugriff auf Datenbank?)",
				log);
	}

	/**
	 * Diese Methode wird aufgerufen, um die Datenbank erstmalig zu 
	 * initialisieren. ACHTUNG: Bestehende Daten werden ohne R�ckfrage 
	 * gel�scht!!!
	 * 
	 * @throws DatabaseException
	 */
	public void firstTimeInit() throws DatabaseException {
		db.wipeOutDatabase();
		try {
			db.executeSqlFile("de/bayen/fibu/dbdefinition/db_definition.sql");
		} catch (IOException e) {
			throw new FiBuRuntimeException(
					"Kann Datendefinitionsdatei nicht lesen", e, log);
		}
	}

	/**
	 * Hiermit wird eine Firma aus den Firmenstammdaten ausgew�hlt. Die ID-Nummer
	 * aus der Datenbank muss dazu bekannt sein.
	 * 
	 * @param nr
	 * @return Record, der die Firmenstammdaten enth�lt
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 * @throws RecordNotExistsDBException 
	 */
	public Record setFirmenstammdaten(int nr) throws NotInitializedException,
			SQL_DBException, RecordNotExistsDBException {
		assertOk();
		try {
			record = db.getTable("Firmenstammdaten").getRecordByPrimaryKey(
					String.valueOf(nr));
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
		return record;
	}

	/**
	 * Hier wird eine neue Firma angelegt, falls das ID-Feld des �bergebenen
	 * Datensatzes noch nicht belegt ist. Ansonsten wird der angegebene Satz
	 * in der Datenbank ge�ndert auf die Werte des �bergebenen Records.
	 * 
	 * @param stamm
	 * @return Record, der die Firmenstammdaten enth�lt
	 * @throws NotInitializedException 
	 * @throws ParseErrorDBException 
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	public Record setFirmenstammdaten(Record stamm)
			throws NotInitializedException, SQL_DBException,
			ParseErrorDBException {
		assertOk();
		try {
			Table table = db.getTable("Firmenstammdaten");
			record = table.setRecordAndGetRecord(stamm);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
		return record;
	}

	/**
	 * ergibt den Record der aktuellen Firmenstammdaten. Ist noch keine bestimmte
	 * Firma gesetzt (mit setFirmenstammdaten()), so wird die erste (und oft
	 * wohl auch die einzige) in der Datenbank genommen.
	 * <p>
	 * Ist noch kein Eintrag vorhanden, wird ein Standard-Eintrag mit
	 * unverf�nglichen Werten angelegt. 
	 * </p><p>
	 * Die Benutzung mehrerer Firmen ist vorerst nicht implementiert, soll durch
	 * diese API jedoch vorbereitet werden.
	 * </p>
	 * @return Record, der die Firmenstammdaten enth�lt
	 * @throws NotInitializedException 
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	public Record getFirmenstammdaten() throws NotInitializedException,
			SQL_DBException {
		assertOk();
		if (record == null) {
			Table table;
			try {
				table = db.getTable("Firmenstammdaten");
				try {
					record = table.getRecordByNumber(0);
				} catch (RecordNotExistsDBException e) {
					record = table.getEmptyRecord();
					record.setField("Firma", "Finanzbuchhaltung");
					record.setField("PeriodeAktuell", "01");
					record.setField("JahrAktuell", "2005");
					return setFirmenstammdaten(record);
				}
			} catch (SQL_getTableDBException e1) {
				throw new ImpossibleException(e1, log);
			} catch (IllegalDefaultValueDBException e1) {
				throw new ImpossibleException(e1, log);
			} catch (ParseErrorDBException e1) {
				throw new ImpossibleException(e1, log);
			}
		}
		return record;
	}

	/**
	 * ergibt den aktuellen Firmennamen
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 */
	public String getFirma() throws SQL_DBException, NotInitializedException {
		try {
			return getFirmenstammdaten().getField("Firma").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Ergibt das aktuelle Buchungsjahr. Dies ist das Jahr, in das automatisch
	 * gebucht wird, d.h. dieser Wert kann z.B. in einer GUI als Vorgabe
	 * verwendet werden.
	 * <p>
	 * Dennoch ist es m�glich, auch in andere Jahre zu buchen, wenn man dies
	 * bei der Buchung angibt.
	 * </p>
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 */
	public String getJahrAktuell() throws SQL_DBException,
			NotInitializedException {
		try {
			return getFirmenstammdaten().getField("JahrAktuell").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setJahrAktuell(String jahr) throws DatabaseException,
			NotInitializedException {
		Record stamm = getFirmenstammdaten();
		stamm.setField("JahrAktuell", jahr);
		setFirmenstammdaten(stamm);
	}

	/**
	 * Ergibt die aktuelle Buchungsperiode. Die Buchungsperiode ist eine
	 * Unterteilung des Buchungsjahres f�r Zwecke der Statistik und Auswertung.
	 * Eine Periode kann z.B. ein Monat oder ein Quartal sein. So k�nnen
	 * nachher Auswertungen gefahren werden, die gleiche Perioden aus 
	 * verschiedenen Jahren vergleichen, etc.
	 * <p>
	 * Die Periode ist ein zweistelliger Text. Bei Monaten kann hier z.B.
	 * 01,02,03,...,12 angegeben werden, andere Texte sind aber genauso
	 * denkbar. Wichtig ist, da� die Perioden innerhalb des Jahres alphabetisch
	 * sortiert vorkommen.
	 * </p><p>
	 * Weicht das Gesch�ftsjahr vom Kalenderjahr ab, so sollte "01" f�r die
	 * erste Periode stehen, nicht f�r Januar.
	 * </p>
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 */
	public String getPeriodeAktuell() throws SQL_DBException,
			NotInitializedException {
		try {
			return getFirmenstammdaten().getField("PeriodeAktuell").format();
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setPeriodeAktuell(String jahr) throws NotInitializedException,
			SQL_DBException, ParseErrorDBException {
		Record stamm = getFirmenstammdaten();
		stamm.setField("PeriodeAktuell", jahr);
		setFirmenstammdaten(stamm);
	}

	/**
	 * Erzeugt ein neues Konto (ohne, das Nummer, Bezeichnung, etc. 
	 * initialisiert werden).
	 * 
	 * @return Konto
	 */
	public Konto createKonto() {
		try {
			return new Konto(db.getTable("Konten"));
		} catch (SQL_getTableDBException e) {
			throw new ImpossibleException(e, log);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Liest ein vorhandenes Konto aus der Datenbank und erzeugt ein
	 * entsprechendes Konto-Objekt.
	 * 
	 * @param ktonr
	 * @return Konto
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	public Konto getKonto(String ktonr) throws SQL_DBException,
			RecordNotExistsDBException {
		try {
			return new Konto(db.getTable("Konten"), ktonr);
		} catch (SQL_getTableDBException e) {
			throw new ImpossibleException(e, log);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Erzeugt ein ganz neues Journal. Das Journal wird direkt in der
	 * Datenbank erzeugt, muss also nicht noch mit write() best�tigt werden.
	 * 
	 * @return Journal
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 */
	public Journal createJournal() throws SQL_DBException,
			NotInitializedException {
		try {
			return new Journal(db.getTable("Journale"), getJahrAktuell(),
					getPeriodeAktuell());
		} catch (SQL_getTableDBException e) {
			throw new ImpossibleException(e, log);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Journal getJournal(Long nummer) throws SQL_DBException,
			RecordNotExistsDBException {
		try {
			return new Journal(db.getTable("Journale"), nummer);
		} catch (SQL_getTableDBException e) {
			throw new ImpossibleException(e, log);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Ergibt eine Liste, die alle Journale der Buchhaltung enth�lt.
	 * @throws SQL_DBException 
	 */
	public List getAlleJournale() throws SQL_DBException {
		List journale = new ArrayList();
		Table table;
		try {
			table = db.getTable("Journale");
			List records = table.getRecordsFromQuery(null, null, true);
			for (Iterator iter = records.iterator(); iter.hasNext();) {
				Record rec = (Record) iter.next();
				journale.add(new Journal(table, (Long) rec.getPrimaryKey()
						.getValue()));
			}
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (RecordNotExistsDBException e) {
			throw new ImpossibleException(e, log);
		}
		return journale;
	}

	/**
	 * Ergibt eine Liste, die nur die nicht-abgeschlossenen Journale der 
	 * Buchhaltung enth�lt. Die Liste ist nach Journalnummern sortiert.
	 * Nicht abgeschlossene Journale werden auch Primanota genannt.
	 * @throws SQL_DBException 
	 */
	public List getOffeneJournale() throws SQL_DBException {
		List journale = new ArrayList();
		try {
			Table table = db.getTable("Journale");
			List records = table.getRecordsFromQuery(table.new QueryCondition(
					"absummiert", QueryCondition.EQUAL, new Boolean(false)),
					"Journalnummer", true);
			for (Iterator iter = records.iterator(); iter.hasNext();) {
				Record rec = (Record) iter.next();
				journale.add(new Journal(table, (Long) rec.getPrimaryKey()
						.getValue()));
			}
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (RecordNotExistsDBException e) {
			throw new ImpossibleException(e, log);
		}
		return journale;
	}
}
/*
 * $Log: Buchhaltung.java,v $
 * Revision 1.9  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und �berall eingef�hrt
 *
 * Revision 1.8  2005/08/18 17:04:24  tbayen
 * Interface GenericObject f�r alle Business-Objekte eingef�hrt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.7  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.6  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch n�tig war
 *
 * Revision 1.5  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und k�rzer
 *
 * Revision 1.4  2005/08/16 12:22:09  tbayen
 * rudiment�res Arbeiten mit Buchungszeilen m�glich
 *
 * Revision 1.3  2005/08/16 07:02:33  tbayen
 * Journal-Klasse steht als Grundger�st
 *
 * Revision 1.2  2005/08/16 00:06:42  tbayen
 * grundlegende Journal-Eigenschaften implementiert
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */