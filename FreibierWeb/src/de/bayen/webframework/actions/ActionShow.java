/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionShow.java,v 1.5 2006/01/22 19:44:24 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.TypeDefinition;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionShow"
 * 
 * @author tbayen
 */
public class ActionShow implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		Table tab = db.getTable(name);
		String primarykey = tab.getRecordDefinition().getPrimaryKey();
		String recordid = (String) uri.get("id");
		Record record = tab.getRecordByPrimaryKey(tab.getRecordDefinition()
				.getFieldDef(primarykey).parse(recordid));
		root.put("record", record);
		// enthaltene Listen suchen und einfügen:
		List lists = new ArrayList();
		root.put("lists", lists);
		List listen = tab.getRecordDefinition().getSublists();
		if (listen != null) {
			for (int i = 0; i < listen.size(); i++) {
				Map listendaten = new HashMap();
				lists.add(listendaten);
				listendaten.put("name", ((Map) listen.get(i)).get("name"));
				String indexcol = (String) ((Map) listen.get(i))
						.get("indexcolumn");
				listendaten.put("indexcolumn", indexcol);
				String tablename = (String) ((Map) listen.get(i)).get("table");
				listendaten.put("tablename", tablename);
				Table listtab = db.getTable(tablename);
				QueryCondition cond = listtab.new QueryCondition(indexcol,
						Table.QueryCondition.EQUAL, listtab
								.getRecordDefinition().getFieldDef(indexcol)
								.parse(recordid));
				listendaten.put("list", listtab.getRecordsFromQuery(cond,
						listtab.getRecordDefinition().getFieldDef(0).getName(),
						true));
				List sublistfields = new ArrayList();
				listendaten.put("fields", sublistfields);
				List felderdef = listtab.getRecordDefinition().getFieldsList();
				for (int j = 0; j < felderdef.size(); j++) {
					// fields-Hash füllen
					String feldname = ((TypeDefinition) felderdef.get(j))
							.getName();
					if (!feldname.equals("id") && !feldname.equals(indexcol)) {
						sublistfields.add(feldname);
					}
				}
			}
		}
	}
}
/*
 * $Log: ActionShow.java,v $
 * Revision 1.5  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.4  2006/01/21 23:10:10  tbayen
 * Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
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