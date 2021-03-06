/* Erzeugt am 12.08.2005 von tbayen
 * $Id: BuchhaltungTest.java,v 1.3 2005/11/24 11:43:32 tbayen Exp $
 */
package de.bayen.fibu.test;

import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.UserSQL_DBException;
import de.bayen.database.test.DatabaseConstructorTest;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;

public class BuchhaltungTest extends TestCase {
	public void testNoDatabase() {
		try {
			Buchhaltung bh = new Buchhaltung("gibtsnicht", "localhost",
					"keinuser", "password");
			bh.close();
			fail("Nicht existierende Datenbank nicht erkannt");
		} catch (Exception e) {
			assertEquals(
					"Verbindung zur SQL-Datenbank 'gibtsnicht' kann nicht ge�ffnet werden",
					e.getMessage());
		}
	}

	public void testFirstTimeInit() {
		try {
			// Um das L�schen der vorhandenen Daten zu testen, f�hre ich hier
			// erstmal einen Test aus der Datenbank-Klasse aus, der Testdaten
			// anlegt. Dann schaue ich in die Datenbank, ob diese Testdaten 
			// drin sind.
			{
				(new DatabaseConstructorTest()).testExecuteSql();
				Database db = new Database("test", "localhost", "test", null);
				int size = db.getTableNamesList().size();
				// Dieser Test funktioniert nur, wenn die Datenbank "test"
				// vorher entweder leer war oder diesen Test hier gemacht hat.
				assertTrue("falsche Anzahl an Tabellen: " + size,
						size == DatabaseConstructorTest.numberOfTables()
								|| size == DatabaseConstructorTest
										.numberOfTables()
										+ numberOfTables());
				db.close();
			}
			// Jetzt erzeuge ich meine eigene Datenbank und pr�fe, ob die
			// Anzahl der Tabellen stimmt
			{
				Buchhaltung bh = new Buchhaltung();
				bh.firstTimeInit();
				Database db = bh.getDatabase();
				assertEquals(numberOfTables(), db.getTableNamesList().size());
				bh.close();
			}
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * ergibt die Anzahl der Tabellen, die in der Definition in 
	 * "db_definition.sql" erzeugt werden.
	 * 
	 * Wird "db_definition.sql" ge�ndert, muss auch diese Methode ge�ndert
	 * werden.
	 * 
	 * @return Anzahl der Tabellen
	 */
	public static int numberOfTables() {
		return 6;
	}

	public void testGetFirmenstammdaten() throws UserSQL_DBException,
			SQL_DBException, NotInitializedException {
		Buchhaltung bh = new Buchhaltung();
		Record stamm = bh.getFirmenstammdaten();
		String firma = stamm.getFormatted("Firma");
		assertEquals("Finanzbuchhaltung", firma);
		bh.close();
	}

	public void testGetFirma() {
		try {
			Buchhaltung bh = new Buchhaltung();
			assertEquals("Finanzbuchhaltung", bh.getFirma());
			bh.close();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
/*
 * $Log: BuchhaltungTest.java,v $
 * Revision 1.3  2005/11/24 11:43:32  tbayen
 * Beschreiben der Stammdaten-Tabelle in Properties und
 * bessere Arbeit mit leerer Datenbank
 *
 * Revision 1.2  2005/08/30 20:14:43  tbayen
 * Zugriff auf Standard-Datenbank, die auch alle anderen Tests benutzen
 *
 * Revision 1.1  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch n�tig war
 *
 * Revision 1.2  2005/08/16 08:52:32  tbayen
 * Grundger�st der Klasse Buchung (mit Test) steht
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */