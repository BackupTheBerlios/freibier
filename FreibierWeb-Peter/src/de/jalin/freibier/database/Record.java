// $Id: Record.java,v 1.2 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;

public interface Record {
	
	public abstract Printable getField(String name) throws DatabaseException;

	public abstract String getFormatted(String name) throws DatabaseException;

	public abstract void setField(String name, String value)
			throws DatabaseException;

}

/*
 *  $Log: Record.java,v $
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
