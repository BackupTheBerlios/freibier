// $Id: DatabaseFactory.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.impl.DatabaseImpl;

public class DatabaseFactory {
	
	public static Database getDatabaseInstance(String dbName, String dbServer,
			String dbUser, String dbPassword) throws DatabaseException {
		Database db = new DatabaseImpl(dbName, dbServer, dbUser, dbPassword);
		return db;
	}
}
/*
 *  $Log: DatabaseFactory.java,v $
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
