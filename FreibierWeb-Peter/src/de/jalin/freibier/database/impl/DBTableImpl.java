//$Id: DBTableImpl.java,v 1.3 2005/02/18 22:17:42 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.crossdb.sql.Column;
import com.crossdb.sql.CrossdbResultSet;
import com.crossdb.sql.IWhereClause;
import com.crossdb.sql.SelectQuery;
import com.crossdb.sql.Table;
import com.crossdb.sql.UpdateQuery;
import com.crossdb.sql.WhereClause;
import com.crossdb.sql.WhereCondition;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

/**
 * Repraesentiert eine einzelne Tabelle aus einer Datenbank. Zugriffe auf diese
 * Klasse werden sofort an die zugrundeliegende Datenbank weitergereicht, d.h.
 * im Normalfall als SQL-Befehle ausgefuehrt.
 * 
 * @author tbayen
 */
public class DBTableImpl implements DBTable {
	
	private static Log log = LogFactory.getLog(DBTableImpl.class);
	
	private String name;
	private DatabaseImpl db;
	private Table tab;
	private Map columnTypeDefinitions;

	protected DBTableImpl(DatabaseImpl db, String name) throws SystemDatabaseException {
		this.db = db;
		this.name = name;
		this.tab = new Table(name);
		columnTypeDefinitions = new HashMap();
		Iterator colIterator = tab.getColumns().iterator();
		Column col = null;
		while (colIterator.hasNext()) {
			col = (Column) colIterator.next();
			// TODO Ressourcebundle uebergeben.
			columnTypeDefinitions.put(col.getName(), TypeDefinitionImpl.create(col, null, db));
		}
	}

	public List getMultipleRecords(int startRecordNr, int numberOfRecords, String orderColumn, boolean ascending) throws DatabaseException {
		return this.getRecords(null, orderColumn, ascending, startRecordNr, numberOfRecords);
	}

	public List getRecordsFromQuery(IWhereClause clause, String orderColumn, boolean ascending) throws DatabaseException {
		return this.getRecords(clause, orderColumn, ascending, 1, DBTable.MAX_FETCH_SIZE);
	}

	public List getRecords(IWhereClause clause, String orderColumn, boolean ascending, int startRecordNr, int numberOfRecords) throws DatabaseException {
		List resList = new ArrayList();
		SelectQuery query = db.getSQLFactory().getSelectQuery();
 		query.addTable(tab.getName());
		Iterator i = tab.getColumns().iterator();
		Column col = null;
		while (i.hasNext()) {
			col = (Column) i.next();
			query.addColumn(col.getName());
		}
		if (clause != null) {
			query.addWhereClause(clause);
		}
		if (orderColumn != null) {
			query.addOrderBy(orderColumn);
		}
		CrossdbResultSet res = db.executeSelectQuery(query);
		try {
			boolean hasMore = res.absolute(startRecordNr);
			int count = 0;
			Record rec = null;
			Map recHash = null;
			while (count < numberOfRecords) {
				if (hasMore) {
					recHash = new HashMap();
					Iterator i2 = tab.getColumns().iterator();
					Column col2 = null;
					ValueObject valueObject = null;
					String colName = null;
					while (i2.hasNext()) {
						col2 = (Column) i2.next();
						colName = col2.getName();
						valueObject = new ValueObject(res.getObject(colName), (TypeDefinition) columnTypeDefinitions.get(colName));
						recHash.put(col2.getName(), valueObject);
					}
					rec = new RecordImpl(this, recHash);
					resList.add(rec);
					count++;
				} else {
					break;
				}
				if (ascending) {
					hasMore = res.next();
				} else {
					hasMore = res.previous();
				}
			}
		} catch (SQLException e) {
    		throw new SystemDatabaseException(
    				"Datensatz nicht vorhanden", e, log);
		}
		return resList;
	}

	public int getNumberOfRecords(IWhereClause clause) throws DatabaseException {
		int numOfRecords = 0;
		SelectQuery query = db.getSQLFactory().getSelectQuery();
		query.addTable(tab.getName());
		query.addFunctionColumn("COUNT", this.getPrimaryKey());
		if (clause != null) {
			query.addWhereClause(clause);
		}
		CrossdbResultSet res = db.executeSelectQuery(query);
		try {
			if (res.next()) {
				numOfRecords = res.getInt(1);
			}
		} catch (SQLException e) {
    		throw new SystemDatabaseException(
    				"Datensatz nicht vorhanden", e, log);
		}
		return numOfRecords;
	}

	public Record getRecordByPrimaryKey(Object pkValue) throws DatabaseException {
		IWhereClause clause = new WhereClause();
		clause.addCondition(new WhereCondition(this.getPrimaryKey(), WhereCondition.EQUAL_TO, pkValue));
		List l = this.getRecords(clause, null, true, 1, 1);
		return (Record) l.get(0);
	}

	public List getGivenColumns(List colNames, int limit) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRecord(Record data) throws DatabaseException {
		// TODO Auto-generated method stub
		UpdateQuery query = db.getSQLFactory().getUpdateQuery();
		WhereCondition condition = new WhereCondition(this.getPrimaryKey(), 
				WhereCondition.EQUAL_TO, data.getPrintable(this.getPrimaryKey()).getValue());
		query.setTable(this.getName());
		query.addWhereCondition(condition);
		Iterator i = tab.getColumns().iterator();
		Column col = null;
		while (i.hasNext()) {
			col = (Column) i.next();
			if (!col.isPrimaryKey()) {
				// TODO addColumn in TypeDefinition delegieren
				query.addColumn(col.getName(), data.printSQL(col.getName()));
			}
		}
		db.executeUpdateQuery(query);
	}

	public void deleteRecord(Record data) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return tab.getName();
	}

	public TypeDefinition getFieldDef(String fieldName) throws SystemDatabaseException {
		// TODO Ressource Bundle uebergeben
		return TypeDefinitionImpl.create(tab.getColumn(fieldName), null, db);
	}

	public String getPrimaryKey() {
		Iterator i = tab.getColumns().iterator();
		String pkName = null;
		Column col = null;
		while (i.hasNext()) {
			col = (Column) i.next();
			if (col.isPrimaryKey()) {
				pkName = col.getName();
				break;
			}
		}
		return pkName;
	}

	public List getFieldsList() {
		Iterator i = tab.getColumns().iterator();
		List l = new ArrayList();
		Column col = null;
		while (i.hasNext()) {
			l.add((Column) i.next());
		}
		return l;
	}

	public void setTable(Table tab) {
		this.tab = tab;
	}

}
/*
 * $Log: DBTableImpl.java,v $
 * Revision 1.3  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.2  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.1  2005/02/13 20:27:14  phormanns
 * Funktioniert bis auf Filter
 *
 * Revision 1.7  2005/02/11 16:46:02  phormanns
 * MySQL geht wieder
 *
 */
