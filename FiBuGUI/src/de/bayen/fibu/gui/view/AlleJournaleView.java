// $Id: AlleJournaleView.java,v 1.2 2005/08/23 19:40:14 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.action.OffeneJournaleAction;
import de.bayen.fibu.gui.control.JournaleControl;
import de.bayen.fibu.gui.widget.TablePart;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.util.ApplicationException;

public class AlleJournaleView extends JournaleView {

	protected void makeButtons(final JournaleControl control) {
		ButtonArea buttons = new ButtonArea(getParent(), 2);
		buttons.addButton(
				"zurück",
				new Back(),
				null, 
				true);
		buttons.addButton(
				"nur offene Journale zeigen",
				new OffeneJournaleAction(),
				getCurrentObject(), 
				false);
	}

	protected String getTitle() {
		return "Alle Journale";
	}

	protected TablePart getJournale(JournaleControl control) throws ApplicationException {
		return control.getAlleJournale();
	}
}

/*
 *  $Log: AlleJournaleView.java,v $
 *  Revision 1.2  2005/08/23 19:40:14  phormanns
 *  Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
