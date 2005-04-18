/* Erzeugt am 06.10.2004 von tbayen
 * $Id: DatabaseConstructorTest.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.database.test;

import junit.framework.TestCase;
import de.bayen.database.Database;
import de.bayen.database.exception.DatabaseException;

/**
 * @author tbayen
 *
 * Hier stehen die JUnit-Testf�lle f�r die "Database"-Klasse
 */
public class DatabaseConstructorTest extends TestCase {

    public void testDatabaseConstructor(){
        Database db = null;
        try {
            // Ein anonymer Benutzer hat in einem Debian-System immer ohne Passwort Zugriff
            // auf die Datenbank "test"
            db = new Database("test","localhost","test",null);
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
        assertTrue(db.ok());
    }
    
    public void testExecuteSql(){
        Database db = null;
        try {
            db = new Database("test","localhost","test",null);
            db.executeSqlFile("de.bayen.database/test/test.sql");
        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }
}

/*
 * $Log: DatabaseConstructorTest.java,v $
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.1  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu �berarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.4  2004/10/07 14:08:24  tbayen
 * Logging eingebaut und executeSQL fertiggestellt
 *
 * Revision 1.3  2004/10/07 13:03:26  tbayen
 * Anpassung an neue Exception
 *
 * Revision 1.2  2004/10/07 13:02:55  tbayen
 * Anpassung an neue Exception
 *
 * Revision 1.1  2004/10/07 11:09:13  phormanns
 * DatabaseConstructorTest
 *
 * Revision 1.1  2004/10/06 22:57:37  tbayen
 * Erste Version eines JUnit-Tests
 *
 */