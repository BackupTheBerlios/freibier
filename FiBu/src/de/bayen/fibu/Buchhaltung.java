/* Erzeugt am 12.08.2005 von tbayen
 * $Id: Buchhaltung.java,v 1.1 2005/08/15 19:13:09 tbayen Exp $
 */
package de.bayen.fibu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;

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
public class Buchhaltung {
	private static Log log = LogFactory.getLog(Buchhaltung.class);
	private Database db = null;
	private Record firmenstamm=null;

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
	 * @throws DatabaseException
	 */
	public Buchhaltung(String name, String server, String user,
			String password) throws DatabaseException {
		db = new Database(name, server, user, password);
		db.setPropertyPath(getClass().getPackage().getName()+".dbdefinition");
	}

	/**
	 * Dieser Konstruktor öffnet die Datenbank mit Standard-Parametern
	 * @throws DatabaseException 
	 *
	 */
	public Buchhaltung() throws DatabaseException {
		// diese Parameter sind in einem Standard-Debian möglich:
		db = new Database("test", "localhost", "test", null);
		db.setPropertyPath(getClass().getPackage().getName()+".dbdefinition");
	}

	public void close() throws SystemDatabaseException {
		db.close();
		db = null;
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
	 * @throws UserDatabaseException 
	 */
	public void assertOk() throws UserDatabaseException {
		if (ok())
			return;
		throw new UserDatabaseException(
				"Buchhaltung nicht initialisiert (kein Zugriff auf Datenbank?)");
	}

	/**
	 * Diese Methode wird aufgerufen, um die Datenbank erstmalig zu 
	 * initialisieren. ACHTUNG: Bestehende Daten werden ohne Rückfrage 
	 * gelöscht!!!
	 * 
	 * @throws DatabaseException
	 */
	public void firstTimeInit() throws DatabaseException {
		db.wipeOutDatabase();
		db.executeSqlFile("de/bayen/fibu/dbdefinition/db_definition.sql");
	}

	/**
	 * Hiermit wird eine Firma aus den Firmenstammdaten ausgewählt. Die ID-Nummer
	 * aus der Datenbank muss dazu bekannt sein.
	 * 
	 * @param nr
	 * @return Record, der die Firmenstammdaten enthält
	 * @throws DatabaseException 
	 */
	public Record setFirmenstammdaten(int nr) throws DatabaseException{
		assertOk();
		firmenstamm=db.getTable("Firmenstammdaten").getRecordByPrimaryKey(String.valueOf(nr));
		return firmenstamm;
	}

	/**
	 * Hier wird eine neue Firma angelegt, falls das ID-Feld des übergebenen
	 * Datensatzes noch nicht belegt ist. Ansonsten wird der angegebene Satz
	 * in der Datenbank geändert auf die Werte des übergebenen Records.
	 * 
	 * @param stamm
	 * @return Record, der die Firmenstammdaten enthält
	 * @throws DatabaseException
	 */
	public Record setFirmenstammdaten(Record stamm) throws DatabaseException{
		assertOk();
		Table table = db.getTable("Firmenstammdaten");
		DataObject stammId=table.setRecordAndReturnID(stamm);
		firmenstamm=table.getRecordByPrimaryKey(stammId);
		return firmenstamm;
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
	 * @throws UserDatabaseException 
	 */
	public Record getFirmenstammdaten() throws DatabaseException{
		assertOk();
		if(firmenstamm==null){
			Table table = db.getTable("Firmenstammdaten");
			try {
				firmenstamm=table.getRecordByNumber(0);
			} catch (UserDatabaseException e) {
				if(e.getMessage().equals("angegebener Datensatz existiert nicht")){
					firmenstamm=table.getEmptyRecord();
					firmenstamm.setField("Firma","Finanzbuchhaltung");
					return setFirmenstammdaten(firmenstamm);
				}else{
					throw new SystemDatabaseException("unbekannte Exception",e,log);
				}
			}
		}
		return firmenstamm;
	}
	
	/**
	 * ergibt den aktuellen Firmennamen
	 * 
	 * @throws DatabaseException 
	 *
	 */
	public String getFirma() throws DatabaseException{
		return getFirmenstammdaten().getField("Firma").format();
	}
	
	/**
	 * Ergibt das aktuelle Buchungsjahr. Dies ist das Jahr, in das automatisch
	 * gebucht wird, d.h. dieser Wert kann z.B. in einer GUI als Vorgabe
	 * verwendet werden.
	 * <p>
	 * Dennoch ist es möglich, auch in andere Jahre zu buchen, wenn man dies
	 * bei der Buchung angibt.
	 * </p>
	 */
	public String getJahrAktuell() throws DatabaseException{
		return getFirmenstammdaten().getField("JahrAktuell").format();
	}
	
	public void setJahrAktuell(String jahr) throws DatabaseException{
		Record stamm = getFirmenstammdaten();
		stamm.setField("JahrAktuell",jahr);
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
	 */
	public String getPeriodeAktuell() throws DatabaseException{
		return getFirmenstammdaten().getField("PeriodeAktuell").format();
	}

	public void setPeriodeAktuell(String jahr) throws DatabaseException{
		Record stamm = getFirmenstammdaten();
		stamm.setField("PeriodeAktuell",jahr);
		setFirmenstammdaten(stamm);
	}

	/**
	 * Erzeugt ein neues Konto (ohne, das Nummer, Bezeichnung, etc. 
	 * initialisiert werden).
	 * 
	 * @return Konto
	 * @throws DatabaseException
	 */
	public Konto createKonto() throws DatabaseException{
		return new Konto(db.getTable("Konten"));
	}

	/**
	 * Liest ein vorhandenes Konto aus der Datenbank und erzeugt ein
	 * entsprechendes Konto-Objekt.
	 * 
	 * @param ktonr
	 * @return Konto
	 * @throws DatabaseException
	 */
	public Konto getKonto(String ktonr) throws DatabaseException{
		return new Konto(db.getTable("Konten"),ktonr);
	}
}
/*
 * $Log: Buchhaltung.java,v $
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */