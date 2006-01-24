/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ServletBanking.java,v 1.1 2006/01/24 00:26:00 tbayen Exp $
 */
package de.bayen.banking;

import de.bayen.webframework.ServletDatabase;

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
	}

}
/*
 * $Log: ServletBanking.java,v $
 * Revision 1.1  2006/01/24 00:26:00  tbayen
 * Erste eigenständige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
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