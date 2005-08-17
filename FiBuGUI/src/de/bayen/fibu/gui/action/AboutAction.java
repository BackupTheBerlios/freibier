// $Id: AboutAction.java,v 1.1 2005/08/17 15:04:56 phormanns Exp $

package de.bayen.fibu.gui.action;

import de.bayen.fibu.gui.view.AboutView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AboutAction implements Action {

	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(AboutView.class, null);
	}

}


//
// $Log: AboutAction.java,v $
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//