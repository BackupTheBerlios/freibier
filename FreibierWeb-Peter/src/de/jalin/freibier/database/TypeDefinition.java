// $Id: TypeDefinition.java,v 1.8 2005/03/21 21:41:11 tbayen Exp $
package de.jalin.freibier.database;

import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.impl.ValueObject;

public interface TypeDefinition {
	
    /**
     * Liefert den Namen des Typs (in der Regel den Spaltennamen).
     * @return
     */
	public abstract String getName();

	/**
	 * Setzt den Typnamen.
	 * @param name
	 */
	public abstract void setName(String name);

	/**
	 * Liefert die Feldlaenge.
	 * @return
	 */
	public abstract int getLength();

	/**
	 * gibt einen SQL-Typ zurück, wie er in java.sql.Types beschrieben ist. Dies
	 * ist der Typ, mit dem der Wert in der eigentlichen Datenbank abgespeichert
	 * ist.
	 */
	public abstract int getSQLType();

	/**
	 * Diese Operation liefert die Java-Klasse, die den eigentlichen Wert intern
	 * verwaltet. Dies ist z.B. der Datentyp, der sich bei DataObject.getValue()
	 * ergibt.
	 */
	public abstract Class getJavaType();

	/**
	 * Liefert einen Default-Wert fuer diesen Typ.
	 * @return
	 */
	public abstract Object getDefaultValue();

	/**
	 * Um Properties benutzen zu koennen, muessen diese in eine Datei
	 * "<tabellenname>.properties" geschrieben werden. Der Pfad zu dieser Datei
	 * sollte dann mit db.setPropertyPath(...) gesetzt werden. Der Name der 
	 * Property beginnt dann mit dem Namen der Spalte, auf die sich die
	 * Property bezieht und einem Punkt. Danach kommt die eigentliche Bezeichnung
	 * der Property. Folgende Werte koennen eingestellt werden:
	 * 
	 * length
	 *   Laenge des Wertes in der Ausgabe (sonst gilt Default-Wert aus der SQL-Definition)
	 * align
	 *   Ausrichtung: left, right, center
	 * foreignkey.table
	 *   Diese Spalte ist ein Fremdschluessel auf die angegebene Tabelle
	 * foreignkey.indexcolumn
	 *   In dieser Spalte der o.a. Tabelle steht der Schluessel
	 * foreignkey.resultcolumn
	 *   Diese Spalte enthaelt eine Beschreibung des Wertes, die ggf. mit ausgegeben wird
	 */
	public abstract String getProperty(String key);

	/**
	 * Diese Methode ergibt eine moeglichst fuer Menschen lesbare String-Version
	 * des Wertes. Er kann z.B. in einem Text-Eingabefeld benutzt werden. Es ist
	 * garantiert, dass dieser Wert mit der parse()-Methode wieder eingelesen und
	 * zurueckgewandelt werden kann.
	 * @param s
	 * @return
	 * @throws SystemDatabaseException
	 */
	public abstract String printText(Object s) throws DatabaseException;

	/**
	 * Diese Methode parst den angegebenen String, der z.B. aus einem 
	 * Texteingabefeld stammen kann und wandelt ihn in die interne Repraesentation
	 * des Datentyps.
	 * @param s
	 * @return
	 * @throws DatabaseException
	 */
	public abstract ValueObject parse(String s) throws DatabaseException;

	/**
	 * Diese Methode ueberprueft, ob der angegebene String ein gueltiger Wert ist,
	 * den man der parse()-Funktion uebergeben kann.
	 * @param s
	 * @return
	 */
	public abstract boolean validate(String s);

	/**
	 * Nur einen Teilstring validieren, also Klaerung der Frage:
	 * Der Benutzer hat gerade eine Taste gedrueckt. Wenn ich die jetzt an der
	 * Cursorposition einfuege, kann dann jemals ein gueltiger String daraus
	 * werden?
	 * Beispiel "11." ist kein gueltiges Datum, kann aber eins werden.
	 * @param s
	 * @return
	 */
	public abstract boolean validatePart(String s);

	public abstract void addColumn(InsertQuery query, Printable printable) 
		throws DatabaseException;

	public abstract void addColumn(UpdateQuery query, Printable printable);
}
/*
 *  $Log: TypeDefinition.java,v $
 *  Revision 1.8  2005/03/21 21:41:11  tbayen
 *  Probleme mit Fremdschluessel gefixt
 *
 *  Revision 1.7  2005/03/03 22:32:45  phormanns
 *  Arbeit an ForeignKeys
 *
 *  Revision 1.6  2005/03/01 21:56:32  phormanns
 *  Long immer als Value-Objekt zu Number-Typen
 *  setRecord macht Insert, wenn PK = Default-Value
 *
 *  Revision 1.5  2005/02/24 22:18:13  phormanns
 *  Tests laufen mit HSQL und MySQL
 *
 *  Revision 1.4  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 *  Revision 1.3  2005/02/16 17:24:52  phormanns
 *  OrderBy und Filter funktionieren jetzt
 *
 *  Revision 1.2  2005/01/28 17:13:25  phormanns
 *  Schnittstelle dokumentiert.
 *
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
