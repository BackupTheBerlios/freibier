/* Erzeugt am 23.10.2004 von tbayen
 * $Id: ForeignKey.java,v 1.1 2005/08/07 21:18:49 tbayen Exp $
 */
package de.bayen.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ein Foreign Key ist ein Fremdschlüssel, der auf einen Wert in einer anderen
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
		// eigentlich müsste ich den referenzierten Wert hier neu lesen,
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
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
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