//$Id: ForeignKey.java,v 1.3 2005/03/03 22:32:45 phormanns Exp $

package de.jalin.freibier.database.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ein Foreign Key ist ein Fremdschluessel, der auf einen Wert in einer anderen
 * Tabelle verweist. Damit nicht bei jedem Zugriff auf einen solchen Foreign Key
 * eine Datenbankabfrage auf diese andere Tabelle geschehen muss, wird der
 * Wert, auf den verwiesen wird, bei der Datenbankabfrage mitgelesen und in
 * diesem Objekt mit abgespeichert.
 */
public class ForeignKey {
	
	protected static Log log = LogFactory.getLog(ForeignKey.class);
	
	private Object key;
	private Object content;

	public ForeignKey(Object key, Object content) {
		//log.trace(" ForeignKey Constructor: " + key + "," + content);
		this.key = key;
		this.content=content;
	}

	public void setKey(Object key) {
		this.key = key;
		// eigentlich muesste ich den referenzierten Wert hier neu lesen,
		// aber bisher wird der Datensatz eh neu gelesen.
		this.content=null;  // Der Content wird jetzt ungültig
	}

	public Object getKey() {
		return key;
	}

	public Object getContent() {
		return content;
	}
}
/*
 * $Log: ForeignKey.java,v $
 * Revision 1.3  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.2  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.5  2004/10/24 14:10:01  tbayen
 * *** empty log message ***
 *
 * Revision 1.4  2004/10/24 14:09:36  tbayen
 * todo bei setKey beseitigt, ist IMHO einstweilen unnötig
 *
 * Revision 1.3  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.2  2004/10/23 12:22:20  tbayen
 * Zugriff auf ForeignKey-Felder per getter/setter
 *
 * Revision 1.1  2004/10/23 11:35:46  tbayen
 * Verwaltung von Foreign Keys in eigenem Datentyp
 *
 */