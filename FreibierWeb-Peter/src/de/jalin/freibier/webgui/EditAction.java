// $Id: EditAction.java,v 1.1 2005/02/25 15:51:29 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;

public class EditAction extends AbstractAction {
	
	public EditAction(Database db) throws DatabaseException {
		super(db);
	}

	public Map performAction(HttpServletRequest request, DialogState state)
			throws DatabaseException {
		String rowId = request.getParameter("row");
		state.updateFromRequest(null, null, null, rowId);
		return readData(state);
	}
}
/*
 *  $Log: EditAction.java,v $
 *  Revision 1.1  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
