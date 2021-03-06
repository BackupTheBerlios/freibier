// $Id: ScrollAction.java,v 1.2 2005/02/25 15:51:29 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;

public class ScrollAction extends AbstractAction {
	
	public ScrollAction(Database db) throws DatabaseException {
		super(db);
	}

	public Map performAction(HttpServletRequest request, DialogState state)
			throws DatabaseException {
		String key = request.getParameter("key");
		state.updateFromRequest(null, null, key, null);
		return readData(state);
	}
}
/*
 *  $Log: ScrollAction.java,v $
 *  Revision 1.2  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
