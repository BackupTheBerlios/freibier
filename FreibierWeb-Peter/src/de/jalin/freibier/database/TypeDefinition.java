// $Id: TypeDefinition.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;

public interface TypeDefinition {
	
	public abstract String getName();

	public abstract void setName(String name);

	public abstract int getLength();

	/**
	 * gibt einen SQL-Typ zurück, wie er in java.sql.Types beschrieben ist. Dies
	 * ist der Typ, mit dem der Wert in der eigentlichen Datenbank abgespeichert
	 * ist.
	 */
	public abstract int getSQLType();

	/**
	 * Diese Klasse ergibt die Java-Klasse, die den eigentlichen Wert intern
	 * verwaltet. Dies ist z.B. der Datentyp, der sich bei DataObject.getValue()
	 * ergibt.
	 */
	public abstract Class getJavaType();

	public abstract Object getDefaultValue();

	/**
	 * Um Properties benutzen zu können, müssen diese in eine Datei
	 * "<tabellenname>.properties" geschrieben werden. Der Pfad zu dieser Datei
	 * sollte dann mit db.setPropertyPath(...) gesetzt werden. Der Name der 
	 * Property beginnt dann mit dem Namen der Spalte, auf die sich die
	 * Property bezieht und einem Punkt. Danach kommt die eigentliche Bezeichnung
	 * der Property. Folgende Werte können eingestellt werden:
	 * 
	 * length
	 *   Länge des Wertes in der Ausgabe (sonst gilt Default-Wert aus der SQL-Definition)
	 * align
	 *   Ausrichtung: left, right, center
	 * foreignkey.table
	 *   Diese Spalte ist ein Fremdschlüssel auf die angegebene Tabelle
	 * foreignkey.indexcolumn
	 *   In dieser Spalte der o.a. Tabelle steht der Schlüssel
	 * foreignkey.resultcolumn
	 *   Diese Spalte enthält eine Beschreibung des Wertes, die ggf. mit ausgegeben wird
	 */
	public abstract String getProperty(String key);

	/**
	 * Diese Methode ergibt eine möglichst für Menschen lesbare String-Version
	 * des Wertes. Er kann z.B. in einem Text-Eingabefeld benutzt werden. Es ist
	 * garantiert, daß dieser Wert mit der parse()-Methode wieder eingelesen und
	 * zurückgewandelt werden kann.
	 * 
	 * @param s
	 * @return
	 * @throws SystemDatabaseException
	 */
	public abstract String format(Object s) throws DatabaseException;

	/**
	 * Diese Methode parst den angegebenen String,der z.B. aus einem 
	 * Texteingabefeld stammen kann und wandelt ihn in die interne Repräsentation
	 * des Datentyps.
	 * 
	 * @param s
	 * @return
	 * @throws DatabaseException
	 */
	public abstract Object parse(String s) throws DatabaseException;

	/**
	 * Diese Methode überprüft, ob der angegebene String ein gültiger Wert ist,
	 * den man der parse()-Funktion übergeben kann.
	 * 
	 * @param s
	 * @return
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
	 * @return
	 */
	public abstract boolean validatePart(String s);
}
/*
 *  $Log: TypeDefinition.java,v $
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
