// $Id: DatabaseFactory.java,v 1.2 2005/01/28 17:13:25 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.impl.DatabaseImpl;

/**
 *  Liefert ein Database-Objekt zur Verwaltung einer Datenbank-Instanz.
 * @author peter
 */
public class DatabaseFactory {
	
    /** 
     * Liefert das Database-Objekt mit den genannten Parametern.
     * @param dbName
     * @param dbServer
     * @param dbUser
     * @param dbPassword
     * @return Database
     * @throws DatabaseException
     */
	public static Database getDatabaseInstance(String dbName, String dbServer,
			String dbUser, String dbPassword) throws DatabaseException {
		Database db = new DatabaseImpl(dbName, dbServer, dbUser, dbPassword);
		return db;
	}
}
/*
 *  $Log: DatabaseFactory.java,v $
 *  Revision 1.2  2005/01/28 17:13:25  phormanns
 *  Schnittstelle dokumentiert.
 *
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
