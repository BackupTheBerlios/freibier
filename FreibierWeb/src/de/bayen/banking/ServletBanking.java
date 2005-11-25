/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ServletBanking.java,v 1.2 2005/11/25 08:59:52 tbayen Exp $
 */
package de.bayen.banking;

import java.io.IOException;
import de.bayen.database.exception.DBRuntimeException;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * HBCI-Banking-Applikation
 * 
 * @author tbayen
 */
public class ServletBanking extends ServletDatabase {
	public void init() {
		// Aus dem Tomcat heraus haben SSL-Connections einen anderen Typ als
		// sonst. Das verwirrt HBCI4Java. :-( Um dem abzuhelfen, hilft der
		// nächste Befehl, obwohl ich nicht sagen kann, was da ganz genau
		// passiert. Evtl. helfen diese beiden Webseiten beim Verständnis:
		// http://www.macromedia.com/cfusion/knowledgebase/index.cfm?id=tn_19418
		// http://forum.java.sun.com/thread.jspa?forumID=2&messageID=948150&threadID=254821
		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");
		super.init();
		// den abgeleiteten Dispatcher, damit der meine eigenen Actions findet:
		actionDispatcher = new ActionDispatcherBanking();
	}

	/**
	 * Diese Methode überlade ich, damit ich die Datenbank initialisieren
	 * kann, falls sie noch nicht existiert.
	 * @throws IOException 
	 */
	protected WebDBDatabase connectDatabase() throws DatabaseException {
		WebDBDatabase db;
		db = super.connectDatabase();
		if (db.getTableNamesList().size() == 0) {
			try {
				db.executeSqlFile("de/bayen/banking/db_definition.sql");
			} catch (IOException e) {
				throw new DBRuntimeException(
						"Kann Definitionsdatei nicht lesen", e);
			}
		}
		return db;
	}
}
/*
 * $Log: ServletBanking.java,v $
 * Revision 1.2  2005/11/25 08:59:52  tbayen
 * kleinere Verbesserungen und Fehlerabfragen
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.4  2005/04/05 21:14:11  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/26 23:07:29  tbayen
 * Auswahl mehrerer Konten für Auszug
 *
 * Revision 1.2  2005/03/26 18:52:57  tbayen
 * Kontoauszug per PinTan abgeholt
 *
 * Revision 1.1  2005/03/25 00:17:00  tbayen
 * Log4J konfiguriert und Logging eingerichtet
 * HBCI4Java eingebunden
 * erster Anfang der Banking-Applikation
 *
 * Revision 1.2  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 */