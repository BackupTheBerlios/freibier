// $Id: GenericIterator.java,v 1.1 2005/08/23 19:56:05 phormanns Exp $
package de.bayen.fibu.gui.data;

import de.bayen.fibu.GenericObject;
import de.willuhn.util.ApplicationException;

public interface GenericIterator {

	public void begin() throws ApplicationException;

	public boolean hasNext();

	public GenericObject next();
}

/*
 *  $Log: GenericIterator.java,v $
 *  Revision 1.1  2005/08/23 19:56:05  phormanns
 *  Neues Paket data f�r Datenobjekte
 *
 *  Revision 1.1  2005/08/23 19:40:15  phormanns
 *  Abh�ngigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 */
