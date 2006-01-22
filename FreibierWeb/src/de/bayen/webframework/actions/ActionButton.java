/* Erzeugt am 24.04.2005 von tbayen
 * $Id: ActionButton.java,v 1.2 2006/01/22 19:44:24 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.database.Database;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ActionDispatcherClassLoader;
import de.bayen.webframework.ServletDatabase;

/**
 * Die Button-Action bearbeitet Buttonklicks, die dann auf eine angegebene
 * Action weitergeleitet werden.
 * <p>
 * Wenn in einem Formular mehrere Buttons mit unterschiedlichen Actions
 * existieren, kann die Action nicht in der URL angegeben werden, da diese
 * nur einmal (im <form>-Tag) steht. In diesem Falle steht in dieser URL
 * die Button-Action. Die eigentlichen Buttons bekommen dann als Name
 * "Action" und als Value die eigentliche Action.
 * 
 * @author tbayen
 */
public class ActionButton implements Action {
	static Logger logger = Logger.getLogger(ActionDispatcherClassLoader.class
			.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		String realaction=req.getParameter("action");
		if(realaction==null){
			logger.error("Button-Action ohne Angabe der wirklichen Action");
		}
		logger.debug("Button-Action mit Ziel "+realaction);
		// Umleitung auf andere Action
//		ad.executeAction("show", req, root, db, servlet);
		// TODO Methode schreiben
	}
}

/*
 * $Log: ActionButton.java,v $
 * Revision 1.2  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.1  2005/08/07 16:56:13  tbayen
 * Produktionsversion 1.5
 *
 */