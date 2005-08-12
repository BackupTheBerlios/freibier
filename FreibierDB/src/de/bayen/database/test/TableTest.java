//  $Id: TableTest.java,v 1.2 2005/08/12 19:27:44 tbayen Exp $
package de.bayen.database.test;

import java.util.List;
import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;

public class TableTest extends TestCase {
    
    private Database db = null;
    private Table tab = null;

    protected void setUp() throws Exception {
        try {
            db = new Database("test","localhost","test",null);
            db.executeSqlFile("de/bayen/database/test/test.sql");
            tab = db.getTable("adressen");
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
	}

	protected void tearDown() throws Exception {
	    db.close();
	}

	public void testGetNumberOfRecords(){
	    try {
            assertEquals(2,tab.getNumberOfRecords());
        } catch (DatabaseException e) {
            fail("Exception bei Select");
        }
	}
	
	public void testGetRecordByValue() {
	    Record data;
        try {
            data = tab.getRecordByValue("nachname", "Hormanns");
            assertEquals("Peter", data.getField("vorname").format());
        } catch (SystemDatabaseException e) {
            fail("Exception bei Select");
        } catch (DatabaseException e) {
            fail("Exception bei Select");
        }
	}
	
	public void testGetRecordByPrimaryKey() {
	    Record data;
        try {
            data = tab.getRecordByPrimaryKey(new Integer(1));
            assertEquals("Peter", data.getField("vorname").format());
        } catch (SystemDatabaseException e) {
            fail("Exception bei Select");
        } catch (DatabaseException e) {
            fail("Exception bei Select");
        }
	}
	
	public void testGetMultipleRecords() {
		List result;
		Record rec;
		try {
			result = tab.getMultipleRecords(0, 10, "nachname", true);
			assertEquals(2, result.size());
			rec = (Record) result.get(0);
			assertEquals("Thomas", rec.getField("vorname").format());
			result = tab.getMultipleRecords(0, 10, "nachname", false);
			assertEquals(2, result.size());
			rec = (Record) result.get(0);
			assertEquals("Peter", rec.getField("vorname").format());
		} catch (DatabaseException e) {
            fail("Exception bei Select");
		}
	}
	
	public void testDeleteRecord() {
	    Record data;
	    try {
            data = tab.getRecordByPrimaryKey(new Integer(1));
            tab.deleteRecord(data);
            try {
                data = tab.getRecordByPrimaryKey(new Integer(1));
                fail("Datensatz muss gelöscht sein");
            } catch (DatabaseException e1) {
                // hier ist der OK-Fall
                assertEquals("angegebener Datensatz existiert nicht", e1.getMessage());
            }
        } catch (DatabaseException e) {
            fail("Exception bei Delete");
        }
	}
}

// TODO Kalender

/*
 *  $Log: TableTest.java,v $
 *  Revision 1.2  2005/08/12 19:27:44  tbayen
 *  Tests laufen wieder alle
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
 *  Revision 1.9  2004/11/01 12:06:46  tbayen
 *  Erste Kalenderversion mit Datenbank-Zugriff
 *
 *  Revision 1.8  2004/10/24 19:15:07  tbayen
 *  ComboBox als Auswahlfeld für Foreign Keys
 *
 *  Revision 1.7  2004/10/17 21:11:59  tbayen
 *  TableGUI ganz von Strings auf Objekte umgestellt, dabei
 *  Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 *  neue Verwaltung von Properties für Datentypen
 *
 *  Revision 1.6  2004/10/16 12:28:14  tbayen
 *  setzen des Property Path möglich (für Properties von Datentypen)
 *
 *  Revision 1.5  2004/10/15 16:14:00  phormanns
 *  Optimierung TableCache liest 20 Records
 *
 *  Revision 1.4  2004/10/14 21:36:12  phormanns
 *  Weitere Datentypen
 *
 *  Revision 1.3  2004/10/09 21:11:06  phormanns
 *  Implementierung der Select- und Delete-Operationen
 *
 *  Revision 1.2  2004/10/09 15:09:15  tbayen
 *  Einführung von DataObject
 *
 *  Revision 1.1  2004/10/09 11:28:24  phormanns
 *  Implementierung der Select- und Delete-Operationen
 *
 *  Revision 1.5  2004/10/07 17:18:26  phormanns
 *  Kommentare
 *
 */