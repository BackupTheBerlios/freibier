// $Id: Database.java,v 1.5 2005/02/21 22:55:25 phormanns Exp $
package de.jalin.freibier.database;

import java.util.List;
import de.jalin.freibier.database.exception.SystemDatabaseException;

/**
 *  Schnittstelle zur Datenbank-Instanz.
 */
public interface Database {
	
    public void createTestData() throws SystemDatabaseException;
    
	/**
	 * Ergibt eine Liste von Strings, die die Namen der Tabellen enthaelt, die
	 * in der Datenbank angelegt sind.
	 */
	public abstract List getTableNamesList() throws SystemDatabaseException;

	/**
	 * Liefert das Table-Objekt fuer eine Datenbank-Tabelle.
	 * @param name
	 * @return Table
	 * @throws SystemDatabaseException
	 */
	public abstract DBTable getTable(String name) throws SystemDatabaseException;

	/**
	 * Schliessen der Datenbank-Ressourcen.
	 * @throws SystemDatabaseException
	 */
	public abstract void close() throws SystemDatabaseException;
}
/* 
 *  $Log: Database.java,v $
 *  Revision 1.5  2005/02/21 22:55:25  phormanns
 *  Hsqldb zugefuegt
 *
 *  Revision 1.4  2005/02/13 20:27:14  phormanns
 *  Funktioniert bis auf Filter
 *
 *  Revision 1.3  2005/01/28 17:13:25  phormanns
 *  Schnittstelle dokumentiert.
 *
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */