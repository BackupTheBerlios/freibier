/* Erzeugt am 12.08.2005 von tbayen
 * $Id: BuchhaltungsDatenTest.java,v 1.2 2005/08/16 08:52:32 tbayen Exp $
 */
package de.bayen.fibu.test;

import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.test.DatabaseConstructorTest;
import de.bayen.fibu.Buchhaltung;

public class BuchhaltungsDatenTest extends TestCase {
	public void testNoDatabase() {
		try {
			Buchhaltung bh = new Buchhaltung("gibtsnicht",
					"localhost", "keinuser", "password");
			bh.close();
			fail("Nicht existierende Datenbank nicht erkannt");
		} catch (Exception e) {
			assertEquals(
					"Verbindung zur SQL-Datenbank 'gibtsnicht' kann nicht geöffnet werden",
					e.getMessage());
		}
	}

	public void testFirstTimeInit() {
		try {
			// Um das Löschen der vorhandenen Daten zu testen, führe ich hier
			// erstmal einen Test aus der Datenbank-Klasse aus, der Testdaten
			// anlegt. Dann schaue ich in die Datenbank, ob diese Testdaten 
			// drin sind.
			{
				(new DatabaseConstructorTest()).testExecuteSql();
				Database db = new Database("test", "localhost", "test", null);
				int size = db.getTableNamesList().size();
				// Dieser Test funktioniert nur, wenn die Datenbank "test"
				// vorher entweder leer war oder diesen Test hier gemacht hat.
				assertTrue("falsche Anzahl an Tabellen: "+size, size == DatabaseConstructorTest.numberOfTables()
						|| size == DatabaseConstructorTest.numberOfTables()
								+ numberOfTables());
				db.close();
			}
			// Jetzt erzeuge ich meine eigene Datenbank und prüfe, ob die
			// Anzahl der Tabellen stimmt
			{
				Buchhaltung bh = new Buchhaltung();
				bh.firstTimeInit();
				bh.close();
				Database db = new Database("test", "localhost", "test", null);
				assertEquals(numberOfTables(), db.getTableNamesList().size());
				db.close();
			}
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * ergibt die Anzahl der Tabellen, die in der Definition in 
	 * "db_definition.sql" erzeugt werden.
	 * 
	 * Wird "db_definition.sql" geändert, muss auch diese Methode geändert
	 * werden.
	 * 
	 * @return Anzahl der Tabellen
	 */
	public static int numberOfTables() {
		return 6;
	}
	
	public void testGetFirmenstammdaten(){
		try {
			Buchhaltung bh = new Buchhaltung();
			Record stamm=bh.getFirmenstammdaten();
			String firma=stamm.getFormatted("Firma");
			assertEquals("Finanzbuchhaltung", firma);
			bh.close();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testGetFirma(){
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
 * $Log: BuchhaltungsDatenTest.java,v $
 * Revision 1.2  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */