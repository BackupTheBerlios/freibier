// $Id: InitAction.java,v 1.1 2005/02/18 22:17:42 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;

public class InitAction implements Action {
	
	private List tableNamesList;
	private Database db;
	
	public InitAction(Database db) throws DatabaseException {
		this.db = db;
		tableNamesList = db.getTableNamesList();
	}

	public Map performAction(DialogState state) throws DatabaseException {
		state.setEditModeOff();
		state.setFirstRowNumber(1);
		state.setMaxRowNumber(DBTable.MAX_FETCH_SIZE);
		state.setOrderByColumn(null);
		Map templateData = new HashMap();
		templateData.put("tablenames", tableNamesList);
		String tableName = state.getTableName();
		if (tableName == null && tableNamesList != null && tableNamesList.size() > 0) {
			tableName = (String) tableNamesList.get(0);
			state.setTableName(tableName);
		}
		DBTable tab = db.getTable(tableName);
		templateData.put("typedefinitions", tab.getFieldsList());
		// TODO Filter einbauen
		templateData.put("data", tab.getRecords(null, null, true, 1, 20));
		return templateData;
	}
	
}

/*
 *  $Log: InitAction.java,v $
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
