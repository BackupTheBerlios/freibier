// $Id: AbstractAction.java,v 1.1 2005/02/25 15:51:29 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

public abstract class AbstractAction implements Action {
	
	private Database db;

	public AbstractAction(Database db) throws DatabaseException {
		this.db = db;
	}

	abstract public Map performAction(HttpServletRequest request,
			DialogState state) throws DatabaseException;

	protected Database getDatabase() {
		return db;
	}

	protected Map readData(DialogState state) throws DatabaseException,
			SystemDatabaseException {
		DBTable tab = db.getTable(state.getTableName());
		Map templateData = new HashMap();
		templateData.put("tablenames", db.getTableNamesList());
		templateData.put("columnnames", tab.getFieldsList());
		templateData.put("data", tab.getRecords(null, state.getOrderByColumn(),
				true, state.getFirstRowNumber(), state.getRowsPerPage()));
		if (state.isEditModeOn()) {
			templateData.put("editkey", state.getEditRecordKey());
		} else {
			templateData.put("editkey", "");
		}
		return templateData;
	}
}
/*
 *  $Log: AbstractAction.java,v $
 *  Revision 1.1  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
