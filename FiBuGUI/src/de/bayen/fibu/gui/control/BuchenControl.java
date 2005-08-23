// $Id: BuchenControl.java,v 1.6 2005/08/23 19:40:14 phormanns Exp $
package de.bayen.fibu.gui.control;

import java.util.Date;
import java.util.List;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.fibu.FibuService;
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.gui.ListIterator;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.widget.DateInput;
import de.bayen.fibu.gui.widget.ListDialog;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.util.ApplicationException;

public class BuchenControl extends AbstractControl {

	private AbstractView view;
	private LabelInput journalnummer;
	private LabelInput startdatum;
	private TextInput belegnummer;
	private TextInput buchungstext;
	private LabelInput erfassungsdatum;
	private DateInput valutadatum;
	private DialogInput konto1;
	
	public BuchenControl(AbstractView view) {
		super(view);
		this.view = view;
	}

	public Input getJournalnummer() throws ApplicationException {
		if (journalnummer == null) {
			journalnummer = 
				new LabelInput(
						((GenericObject) view.getCurrentObject()).getAttribute("Journalnummer").toString()
					);
		}
		return journalnummer;
	}

	public Input getStartdatum() throws ApplicationException {
		if (startdatum == null) {
			GenericObject go = ((GenericObject) view.getCurrentObject());
			Object start = go.getAttribute("Startdatum");
			String label = Settings.getDateFormat().format(start);
			startdatum = new LabelInput(label);
		}
		return startdatum;
	}

	public Input getBelegnummer() {
		if (belegnummer == null) {
			belegnummer = new TextInput("");
		}
		return belegnummer;
	}

	public Input getBuchungstext() {
		if (buchungstext == null) {
			buchungstext = new TextInput("");
		}
		return buchungstext;
	}

	public Input getErfassungsdatum() {
		if (erfassungsdatum == null) {
			erfassungsdatum = new LabelInput(Settings.getDateFormat().format(new Date()));
		}
		return erfassungsdatum;
	}

	public Input getValutadatum() {
		if (valutadatum == null) {
			valutadatum = new DateInput(new Date(), Settings.getDateFormat());
		}
		return valutadatum;
	}

	public Input getKonto1() throws ApplicationException {
		if (konto1 == null) {
			try {
				final FibuService fibuService = Settings.getFibuService();
				ListDialog dialog = new ListDialog(new ListIterator() {
					public List reloadList() throws ApplicationException {
						try {
							return fibuService.getBilanzkonto().getUnterkonten();
						} catch (SQL_DBException e) {
							throw new ApplicationException("Fehler beim Lesen der Unterkonten", e);
						}
					}}, ListDialog.POSITION_MOUSE);
				dialog.setTitle("Bitte ein Konto auswählen");
				dialog.addColumn("Kto.-Nr.", "Kontonummer");
				dialog.addColumn("Bezeichnung", "Bezeichnung");
				dialog.addCloseListener(new Listener() {

					public void handleEvent(Event event) {
						konto1.setText((String) ((GenericObject) event.data).getAttribute("Kontonummer"));
					}
					
				});
				konto1 = new DialogInput("0000", dialog);
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return konto1;
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
 *  Revision 1.6  2005/08/23 19:40:14  phormanns
 *  Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.5  2005/08/21 20:18:25  phormanns
 *  Erste Widgets für Buchen-Dialog
 *
 *  Revision 1.3  2005/08/18 19:06:06  tbayen
 *  Buchungsfelder in Buchungsdialog
 *
 *  Revision 1.2  2005/08/18 17:45:48  tbayen
 *  generischer Wrapper für FiBu-Objekte
 *
 *  Revision 1.1  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 */
