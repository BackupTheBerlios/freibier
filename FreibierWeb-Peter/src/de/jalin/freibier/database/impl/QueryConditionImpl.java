// $Id: QueryConditionImpl.java,v 1.3 2005/01/29 22:10:02 phormanns Exp $
package de.jalin.freibier.database.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.QueryCondition;
import de.jalin.freibier.database.Table;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

/**
 * Diese Hilfsklasse wird benutzt, um WHERE-Konditionen an 
 * getRecordsFromQuery() zu übergeben.
 */
public class QueryConditionImpl implements QueryCondition {
	
	private static final Log log = LogFactory.getLog(QueryConditionImpl.class);
	
	private String column;
	private int operator;
	private Object value;
	private QueryCondition next = null;
	private Table tab = null;
	
	public QueryConditionImpl(Table tab, String column, int operator, Object value) {
		super();
		this.column = column;
		this.operator = operator;
		this.value = value;
		this.tab = tab;
		this.next = null;
	}

	public void and(QueryCondition cond) {
		if (next == null) {
			next = cond;
 		} else {
 			next.and(cond);
 		}
	}

	public String expression() throws DatabaseException {
		String erg = " AND " + column;
		switch (operator) {
		case EQUAL:				erg += " = ";	break;
		case GREATER:			erg += " > ";	break;
		case GREATER_OR_EQUAL:	erg += " >= ";	break;
		case LESS:				erg += " < ";	break;
		case LESS_OR_EQUAL:		erg += " <= ";	break;
		case LIKE:				erg += " LIKE ";break;
		default:
			throw new SystemDatabaseException("falsche QueryConditionImpl: ("
					+ column + "," + operator + "," + "value" + ")", log);
		}
		DataObject val=new DataObject(value,tab.getFieldDef(column));
		erg += SQLPrinter.print(val);
		if (next != null)
			erg += next.expression();
		return erg;
	}
}
/*
 *  $Log: QueryConditionImpl.java,v $
 *  Revision 1.3  2005/01/29 22:10:02  phormanns
 *  SQL zum Teil nach MysqlTableImpl verschoben
 *  MySQL LIMIT-Statement durch JDBC 3.0 ersetzt
 *
 *  Revision 1.2  2005/01/29 20:21:59  phormanns
 *  RecordDefinition in TableImpl integriert
 *
 *  Revision 1.1  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
