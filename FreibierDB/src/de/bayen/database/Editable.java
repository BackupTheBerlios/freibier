/* Erzeugt am 11.10.2004 von tbayen
 * $Id: Editable.java,v 1.2 2005/08/21 17:06:59 tbayen Exp $
 */
package de.bayen.database;

import de.bayen.database.exception.DatabaseException;

/**
 * Ein "Editable" OBjekt ist grunds�tzlich editierbar und besitzt bestimmte
 * Funktionen, um auf seinen Inhalt zuzugreifen bzw. diesen zu �ndern.
 * 
 * Ein Editor-Objekt kann nun auf diese Schnittstelle zugreifen, um eine
 * konkrete Eingabem�glichkeit zu schaffen.
 * 
 * @author tbayen
 */
public interface Editable extends Printable {
	/**
	 * Diese Methode ergibt eine m�glichst f�r Menschen lesbare String-Version
	 * des Wertes. Er kann z.B. in einem Text-Eingabefeld benutzt werden. Es ist
	 * garantiert, da� dieser Wert mit der parse()-Methode wieder eingelesen und
	 * zur�ckgewandelt werden kann.
	 * 
	 * @return String-Version des Wertes
	 */
	public abstract String format();

	public abstract String getDefaultValue();

	/**
	 * Diese Methode parst den angegebenen String,der z.B. aus einem 
	 * Texteingabefeld stammen kann und speichert ihn in das Objekt zur�ck.
	 * 
	 * @param s
	 * @return String
	 * @throws DatabaseException
	 */
	public abstract String parse(String s);

	/**
	 * Diese Methode �berpr�ft, ob der angegebene String ein g�ltiger Wert ist,
	 * den man der parse()-Funktion �bergeben kann.
	 * 
	 * @param s
	 * @return boolean (ist der Wert g�ltig?)
	 */
	public abstract boolean validate(String s);

	/**
	 * Nur einen Teilstring validieren, also Kl�rung der Frage:
	 * Der Benutzer hat gerade eine Taste gedr�ckt. Wenn ich die jetzt an der
	 * Cursorposition einf�ge, kann dann jemals ein g�ltiger String daraus
	 * werden?
	 * Beispiel "11." ist kein g�ltiges Datum, kann aber eins werden.
	 * 
	 * @param s
	 * @return boolean (ist der Wert g�ltig?)
	 */
	public abstract boolean validatePart(String s);
}
/*
 * $Log: Editable.java,v $
 * Revision 1.2  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und �berall eingef�hrt
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
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.1  2004/10/11 14:29:37  tbayen
 * Framework f�r Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 */