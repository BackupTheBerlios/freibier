// $Id: InitAction.java,v 1.2 2005/02/25 15:51:29 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;

public class InitAction extends AbstractAction {
	
	public InitAction(Database db) throws DatabaseException {
		super(db);
	}

	public Map performAction(HttpServletRequest request, DialogState state)
			throws DatabaseException {
		state.setEditModeOff();
		state.setFirstRowNumber(1);
		state.setMaxRowNumber(DBTable.MAX_FETCH_SIZE);
		state.setOrderByColumn(null);
		String tableName = state.getTableName();
		List tableNamesList = this.getDatabase().getTableNamesList();
		if (tableName == null && tableNamesList != null
				&& tableNamesList.size() > 0) {
			tableName = (String) tableNamesList.get(0);
			state.setTableName(tableName);
		}
		return readData(state);
	}
}
/*
 *  $Log: InitAction.java,v $
 *  Revision 1.2  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
