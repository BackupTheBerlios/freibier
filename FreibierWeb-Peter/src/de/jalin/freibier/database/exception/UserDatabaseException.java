/* Erzeugt am 01.10.2004 von tbayen
 * $Id: UserDatabaseException.java,v 1.1 2004/12/31 17:12:51 phormanns Exp $
 */
package de.jalin.freibier.database.exception;

import org.apache.commons.logging.Log;

/**
 * @author tbayen
 *
 * Diese Klasse repräsentiert Exceptions, die dazu bestimmt sind, an den User weitergereicht 
 * zu werden. Im Normallfall sollte der angegebene Fehlertext dem Benutzer ausgegeben und die
 * Operation abgebrochen werden.
 */
public class UserDatabaseException extends DatabaseException {

    public UserDatabaseException(String message) {
        super(message);
    }

    public UserDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDatabaseException(String message, Log log) {
        super(message);
        log.error(message);
    }

    public UserDatabaseException(String message, Throwable cause, Log log) {
        super(message, cause);
        log.error(message,cause);
    }
}

/*
 * $Log: UserDatabaseException.java,v $
 * Revision 1.1  2004/12/31 17:12:51  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.3  2004/10/07 17:15:33  tbayen
 * Datenbankklassen bis auf Table fertig für weitere Tests
 *
 * Revision 1.2  2004/10/07 14:08:24  tbayen
 * Logging eingebaut und executeSQL fertiggestellt
 *
 * Revision 1.1  2004/10/04 12:39:00  tbayen
 * Projekt neu erstellt und einige Exception-Klassen erzeugt
 *
*/