// $Id: OffenesJournalMenu.java,v 1.1 2005/08/18 11:24:12 phormanns Exp $
package de.bayen.fibu.gui.menu;

import de.bayen.fibu.gui.action.BuchenAction;
import de.bayen.fibu.gui.action.BuchungenAction;
import de.bayen.fibu.gui.action.JournalAbsummierenAction;
import de.bayen.fibu.gui.action.JournalLoeschenAction;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;

public class OffenesJournalMenu extends ContextMenu {

	public OffenesJournalMenu() {
		addItem(new ContextMenuItem("buchen", new BuchenAction()));
		addItem(new ContextMenuItem("zeigen", new BuchungenAction()));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new ContextMenuItem("löschen", new JournalLoeschenAction()));
		addItem(new ContextMenuItem("absummieren", new JournalAbsummierenAction()));
	}
}

/*
 *  $Log: OffenesJournalMenu.java,v $
 *  Revision 1.1  2005/08/18 11:24:12  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
