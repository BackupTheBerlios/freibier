// $Id: StammdatenView.java,v 1.2 2005/08/18 11:24:11 phormanns Exp $

package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.control.StammdatenControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;

public class StammdatenView extends AbstractView {

	public void bind() throws Exception {
		final StammdatenControl control = new StammdatenControl(this);
		GUI.getView().setTitle("Firma");
		LabelGroup group = new LabelGroup(getParent(), "Stammdaten");
		group.addLabelPair("Firma", control.getFirma());
		group.addLabelPair("Bilanzkonto", control.getBilanzkonto());
		group.addLabelPair("Jahr aktuell", control.getJahr());
		group.addLabelPair("Periode aktuell", control.getPeriode());
		ButtonArea buttons = new ButtonArea(getParent(), 1);
		buttons.addButton(
			"Stammdaten speichern", 
			new Action() {
				public void handleAction(Object context) throws ApplicationException {
					control.save();
				}
			}, 
			getCurrentObject(), 
			true);
	}

	public void unbind() throws ApplicationException {
	}

}


//
// $Log: StammdatenView.java,v $
// Revision 1.2  2005/08/18 11:24:11  phormanns
// Neue FiBu Version von Thomas
// Anzeige Journal-Listen
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//