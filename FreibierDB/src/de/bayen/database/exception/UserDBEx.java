/* Erzeugt am 01.10.2004 von tbayen
 * $Id: UserDBEx.java,v 1.1 2005/08/21 17:06:59 tbayen Exp $
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
public class UserDBEx extends DatabaseException {

    public UserDBEx(String message) {
        super(message);
    }

    public UserDBEx(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDBEx(String message, Log log) {
        super(message);
        log.error(message);
    }

    public UserDBEx(String message, Throwable cause, Log log) {
        super(message, cause);
        log.error(message,cause);
    }

    /**
     * Eine SQL-Exception, die von den JDBC-Klassen ausgeht und die 
     * voraussichtlich für den Benutzer interesant ist. Normalerweise werden
     * SQL-Exceptions in FreibierDB als SysDBEx.SQL_DBException weitergegeben.
     * Hier geht es z.B. um die Information, das der SQL-Server nicht 
     * erreichbar ist. Das geht ggf. den User an.
     */
    public static class UserSQL_DBException extends UserDBEx{

    	public UserSQL_DBException(String message, Log log) {
    		super(message, log);
    	}
    	public UserSQL_DBException(String message, Throwable cause, Log log) {
    		super(message, cause, log);
    	}
    }

    /**
     * Ein angegebener Datensatz existiert nicht.
     */
    public static class RecordNotExistsDBException extends UserDBEx{

    	public RecordNotExistsDBException(String message, Log log) {
    		super(message, log);
    	}
    	public RecordNotExistsDBException(String message, Throwable cause, Log log) {
    		super(message, cause, log);
    	}
    }
}

/*
 * $Log: UserDBEx.java,v $
 * Revision 1.1  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
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