/* Erzeugt am 20.08.2005 von tbayen
 * $Id: FiBuException.java,v 1.2 2005/08/30 21:05:53 tbayen Exp $
 */
package de.bayen.fibu.exceptions;

import org.apache.commons.logging.Log;

public class FiBuException extends Exception {
	public FiBuException(String message) {
		super(message);
	}

	public FiBuException(String message, Throwable cause) {
		super(message, cause);
	}

	public FiBuException(String message, Log log) {
		super(message);
		log.error(message);
	}

	public FiBuException(String message, Throwable cause, Log log) {
		super(message, cause);
		log.error(message);
	}

	/*
	 * FiBu ist nicht richtig initialisiert.
	 */
	public static class NotInitializedException extends FiBuException {
		public NotInitializedException(String message, Log log) {
			super(message, log);
		}

		public NotInitializedException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/*
	 * Fehler beim Einlesen von Daten oder Dateien.
	 */
	public static class ParserException extends FiBuException {
		public ParserException(String message, Log log) {
			super(message, log);
		}

		public ParserException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}
}
/*
 * $Log: FiBuException.java,v $
 * Revision 1.2  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 * Revision 1.1  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 */