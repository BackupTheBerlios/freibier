/* Erzeugt am 18.08.2005 von tbayen
 * $Id: ObjectWrapper.java,v 1.3 2005/08/21 20:18:09 phormanns Exp $
 */
package de.bayen.fibu;

import java.rmi.RemoteException;
import de.willuhn.datasource.GenericObject;

public class ObjectWrapper implements GenericObject {
	
	private de.bayen.fibu.GenericObject gobject;

	public ObjectWrapper(de.bayen.fibu.GenericObject gobject){
		this.gobject = gobject;
	}

	protected de.bayen.fibu.GenericObject getGObject() {
		return gobject;
	}
	
	public boolean equals(GenericObject genObj) throws RemoteException {
		return gobject.equals(((ObjectWrapper) genObj).getGObject());
	}

	public Object getAttribute(String property) throws RemoteException {
		return gobject.getAttribute(property);
	}

	public String[] getAttributeNames() throws RemoteException {
		return gobject.getAttributeNames();
	}

	public String getID() throws RemoteException {
		return gobject.getGOID();
	}

	public String getPrimaryAttribute() throws RemoteException {
		return gobject.getPrimaryAttribute();
	}
	
}

/*
 * $Log: ObjectWrapper.java,v $
 * Revision 1.3  2005/08/21 20:18:09  phormanns
 * Erste Widgets für Buchen-Dialog
 *
 * Revision 1.1  2005/08/18 17:45:23  tbayen
 * generischer Wrapper für FiBu-Objekte
 *
 */