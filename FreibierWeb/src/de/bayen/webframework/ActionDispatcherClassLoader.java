/* Erzeugt am 19.03.2005 von tbayen
 * $Id: ActionDispatcherClassLoader.java,v 1.3 2005/08/07 16:56:14 tbayen Exp $
 */
package de.bayen.webframework;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.database.exception.DatabaseException;

/**
 * Dieser ActionDispatcher lädt eine durch den Namen der Action bezeichnete
 * {@link de.bayen.webframework.Action}-Klasse und führt dort  die
 * <code>executeAction<code>-Methode aus.
 * <p>
 * Wird diese Klasse abgeleitet, so werden die Action-Klassen zuerst im
 * Paket der abgeleiteten Klasse, dann im Paket dieser Basisklasse (dort 
 * sind die Standard-Actions).
 * <p>
 * Action-Klassen befinden sich immer in einem Unterpackage 
 * <code>...actions</code> und haben immer einen Namen, der mit "Action" anfängt, 
 * gefolgt vom grossgeschriebenen Namen der Action.
 * 
 * @author tbayen
 */
public class ActionDispatcherClassLoader implements ActionDispatcher {
	static Logger logger = Logger.getLogger(ActionDispatcherClassLoader.class
			.getName());

	public void executeAction(String action, HttpServletRequest req, Map root,
			WebDBDatabase db, ServletDatabase servlet) throws DatabaseException, ServletException {
		logger.debug("ActionDispatcher.executeAction('" + action + "', ...)");
		if (action.equals("nothing")) {
			return;
		}
		// richtige Action ausführen
		boolean fertig = false;
		Class cl = this.getClass();
		while (cl != Object.class && fertig == false) {
			try {
				String classname = cl.getPackage().getName()
						+ ".actions.Action"
						+ action.substring(0, 1).toUpperCase()
						+ action.substring(1);
				cl = cl.getSuperclass();
				Class actionclass = Class.forName(classname);
				Action a = ((Action) actionclass.newInstance());
				fertig = true;
				a.executeAction(this, req, root, db, servlet);
			} catch (ClassNotFoundException e) {} 
			  catch (InstantiationException e) {} 
			  catch (IllegalAccessException e) {}
		}
		if (!fertig) {
			throw new ServletException("unbekannte Action '" + action + "'");
		}
	}
}
/*
 * $Log: ActionDispatcherClassLoader.java,v $
 * Revision 1.3  2005/08/07 16:56:14  tbayen
 * Produktionsversion 1.5
 *
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:08  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/26 03:10:44  tbayen
 * Banking-Applikation kann per Chipkarte
 * Auszüge abholen und anzeigen
 *
 * Revision 1.2  2005/03/25 00:16:59  tbayen
 * Log4J konfiguriert und Logging eingerichtet
 * HBCI4Java eingebunden
 * erster Anfang der Banking-Applikation
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
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