// $Id: Record.java,v 1.6 2005/02/16 17:24:52 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;

/**
 * Schnittstelle fuer einen Datensatz.
 */
public interface Record {

    /**
     * Liefert ein einzelnes Datenfeld als Printable-Objekt.
     * @param name
     * @return
     * @throws DatabaseException
     */
	public abstract Printable getField(String name) throws DatabaseException;

	/**
	 * Liefert ein einzelnes Datenfeld aufbereitet als formatierten String.
	 * @param name
	 * @return
	 * @throws DatabaseException
	 */
	public abstract String printText(String name) throws DatabaseException;

	public abstract String printSQL(String name) throws DatabaseException;

	/**
	 * Setzt den Wert eines Datenfelds. 
	 * Der angegebene Value-String wird geparst. 
	 * @param name
	 * @param value
	 * @throws DatabaseException
	 */
	public abstract void setField(String name, String value)
			throws DatabaseException;

	public abstract DBTable getTable();

	/**
	 * @param name
	 * @return
	 */
	public abstract Object getValue(String name);

}

/*
 *  $Log: Record.java,v $
 *  Revision 1.6  2005/02/16 17:24:52  phormanns
 *  OrderBy und Filter funktionieren jetzt
 *
 *  Revision 1.5  2005/02/13 20:27:14  phormanns
 *  Funktioniert bis auf Filter
 *
 *  Revision 1.4  2005/01/29 20:21:59  phormanns
 *  RecordDefinition in TableImpl integriert
 *
 *  Revision 1.3  2005/01/28 17:13:25  phormanns
 *  Schnittstelle dokumentiert.
 *
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
