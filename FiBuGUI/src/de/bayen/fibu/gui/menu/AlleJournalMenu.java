// $Id: AlleJournalMenu.java,v 1.2 2005/08/23 19:40:15 phormanns Exp $
package de.bayen.fibu.gui.menu;

import de.bayen.fibu.gui.action.BuchungenAction;
import de.bayen.fibu.gui.widget.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;

public class AlleJournalMenu extends ContextMenu {

	public AlleJournalMenu() {
		addItem(new ContextMenuItem("zeigen", new BuchungenAction()));
	}
}

/*
 *  $Log: AlleJournalMenu.java,v $
 *  Revision 1.2  2005/08/23 19:40:15  phormanns
 *  Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.1  2005/08/18 11:24:12  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
