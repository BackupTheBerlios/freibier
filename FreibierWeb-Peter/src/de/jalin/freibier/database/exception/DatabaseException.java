/* Erzeugt am 01.10.2004 von tbayen
 * $Id: DatabaseException.java,v 1.1 2004/12/31 17:12:51 phormanns Exp $
 */
package de.jalin.freibier.database.exception;

/**
 * @author tbayen
 *
 * Dies ist die Mutterklasse aller von den Klassen des "database"-Paketes geworfenen
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
 * Revision 1.1  2004/12/31 17:12:51  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.2  2004/10/07 14:08:24  tbayen
 * Logging eingebaut und executeSQL fertiggestellt
 *
 * Revision 1.1  2004/10/04 12:39:00  tbayen
 * Projekt neu erstellt und einige Exception-Klassen erzeugt
 *
*/