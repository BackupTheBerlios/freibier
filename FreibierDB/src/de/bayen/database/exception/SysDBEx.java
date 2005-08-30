/* Erzeugt am 01.10.2004 von tbayen
 * $Id: SysDBEx.java,v 1.2 2005/08/30 20:31:03 tbayen Exp $
 */
package de.bayen.database.exception;

import org.apache.commons.logging.Log;

/**
 * @author tbayen
 *
 * Diese Klasse repräsentiert Exceptions, die innerhalb des die Datenbank benutzenden Programms
 * abgefangen werden sollten.
 */
public class SysDBEx extends DatabaseException {
	public SysDBEx(String message, Log log) {
		super(message);
		log.error(message);
	}

	public SysDBEx(String message, Throwable cause, Log log) {
		super(message, cause);
		log.error(message, cause);
	}

	/**
	 * SQL-Exception, die ursprünglich vom JDBC-Treiber kommt und innerhalb 
	 * der Datenbank-Klassen aufgefangen wurde. Leider wirft JDBC keine 
	 * Exceptions, anhand deren Typ man eine Information über die Art des 
	 * Fehlers bekommen könnte, sonst würde ich das hier ableiten und 
	 * speziellere Exception-Klassen benutzen.
	 * <p>
	 * Falls aus dem Kontext der Exception näher hervorgeht, worum es geht,
	 * wird eine Unterklasse dieser Exception benutzt.
	 * </p>
	 */
	public static class SQL_DBException extends SysDBEx {
		public SQL_DBException(String message, Log log) {
			super(message, log);
		}

		public SQL_DBException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/**
	 * Die Tabellenliste kann nicht gelesen werden.
	 */
	public static class SQL_getTableListDBException extends
			SysDBEx.SQL_DBException {
		public SQL_getTableListDBException(String message, Log log) {
			super(message, log);
		}

		public SQL_getTableListDBException(String message, Throwable cause,
				Log log) {
			super(message, cause, log);
		}
	}

	/**
	 * Auf eine Tabelle kann nicht per SQL zugegriffen werden.
	 */
	public static class SQL_getTableDBException extends SysDBEx.SQL_DBException {
		public SQL_getTableDBException(String message, Log log) {
			super(message, log);
		}

		public SQL_getTableDBException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/** 
	 * Default-Wert eines Datentyps ist nicht gültig.
	 */
	public static class IllegalDefaultValueDBException extends
			SysDBEx.SQL_DBException {
		public IllegalDefaultValueDBException(String message, Log log) {
			super(message, log);
		}

		public IllegalDefaultValueDBException(String message, Throwable cause,
				Log log) {
			super(message, cause, log);
		}
	}

	/** 
	 * Falscher Datentyp wurde verwendet.
	 */
	public static class WrongTypeDBException extends SysDBEx.SQL_DBException {
		public WrongTypeDBException(String message, Log log) {
			super(message, log);
		}

		public WrongTypeDBException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/** 
	 * Dies ist kein ForeignKey, es wird aber einer benötigt.
	 */
	public static class NotAForeignKeyDBException extends
			SysDBEx.SQL_DBException {
		public NotAForeignKeyDBException(String message, Log log) {
			super(message, log);
		}

		public NotAForeignKeyDBException(String message, Throwable cause,
				Log log) {
			super(message, cause, log);
		}
	}

	/** 
	 * In einer QueryCondition für eine Datenbankabfrage ist ein Fehler.
	 */
	public static class IllegalQueryConditionDBException extends
			SysDBEx.SQL_DBException {
		public IllegalQueryConditionDBException(String message, Log log) {
			super(message, log);
		}

		public IllegalQueryConditionDBException(String message,
				Throwable cause, Log log) {
			super(message, cause, log);
		}
	}

	/** 
	 * Ein Record kann nicht gespeichert werden.
	 */
	public static class CantSaveRecordDBException extends
			SysDBEx.SQL_DBException {
		public CantSaveRecordDBException(String message, Log log) {
			super(message, log);
		}

		public CantSaveRecordDBException(String message, Throwable cause,
				Log log) {
			super(message, cause, log);
		}
	}

	/**
	 * Der Typ wird (z.B. von einem Printer) nicht unterstützt
	 */
	public static class TypeNotSupportedDBException extends
			SysDBEx{
		public TypeNotSupportedDBException(String message, Log log) {
			super(message, log);
		}

		public TypeNotSupportedDBException(String message, Throwable cause,
				Log log) {
			super(message, cause, log);
		}
	}

	/**
	 * Daten können nicht richtig geparst werden (String hat das falsche Format).
	 */
	public static class ParseErrorDBException extends SysDBEx {
		public ParseErrorDBException(String message, Log log) {
			super(message, log);
		}

		public ParseErrorDBException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}
	
	/**
	 * Ein Fehler ist bei der Behandlung einer Transaktion aufgetreten (z.B. 
	 * beim Commit).
	 */
	public static class TransactionDBException extends SysDBEx.SQL_DBException {
		public TransactionDBException(String message, Log log) {
			super(message, log);
		}

		public TransactionDBException(String message, Throwable cause, Log log) {
			super(message, cause, log);
		}
	}
	
}
/*
 * $Log: SysDBEx.java,v $
 * Revision 1.2  2005/08/30 20:31:03  tbayen
 * erweiterte Querys mit direkter SQL-Syntax möglich
 *
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
 * Revision 1.2  2004/10/07 14:08:24  tbayen
 * Logging eingebaut und executeSQL fertiggestellt
 *
 * Revision 1.1  2004/10/04 12:39:00  tbayen
 * Projekt neu erstellt und einige Exception-Klassen erzeugt
 *
 */