/* Erzeugt am 20.02.2005 von tbayen
 * $Id: WebDBDatabase.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.webframework;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import de.bayen.database.Database;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;

/**
 * Wrapper um die de.bayen.database-Klasse, der im Servlet besser funktioniert
 * 
 * @author tbayen
 */
public class WebDBDatabase extends Database implements
		HttpSessionBindingListener {
	public WebDBDatabase(String name, String server, String user,
			String password) throws DatabaseException {
		super(name, server, user, password);
	}

	/*
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {}

	/*
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		try {
			close();
		} catch (SystemDatabaseException e) {
			//log.error("Fehler beim Schliessen der Datenbank",e);
		}
	}
}
/*
 * $Log: WebDBDatabase.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:08  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.2  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/23 11:40:58  tbayen
 * recht taugliche Version mit Authentifizierung und
 * Trennung von allgem. und applik.-spezifischen Dingen
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 */