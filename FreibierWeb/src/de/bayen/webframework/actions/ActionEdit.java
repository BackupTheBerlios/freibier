/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionEdit.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.util.HttpMultipartRequest;
import de.bayen.util.HttpMultipartRequest.UploadedFile;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionEdit"
 * 
 * @author tbayen
 */
public class ActionEdit implements Action {
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
		Set param = req.getParameterMap().keySet();
		Iterator i = param.iterator();
		while (i.hasNext()) {
			String parameter = (String) i.next();
			if (parameter.charAt(0) == '_') {
				String feldname = parameter.substring(1);
				// Mit _ beginnen alle Datenfelder in meinen Formularen
				if (req instanceof HttpMultipartRequest) {
					// Wenn ich ein Multipartreq bin, könnte ich einen
					// File-Upload enthalten
					HttpMultipartRequest mreq = (HttpMultipartRequest) req;
					UploadedFile file = mreq.getUploadedFile(parameter);
					if (file != null) {
						// Ja, das ist wirklich ein UploadedFile
						DataObject obj = new DataObject(new BLOB(file.data),
								record.getRecordDefinition().getFieldDef(
										feldname));
						record.setField(feldname, obj);
					} else {
						record.setField(feldname, req.getParameter(parameter));
					}
				}else{
					record.setField(feldname, req.getParameter(parameter));
				}
			}
		}
		// und in die Datenbank schreiben:
		tab.setRecord(record);
		// Umleitung auf andere Action
		uri.put("action", "show");
		uri.put("id", recordid);
		ad.executeAction("show", req, root, db);
	}
}
/*
 * $Log: ActionEdit.java,v $
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:10  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.4  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
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