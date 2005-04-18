/* Erzeugt am 05.04.2005 von tbayen
 * $Id: ActionDtausimport.java,v 1.2 2005/04/18 10:57:55 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Diese Action liest DTAUS-Dateien aus dem Filesystem und schreibt sie in
 * die Pool-Tabelle. Von dort können Sie dann per Webinterface verschickt
 * werden.
 * 
 * @author tbayen
 */
public class ActionDtausimport implements Action {
	static Logger logger = Logger.getLogger(ActionDtausimport.class.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
		logger.debug("ActionDtausimport");
		Table pool = db.getTable("Pool");
		try {
			String dirname = servlet.getProperty("dtausimport.directory");
			File[] dir = (new File(dirname)).listFiles();
			for (int i = 0; i < dir.length; i++) {
				InputStream stream = new FileInputStream(dir[i]);
				byte[] puffer = new byte[1000000];
				int laenge = stream.read(puffer);
				byte[] dtaus = new byte[laenge];
				for (int j = 0; j < laenge; j++) {
					dtaus[j] = puffer[j];
				}
				stream.close();
				// dtaus ist gelesen, jetzt speichern:
				Record fisch = pool.getEmptyRecord();
				DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				String now = df.format(new Date());
				fisch.setField("Bezeichnung", "importierte Datei '"
						+ dir[i].getName() + "' vom " + now);
				fisch.setField("Dateiname", dir[i].getName());
				fisch.setField("DTAUS", new DataObject(new BLOB(dtaus), fisch
						.getRecordDefinition().getFieldDef("DTAUS")));
				pool.setRecord(fisch);
				//dir[i].delete();
			}
		} catch (FileNotFoundException e) {} catch (IOException e) {}
	}
}
/*
 * $Log: ActionDtausimport.java,v $
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