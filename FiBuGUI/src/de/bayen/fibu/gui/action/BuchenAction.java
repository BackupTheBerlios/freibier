// $Id: BuchenAction.java,v 1.1 2005/08/18 11:24:11 phormanns Exp $
package de.bayen.fibu.gui.action;

import de.bayen.fibu.gui.view.BuchenView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class BuchenAction implements Action {
	
	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(BuchenView.class, null);
	}
}

/*
 *  $Log: BuchenAction.java,v $
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
