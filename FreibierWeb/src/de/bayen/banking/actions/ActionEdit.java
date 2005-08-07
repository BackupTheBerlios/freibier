/* Erzeugt am 05.06.2005 von tbayen
 * $Id: ActionEdit.java,v 1.1 2005/08/07 16:56:15 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.UserDatabaseException;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Die Edit-Action habe ich in der Banking-Applikation überladen, um beim
 * editieren von Transaktionen neu eingegebene Daten in den Vorlagen zu
 * speichern.
 * 
 * @author tbayen
 */
public class ActionEdit extends de.bayen.webframework.actions.ActionEdit {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		super.executeAction(ad, req, root, db, servlet);
		// Falls das hier die "Transaktionen"-Tabelle ist, speichere ich 
		// die Werte als Vorlage ab:
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		if (name.equals("Transaktionen")) {
			Table tab = db.getTable(name);
			Record record;
			String kontonummer = req.getParameter("_Kontonummer");
			try {
				record = tab.getRecordByValue("Kontonummer", kontonummer);
			} catch (DatabaseException e) {
				record = null;
			}
			if (kontonummer != null && kontonummer.length() > 0) {
				if ((record == null)
						|| (record.getField("BLZ").format().equals(req
								.getParameter("_BLZ")))) {
					record = tab.getEmptyRecord();
					record.setField("Empfaenger", req
							.getParameter("_Empfaenger"));
					record.setField("Kontonummer", req
							.getParameter("_Kontonummer"));
					record.setField("BLZ", req.getParameter("_BLZ"));
					Record vorlage;
					try {
						vorlage = db.getTable("Ausgangskoerbe")
								.getRecordByValue("Bezeichnung", "Vorlagen");
					} catch (UserDatabaseException e) {
						// existiert der Vorlagenkorb gar nicht?
						vorlage = null;
					}
					if (vorlage != null) {
						record.setField("Ausgangskorb", db.getTable(
								"Ausgangskoerbe").getRecordByValue(
								"Bezeichnung", "Vorlagen").getField("id")
								.format());
						tab.setRecord(record);
					}
				}
			}
		}
	}
}
/*
 * $Log: ActionEdit.java,v $
 * Revision 1.1  2005/08/07 16:56:15  tbayen
 * Produktionsversion 1.5
 *
 */