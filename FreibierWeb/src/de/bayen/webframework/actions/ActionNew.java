/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionNew.java,v 1.4 2006/01/22 19:44:24 tbayen Exp $
 */
package de.bayen.webframework.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * TODO Klassenbeschreibung f�r die Klasse "ActionNew"
 * 
 * @author tbayen
 */
public class ActionNew implements Action {

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		Table tab = db.getTable(name);
		// TODO: Das geh�rt in die Database-Klassen:
		// Der Datensatz existiert nicht, also erzeuge ich erstmal
		// einen leeren, besorge mir dessen ID und hole mir dann
		// den neuen Datensatz.
		// TODO: ausserdem ist das ein Fall f�r eine Transaktion
		Record record = tab.getEmptyRecord();
		tab.setRecord(record);
		// TODO das geh�rt irgendwie in Database gekapselt
		Map erg = db.executeSelectSingleRow("SELECT LAST_INSERT_ID()");
		String recordid = erg.get("last_insert_id()").toString();
		// Umleitung auf andere Action
		// Ich leite auf edit um, dann kann man beim new-Aufruf noch per
		// Parameter direkt einige Felder neu f�llen. edit ruft dann show
		// auf, so dass der neue Datensatz direkt angezeigt werden kann.
		uri.put("id", recordid);
		ad.executeAction("edit", req, root, db, servlet);
	}
}

/*
 * $Log: ActionNew.java,v $
 * Revision 1.4  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unn�tig, wurde gel�scht
 *
 * Revision 1.3  2005/08/07 16:56:13  tbayen
 * Produktionsversion 1.5
 *
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:10  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.1  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette �berarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */