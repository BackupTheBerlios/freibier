// $Id: QueryCondition.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.database;

import de.jalin.freibier.database.exception.DatabaseException;

public interface QueryCondition {
	
	// Konstanten für den Operator:
	public static final int EQUAL = 0;
	public static final int GREATER_OR_EQUAL = 1;
	public static final int GREATER = 2;
	public static final int LESS = 3;
	public static final int LESS_OR_EQUAL = 4;
	public static final int LIKE = 5;

	public abstract void and(QueryCondition cond);

	public abstract String expression() throws DatabaseException;
}
/*
 *  $Log: QueryCondition.java,v $
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
