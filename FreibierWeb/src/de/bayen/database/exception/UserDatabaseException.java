/* Erzeugt am 01.10.2004 von tbayen
 * $Id: UserDatabaseException.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.database.exception;

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