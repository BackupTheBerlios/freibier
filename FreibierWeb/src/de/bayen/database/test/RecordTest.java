/* Erzeugt am 10.10.2004 von tbayen
 * $Id: RecordTest.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.database.test;

import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;

public class RecordTest extends TestCase {
	private Database db = null;
	private Table tab = null;

	protected void setUp() throws Exception {
		try {
			db = new Database("test", "localhost", "test", null);
			db.executeSqlFile("de.bayen.database/test/test.sql");
			tab = db.getTable("adressen");
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	protected void tearDown() throws Exception {
		db.close();
	}

	public void testFieldName() {
		try {
			Record rec = null;
			rec = tab.getRecordByPrimaryKey(new Integer(1));
			try {
				rec.getField("schwubbeldibu").parse("bla");
				fail("illegaler Feldname nicht erkannt");
			} catch (DatabaseException e1) {
				assertEquals("Angefordertes Feld existiert nicht: schwubbeldibu", e1
						.getMessage());
			}
			try {
				String name = rec.getField("nachname").format();
				assertEquals("Hormanns", name);
			} catch (DatabaseException e1) {
				fail(e1.getMessage());
			}
			try {
				String name = rec.getField("lebensalter").format();
				assertEquals("", name); // enthält Null...
				Object obj = rec.getField("lebensalter").getValue();
				assertEquals(null, obj); // enthält Null...
			} catch (DatabaseException e1) {
				fail(e1.getMessage());
			}
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}
}
/*
 * $Log: RecordTest.java,v $
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
 * Revision 1.4  2004/10/18 11:45:01  tbayen
 * Vorbereitung auf Bearbeitung von Fremdschlüsseln
 * Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 * Revision 1.3  2004/10/15 19:46:59  tbayen
 * Fehler bei Nullwerten behoben und Tests erweitert
 *
 * Revision 1.2  2004/10/15 19:43:20  tbayen
 * kleiner Fehler im Test behoben
 *
 * Revision 1.1  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingefügt
 *
 */