/* Erzeugt am 03.04.2005 von tbayen
 * $Id: ActionShow.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.banking.hbci.DTAUSReader;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * Die Show-Action habe ich in der Banking-Applikation abgeleitet, um
 * bei der Anzeige von Pool-Dateien noch zusätzliche Daten, die ich aus
 * der DTAUS-Datei (die als BLOB in der Datenbank liegt) extrahiere, 
 * anzeigen zu können.
 * 
 * @author tbayen
 */
public class ActionShow extends de.bayen.webframework.actions.ActionShow {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db) throws DatabaseException,
			ServletException {
		super.executeAction(ad, req, root, db);
		// Falls das hier die "Pool"-Tabelle ist, habe ich da noch was zu sagen...
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		if (name.equals("Pool")) {
			Record record = (Record) root.get("record");
			BLOB dtausblob = ((BLOB) record.getField("DTAUS").getValue());
			DTAUSReader dtaus = new DTAUSReader(dtausblob.toByteArray());
			root.put("dtausblz", dtaus.getBLZ());
			root.put("dtauskontonummer", dtaus.getKontonummer());
			root.put("dtaussumme", dtaus.getSumme());
			root.put("dtauslg", dtaus.getLastOrGut());
		}
	}
}
/*
 * $Log: ActionShow.java,v $
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */