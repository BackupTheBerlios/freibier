/* Erzeugt am 20.01.2006 von tbayen
 * $Id: ActionImage.java,v 1.2 2006/01/22 19:44:24 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.database.Database;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

public class ActionImage implements Action {
	static Logger logger = Logger.getLogger(ActionImage.class.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		Map uri = (Map) root.get("uri");
		String name = req.getParameter("name");
		String theme = (String) uri.get("theme");
		ClassLoader cl = this.getClass().getClassLoader();
		String path = servlet.getClass().getPackage().getName().replace('.',
				'/')
				+ "/templates";
		if (!theme.equals("")) {
			theme = "/" + theme;
		}
		logger.debug("ActionImage: Bild wird geladen: '" + path + theme
				+ "/images/" + name + "'");
		InputStream stream;
		// erst versuche ich es im Theme meiner Applikation
		stream = cl.getResourceAsStream(path + theme + "/images/" + name);
		if (stream == null)
			// dann vielleicht im Hauptverzeichnis meiner Applikation
			stream = cl.getResourceAsStream(path + "/images/" + name);
		if (stream == null) {
			// dann vielleicht im Hauptverzeichnis der WebDatabase-Bibliothek
			path = ServletDatabase.class.getPackage().getName().replace('.',
					'/')
					+ "/templates";
			stream = cl.getResourceAsStream(path + "/images/" + name);
		}
		if (stream == null)
			// nutzt alles nix, das Image gibt es nicht
			throw new RuntimeException("Image '" + path + "/images/" + name
					+ "' kann nicht geladen werden");
		try {
			byte buffer[] = new byte[100000];
			stream.read(buffer);
			root.put("binarydata", buffer);
			root.put("contenttype", "image/png");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
/*
 * $Log: ActionImage.java,v $
 * Revision 1.2  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.1  2006/01/21 23:10:10  tbayen
 * Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
 *
 */