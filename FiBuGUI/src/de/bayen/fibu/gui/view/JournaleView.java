// $Id: JournaleView.java,v 1.1 2005/08/18 11:24:11 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.control.JournaleControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.util.ApplicationException;

abstract public class JournaleView extends AbstractView {
	
	abstract protected String getTitle();
	
	abstract protected TablePart getJournale(JournaleControl control) throws ApplicationException;

	abstract protected void makeButtons(JournaleControl control);

	public void bind() throws Exception {
		final JournaleControl control = new JournaleControl(this);
		final TablePart journale = getJournale(control); 
		GUI.getView().setTitle(getTitle());
		journale.paint(this.getParent());
		makeButtons(control);
	}

	public void unbind() throws ApplicationException {
	}
}

/*
 *  $Log: JournaleView.java,v $
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
