/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionTables.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionTables"
 * 
 * @author tbayen
 */
public class ActionTables implements Action {
	/*
	 * @see de.bayen.webframework.actions.Action#executeAction(de.bayen.webframework.ActionDispatcher, javax.servlet.http.HttpServletRequest, java.util.Map, de.bayen.webframework.WebDBDatabase)
	 */
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db) throws DatabaseException,
			ServletException {
		root.put("list", db.getTableNamesList());
	}
}

/*
 * $Log: ActionTables.java,v $
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:10  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */