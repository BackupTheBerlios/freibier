// $Id: StammdatenForm.java,v 1.8 2005/11/24 17:43:01 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.dialogs.KontoAuswahlDialog;
import de.jalin.fibu.server.customer.CustomerData;
import de.jalin.fibu.server.konto.KontoData;

public class StammdatenForm extends AbstractForm {

	private FiBuGUI gui;
	private FiBuFacade fibu;
	
	private JTextField tfJahrAktuell;
	private JTextField tfPeriodeAktuell;
	private JTextField tfFirma;
	private JTextField tfBilanzKtoNr;
	private JTextField tfGuVKtoNr;
	private JTextField tfBilanzKtoBez;
	private JTextField tfGuVKtoBez;
	private CustomerData customer;

	public StammdatenForm(FiBuGUI gui) {
		this.gui = gui;
		this.fibu = gui.getFiBuFacade();
		this.customer = fibu.getCustomer();
	}

	public boolean validateAndSave() {
		try {
			customer = fibu.getCustomer();
			customer.setFirma(tfFirma.getText());
			customer.setJahr(tfJahrAktuell.getText());
			customer.setPeriode(tfPeriodeAktuell.getText());
			fibu.setCustomer(customer, tfBilanzKtoNr.getText().trim(), tfGuVKtoNr.getText().trim());
			return true;
		} catch (FiBuException e) {
			gui.handleException(e);
			return false;
		}
	}

	public Component getEditor() {
		try {
			customer = fibu.getCustomer();
			tfJahrAktuell = createTextField(customer.getJahr(), true);
			tfPeriodeAktuell = createTextField(customer.getPeriode(), true);
			tfFirma = createTextField(customer.getFirma(), true);
			KontoData bilanzKto = fibu.getBilanzKonto();
			if (bilanzKto != null) {
				tfBilanzKtoNr = createTextField(bilanzKto.getKontonr(), true);
				tfBilanzKtoBez = createTextField(bilanzKto.getBezeichnung(), false);
			} else {
				tfBilanzKtoNr = createTextField("", true);
				tfBilanzKtoBez = createTextField("", false);
			}
			KontoData guvKto = fibu.getGuVKonto();
			if (guvKto != null) {
				tfGuVKtoNr = createTextField(guvKto.getKontonr(), true);
				tfGuVKtoBez = createTextField(guvKto.getBezeichnung(), false);
			} else {
				tfGuVKtoNr = createTextField("", true);
				tfGuVKtoBez = createTextField("", false);
			}
			tfBilanzKtoNr.addFocusListener(new KontoNrListener(fibu, tfBilanzKtoNr, tfBilanzKtoBez, null, null));
			tfGuVKtoNr.addFocusListener(new KontoNrListener(fibu, tfGuVKtoNr, tfGuVKtoBez, null, null));
			JButton btBilanzKto = new JButton("...");
			btBilanzKto.setFocusable(false);
			btBilanzKto.addActionListener(
					new KontoAuswahlDialog(gui, tfBilanzKtoNr, tfBilanzKtoBez, null, null));
			JButton btGuVKto = new JButton("...");
			btGuVKto.setFocusable(false);
			btGuVKto.addActionListener(
					new KontoAuswahlDialog(gui, tfGuVKtoNr, tfGuVKtoBez, null, null));
			FormLayout layout = new FormLayout(
					"4dlu, right:pref, 4dlu, 48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu",
					"4dlu, pref, 4dlu, pref, 2dlu, pref, 8dlu, "
						+ "pref, 4dlu, pref, 2dlu, pref, 4dlu");
			CellConstraints constraints = new CellConstraints();
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			builder.addSeparator("Stammdaten", constraints.xyw(2, 2, 7));
			builder.addLabel("&Firma:", constraints.xy(2, 4));
			tfFirma.setFocusAccelerator('f');
			builder.add(tfFirma, constraints.xyw(4, 4, 5));
			builder.addLabel("&Periode/Jahr:", constraints.xy(2, 6));
			tfPeriodeAktuell.setFocusAccelerator('p');
			builder.add(tfPeriodeAktuell, constraints.xy(4, 6));
			tfJahrAktuell.setFocusAccelerator('j');
			builder.add(tfJahrAktuell, constraints.xyw(6, 6, 3));
			builder.addSeparator("Einstiegskonten", constraints.xyw(2, 8, 7));
			builder.addLabel("&Bilanzkonto:", constraints.xy(2, 10));
			tfBilanzKtoNr.setFocusAccelerator('b');
			builder.add(tfBilanzKtoNr, constraints.xy(4, 10));
			builder.add(tfBilanzKtoBez, constraints.xy(6, 10));
			builder.add(btBilanzKto, constraints.xy(8, 10));
			builder.addLabel("&GuV-Konto:", constraints.xy(2, 12));
			tfGuVKtoNr.setFocusAccelerator('g');
			builder.add(tfGuVKtoNr, constraints.xy(4, 12));
			builder.add(tfGuVKtoBez, constraints.xy(6, 12));
			builder.add(btGuVKto, constraints.xy(8,12));
			return builder.getPanel();
		} catch (FiBuException e) {
			gui.handleException(e);
			return new JPanel();
		}
	}
}

/*
 *  $Log: StammdatenForm.java,v $
 *  Revision 1.8  2005/11/24 17:43:01  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.7  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.6  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.5  2005/11/15 21:20:36  phormanns
 *  Refactorings in FiBuGUI
 *  Focus und Shortcuts in BuchungsForm und StammdatenForm
 *
 *  Revision 1.4  2005/11/11 21:40:35  phormanns
 *  Einstiegskonten im Stammdaten-Form
 *
 *  Revision 1.3  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 *  Revision 1.2  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
