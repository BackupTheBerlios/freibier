// $Id: ActionFactory.java,v 1.4 2005/03/03 22:32:45 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

public class ActionFactory {
	
	private static final Log log = LogFactory.getLog(ActionFactory.class);
	
	private Map actionsMap;
	
	public ActionFactory(Database db) throws DatabaseException {
		actionsMap = new HashMap();
		actionsMap.put("init", new InitAction(db));
		actionsMap.put("table", new TableAction(db));
		actionsMap.put("scroll", new ScrollAction(db));
		actionsMap.put("order", new OrderByAction(db));
		actionsMap.put("edit", new EditAction(db));
		actionsMap.put("save", new SaveAction(db));
	}
	
	public Action getAction(String name) throws DatabaseException {
		Object mapObject = actionsMap.get(name);
		if (mapObject instanceof Action) {
			return (Action) mapObject;
		} else {
			throw new SystemDatabaseException("Action " + name + " nicht definiert.", log);
		}
	}
}

/*
 *  $Log: ActionFactory.java,v $
 *  Revision 1.4  2005/03/03 22:32:45  phormanns
 *  Arbeit an ForeignKeys
 *
 *  Revision 1.3  2005/02/28 21:52:38  phormanns
 *  SaveAction begonnen
 *
 *  Revision 1.2  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
