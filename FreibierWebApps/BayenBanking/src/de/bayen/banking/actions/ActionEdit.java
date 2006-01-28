/* Erzeugt am 05.06.2005 von tbayen
 * $Id: ActionEdit.java,v 1.3 2006/01/28 14:19:17 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.UserDBEx;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * Die Edit-Action habe ich in der Banking-Applikation überladen, um beim
 * editieren von Transaktionen neu eingegebene Daten in den Vorlagen zu
 * speichern.
 * 
 * @author tbayen
 */
public class ActionEdit extends de.bayen.webframework.actions.ActionEdit {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
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
					} catch (UserDBEx e) {
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
		super.executeAction(ad, req, root, db, servlet);
	}
}
/*
 * $Log: ActionEdit.java,v $
 * Revision 1.3  2006/01/28 14:19:17  tbayen
 * Zahlungsart in Transaktionen ermöglicht, Abbuch. und Lastschr. zu mischen
 *
 * Revision 1.2  2006/01/26 13:49:20  tbayen
 * Suchen: automatische Anlage von Vorlagen funktionierte nicht richtig
 *
 * Revision 1.1  2006/01/24 00:26:01  tbayen
 * Erste eigenständige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
 * Revision 1.2  2005/11/25 08:59:52  tbayen
 * kleinere Verbesserungen und Fehlerabfragen
 *
 * Revision 1.1  2005/08/07 16:56:15  tbayen
 * Produktionsversion 1.5
 *
 */