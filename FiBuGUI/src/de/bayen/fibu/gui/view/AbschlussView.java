// $Id: AbschlussView.java,v 1.1 2005/08/18 11:24:11 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AbschlussView extends AbstractView {

	public void bind() throws Exception {
		GUI.getView().setTitle("Abschlu�");
	}

	public void unbind() throws ApplicationException {
	}
}

/*
 *  $Log: AbschlussView.java,v $
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
