/* Erzeugt am 21.03.2005 von tbayen
 * $Id: ActionDispatcherBanking.java,v 1.1 2006/01/24 00:26:00 tbayen Exp $
 */
package de.bayen.banking;

import de.bayen.webframework.ActionDispatcherClassLoader;

/**
 * Die Basisklasse wird hier nur abgeleitet, damit sie die Actions findet,
 * die in meinem eigenen Paket stehen.
 * 
 * @author tbayen
 */
public class ActionDispatcherBanking extends ActionDispatcherClassLoader {}

/*
 * $Log: ActionDispatcherBanking.java,v $
 * Revision 1.1  2006/01/24 00:26:00  tbayen
 * Erste eigenständige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.2  2005/04/05 21:14:11  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.1  2005/03/25 00:17:00  tbayen
 * Log4J konfiguriert und Logging eingerichtet
 * HBCI4Java eingebunden
 * erster Anfang der Banking-Applikation
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
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