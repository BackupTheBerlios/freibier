// $Id: OffenesJournalMenu.java,v 1.2 2005/08/23 19:40:15 phormanns Exp $
package de.bayen.fibu.gui.menu;

import de.bayen.fibu.gui.action.BuchenAction;
import de.bayen.fibu.gui.action.BuchungenAction;
import de.bayen.fibu.gui.action.JournalAbsummierenAction;
import de.bayen.fibu.gui.action.JournalLoeschenAction;
import de.bayen.fibu.gui.widget.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;

public class OffenesJournalMenu extends ContextMenu {

	public OffenesJournalMenu() {
		addItem(new ContextMenuItem("buchen", new BuchenAction()));
		addItem(new ContextMenuItem("zeigen", new BuchungenAction()));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new ContextMenuItem("l�schen", new JournalLoeschenAction()));
		addItem(new ContextMenuItem("absummieren", new JournalAbsummierenAction()));
	}
}

/*
 *  $Log: OffenesJournalMenu.java,v $
 *  Revision 1.2  2005/08/23 19:40:15  phormanns
 *  Abh�ngigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.1  2005/08/18 11:24:12  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
