/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionList.java,v 1.4 2006/01/22 19:44:24 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Database;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionList"
 * 
 * @author tbayen
 */
public class ActionList implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		root.put("list", populateTableData(db.getTable(name), (String) root
				.get("order"), (String) root.get("orderdir"), null));
	}

	private List populateTableData(Table tab, String ordercol, String orderdir,
			String filter) throws DatabaseException {
		QueryCondition cond = null;
		if (filter != null) {
			String condparts[] = filter.split("=");
			cond = tab.new QueryCondition(condparts[0],
					Table.QueryCondition.EQUAL, tab.getRecordDefinition()
							.getFieldDef(condparts[0]).parse(condparts[1]));
		}
		List records = tab.getRecordsFromQuery(cond, ordercol, orderdir
				.equals("ASC"));
		return records;
	}
}
/*
 * $Log: ActionList.java,v $
 * Revision 1.4  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.3  2005/08/12 22:57:11  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:09  tbayen
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