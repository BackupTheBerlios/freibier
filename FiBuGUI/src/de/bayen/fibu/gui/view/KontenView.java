// $Id: KontenView.java,v 1.2 2005/08/26 17:40:46 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.control.KontenControl;
import de.bayen.fibu.gui.widget.TreePart;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class KontenView extends AbstractView {

	public void bind() throws Exception {
		KontenControl control = new KontenControl(this);
		GUI.getView().setTitle("Kontenhierarchie");
		TreePart ktoBaum = control.getKontenBaum();
		ktoBaum.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}
}

/*
 *  $Log: KontenView.java,v $
 *  Revision 1.2  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
