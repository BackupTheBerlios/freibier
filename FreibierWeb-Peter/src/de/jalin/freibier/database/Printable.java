//$Id: Printable.java,v 1.1 2004/12/31 17:12:42 phormanns Exp $

package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;

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
	 * @throws DatabaseException
	 */
	public abstract String format() throws DatabaseException;

	/**
	 * Diese Funktion gibt den Typ des Objektes zur�ck (z.B. 
	 * TypeDefinitionInteger.class). Normalerweise ist das die Klasse des 
	 * Objektes. Wenn es sich aber um eine Objektklasse handelt, die 
	 * eigentliche Objekteigenschaften verbirgt, wie z.B. database.DataObject,
	 * dann kann hier auch ein anderer Typ zur�ckgegeben werden.
	 * 
	 * Dieser Typ kann von Printern benutzt werden, um auf spezielle Eigenschaften
	 * der Objekte zuzugreifen, wenn dies n�tig sein sollte.
	 * 
	 * @return
	 */
	public abstract Class getType();

	public abstract Object getValue();
}
/*
 * $Log: Printable.java,v $
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste �ffentliche Version
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