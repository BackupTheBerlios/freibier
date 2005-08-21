/* Erzeugt am 11.10.2004 von tbayen
 * $Id: Printable.java,v 1.2 2005/08/21 17:06:59 tbayen Exp $
 */
package de.bayen.database;

import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;

/**
 * Ein "Printable"-Objekt besitzt bestimmte Funktionen, die man ben�tigt, um es
 * auf unterschiedliche Art und Weise auszugeben.
 * 
 * Printer-Objekte k�nnen auf diese Eigenschaften zugreifen, um eine konkrete
 * Ausgabe oder anderweitige Darstellung eines Objektes zu erzeugen.
 * 
 * @author tbayen
 */
public interface Printable {
	
	public abstract String getName();

	public abstract int getLength();

	/**
	 * Diese Methode ergibt eine m�glichst f�r Menschen lesbare String-Version
	 * des Wertes.
	 * @throws WrongTypeDBException 
	 * @throws DatabaseException
	 */
	public abstract String format() throws WrongTypeDBException;

	/**
	 * Diese Funktion gibt den Typ des Objektes zur�ck (z.B. 
	 * TypeDefinitionInteger.class). Normalerweise ist das die Klasse des 
	 * Objektes. Wenn es sich aber um eine Objektklasse handelt, die 
	 * eigentliche Objekteigenschaften verbirgt, wie z.B. de.bayen.database.DataObject,
	 * dann kann hier auch ein anderer Typ zur�ckgegeben werden.
	 * 
	 * Dieser Typ kann von Printern benutzt werden, um auf spezielle Eigenschaften
	 * der Objekte zuzugreifen, wenn dies n�tig sein sollte.
	 * 
	 * @return Class - Typ des Objektes
	 */
	public abstract Class getType();

	public abstract Object getValue();
}
/*
 * $Log: Printable.java,v $
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
 * Revision 1.4  2004/10/24 19:15:07  tbayen
 * ComboBox als Auswahlfeld f�r Foreign Keys
 *
 * Revision 1.3  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.2  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.1  2004/10/11 14:29:37  tbayen
 * Framework f�r Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 */