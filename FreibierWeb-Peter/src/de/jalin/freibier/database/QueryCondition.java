// $Id: QueryCondition.java,v 1.2 2005/01/28 17:13:25 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;

public interface QueryCondition {
	
	// Konstanten fuer den Operator:
	public static final int EQUAL = 0;
	public static final int GREATER_OR_EQUAL = 1;
	public static final int GREATER = 2;
	public static final int LESS = 3;
	public static final int LESS_OR_EQUAL = 4;
	public static final int LIKE = 5;

	/**
	 * Erweitert die Query-Bedingung um eine weitere Bedingung.
	 * @param cond
	 */
	public abstract void and(QueryCondition cond);

	/** 
	 * Liefert einen SQL-Ausdruck fuer die Bedingung.
	 * Der SQL-Ausdruck kann in einer SQL-Where-Bedingung benutzt
	 * werden.
	 * @return
	 * @throws DatabaseException
	 */
	public abstract String expression() throws DatabaseException;
}
/*
 *  $Log: QueryCondition.java,v $
 *  Revision 1.2  2005/01/28 17:13:25  phormanns
 *  Schnittstelle dokumentiert.
 *
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
