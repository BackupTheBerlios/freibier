/* Erzeugt am 21.03.2005 von tbayen
 * $Id: Action.java,v 1.3 2006/01/22 19:44:24 tbayen Exp $
 */
package de.bayen.webframework;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Database;
import de.bayen.database.exception.DatabaseException;

/**
 * Klassen, die dieses Interface implementieren, können eine Action
 * ausführen, die Funktionen auf der Datenbank ausführt und/oder Daten im
 * Model-root anlegt.
 * <p>
 * Action-Klassen befinden sich immer in einem Unterpackage 
 * <code>...actions<code> und haben immer einen Namen, der mit "Action" anfängt, 
 * gefolgt vom grossgeschriebenen Namen der Action.
 * 
 * @author tbayen
 */
public interface Action {
	/**
	 * ruft die Action auf, die in dieser Klasse implementiert wurde. Wird
	 * für gewöhnlich von einem 
	 * {@link de.bayen.webframework.ActionDispatcherClassLoader} bzw. einer 
	 * Tochterklasse desselben aufgerufen.
	 * @param ad ist der <code>ActionDispatcher</code>, der mich aufgerufen hat.
	 * evtl. kann ich diesen benutzen, um eine weitere Action aufzurufen
	 * (Forwarding als Ersatz oder nach meiner eigenen Funktion).
	 * @param req Request, wie er vom Servlet empfangen wurde
	 * @param root Die Map, die mein Model enthält, das später dem view 
	 * übergeben wird
	 * @param db Datenbankverbindung
	 * @param servlet TODO
	 * 
	 * @throws DatabaseException
	 * @throws ServletException
	 */
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet) throws DatabaseException,
			ServletException;
}
/*
 * $Log: Action.java,v $
 * Revision 1.3  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
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
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */