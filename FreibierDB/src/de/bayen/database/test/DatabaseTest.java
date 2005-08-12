//  $Id: DatabaseTest.java,v 1.3 2005/08/12 19:27:41 tbayen Exp $
package de.bayen.database.test;

import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;

public class DatabaseTest extends TestCase {
    private Database db=null;
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
        try {
            db = new Database("test","localhost","test",null);
            db.executeSqlFile("de/bayen/database/test/test.sql");
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
	    db.close();
	}

	public void testGetTablesList(){
	    try {
            List list = db.getTableNamesList();
            assertEquals(4,list.size());
        } catch (SystemDatabaseException e) {
            fail(e.getMessage());
        }
	}

	public void testGetTable(){
	    try {
            Table tab=db.getTable("adressen");
            assertEquals("adressen",tab.getName());
        } catch (SystemDatabaseException e) {
            fail(e.getMessage());
        }
	    try {
            db.getTable("adressen2");
            fail("Exception erwartet.");
        } catch (SystemDatabaseException e) {
            assertEquals("Keine Primärschlüsselspalte definiert",e.getMessage());
        }
	}

	public void testExecuteSelectSingleRow() {
		try {
			Map hash = db.executeSelectSingleRow("select * from adressen");
			assertEquals("Hormanns", hash.get("nachname"));
		} catch (DatabaseException e) {
			fail("Select geht nicht");
		}
	}
	
	public void testExecuteSelectMultipleRows() {
		try {
			List result = db.executeSelectMultipleRows("select * from adressen");
			assertEquals(2, result.size());
			Map record = (Map) result.get(1);
			assertEquals("Bayen", record.get("nachname"));
		} catch (DatabaseException e) {
			fail("Select geht nicht");
		}
	}
	
}

/*
 *  $Log: DatabaseTest.java,v $
 *  Revision 1.3  2005/08/12 19:27:41  tbayen
 *  Tests laufen wieder alle
 *
 *  Revision 1.2  2005/08/08 06:35:29  tbayen
 *  Compiler-Warnings bereinigt
 *
 *  Revision 1.1  2005/08/07 21:18:49  tbayen
 *  Version 1.0 der Freibier-Datenbankklassen,
 *  extrahiert aus dem Projekt WebDatabase V1.5
 *
 *  Revision 1.1  2005/04/05 21:34:47  tbayen
 *  WebDatabase 1.4 - freigegeben auf Berlios
 *
 *  Revision 1.1  2005/03/23 18:03:10  tbayen
 *  Sourcecodebaum reorganisiert und
 *  Javadoc-Kommentare überarbeitet
 *
 *  Revision 1.1  2005/02/21 16:11:53  tbayen
 *  Erste Version mit Tabellen- und Datensatzansicht
 *
 *  Revision 1.11  2004/10/24 13:10:20  tbayen
 *  Merken des Typs des Zielwertes eines Foreign Keys
 *  formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 *  Revision 1.10  2004/10/18 11:45:01  tbayen
 *  Vorbereitung auf Bearbeitung von Fremdschlüsseln
 *  Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 *  Revision 1.9  2004/10/15 16:14:00  phormanns
 *  Optimierung TableCache liest 20 Records
 *
 *  Revision 1.8  2004/10/14 21:04:52  tbayen
 *  Methode getTableNamesList() umbenannt
 *
 *  Revision 1.7  2004/10/13 19:11:30  tbayen
 *  Erstellung von TableGUI und TestWindow,
 *  dazu Überarbeitung und Debugging vieler anderer Klassen
 *
 *  Revision 1.6  2004/10/09 11:28:24  phormanns
 *  Implementierung der Select- und Delete-Operationen
 *
 *  Revision 1.5  2004/10/07 17:18:26  phormanns
 *  Kommentare
 *
 */