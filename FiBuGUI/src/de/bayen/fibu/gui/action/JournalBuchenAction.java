// $Id: JournalBuchenAction.java,v 1.1 2005/08/18 14:08:13 phormanns Exp $
package de.bayen.fibu.gui.action;

import de.bayen.fibu.gui.view.BuchenView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class JournalBuchenAction implements Action {

	public void handleAction(Object context) throws ApplicationException {
		if (context != null) {
			GUI.startView(BuchenView.class, context);
		}
	}
}

/*
 *  $Log: JournalBuchenAction.java,v $
 *  Revision 1.1  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
