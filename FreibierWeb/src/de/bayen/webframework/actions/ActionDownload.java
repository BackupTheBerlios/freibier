/* Erzeugt am 27.03.2005 von tbayen
 * $Id: ActionDownload.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionDownload"
 * 
 * @author tbayen
 */
public class ActionDownload implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db) throws DatabaseException,
			ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		List fields = (List) root.get("fields");
		Table tab = db.getTable(name);
		String primarykey = tab.getRecordDefinition().getPrimaryKey();
		String recordid = (String) ((Map) root.get("uri")).get("id");
		Record record = tab.getRecordByPrimaryKey(tab.getRecordDefinition()
				.getFieldDef(primarykey).parse(recordid));
		root.put("binarydata",record.getField(req.getParameter("field")).getValue());
		String contenttype=req.getParameter("contenttype");
		if(contenttype==null){
			// TODO: hier könnte man eine automatische Analyse machen
			contenttype="application/octet-stream";
		}
		root.put("contenttype",contenttype);
	}
}

/*
 * $Log: ActionDownload.java,v $
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:10  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.1  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 */