/* Erzeugt am 18.08.2005 von tbayen
 * $Id: ObjectWrapper.java,v 1.1 2005/08/18 17:45:23 tbayen Exp $
 */
package de.bayen.fibu;

import java.rmi.RemoteException;
import de.bayen.database.exception.DatabaseException;
import de.willuhn.datasource.GenericObject;

public class ObjectWrapper implements GenericObject {
	
	private de.bayen.fibu.GenericObject gobject;

	public ObjectWrapper(de.bayen.fibu.GenericObject gobject){
		this.gobject = gobject;
	}

	protected de.bayen.fibu.GenericObject getGObject() {
		return gobject;
	}
	
	public boolean equals(GenericObject arg) throws RemoteException {
		try {
			return gobject.equals(((ObjectWrapper) arg).getGObject());
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim generischen Datenbankzugriff", e);
		}
	}

	public Object getAttribute(String arg) throws RemoteException {
		try {
			return gobject.getAttribute(arg);
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim generischen Datenbankzugriff", e);
		}
	}

	public String[] getAttributeNames() throws RemoteException {
		try {
			return gobject.getAttributeNames();
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim generischen Datenbankzugriff", e);
		}
	}

	public String getID() throws RemoteException {
		try {
			return gobject.getGOID();
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim generischen Datenbankzugriff", e);
		}
	}

	public String getPrimaryAttribute() throws RemoteException {
		try {
			return gobject.getPrimaryAttribute();
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim generischen Datenbankzugriff", e);
		}
	}
	
}

/*
 * $Log: ObjectWrapper.java,v $
 * Revision 1.1  2005/08/18 17:45:23  tbayen
 * generischer Wrapper für FiBu-Objekte
 *
 */