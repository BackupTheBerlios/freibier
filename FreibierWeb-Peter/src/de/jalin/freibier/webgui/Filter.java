// $Id: Filter.java,v 1.2 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import de.jalin.freibier.database.QueryCondition;
import de.jalin.freibier.database.Table;
import de.jalin.freibier.database.exception.DatabaseException;

public class Filter {

	public static final int FT_LIKE = 1;
	
	private boolean filterEnabled = false;
	private Map tableFilterMap = null;
	
	public Filter() {
		filterEnabled = false;
		tableFilterMap = new HashMap();
	}
	
	public boolean isFilterEnabled() {
		return filterEnabled;
	}
	
	public void setFilterEnabled(boolean enabled) {
		filterEnabled = enabled;
	}
	
	public void resetAll() {
		filterEnabled = false;
		tableFilterMap.clear();
	}
	
	public void resetTable(String tableName) {
		tableFilterMap.remove(tableName);
	}
	
	public void addFilter(String tableName, String columnName, 
			int filterType, String filterPattern) {
		TableFilter tabFt = (TableFilter) tableFilterMap.get(tableName);
		if (tabFt == null) {
			tabFt = new TableFilter();
			tabFt.tableName = tableName;
			tabFt.columnFilterMap = new HashMap();
			tableFilterMap.put(tabFt.tableName, tabFt);
		}
		ColumnFilter colFt = new ColumnFilter();
		colFt.tableName = tableName;
		colFt.columnName = columnName;
		colFt.filterType = filterType;
		colFt.filterPattern = filterPattern;
		tabFt.columnFilterMap.put(columnName, colFt);
	}
	
	private class TableFilter {
		String tableName;
		Map columnFilterMap;
	}
	
	private class ColumnFilter {
		String tableName;
		String columnName;
		int filterType;
		String filterPattern;
	}

	public QueryCondition createQueryCondition(Table tab) throws DatabaseException {
		QueryCondition queryFilter = null;
		TableFilter tabFt = (TableFilter) tableFilterMap.get(tab.getName());
		if (tabFt != null) {
			Iterator ftIterator = tabFt.columnFilterMap.keySet().iterator();
			String fieldName = null;
			ColumnFilter colFt = null;
			if (ftIterator.hasNext()) {
				fieldName = (String) ftIterator.next();
				colFt = (ColumnFilter) tabFt.columnFilterMap.get(fieldName);
				queryFilter = tab.createQueryCondition(fieldName, QueryCondition.LIKE, 
						tab.getFieldDef(fieldName).parse(colFt.filterPattern));
			}
			while (ftIterator.hasNext()) {
				fieldName = (String) ftIterator.next();
				colFt = (ColumnFilter) tabFt.columnFilterMap.get(fieldName);
				queryFilter.and(tab.createQueryCondition(fieldName, QueryCondition.LIKE, 
						tab.getFieldDef(fieldName).parse(colFt.filterPattern)));
			}
		}
		return queryFilter;
	}
	
	public String getFilterPattern(String tableName, String columnName) {
		TableFilter tf = (TableFilter) tableFilterMap.get(tableName);
		if (tf != null) {
			ColumnFilter cf = (ColumnFilter) tf.columnFilterMap.get(columnName);
			if (cf != null) {
				return cf.filterPattern;
			}
		}
		return "";
	}
}

/*
 *  $Log: Filter.java,v $
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 *  Revision 1.1  2004/12/31 17:13:11  phormanns
 *  Erste öffentliche Version
 *
 */