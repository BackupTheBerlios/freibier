/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ServletTBayen.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $
 */
package de.bayen.kontaktdaten;

import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ServletTBayen"
 * 
 * @author tbayen
 */
public class ServletTBayen extends ServletDatabase {
	public void init() {
		super.init();
		// den abgeleiteten Dispatcher, damit der meine eigenen Actions findet:
//		actionDispatcher = new ActionDispatcherBanking();
	}

	/**
	 * Diese Methode überlade ich, damit ich die Datenbank initialisieren
	 * kann, falls sie noch nicht existiert.
	 */
	protected WebDBDatabase connectDatabase() throws DatabaseException {
		WebDBDatabase db;
		db = super.connectDatabase();
		if (db.getTableNamesList().size() == 0) {
			db.executeSqlFile("de/bayen/tbayen/db_definition.sql");
		}
		return db;
	}
	
}

/*
 * $Log: ServletTBayen.java,v $
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:11  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/25 00:17:00  tbayen
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