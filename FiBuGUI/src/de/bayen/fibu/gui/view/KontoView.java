// $Id: KontoView.java,v 1.1 2005/08/26 17:40:46 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.control.KontoControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;

public class KontoView extends AbstractView {

	public void bind() throws Exception {
		final KontoControl control = new KontoControl(this);
		GUI.getView().setTitle("Konto");
		LabelGroup grp = new LabelGroup(getParent(), "Konto");
		grp.addLabelPair("Kontonummer", control.getKontonummer());
		grp.addLabelPair("Bezeichnung", control.getBezeichnung());
		grp.addLabelPair("MWSt", control.getMWSt());
		grp.addLabelPair("Gewichtung", control.getGewicht());
		ButtonArea buttons = new ButtonArea(getParent(), 4);
		buttons.addButton("zurück", new Back());
		buttons.addButton("Speichern", new Action() {

			public void handleAction(Object context) throws ApplicationException {
				control.saveKonto();
			}
			
		}, getCurrentObject());
		buttons.addButton("Neues Unterkonto", new Action() {

			public void handleAction(Object context) throws ApplicationException {
				control.createKonto();
			}
			
		}, getCurrentObject());
		buttons.addButton("Buchungen", new Action() {

			public void handleAction(Object context) throws ApplicationException {
				// TODO Auto-generated method stub
			}
			
		}, getCurrentObject());
	}

	public void unbind() throws ApplicationException {
	}
}

/*
 *  $Log: KontoView.java,v $
 *  Revision 1.1  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 */
