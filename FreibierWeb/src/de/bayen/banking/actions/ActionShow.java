/* Erzeugt am 03.04.2005 von tbayen
 * $Id: ActionShow.java,v 1.3 2005/04/19 17:17:04 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.banking.hbci.DTAUSReader;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
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
	static Logger logger = Logger.getLogger(ActionShow.class.getName());
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		super.executeAction(ad, req, root, db, servlet);
		// Falls das hier die "Pool"-Tabelle ist, habe ich da noch was zu sagen...
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		if (name.equals("Pool")) {
			Record record = (Record) root.get("record");
			BLOB dtausblob = ((BLOB) record.getField("DTAUS").getValue());
			// Lesen der DTAUS-Daten, die ausgegeben werden sollen
			try {
				DTAUSReader dtaus = new DTAUSReader(dtausblob.toByteArray());
				root.put("dtausblz", dtaus.getA_BLZ());
				root.put("dtauskontonummer", dtaus.getA_Kontonummer());
				root.put("dtaussumme", dtaus.getE_Summe());
				root.put("dtauslg", dtaus.getA_LastOrGut());
			} catch (Exception e) {
				throw new ServletException("Fehler beim Lesen der DTAUS-Datei",
						e);
			}
			
			// Vorbereiten für das Einlesen von DTAUS-Dateien
			List liste = new ArrayList();
			root.put("korbliste", liste);
			Table koerbe=db.getTable("Ausgangskoerbe");
			for(int i=0; i<koerbe.getNumberOfRecords(); i++){
				logger.debug(new Integer(i));
				Record korb=koerbe.getRecordByNumber(i);
				Map map=new HashMap();
				liste.add(map);
				map.put("name",korb.getFormatted("Bezeichnung"));
				map.put("value",korb.getFormatted("id"));
			}

		}
	}
}
/*
 * $Log: ActionShow.java,v $
 * Revision 1.3  2005/04/19 17:17:04  tbayen
 * DTAUS-Dateien wieder einlesen in die Datenbank
 *
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */