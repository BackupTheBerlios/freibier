/* Erzeugt am 18.10.2004 von tbayen
 * $Id: ForeignKeyTest.java,v 1.2 2005/08/12 19:27:41 tbayen Exp $
 */
package de.bayen.database.test;

import junit.framework.TestCase;
import de.bayen.database.DataObject;
import de.bayen.database.Database;
import de.bayen.database.NicePrinter;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;

public class ForeignKeyTest extends TestCase {
	private Database db=null;

	protected void setUp() throws Exception {
        try {
            db = new Database("test","localhost","test",null);
    		db.setPropertyPath(ForeignKeyTest.class.getPackage().getName());
            db.executeSqlFile("de/bayen/database/test/test.sql");
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
	}

	protected void tearDown() throws Exception {
	    db.close();
	}
	
	public void testReadForeignKey(){
		Table tab;
		try {
			tab = db.getTable("adressen");
		} catch (SystemDatabaseException e) {
			fail("Kann Tabelle nicht lesen");
			return;
		}
		Record rec;
		try {
			rec=tab.getRecordByValue("nachname","Hormanns");
		} catch (DatabaseException e1) {
			fail("Kann Datensatz nicht lesen");
			return;
		}
		DataObject obj;
		try {
			obj=rec.getField("sprache");
		} catch (DatabaseException e2) {
			fail("Kann Datenfeld mit Foreign Key nicht lesen");
			return;
		}
		try {
			assertEquals("5",obj.format());
		} catch (DatabaseException e3) {
			fail("Fehler beim Formatieren des Foreign Keys");
		}
	}

	public void testReadProperties(){
		Table tab;
		db.setPropertyPath(ForeignKeyTest.class.getPackage().getName());
		try {
			tab = db.getTable("adressen");
		} catch (SystemDatabaseException e) {
			fail("Kann Tabelle nicht lesen");
			return;
		}
		Record rec;
		try {
			rec=tab.getRecordByValue("nachname","Hormanns");
		} catch (DatabaseException e1) {
			fail("Kann Datensatz nicht lesen");
			return;
		}
		DataObject obj;
		try {
			obj=rec.getField("sprache");
		} catch (DatabaseException e2) {
			fail("Kann Datenfeld mit Foreign Key nicht lesen");
			return;
		}
		// bis hierhin war das nichts neues
		assertEquals("programmiersprachen",obj.getProperty("foreignkey.table"));
	}
	
	public void testNicePrinter(){
		Table tab;
		try {
			tab = db.getTable("adressen");
		} catch (SystemDatabaseException e) {
			fail("Kann Tabelle nicht lesen");
			return;
		}
		Record rec;
		try {
			rec=tab.getRecordByValue("nachname","Hormanns");
		} catch (DatabaseException e1) {
			fail("Kann Datensatz nicht lesen");
			return;
		}
		DataObject obj;
		try {
			obj=rec.getField("sprache");
		} catch (DatabaseException e2) {
			fail("Kann Datenfeld mit Foreign Key nicht lesen");
			return;
		}
		// ab hier wirds aufregend:
		try {
			assertEquals("Java", NicePrinter.print(obj));
		} catch (DatabaseException e4) {
			fail("Fehler in NicePrinter beim Foreign Key");
		}
	}

	public void testFormattedReference(){
		// das referenzierte Feld ist ein Datum, also muss es ebenfalls formatiert werden
		Table tab;
		try {
			tab = db.getTable("adressen");
		} catch (SystemDatabaseException e) {
			fail("Kann Tabelle nicht lesen");
			return;
		}
		Record rec;
		try {
			rec=tab.getRecordByValue("nachname","Hormanns");
		} catch (DatabaseException e1) {
			fail("Kann Datensatz nicht lesen");
			return;
		}
		DataObject obj;
		try {
			obj=rec.getField("lieblingstag");
		} catch (DatabaseException e2) {
			fail("Kann Datenfeld mit Foreign Key nicht lesen");
			return;
		}
		// ab hier wirds aufregend:
		try {
			assertEquals("24.12.2004", NicePrinter.print(obj));
		} catch (DatabaseException e4) {
			fail("Fehler in NicePrinter beim Foreign Key");
		}
	}
}

/*
 * $Log: ForeignKeyTest.java,v $
 * Revision 1.2  2005/08/12 19:27:41  tbayen
 * Tests laufen wieder alle
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.5  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen für DATE und TIME
 *
 * Revision 1.4  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.3  2004/10/23 17:24:20  tbayen
 * ForeignKeys werden bei allen Selects mitgeholt
 *
 * Revision 1.2  2004/10/18 12:10:14  tbayen
 * Test für den Zugriff auf Properties
 *
 * Revision 1.1  2004/10/18 11:45:01  tbayen
 * Vorbereitung auf Bearbeitung von Fremdschlüsseln
 * Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 */