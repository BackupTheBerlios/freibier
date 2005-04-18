/* Erzeugt am 19.03.2005 von tbayen
 * $Id: ActionDispatcher.java,v 1.2 2005/04/18 10:57:55 tbayen Exp $
 */
package de.bayen.webframework;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.exception.DatabaseException;

/**
 * Ein ActionDispatcher ruft zu einer Action (die als String angegeben wird)
 * die passende Methode auf. Diese Methode kann am einfachsten innerhalb des
 * ActionDispatchers selber implementiert werden, man kann aber auch z.B. einen
 * komplexen Mechanismus implementieren, der die entsprechende Methode bzw.
 * deren Klasse erst sucht, lädt und dann aufruft 
 * ({@link de.bayen.webframework.ActionDispatcherClassLoader}).
 * 
 * @author tbayen
 */
public interface ActionDispatcher {
	/**
	 * Diese Methode wird vom Servlet aufgerufen, wenn die Analyse der URI
	 * ergibt, daß eine Action aufgerufen werden soll. Sie sorgt dann dafür,
	 * daß die Action auch ausgeführt wird. Das Ergebnis der Action kann ein
	 * Seiteneffekt sein (z.B. Änderungen in der Datenbank), kann aber auch
	 * eine Ausgabe bzw. Änderung im Model (in der <code>Map root</code>) 
	 * sein.
	 * @param action
	 * @param req
	 * @param root
	 * @param db
	 * @param servlet TODO
	 * 
	 * @throws DatabaseException
	 * @throws ServletException
	 */
	public void executeAction(String action, HttpServletRequest req, Map root,
			WebDBDatabase db, ServletDatabase servlet) throws DatabaseException, ServletException;
}

/*
 * $Log: ActionDispatcher.java,v $
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