// $Id: AbschlussAction.java,v 1.1 2005/08/18 11:24:11 phormanns Exp $
package de.bayen.fibu.gui.action;

import de.bayen.fibu.gui.view.AbschlussView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AbschlussAction implements Action {

	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(AbschlussView.class, null);
	}
}

/*
 *  $Log: AbschlussAction.java,v $
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
