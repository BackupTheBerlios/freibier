// $Id: MysqlTableImpl.java,v 1.1 2005/01/29 22:10:02 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.util.Iterator;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.impl.type.TypeDefinitionForeignKey;

public class MysqlTableImpl extends TableImpl {
	
	public MysqlTableImpl(DatabaseImpl db, String name) {
		super(db, name);
	}

	/**
	 * Ergibt ein SQL-Select-Statement, das geeignet ist, alle Daten für diese
	 * Tabelle einzulesen. Dabei werden auch durch Fremdschlüssel referenzierte
	 * Werte miteingelesen. 
	 * 
	 * Das Statement enthaelt immer eine WHERE-Klausel am Ende, so dass mit 
	 * "AND ..." weitere WHERE-Bedingungen angehaengt werden koennen.
	 */
	protected String makeSelectStatement() {
		String felder = "";
		String tabellen = "`" + getName() + "`";
		String where = null;
		Iterator i = getColumnsList().iterator();
		TypeDefinitionImpl feldtyp;
		while (i.hasNext()) {
			feldtyp = (TypeDefinitionImpl) i.next();
			if (!felder.equals("")) {
				felder += ", ";
			}
			felder += getName() + "." + feldtyp.getName();
			if (feldtyp instanceof TypeDefinitionForeignKey) {
				String tabelle = feldtyp.getProperty("foreignkey.table");
				String spalte = feldtyp.getProperty("foreignkey.resultcolumn");
				felder += ", " + tabelle + "." + spalte + " AS " + feldtyp.getName() +"_foreign";
				tabellen += ", `" + tabelle + "`";
				if (where == null) {
					where = " WHERE ";
				} else {
					where += " AND ";
				}
				where += getName() + "." + feldtyp.getName() + "=" + tabelle
						+ "." + feldtyp.getProperty("foreignkey.indexcolumn");
			}
		}
		if (where == null)
			where = " WHERE 1";
		return "SELECT " + felder + " FROM " + tabellen + where;
		// Beispiel:
		// SELECT adressen.vorname, adressen.nachname, adressen.lebensalter, adressen.sprache, programmiersprachen.name, 
		// adressen.id FROM `adressen`,Programmiersprachen where programmiersprachen.id=adressen.sprache
	}

	protected String makeWhereExpression(String columnName, Object value) throws DatabaseException {
		TypeDefinition def = this.getFieldDef(columnName);
		return (getName() + "." + def.getName() + "=" 
				+ SQLPrinter.print(new DataObject(value, def)));
	}

	protected String makeDeleteStatement(String value) throws DatabaseException {
		String stmt = "DELETE FROM "
				+ getName()
				+ " WHERE "
				+ makeWhereExpression(this.getPrimaryKey(), value);
		return null;
	}

	protected String makeReplaceStatement(Record data) throws DatabaseException {
		String sql = "REPLACE INTO `" + getName() + "` SET ";
		for (int i = 0; i < this.getFieldsList().size(); i++) {
			if (i != 0) {
				sql += ", ";
			}
			String feldname = this.getFieldDef(i).getName();
			sql += "`" + feldname + "` = "
					+ SQLPrinter.print(data.getField(feldname));
		}
		return sql;
	}

	protected String makeOrderByExpression(String columnName, boolean ascending) throws DatabaseException {
		String orderBy = "";
		if (columnName != null && columnName.length() > 0) {
			orderBy = "ORDER BY "
					+ getName() + "." + columnName + (ascending ? " ASC" : " DESC")
					+ ", " + getName() + "." + getPrimaryKey()
					+ (ascending ? " ASC" : " DESC");
		}
		return orderBy;
	}

}

/*
 *  $Log: MysqlTableImpl.java,v $
 *  Revision 1.1  2005/01/29 22:10:02  phormanns
 *  SQL zum Teil nach MysqlTableImpl verschoben
 *  MySQL LIMIT-Statement durch JDBC 3.0 ersetzt
 *
 */
