// $Id: BuchenControl.java,v 1.10 2005/08/26 20:48:47 phormanns Exp $
package de.bayen.fibu.gui.control;

import java.math.BigDecimal;
import java.util.Date;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.Betrag;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Buchungszeile;
import de.bayen.fibu.FibuService;
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.Journal;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.data.KontoKnoten;
import de.bayen.fibu.gui.data.ObjectWrapper;
import de.bayen.fibu.gui.widget.DateInput;
import de.bayen.fibu.gui.widget.TreeSelectInput;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DecimalInput;
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
	private DecimalInput betrag1;
	private DecimalInput betrag2;
	private DecimalInput betrag3;
	private TreeSelectInput konto1;
	private TreeSelectInput konto2;
	private TreeSelectInput konto3;
	
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
				konto1 = new TreeSelectInput(new KontoKnoten(fibuService.getBilanzkonto()));
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return konto1;
	}

	public Input getBetrag1() {
		if (betrag1 == null) {
			betrag1 = new DecimalInput(0.0d, Settings.getMoneyFormat());
		}
		return betrag1;
	}

	public Input getKonto2() throws ApplicationException {
		if (konto2 == null) {
			try {
				final FibuService fibuService = Settings.getFibuService();
				konto2 = new TreeSelectInput(new KontoKnoten(fibuService.getBilanzkonto()));
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return konto2;
	}

	public Input getBetrag2() {
		if (betrag2 == null) {
			betrag2 = new DecimalInput(0.0d, Settings.getMoneyFormat());
		}
		return betrag2;
	}

	public Input getKonto3() throws ApplicationException {
		if (konto3 == null) {
			try {
				final FibuService fibuService = Settings.getFibuService();
				konto3 = new TreeSelectInput(new KontoKnoten(fibuService.getBilanzkonto()));
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}
		}
		return konto3;
	}

	public Input getBetrag3() {
		if (betrag3 == null) {
			betrag3 = new DecimalInput(0.0d, Settings.getMoneyFormat());
		}
		return betrag3;
	}

	public void speichereBuchung() {
		ObjectWrapper obj = (ObjectWrapper) view.getCurrentObject();
		Journal journal = (Journal) obj.getGObject();
		try {
			Buchung buchung = journal.createBuchung();
			buchung.setJournal(journal);
			buchung.setBelegnummer((String) belegnummer.getValue());
			buchung.setBuchungstext((String) buchungstext.getValue());
			buchung.setValutadatum((Date) valutadatum.getValue());
			Buchungszeile buchungszeile1 = buchung.createZeile();
			buchungszeile1.setBuchung(buchung);
			Double b1 = (Double) betrag1.getValue();
			buchungszeile1.setBetrag(new Betrag(new BigDecimal(b1.doubleValue()), true));
			String ktotxt1 = konto1.getText();
			String ktonr1 = ktotxt1.substring(0, ktotxt1.indexOf(" "));
			buchungszeile1.setKonto(
						Settings.getFibuService().getFiBu().getKonto(ktonr1)
					);
			Buchungszeile buchungszeile2 = buchung.createZeile();
			buchungszeile2.setBuchung(buchung);
			Double b2 = (Double) betrag2.getValue();
			buchungszeile2.setBetrag(new Betrag(new BigDecimal(b2.doubleValue()), false));
			String ktotxt2 = konto2.getText();
			String ktonr2 = ktotxt2.substring(0, ktotxt2.indexOf(" "));
			buchungszeile2.setKonto(
						Settings.getFibuService().getFiBu().getKonto(ktonr2)
					);
			buchung.write();
		} catch (SQL_DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordNotExistsDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/*
 *  $Log: BuchenControl.java,v $
 *  Revision 1.10  2005/08/26 20:48:47  phormanns
 *  Erste Buchung in der Datenbank
 *
 *  Revision 1.9  2005/08/26 19:19:44  phormanns
 *  Hierarchie-Auswahl für erstes Konto im Buchungsdialog
 *
 *  Revision 1.8  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 *  Revision 1.7  2005/08/23 19:56:05  phormanns
 *  Neues Paket data für Datenobjekte
 *
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
