/* Erzeugt am 01.10.2004 von tbayen
 * $Id: DBRuntimeException.java,v 1.1 2005/08/21 17:06:59 tbayen Exp $
 */
package de.bayen.database.exception;

import org.apache.commons.logging.Log;

/**
 * Exceptions, die normalerweise nicht abgefangen werden. Bei diesen Exceptions
 * handelt es sich um Fehler, die auf Programmierfehler schliessen lassen und
 * die eigentlich niemals auftreten sollten.
 * 
 * @author tbayen
 */
public class DBRuntimeException extends RuntimeException {
	public DBRuntimeException(String message) {
		super(message);
	}

	public DBRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBRuntimeException(String message, Log log) {
		super(message);
		log.error(message);
	}

	public DBRuntimeException(String message, Throwable cause, Log log) {
		super(message, cause);
		log.error(message, cause);
	}

	/** 
	 * Ein bestimmter Treiber (kann auch eine Hilfsklasse oder eine sonstige
	 * Datei sein, die Treiberdaten enthält), der eigentlich auf jeden Fall 
	 * da sein sollte, kann nicht geladen werden.
	 */
	public static class DriverNotFoundException extends DBRuntimeException {
		public DriverNotFoundException(String message, Log log) {
			super(message, log);
		}

		public DriverNotFoundException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/** 
	 * Ein angegebener Datentyp existiert nicht. Entweder handelt es sich um
	 * einen Schreibfehler im Programm oder die entsprechende Klasse
	 * TypeDefinitionXXX muss implementiert werden.
	 */
	public static class TypeNotFoundException extends DBRuntimeException {
		public TypeNotFoundException(String message, Log log) {
			super(message, log);
		}

		public TypeNotFoundException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/**
	 * Das angegebene Feld existiert in diesem Record(dieser Tabelle) nicht.
	 */
	public static class IllegalFieldNameException extends DBRuntimeException {
		public IllegalFieldNameException(String message, Log log) {
			super(message, log);
		}

		public IllegalFieldNameException(String message, Throwable cause,
				Log log) {
			super(message, cause, log);
		}
	}

	/**
	 * Eine Datenbank-Tabelle muss immer genau einen Primärschlüssel haben.
	 */
	public static class WrongNumberOfPrimaryKeysException extends
			DBRuntimeException {
		public WrongNumberOfPrimaryKeysException(String message, Log log) {
			super(message, log);
		}

		public WrongNumberOfPrimaryKeysException(String message,
				Throwable cause, Log log) {
			super(message, cause, log);
		}
	}
	
	/**
	 * Wenn ich vorhandene Exceptions abfange und genau weiss, daß diese nicht
	 * auftreten können, werfe ich sie hiermit neu aus.
	 */
	public static class ImpossibleDBException extends DBRuntimeException{
		public ImpossibleDBException(Throwable cause, Log log){
			super("",cause,log);
		}
	}
}
/*
 * $Log: DBRuntimeException.java,v $
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