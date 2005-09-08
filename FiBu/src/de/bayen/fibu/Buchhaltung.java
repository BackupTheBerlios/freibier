/* Erzeugt am 12.08.2005 von tbayen
 * $Id: Buchhaltung.java,v 1.13 2005/09/08 06:27:44 tbayen Exp $
 */
package de.bayen.fibu;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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
import de.bayen.fibu.util.Drucktabelle;

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

	/**
	 * Dies main-Methode ist hier nur zu Testzwecken während der Entwicklung
	 * der Klasse.
	 */
	public static void main(String[] args) {
		log.info("Buchhaltung.main()");
	}

	/**
	 * Der Konstruktor öffnet die Datenbankverbindung und lässt so überhaupt erst
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
	 * Dieser Konstruktor öffnet die Datenbank mit Standard-Parametern.
	 * Dazu liest es zuerst die Datei <code>mysql.properties</code> im
	 * Hauptverzeichnis der laufenden Applikation (nicht der Bibliothek).
	 * Wenn diese nicht existiert, wird die properties-Datei aus der
	 * Bibliothek geladen (Datenbank "fibu", User "fibu"). Wenn diese 
	 * auch nicht zu einer Verbindung führt, wird die MySQL-Test-Datenbank 
	 * genommen, die Debian standardmäßig installiert.
	 */
	public Buchhaltung() throws UserSQL_DBException {
		db = null;
		openStandardDatabase();
	}

	/**
	 * Dieser Konstruktor liest die angegebene Datei als Properties-Datei
	 * ein und öffnet dann die beschriebene Datenbank. Sollte dies nicht
	 * möglich sein, wird an den Standard-Orten nach der Datenbank gesucht
	 * (s.o.). Übrigens kann man mit <code>System.getProperty("user.home")</code>
	 * das Home-Verzeichnis als Anfang des Confignamens besorgen.
	 * @see Buchhaltung#Buchhaltung()
	 * @param configname
	 * @throws UserSQL_DBException
	 */
	public Buchhaltung(String configname) throws UserSQL_DBException {
		db = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(configname);
			if (fileInputStream != null) {
				Properties mysqlProps = new Properties();
				mysqlProps.load(fileInputStream);
				db = new Database(mysqlProps.getProperty("database"),
						mysqlProps.getProperty("server"), mysqlProps
								.getProperty("user"), mysqlProps
								.getProperty("password"));
			}
		} catch (IOException e) {} catch (UserSQL_DBException e) {}
		if (db == null) {
			openStandardDatabase();
		}
	}

	private void openStandardDatabase() throws UserSQL_DBException {
		// Standardparameter lesen:
		try {
			InputStream systemResourceAsStream = ClassLoader
					.getSystemResourceAsStream("mysql.properties");
			if (systemResourceAsStream != null) {
				Properties mysqlProps = new Properties();
				mysqlProps.load(systemResourceAsStream);
				db = new Database(mysqlProps.getProperty("database"),
						mysqlProps.getProperty("server"), mysqlProps
								.getProperty("user"), mysqlProps
								.getProperty("password"));
			}
		} catch (IOException e) {} catch (UserSQL_DBException e) {}
		if (db == null) {
			try {
				Properties mysqlProps = new Properties();
				mysqlProps.load(getClass().getResourceAsStream(
						"dbdefinition/mysql.properties"));
				db = new Database(mysqlProps.getProperty("database"),
						mysqlProps.getProperty("server"), mysqlProps
								.getProperty("user"), mysqlProps
								.getProperty("password"));
			} catch (IOException e) {} catch (UserSQL_DBException e) {}
		}
		if (db == null) {
			// bisherige Versuche nicht geklappt?
			// diese Parameter sind in einem Standard-Debian möglich:
			db = new Database("test", "localhost", "test", null);
		}
		db.setPropertyPath(getClass().getPackage().getName() + ".dbdefinition");
	}

	public void close() throws SQL_DBException {
		db.close();
		db = null;
	}

	/**
	 * Diese Methode bietet den direkten Zugriff auf die Datenbank. Sie sollte
	 * normalerweise nicht nötig sein. Bisher wird sie nur beim Debugging,
	 * also bei Tests, benutzt. Falls direkter DB-Zugriff für ein Spezialproblem
	 * nötig erscheint, sollten besser die FiBu-Klassen erweitert werden.
	 */
	public Database getDatabase() {
		return db;
	}

	/**
	 * Hiermit kann geprüft werden, ob die Buchhaltung initialisiert ist,
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
	 * initialisieren. ACHTUNG: Bestehende Daten werden ohne Rückfrage 
	 * gelöscht!!!
	 * @throws UserSQL_DBException 
	 * @throws SQL_DBException 
	 */
	public void firstTimeInit() throws UserSQL_DBException, SQL_DBException {
		db.wipeOutDatabase();
		try {
			db.executeSqlFile("de/bayen/fibu/dbdefinition/db_definition.sql");
		} catch (IOException e) {
			throw new FiBuRuntimeException(
					"Kann Datendefinitionsdatei nicht lesen", e, log);
		}
	}

	/**
	 * Hiermit wird eine Firma aus den Firmenstammdaten ausgewählt. Die ID-Nummer
	 * aus der Datenbank muss dazu bekannt sein.
	 * 
	 * @param nr
	 * @return Record, der die Firmenstammdaten enthält
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
	 * Hier wird eine neue Firma angelegt, falls das ID-Feld des übergebenen
	 * Datensatzes noch nicht belegt ist. Ansonsten wird der angegebene Satz
	 * in der Datenbank geändert auf die Werte des übergebenen Records.
	 * 
	 * @param stamm
	 * @return Record, der die Firmenstammdaten enthält
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
	 * unverfänglichen Werten angelegt. 
	 * </p><p>
	 * Die Benutzung mehrerer Firmen ist vorerst nicht implementiert, soll durch
	 * diese API jedoch vorbereitet werden.
	 * </p>
	 * @return Record, der die Firmenstammdaten enthält
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
	 * Dennoch ist es möglich, auch in andere Jahre zu buchen, wenn man dies
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
	 * Unterteilung des Buchungsjahres für Zwecke der Statistik und Auswertung.
	 * Eine Periode kann z.B. ein Monat oder ein Quartal sein. So können
	 * nachher Auswertungen gefahren werden, die gleiche Perioden aus 
	 * verschiedenen Jahren vergleichen, etc.
	 * <p>
	 * Die Periode ist ein zweistelliger Text. Bei Monaten kann hier z.B.
	 * 01,02,03,...,12 angegeben werden, andere Texte sind aber genauso
	 * denkbar. Wichtig ist, daß die Perioden innerhalb des Jahres alphabetisch
	 * sortiert vorkommen.
	 * </p><p>
	 * Weicht das Geschäftsjahr vom Kalenderjahr ab, so sollte "01" für die
	 * erste Periode stehen, nicht für Januar.
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
	 * ergibt das Bilanzkonto (also das Urkonto der gesamten Buchhaltung)
	 * 
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 */
	public Konto getBilanzKonto() throws SQL_DBException,
			NotInitializedException {
		try {
			return new Konto(db.getTable("Konten"), (Long)getFirmenstammdaten().getField("Bilanzkonto").getValue());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		} catch (RecordNotExistsDBException e) {
			throw new NotInitializedException("Bilanzkonto existiert nicht",
					log);
		} catch (SQL_getTableDBException e) {
			throw new ImpossibleException(e, log);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (SQL_DBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}
	
	public void setBilanzkonto(Konto kto) throws SQL_DBException, NotInitializedException{
		try {
			Record stamm = getFirmenstammdaten();
			stamm.setField("Bilanzkonto", kto.getID());
			setFirmenstammdaten(stamm);
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * ergibt das Gewinn- und Verlustkonto
	 * 
	 * @throws NotInitializedException 
	 * @throws SQL_DBException 
	 */
	public Konto getGuVKonto() throws SQL_DBException,
			NotInitializedException {
		try {
			return new Konto(db.getTable("Konten"), (Long)getFirmenstammdaten().getField("GuVKonto").getValue());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		} catch (RecordNotExistsDBException e) {
			throw new NotInitializedException("Gewinn- und Verlustkonto existiert nicht",
					log);
		} catch (SQL_getTableDBException e) {
			throw new ImpossibleException(e, log);
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (SQL_DBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setGuVKonto(Konto kto) throws SQL_DBException, NotInitializedException{
		try {
			Record stamm = getFirmenstammdaten();
			stamm.setField("GuVKonto", kto.getID());
			setFirmenstammdaten(stamm);
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Ergibt eine Liste der Konten (d.h. eine Liste von Strings mit 
	 * Kontonummern)
	 * 
	 * @return Liste mit Kontonummer-Strings
	 * @throws SQL_DBException 
	 */
	public List getKontenListe() throws SQL_DBException {
		try {
			List konten = getDatabase().getTable("Konten").getRecordsFromQuery(
					null, "Kontonummer", true);
			List ergebnis = new ArrayList(konten.size());
			for (int i = 0; i < konten.size(); i++) {
				ergebnis.add(i, ((Record) konten.get(i))
						.getField("Kontonummer").format());
			}
			return ergebnis;
		} catch (IllegalDefaultValueDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Erzeugt ein ganz neues Journal. Das Journal wird direkt in der
	 * Datenbank erzeugt, muss also nicht noch mit write() bestätigt werden.
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
	 * Ergibt eine Liste, die alle Journale der Buchhaltung enthält.
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
	 * Buchhaltung enthält. Die Liste ist nach Journalnummern sortiert.
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

	public String printSaldenliste() throws SQL_DBException {
		String spalten[] = {
				"Nr", "Bezeichnung", "Soll", "Haben"
		};
		int breiten[] = {
				6, 40, 14, 14
		};
		int ausrichtung[] = {
				Drucktabelle.RECHTSBUENDIG, Drucktabelle.LINKSBUENDIG,
				Drucktabelle.RECHTSBUENDIG, Drucktabelle.RECHTSBUENDIG
		};
		Drucktabelle tab = new Drucktabelle(spalten, breiten, ausrichtung);
		String erg = tab.printUeberschrift(true) + "\n";
		List konten = getKontenListe();
		for (Iterator iter = konten.iterator(); iter.hasNext();) {
			String kontonummer = (String) iter.next();
			Konto konto;
			try {
				konto = getKonto(kontonummer);
			} catch (RecordNotExistsDBException e) {
				throw new ImpossibleException(e, log);
			}
			erg += konto.printSaldenzeile(tab, 0) + "\n";
		}
		return erg;
	}
}
/*
 * $Log: Buchhaltung.java,v $
 * Revision 1.13  2005/09/08 06:27:44  tbayen
 * Buchhaltung.getBilanzkonto() überarbeitet
 *
 * Revision 1.12  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 * Revision 1.11  2005/08/21 20:32:03  tbayen
 * Datenbankparameter werden nacheinander an verschiedenen Quellen gesucht
 *
 * Revision 1.10  2005/08/21 17:26:12  tbayen
 * doppelte Variable in fast allen von AbstractObject abgeleiteten Klassen
 *
 * Revision 1.9  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.8  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.7  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.6  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 * Revision 1.5  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
 * Revision 1.4  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
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