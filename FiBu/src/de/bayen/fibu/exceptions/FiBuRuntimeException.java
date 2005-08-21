/* Erzeugt am 20.08.2005 von tbayen
 * $Id: FiBuRuntimeException.java,v 1.1 2005/08/21 17:08:55 tbayen Exp $
 */
package de.bayen.fibu.exceptions;

import org.apache.commons.logging.Log;

/**
 * Exceptions, die im Fibu-Programm entstehen, und die eigentlich gar 
 * nicht vorkommen dürften, sind FiBuRuntimeExceptions. Eine solche
 * Exception weist normalerweise auf einen Programmierfehler hin.
 */
public class FiBuRuntimeException extends RuntimeException {
	public FiBuRuntimeException(String message) {
		super(message);
	}

	public FiBuRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public FiBuRuntimeException(String message, Log log) {
		super(message);
		log.error(message);
	}

	public FiBuRuntimeException(String message, Throwable cause, Log log) {
		super(message, cause);
		log.error(message);
	}
}
/*
 * $Log: FiBuRuntimeException.java,v $
 * Revision 1.1  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 */