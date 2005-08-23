// $Id: ListIterator.java,v 1.1 2005/08/23 19:56:05 phormanns Exp $
package de.bayen.fibu.gui.data;

import java.util.List;
import de.bayen.fibu.GenericObject;
import de.willuhn.util.ApplicationException;

public abstract class ListIterator implements GenericIterator {
	
	private List genObjList;
	private int idx;

	public ListIterator() {
		this.genObjList = null;
		this.idx = 0;
	}

	public abstract List reloadList() throws ApplicationException;

	public boolean hasNext() {
		return idx < size();
	}

	public GenericObject next() {
		GenericObject jrnl = new ObjectWrapper((GenericObject) genObjList.get(idx));
		idx++;
		return jrnl;
	}

	public void begin() throws ApplicationException {
		genObjList = reloadList();
		idx = 0;
	}

	public int size() {
		return genObjList != null ? genObjList.size() : 0;
	}

}
/*
 *  $Log: ListIterator.java,v $
 *  Revision 1.1  2005/08/23 19:56:05  phormanns
 *  Neues Paket data für Datenobjekte
 *
 *  Revision 1.2  2005/08/23 19:40:14  phormanns
 *  Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.1  2005/08/21 20:18:56  phormanns
 *  Erste Widgets für Buchen-Dialog
 *
 */
