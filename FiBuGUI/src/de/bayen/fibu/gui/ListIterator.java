// $Id: ListIterator.java,v 1.1 2005/08/21 20:18:56 phormanns Exp $
package de.bayen.fibu.gui;

import java.rmi.RemoteException;
import java.util.List;
import de.bayen.fibu.ObjectWrapper;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.util.ApplicationException;

public abstract class ListIterator implements GenericIterator {
	private List genObjList;
	private int idx;

	public ListIterator() {
		this.genObjList = null;
		this.idx = 0;
	}

	public abstract List reloadList() throws ApplicationException;

	public boolean hasNext() throws RemoteException {
		return idx < size();
	}

	public GenericObject next() throws RemoteException {
		GenericObject jrnl = new ObjectWrapper((de.bayen.fibu.GenericObject) genObjList.get(idx));
		idx++;
		return jrnl;
	}

	public GenericObject previous() throws RemoteException {
		if (idx > 0)
			idx--;
		return new ObjectWrapper((de.bayen.fibu.GenericObject) genObjList.get(idx));
	}

	public void begin() throws RemoteException {
		try {
			genObjList = reloadList();
		} catch (ApplicationException e) {
			throw new RemoteException("Fehler beim Laden der Tabelle", e);
		}
		idx = 0;
	}

	public int size() throws RemoteException {
		return genObjList != null ? genObjList.size() : 0;
	}

	public GenericObject contains(GenericObject arg0) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
/*
 *  $Log: ListIterator.java,v $
 *  Revision 1.1  2005/08/21 20:18:56  phormanns
 *  Erste Widgets für Buchen-Dialog
 *
 */
