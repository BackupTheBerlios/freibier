// $Id: OffeneJournaleView.java,v 1.4 2005/08/23 19:40:14 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.action.AlleJournaleAction;
import de.bayen.fibu.gui.control.JournaleControl;
import de.bayen.fibu.gui.widget.TablePart;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.util.ApplicationException;

public class OffeneJournaleView extends JournaleView {

	protected void makeButtons(final JournaleControl control) {
		ButtonArea buttons = new ButtonArea(getParent(), 3);
		buttons.addButton(
				"zurück",
				new Back(),
				null, 
				true);
		buttons.addButton(
				"neues Journal starten",
				new Action() {
					public void handleAction(Object context) throws ApplicationException {
						control.createJournal();
					}},
				null, 
				false);
		buttons.addButton(
				"alle Journale zeigen",
				new AlleJournaleAction(),
				getCurrentObject(), 
				false);
	}

	protected String getTitle() {
		return "Offene Journale";
	}

	protected TablePart getJournale(JournaleControl control) throws ApplicationException {
		return control.getOffeneJournale();
	}
}

/*
 *  $Log: OffeneJournaleView.java,v $
 *  Revision 1.4  2005/08/23 19:40:14  phormanns
 *  Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.3  2005/08/21 17:11:11  tbayen
 *  kleinere Warnungen beseitigt
 *
 *  Revision 1.2  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
