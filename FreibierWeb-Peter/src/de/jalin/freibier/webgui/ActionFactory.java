// $Id: ActionFactory.java,v 1.1 2005/02/18 22:17:42 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.HashMap;
import java.util.Map;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;


public class ActionFactory {
	
	private Map actionsMap;
	
	public ActionFactory(Database db) throws DatabaseException {
		actionsMap = new HashMap();
		actionsMap.put("init", new InitAction(db));
		actionsMap.put("scroll", new ScrollAction(db));
	}
	
	public Action getAction(String name) {
		return (Action) actionsMap.get(name);
	}
}

/*
 *  $Log: ActionFactory.java,v $
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
