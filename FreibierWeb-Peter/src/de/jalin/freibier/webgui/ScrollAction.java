// $Id: ScrollAction.java,v 1.1 2005/02/18 22:17:42 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.Map;

import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;

public class ScrollAction implements Action {
	
	private Database db;

	public ScrollAction(Database db) {
		this.db = db;
	}

	public Map performAction(DialogState state) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
}

/*
 *  $Log: ScrollAction.java,v $
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
