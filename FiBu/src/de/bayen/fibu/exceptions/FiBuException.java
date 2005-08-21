/* Erzeugt am 20.08.2005 von tbayen
 * $Id: FiBuException.java,v 1.1 2005/08/21 17:08:55 tbayen Exp $
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
}
/*
 * $Log: FiBuException.java,v $
 * Revision 1.1  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 */