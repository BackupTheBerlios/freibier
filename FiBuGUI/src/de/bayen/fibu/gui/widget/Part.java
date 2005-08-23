/**********************************************************************
 * $Source: /home/xubuntu/berlios_backup/github/tmp-cvs/freibier/Repository/FiBuGUI/src/de/bayen/fibu/gui/widget/Part.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/23 19:40:14 $
 * $Author: phormanns $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.bayen.fibu.gui.widget;

import org.eclipse.swt.widgets.Composite;
import de.willuhn.util.ApplicationException;

/**
 * Generische GUI-Komponente.
 * Der primaere Vorteil gegenueber einem herkoemmlichen
 * SWT-Widget liegt darin, dass die Komponente vollstaendig
 * erzeugt werden kann noch bevor es gemalt wird. Hae? Alle
 * SWT-Widgets verlangen im Konstruktor ein Parent (in der Regel
 * ein Composite). Das heisst, man muss zum Zeitpunkt der Instanziierung
 * bereits wissen, <b>wo</b> das Widget hingemalt werden soll.
 * Dies ist hier nicht der Fall. Der <b>Part</b> wird erst gemalt,
 * wenn dessen paint-Methode aufgerufen wird.
 */
public interface Part {

	/**
	 * Malt die Komponente in das angegebene Composite.
   * @param parent das Composite.
	 * @throws RemoteException
   */
  public void paint(Composite parent) throws ApplicationException;

}


/**********************************************************************
 * $Log: Part.java,v $
 * Revision 1.1  2005/08/23 19:40:14  phormanns
 * Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 * Revision 1.3  2005/03/09 01:06:36  web0
 * @D javadoc fixes
 *
 * Revision 1.2  2004/11/10 15:53:23  willuhn
 * @N Panel
 *
 * Revision 1.1  2004/07/09 00:12:47  willuhn
 * @C Redesign
 *
 * Revision 1.1  2004/04/12 19:15:58  willuhn
 * @C refactoring
 * @N forms
 *
 **********************************************************************/