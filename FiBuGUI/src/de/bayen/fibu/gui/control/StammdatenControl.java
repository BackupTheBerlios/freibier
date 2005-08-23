// $Id: StammdatenControl.java,v 1.2 2005/08/23 19:56:05 phormanns Exp $

package de.bayen.fibu.gui.control;

import java.rmi.RemoteException;

import de.bayen.database.Record;
import de.bayen.fibu.FibuService;
import de.bayen.fibu.gui.FiBuPlugin;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.data.RecordObject;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

public class StammdatenControl extends AbstractControl {

	private Input firma;
	private Input bilanzkonto;
	private Input jahr;
	private Input periode;
	private RecordObject stammdaten;
	
	public StammdatenControl(AbstractView view) {
		super(view);
	}

	public RecordObject getStammdaten() throws ApplicationException {
		if (stammdaten == null) {
			try {
				FibuService service = (FibuService) Application.getServiceFactory().lookup(FiBuPlugin.class, "buchhaltung");
				Record stammdatenRecord = service.getFiBu().getFirmenstammdaten();
				stammdaten = new RecordObject(stammdatenRecord);
				this.view.setCurrentObject(stammdaten);
			} catch (Exception e) {
				throw new ApplicationException("FiBu-Service nicht gefunden", e);
			}
		}
		return stammdaten;
	}
	
	public Input getFirma() throws ApplicationException {
		if (firma == null) {
			try {
				firma = new TextInput((String) getStammdaten().getAttribute("Firma"));
			} catch (RemoteException e) {
				throw new ApplicationException("Fehler beim Zugriff auf Firmenstammdaten", e);
			}
		}
		return firma;
	}
	
	public Input getBilanzkonto() throws ApplicationException {
		if (bilanzkonto == null) {
			try {
				bilanzkonto = new DecimalInput(
						((Long) getStammdaten().getAttribute("Bilanzkonto")).doubleValue(), 
						Settings.getKontoFormat());
			} catch (RemoteException e) {
				throw new ApplicationException("Fehler beim Zugriff auf Firmenstammdaten", e);
			}
		}
		return bilanzkonto;
	}
	
	public Input getJahr() throws ApplicationException {
		if (jahr == null) {
			try {
				jahr = new TextInput(
						(String) getStammdaten().getAttribute("JahrAktuell"), 
						4);
			} catch (RemoteException e) {
				throw new ApplicationException("Fehler beim Zugriff auf Firmenstammdaten", e);
			}
		}
		return jahr;
	}

	public Input getPeriode() throws ApplicationException {
		if (periode == null) {
			try {
				periode = new TextInput((String) getStammdaten().getAttribute("PeriodeAktuell"), 2);
			} catch (RemoteException e) {
				throw new ApplicationException("Fehler beim Zugriff auf Firmenstammdaten", e);
			}
		}
		return periode;
	}

	public void save() throws ApplicationException {
		try {
			FibuService service = (FibuService) Application.getServiceFactory().lookup(FiBuPlugin.class, "buchhaltung");
			getStammdaten().setAttribute("Firma", (String) getFirma().getValue());
			getStammdaten().setAttribute("Bilanzkonto", Settings.getKontoFormat().format(((Double) getBilanzkonto().getValue()).doubleValue()));
			getStammdaten().setAttribute("JahrAktuell", (String) getJahr().getValue());
			getStammdaten().setAttribute("PeriodeAktuell", (String) getPeriode().getValue());
			service.getFiBu().setFirmenstammdaten(getStammdaten().getRecord());
		} catch (Exception e) {
			throw new ApplicationException("Fehler beim Speichern der Firmenstammdaten", e);
		}
		
	}
}


//
// $Log: StammdatenControl.java,v $
// Revision 1.2  2005/08/23 19:56:05  phormanns
// Neues Paket data für Datenobjekte
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//