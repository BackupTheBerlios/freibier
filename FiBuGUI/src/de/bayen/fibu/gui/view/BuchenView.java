// $Id: BuchenView.java,v 1.5 2005/08/26 20:48:47 phormanns Exp $
package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.control.BuchenControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;

public class BuchenView extends AbstractView {

	public void bind() throws Exception {
		final BuchenControl control = new BuchenControl(this);
		GUI.getView().setTitle("Buchen");
		LabelGroup grpJournal = new LabelGroup(getParent(), "Journal");
		grpJournal.addLabelPair("Journalnummer", control.getJournalnummer());
		grpJournal.addLabelPair("Startdatum", control.getStartdatum());
		LabelGroup grpBuchung = new LabelGroup(getParent(), "Buchung");
		grpBuchung.addLabelPair("Belegnummer", control.getBelegnummer());
		grpBuchung.addLabelPair("Buchungstext", control.getBuchungstext());
		grpBuchung.addLabelPair("Erfassungsdatum", control.getErfassungsdatum());
		grpBuchung.addLabelPair("Valutadatum", control.getValutadatum());
		LabelGroup grpZeile1 = new LabelGroup(getParent(), "Konto 1");
		grpZeile1.addLabelPair("Konto", control.getKonto1());
		grpZeile1.addLabelPair("Betrag", control.getBetrag1());
		LabelGroup grpZeile2 = new LabelGroup(getParent(), "Konto 2");
		grpZeile2.addLabelPair("Konto", control.getKonto2());
		grpZeile2.addLabelPair("Betrag", control.getBetrag2());
		LabelGroup grpZeile3 = new LabelGroup(getParent(), "Konto 3");
		grpZeile3.addLabelPair("Konto", control.getKonto3());
		grpZeile3.addLabelPair("Betrag", control.getBetrag3());
		ButtonArea buttons = new ButtonArea(getParent(), 2);
		buttons.addButton("zurück", new Back());
		buttons.addButton("speichern", new Action() {

			public void handleAction(Object context) throws ApplicationException {
				control.speichereBuchung();
			}});
	}

	public void unbind() throws ApplicationException {
	}
}

/*
 *  $Log: BuchenView.java,v $
 *  Revision 1.5  2005/08/26 20:48:47  phormanns
 *  Erste Buchung in der Datenbank
 *
 *  Revision 1.4  2005/08/21 20:18:56  phormanns
 *  Erste Widgets für Buchen-Dialog
 *
 *  Revision 1.3  2005/08/18 19:06:28  tbayen
 *  Buchungsfelder in Buchungsdialog
 *
 *  Revision 1.2  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/08/18 11:24:11  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
