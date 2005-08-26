// $Id: GenericObjectNode.java,v 1.1 2005/08/26 17:40:46 phormanns Exp $
package de.bayen.fibu.gui.data;

import de.bayen.fibu.GenericObject;
import de.willuhn.util.ApplicationException;

public interface GenericObjectNode extends GenericObject {

	public GenericIterator getChildren() throws ApplicationException;

}

/*
 *  $Log: GenericObjectNode.java,v $
 *  Revision 1.1  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 */
