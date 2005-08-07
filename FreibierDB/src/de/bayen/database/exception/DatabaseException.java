/* Erzeugt am 01.10.2004 von tbayen
 * $Id: DatabaseException.java,v 1.1 2005/08/07 21:18:49 tbayen Exp $
 */
package de.bayen.database.exception;

/**
 * @author tbayen
 *
 * Dies ist die Mutterklasse aller von den Klassen des "de.bayen.database"-Paketes geworfenen
 * Exceptions. Hiervon sind die beiden spezifischeren Klassen SystemDatabaseException
 * und UserDatabaseException abgeleitet.
 */
public class DatabaseException extends Exception {
    protected DatabaseException(String message) {
        super(message);
    }

    protected DatabaseException(Throwable cause) {
        super(cause);
    }

    protected DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

/*
 * $Log: DatabaseException.java,v $
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
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.2  2004/10/07 14:08:24  tbayen
 * Logging eingebaut und executeSQL fertiggestellt
 *
 * Revision 1.1  2004/10/04 12:39:00  tbayen
 * Projekt neu erstellt und einige Exception-Klassen erzeugt
 *
*/