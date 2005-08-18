// $Id: OffeneJournaleView.java,v 1.1 2005/08/18 11:24:11 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.action.ActionJournalBuchen;
import de.bayen.fibu.gui.action.AlleJournaleAction;
import de.bayen.fibu.gui.control.JournaleControl;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.parts.TablePart;
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
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
