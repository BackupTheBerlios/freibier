/* Erzeugt am 18.08.2005 von tbayen
 * $Id: ObjectWrapper.java,v 1.3 2005/08/26 20:48:47 phormanns Exp $
 */
package de.bayen.fibu.gui.data;

import de.bayen.fibu.GenericObject;


public class ObjectWrapper implements GenericObject {
	
	private GenericObject gobject;

	public ObjectWrapper(GenericObject gobject){
		this.gobject = gobject;
	}

	public GenericObject getGObject() {
		return gobject;
	}
	
	public boolean equals(GenericObject genObj) {
		return gobject.equals(((ObjectWrapper) genObj).getGObject());
	}

	public Object getAttribute(String property) {
		return gobject.getAttribute(property);
	}

	public String[] getAttributeNames() {
		return gobject.getAttributeNames();
	}

	public String getGOID() {
		return gobject.getGOID();
	}

	public String getPrimaryAttribute() {
		return gobject.getPrimaryAttribute();
	}
	
	public String toString() {
		return gobject.toString();
	}
}

/*
 * $Log: ObjectWrapper.java,v $
 * Revision 1.3  2005/08/26 20:48:47  phormanns
 * Erste Buchung in der Datenbank
 *
 * Revision 1.2  2005/08/26 19:19:44  phormanns
 * Hierarchie-Auswahl für erstes Konto im Buchungsdialog
 *
 * Revision 1.1  2005/08/23 19:56:05  phormanns
 * Neues Paket data für Datenobjekte
 *
 * Revision 1.4  2005/08/23 19:40:15  phormanns
 * Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 * Revision 1.3  2005/08/21 20:18:09  phormanns
 * Erste Widgets für Buchen-Dialog
 *
 * Revision 1.1  2005/08/18 17:45:23  tbayen
 * generischer Wrapper für FiBu-Objekte
 *
 */