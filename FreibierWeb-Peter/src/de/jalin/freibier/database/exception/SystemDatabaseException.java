/* Erzeugt am 01.10.2004 von tbayen
 * $Id: SystemDatabaseException.java,v 1.1 2004/12/31 17:12:51 phormanns Exp $
 */
package de.jalin.freibier.database.exception;

import org.apache.commons.logging.Log;

/**
 * @author tbayen
 *
 * Diese Klasse repräsentiert Exceptions, die innerhalb des die Datenbank benutzenden Programms
 * abgefangen werden sollten.
 */
public class SystemDatabaseException extends DatabaseException {

    public SystemDatabaseException(String message, Log log) {
        super(message);
        log.error(message);
    }

    public SystemDatabaseException(String message, Throwable cause, Log log) {
        super(message, cause);
        log.error(message,cause);
    }
}

/*
 * $Log: SystemDatabaseException.java,v $
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