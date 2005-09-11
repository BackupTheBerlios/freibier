package de.bayen.fibu.exceptions;

import org.apache.commons.logging.Log;

/**
 * Diese Exception werfe ich an Stellen, wo ich nicht-Runtime-Exceptions
 * auffange, die bei sauberer Programmierung gar nicht auftreten können.
 */
public class ImpossibleException extends FiBuRuntimeException {
	public ImpossibleException(Throwable cause, Log log) {
		super("", cause, log);
	}
	public ImpossibleException(Throwable cause) {
		super("", cause);
	}
}