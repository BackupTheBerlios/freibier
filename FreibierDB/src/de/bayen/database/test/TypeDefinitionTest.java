/* Erzeugt am 10.10.2004 von tbayen
 * $Id: TypeDefinitionTest.java,v 1.2 2005/08/12 19:27:44 tbayen Exp $
 */
package de.bayen.database.test;

import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;

public class TypeDefinitionTest extends TestCase {
	private Database db = null;
	private Table tab = null;

	protected void setUp() throws Exception {
		try {
			db = new Database("test", "localhost", "test", null);
			db.executeSqlFile("de/bayen/database/test/test.sql");
			tab = db.getTable("adressen");
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	protected void tearDown() throws Exception {
		db.close();
	}

	public void testTypeDefinitionIntegerParse() {
		Record rec = null;
		try {
			rec = tab.getRecordByPrimaryKey(new Integer(2));
			try {
				if (rec != null) {
					rec.setField("lebensalter", "34"); // spontane Alterung
					tab.setRecord(rec);
				}
			} catch (DatabaseException e1) {
				fail(e1.getMessage());
			}
			try {
				assertEquals("34", tab.getRecordByPrimaryKey(new Integer(2)).getField(
						"lebensalter").format());
			} catch (DatabaseException e2) {
				fail(e2.getMessage());
			}
			try {
				if (rec != null) {
					rec.getField("lebensalter").parse("vierunddreissig");
					tab.setRecord(rec);
				}
				fail("alphanumerische Zeichen in Integer nicht erkannt");
			} catch (DatabaseException e1) {}
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testSQLPrinter() {
		Record rec = null;
		try {
			rec = tab.getRecordByPrimaryKey(new Integer(2));
			try {
				if (rec != null) {
					rec.setField("vorname", "\"100-%er\\'Thomas'\"");
					tab.setRecord(rec);
				}
			} catch (DatabaseException e1) {
				fail(e1.getMessage());
			}
			try {
				assertEquals("\"100-%er\\'Thomas'\"", tab
						.getRecordByPrimaryKey(new Integer(2)).getField("vorname")
						.format());
			} catch (DatabaseException e2) {
				fail(e2.getMessage());
			}
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}
}
/*
 * $Log: TypeDefinitionTest.java,v $
 * Revision 1.2  2005/08/12 19:27:44  tbayen
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
 * Revision 1.4  2004/10/15 18:46:04  phormanns
 * Fehler in Sortierung
 * Fehler in Testfällen
 *
 * Revision 1.3  2004/10/11 14:29:37  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.2  2004/10/11 12:55:11  tbayen
 * Table.setRecord implementiert
 *
 * Revision 1.1  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingefügt
 *
 */