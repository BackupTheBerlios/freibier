/* Erzeugt am 19.03.2005 von tbayen
 * $Id: URIParser.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.webframework;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Ein URIParser stellt eine Methode zur Verf�gung, um grundlegende Informationen
 * �ber den aktuellen Request (wie z.B. die vom Benutzer angefragte Action) aus 
 * der URI, die der Benutzer �bergeben hat, zu extrahieren.
 * 
 * Dieses Interface ist nat�rlich eng damit verbunden, wie Informationen in einem
 * HTML-Dokument kodiert werden. Daher muss man bei einer eigenen Implementation
 * dieser Klasse evtl. die Templates neu implementieren, die URIs erzeugen
 * (obwohl ich versucht habe, das zu vermeiden).
 *
 * Falls jemand Informationen als Parameter an die URI anh�ngen will, ist zu
 * beachten, da� das bei Formularaufrufen per POST-Methode nicht geht, weil
 * die Parameter, die in der form-URI stehen, einfach vom Browser ignoriert
 * (d.h. gel�scht) werden.
 *  
 * @author tbayen
 */
public interface URIParser {
	/*
	 * Die Daten (theme, view, action, table, id, etc.) werden aus dem Request 
	 * extrahiert und dann in einen Hash geschrieben.
	 * 
	 * @param req
	 * @return Map enth�lt Strings
	 */
	public Map parseURI(HttpServletRequest req);
	/**
	 * Diese Funktion erzeugt aus den vorhandenen URI-Daten wieder einen
	 * String. Dabei k�nnen zwei Maps �bergeben werden: Eine, die die originalen
	 * Werte enth�lt und eine, die nur �nderungen enth�lt.

	 * @param original - Map, die die Original-URI-Daten enth�lt, die diesen 
	 * Request ausgel�st haben
	 * @param changes - Map, die nur �nderungen in obiger Map enth�lt
	 * @param params - Map, die die als Query-Parameter zu �bergebenden Werte 
	 * enth�lt
	 * @return String kann als href-Link in ein HTML-Dokument geschrieben werden
	 */
	public String createURI(Map original, Map changes, Map params);
}

/*
 * $Log: URIParser.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:08  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette �berarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */