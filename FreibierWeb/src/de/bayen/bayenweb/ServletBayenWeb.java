/* Erzeugt am 23.02.2005 von tbayen
 * $Id: ServletBayenWeb.java,v 1.2 2005/11/25 08:59:52 tbayen Exp $
 */
package de.bayen.bayenweb;

import java.io.IOException;
import de.bayen.database.exception.DBRuntimeException;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Diese Klasse erzeugt die Datenbankapplikation der Firma Bayen
 * 
 * Zum einen können hier bestimmte Methoden überladen sein, zum anderen
 * definiert sie auch durch das Package, in dem sie sich befindet, den
 * Ort, an dem sich z.B. die Datenbankdefinitions-Dateien befinden.
 */
public class ServletBayenWeb extends ServletDatabase {
	public void init() {
		super.init();
		// den abgeleiteten Dispatcher, damit der meine eigenen Actions findet:
		actionDispatcher = new ActionDispatcherBayen();
	}

	/**
	 * Diese Methode überlade ich, damit ich die Datenbank initialisieren
	 * kann, falls sie noch nicht existiert.
	 */
	protected WebDBDatabase connectDatabase() throws DatabaseException {
		WebDBDatabase db;
		db = super.connectDatabase();
		if (db.getTableNamesList().size() == 0) {
			try {
				db.executeSqlFile("de/bayen/bayenweb/db_definition.sql");
			} catch (IOException e) {
				throw new DBRuntimeException(
						"Kann Definitionsdatei nicht lesen", e);
			}
		}
		return db;
	}
}
/*
 * $Log: ServletBayenWeb.java,v $
 * Revision 1.2  2005/11/25 08:59:52  tbayen
 * kleinere Verbesserungen und Fehlerabfragen
 *
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.6  2005/04/05 21:14:11  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.5  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.4  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 * Revision 1.3  2005/03/02 18:02:56  tbayen
 * Probleme mit leeren Tabellen behoben
 * Einige Änderungen wie mit Günther besprochen
 *
 * Revision 1.2  2005/02/28 01:28:48  tbayen
 * Import-Funktion eingebaut
 * Diese Version erstmalig auf dem Produktionsserver installiert
 *
 * Revision 1.1  2005/02/23 11:40:58  tbayen
 * recht taugliche Version mit Authentifizierung und
 * Trennung von allgem. und applik.-spezifischen Dingen
 *
 */