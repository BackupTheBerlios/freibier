// $Id: BuchenControl.java,v 1.2 2005/08/18 17:45:48 tbayen Exp $
package de.bayen.fibu.gui.control;

import java.rmi.RemoteException;
import java.util.Date;
import de.bayen.fibu.gui.Settings;
import de.willuhn.datasource.GenericObject;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.util.ApplicationException;

public class BuchenControl extends AbstractControl {

	private AbstractView view;
	private LabelInput journalnummer;
	private LabelInput startdatum;
	
	public BuchenControl(AbstractView view) {
		super(view);
		this.view = view;
	}

	public Input getJournalnummer() throws ApplicationException {
		if (journalnummer == null) {
			try {
				journalnummer = 
					new LabelInput(
							((GenericObject) view.getCurrentObject()).getAttribute("Journalnummer").toString()
						);
			} catch (RemoteException e) {
				throw new ApplicationException("Fehler beim Zugriff auf gewähltes Journal", e);
			}
		}
		return journalnummer;
	}

	public Input getStartdatum() throws ApplicationException {
		if (startdatum == null) {
			try {
				GenericObject go = ((GenericObject) view.getCurrentObject());
				Object start = go.getAttribute("Startdatum");
				String label = Settings.getDateFormat().format(start);
				startdatum = new LabelInput(label);
			} catch (RemoteException e) {
				throw new ApplicationException("Fehler beim Zugriff auf gewähltes Journal", e);
			}
		}
		return startdatum;
	}

	public Input getBelegnummer() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getBuchungstext() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getErfassungsdatum() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getValutadatum() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getKonto1() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getBetrag1() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getKonto2() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getBetrag2() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getKonto3() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input getBetrag3() {
		// TODO Auto-generated method stub
		return null;
	}
}

/*
 *  $Log: BuchenControl.java,v $
 *  Revision 1.2  2005/08/18 17:45:48  tbayen
 *  generischer Wrapper für FiBu-Objekte
 *
 *  Revision 1.1  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 */
