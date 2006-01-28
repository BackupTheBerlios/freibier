/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionNew.java,v 1.5 2006/01/28 17:10:34 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * Implementation der Action "new"
 * 
 * @author tbayen
 */
public class ActionNew implements Action {

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		Table tab = db.getTable(name);
		Record record = tab.getEmptyRecord();
		String recordid=tab.setRecordAndReturnID(record).toString();
		// Umleitung auf andere Action
		// Ich leite auf edit um, dann kann man beim new-Aufruf noch per
		// Parameter direkt einige Felder neu füllen. edit ruft dann show
		// auf, so dass der neue Datensatz direkt angezeigt werden kann.
		uri.put("id", recordid);
		ad.executeAction("edit", req, root, db, servlet);
	}
}

/*
 * $Log: ActionNew.java,v $
 * Revision 1.5  2006/01/28 17:10:34  tbayen
 * kleinere Todos (die meisten in der Doku) abgearbeitet
 *
 * Revision 1.4  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.3  2005/08/07 16:56:13  tbayen
 * Produktionsversion 1.5
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