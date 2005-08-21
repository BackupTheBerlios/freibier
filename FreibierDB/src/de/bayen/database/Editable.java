/* Erzeugt am 11.10.2004 von tbayen
 * $Id: Editable.java,v 1.2 2005/08/21 17:06:59 tbayen Exp $
 */
package de.bayen.database;

import de.bayen.database.exception.DatabaseException;

/**
 * Ein "Editable" OBjekt ist grundsätzlich editierbar und besitzt bestimmte
 * Funktionen, um auf seinen Inhalt zuzugreifen bzw. diesen zu ändern.
 * 
 * Ein Editor-Objekt kann nun auf diese Schnittstelle zugreifen, um eine
 * konkrete Eingabemöglichkeit zu schaffen.
 * 
 * @author tbayen
 */
public interface Editable extends Printable {
	/**
	 * Diese Methode ergibt eine möglichst für Menschen lesbare String-Version
	 * des Wertes. Er kann z.B. in einem Text-Eingabefeld benutzt werden. Es ist
	 * garantiert, daß dieser Wert mit der parse()-Methode wieder eingelesen und
	 * zurückgewandelt werden kann.
	 * 
	 * @return String-Version des Wertes
	 */
	public abstract String format();

	public abstract String getDefaultValue();

	/**
	 * Diese Methode parst den angegebenen String,der z.B. aus einem 
	 * Texteingabefeld stammen kann und speichert ihn in das Objekt zurück.
	 * 
	 * @param s
	 * @return String
	 * @throws DatabaseException
	 */
	public abstract String parse(String s);

	/**
	 * Diese Methode überprüft, ob der angegebene String ein gültiger Wert ist,
	 * den man der parse()-Funktion übergeben kann.
	 * 
	 * @param s
	 * @return boolean (ist der Wert gültig?)
	 */
	public abstract boolean validate(String s);

	/**
	 * Nur einen Teilstring validieren, also Klärung der Frage:
	 * Der Benutzer hat gerade eine Taste gedrückt. Wenn ich die jetzt an der
	 * Cursorposition einfüge, kann dann jemals ein gültiger String daraus
	 * werden?
	 * Beispiel "11." ist kein gültiges Datum, kann aber eins werden.
	 * 
	 * @param s
	 * @return boolean (ist der Wert gültig?)
	 */
	public abstract boolean validatePart(String s);
}
/*
 * $Log: Editable.java,v $
 * Revision 1.2  2005/08/21 17:06:59  tbayen
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
 * Revision 1.1  2004/10/11 14:29:37  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 */