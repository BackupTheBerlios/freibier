/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionSearch.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionSearch"
 * 
 * @author tbayen
 */
public class ActionSearch implements Action {
	/*
	 * @see de.bayen.webframework.actions.Action#executeAction(de.bayen.webframework.ActionDispatcher, javax.servlet.http.HttpServletRequest, java.util.Map, de.bayen.webframework.WebDBDatabase)
	 */
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db) throws DatabaseException,
			ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		List fields = (List) root.get("fields");
		Table tab = db.getTable(name);
		// QueryCondition zusammenbauen:
		QueryCondition condi = null;
		Set params = req.getParameterMap().keySet();
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String parameter = (String) it.next();
			if (parameter.charAt(0) == '_') {
				// Mit _ beginnen alle Datenfelder in meinen Formularen
				QueryCondition nextcond = tab.new QueryCondition(parameter
						.substring(1), Table.QueryCondition.EQUAL, req
						.getParameter(parameter));
				if (condi == null) {
					condi = nextcond;
				} else {
					condi.and(nextcond);
				}
			}
		}
		List records = tab.getRecordsFromQuery(condi, tab.getRecordDefinition()
				.getPrimaryKey(), true);
		if (records.size() == 0) {
			return;
		} else {
			// Umleitung auf andere Action
			uri.put("action", "show");
			Record record = (Record) records.get(0);
			uri.put("id", record.getField(
					record.getRecordDefinition().getPrimaryKey()).format());
			ad.executeAction("show", req, root, db);
		}
	}
}

/*
 * $Log: ActionSearch.java,v $
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