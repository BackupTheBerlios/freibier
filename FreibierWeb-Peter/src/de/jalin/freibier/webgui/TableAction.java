// $Id: TableAction.java,v 1.1 2005/03/03 22:32:45 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;

public class TableAction extends AbstractAction {
	
	public TableAction(Database db) throws DatabaseException {
		super(db);
	}

	public Map performAction(HttpServletRequest request, DialogState state)
			throws DatabaseException {
		state.setEditModeOff();
		state.setFirstRowNumber(1);
		state.setMaxRowNumber(DBTable.MAX_FETCH_SIZE);
		state.setOrderByColumn(null);
		state.setTableName(request.getParameter("table"));
		return readData(state);
	}
}
/*
 *  $Log: TableAction.java,v $
 *  Revision 1.1  2005/03/03 22:32:45  phormanns
 *  Arbeit an ForeignKeys
 *
 *  Revision 1.2  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
