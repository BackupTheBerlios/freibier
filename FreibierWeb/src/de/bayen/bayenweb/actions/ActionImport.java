/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionImport.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $
 */
package de.bayen.bayenweb.actions;

import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.WebDBDatabase;

/**
 * TODO Klassenbeschreibung für die Klasse "ActionImport"
 * 
 * @author tbayen
 */
public class ActionImport implements Action {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db) throws DatabaseException,
			ServletException {
		db
				.executeUpdate("INSERT INTO kontaktdaten.Kunden("
						+ "Kundennr,Durstname,Betreiber,Objektname,Strasse,PLZ,Ort,Betreuer,"
						+ "Liefertag,Anruftag,Betriebstyp,Bierkuehlung,Lieferstatus "
						+ ") "
						+ "SELECT KundenNr, Bezeichnung, Name1, Name2, Strasse, Plz, Ort, VertreterNr,"
						+ " 1, 1, 1, 1, 1 " + "FROM durstimport.Kunden1 "
						+ "WHERE durstimport.Kunden1.KundenNr >10000 "
						+ "AND durstimport.Kunden1.KundenNr <40000 "
						+ "AND durstimport.Kunden1.VertreterNr = 61");
		db
				.executeUpdate("UPDATE kontaktdaten.Kunden,durstimport.Kunden2 "
						+ "SET kontaktdaten.Kunden.Steuernr=durstimport.Kunden2.realUID "
						+ "WHERE kontaktdaten.Kunden.Kundennr=durstimport.Kunden2.KundenNum");
		db
				.executeUpdate("INSERT INTO kontaktdaten.DurstTelefonliste (Telefon, Bemerkung, Kunde) "
						+ "SELECT DTelefon.TelefonNr, DTelefon.Ansprechp, Kunden.id "
						+ "FROM durstimport.DTelefon, kontaktdaten.Kunden "
						+ "WHERE durstimport.DTelefon.NumAdresse LIKE CONCAT('%', kontaktdaten.Kunden.Kundennr)");
	}
}
/*
 * $Log: ActionImport.java,v $
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.4  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.2  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */