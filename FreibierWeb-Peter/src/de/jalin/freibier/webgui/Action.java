// $Id: Action.java,v 1.2 2005/02/25 15:51:29 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import de.jalin.freibier.database.exception.DatabaseException;

public interface Action {
	
	public abstract Map performAction(HttpServletRequest request, DialogState state) throws DatabaseException;
}

/*
 *  $Log: Action.java,v $
 *  Revision 1.2  2005/02/25 15:51:29  phormanns
 *  EditAction, OrderByAction
 *
 *  Revision 1.1  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 */
