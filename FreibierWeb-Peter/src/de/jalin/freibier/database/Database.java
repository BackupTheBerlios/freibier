// $Id: Database.java,v 1.2 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.database;

import java.util.List;
import de.jalin.freibier.database.exception.SystemDatabaseException;

public interface Database {
	
	/**
	 * Ergibt eine Liste von Strings, die die Namen der Tabellen enthält, die
	 * in der Datenbank angelegt sind.
	 */
	public abstract List getTableNamesList() throws SystemDatabaseException;

	public abstract Table getTable(String name) throws SystemDatabaseException;

	public abstract void close() throws SystemDatabaseException;
}
/* 
 *  $Log: Database.java,v $
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */