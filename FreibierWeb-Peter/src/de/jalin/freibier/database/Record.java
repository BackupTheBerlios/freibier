// $Id: Record.java,v 1.4 2005/01/29 20:21:59 phormanns Exp $
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
	public abstract String getFormatted(String name) throws DatabaseException;

	/**
	 * Setzt den Wert eines Datenfelds. 
	 * Der angegebene Value-String wird geparst. 
	 * @param name
	 * @param value
	 * @throws DatabaseException
	 */
	public abstract void setField(String name, String value)
			throws DatabaseException;

	public abstract Table getTable();

}

/*
 *  $Log: Record.java,v $
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
