/**********************************************************************
 * $Source: /home/xubuntu/berlios_backup/github/tmp-cvs/freibier/Repository/FiBuGUI/src/de/bayen/fibu/gui/widget/TableChangeListener.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/08/26 17:40:46 $
 * $Author: phormanns $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.bayen.fibu.gui.widget;

import de.bayen.fibu.GenericObject;
import de.willuhn.util.ApplicationException;

/**
 * Ein Listener, der an eine Tabelle via <code>addChangeListener()</code> gehaengt werden kann, wenn einzelne
 * Spalten direkt in der Tabelle aenderbar sein sollen. Die Spalten muessen
 * mit der Funktion <code>table.addColumn(String title, String field, Formatter f, boolean changeable)</code>
 * hinzugefuegt werden, wobei changeable=true sein muss um die Spalten als aenderbar
 * zu markieren. Sofern der Wert eines solchen Feldes vom Benutzer geaendert
 * wurde, werden alle registrieren TableChangeListener ueber die Aenderung
 * informiert. Es ist dann deren Aufgabe, den geaenderten Wert im Fachobjekt
 * zu uebernehmen.  
 */
public interface TableChangeListener {
	/**
	 * Wird aufgerufen, wenn der Wert eines Feldes geaendert wurde.
	 * @param object das zugehoerige Fachobjekt.
	 * @param attribute der Name des geaenderten Attributes.
	 * @param newValue der neue Wert des Attributes.
	 * @throws ApplicationException
	 */
	public void itemChanged(GenericObject object, String attribute,
			String newValue) throws ApplicationException;
}
/*********************************************************************
 * $Log: TableChangeListener.java,v $
 * Revision 1.2  2005/08/26 17:40:46  phormanns
 * Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 * Revision 1.1  2005/08/23 19:40:14  phormanns
 * Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 * Revision 1.1  2005/06/29 16:54:38  web0
 * @N editierbare Tabellen
 *
 *********************************************************************/
