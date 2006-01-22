/* Erzeugt am 27.03.2005 von tbayen
 * $Id: ActionDownload.java,v 1.4 2006/01/22 19:44:24 tbayen Exp $
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
 * TODO Klassenbeschreibung f�r die Klasse "ActionDownload"
 * 
 * @author tbayen
 */
public class ActionDownload implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		Table tab = db.getTable(name);
		String primarykey = tab.getRecordDefinition().getPrimaryKey();
		String recordid = (String) ((Map) root.get("uri")).get("id");
		Record record = tab.getRecordByPrimaryKey(tab.getRecordDefinition()
				.getFieldDef(primarykey).parse(recordid));
		root.put("binarydata", record.getField(req.getParameter("field"))
				.getValue());
		String contenttype = req.getParameter("contenttype");
		if (contenttype == null) {
			// TODO: hier k�nnte man eine automatische Analyse machen
			contenttype = "application/octet-stream";
		}
		root.put("contenttype", contenttype);
	}
}
/*
 * $Log: ActionDownload.java,v $
 * Revision 1.4  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unn�tig, wurde gel�scht
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
 * Revision 1.1  2005/03/28 03:09:45  tbayen
 * Bin�rdaten (BLOBS) in der Datenbank und im Webinterface
 *
 */