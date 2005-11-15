// $Id: StammdatenForm.java,v 1.5 2005/11/15 21:20:36 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.dialogs.KontoAuswahlDialog;
import de.jalin.fibu.gui.tree.Editable;

public class StammdatenForm implements Editable {

	private FiBuFacade fibu;
	
	private JTextField tfJahrAktuell;
	private JTextField tfPeriodeAktuell;
	private JTextField tfFirma;
	private JTextField tfBilanzKtoNr;
	private JTextField tfGuVKtoNr;
	private JTextField tfBilanzKtoBez;
	private JTextField tfGuVKtoBez;

	public StammdatenForm(FiBuFacade fibu) throws FiBuException {
		this.fibu = fibu;
	}

	public boolean validateAndSave() throws FiBuException {
		try {
			fibu.setFirma(tfFirma.getText());
			fibu.setJahrAktuell(tfJahrAktuell.getText());
			fibu.setPeriodeAktuell(tfPeriodeAktuell.getText());
			fibu.setBilanzKonto(tfBilanzKtoNr.getText());
			fibu.setGuVKonto(tfGuVKtoNr.getText());
		} catch (FiBuException e) {
			return false;
		}
		return true;
	}

	public Component getEditor() throws FiBuException {
		tfJahrAktuell = new JTextField(fibu.getJahrAktuell());
		tfPeriodeAktuell = new JTextField(fibu.getPeriodeAktuell());
		tfFirma = new JTextField(fibu.getFirma());
		Konto bilanzKto = fibu.getBilanzKonto();
		if (bilanzKto != null) {
			tfBilanzKtoNr = new JTextField(bilanzKto.getKontonummer());
			tfBilanzKtoBez = new JTextField(bilanzKto.getBezeichnung());
		} else {
			tfBilanzKtoNr = new JTextField("");
			tfBilanzKtoBez = new JTextField("");
		}
		Konto guvKto = fibu.getGuVKonto();
		if (guvKto != null) {
			tfGuVKtoNr = new JTextField(guvKto.getKontonummer());
			tfGuVKtoBez = new JTextField(guvKto.getBezeichnung());
		} else {
			tfGuVKtoNr = new JTextField("");
			tfGuVKtoBez = new JTextField("");
		}
		tfBilanzKtoNr.addFocusListener(new KontoNrListener(fibu, tfBilanzKtoNr, tfBilanzKtoBez, null));
		tfBilanzKtoBez.setEditable(false);
		tfBilanzKtoBez.setFocusable(false);
		tfGuVKtoNr.addFocusListener(new KontoNrListener(fibu, tfGuVKtoNr, tfGuVKtoBez, null));
		tfGuVKtoBez.setEditable(false);
		tfGuVKtoBez.setFocusable(false);
		JButton btBilanzKto = new JButton("...");
		btBilanzKto.addActionListener(new KontoAuswahlDialog(fibu, tfBilanzKtoNr, tfBilanzKtoBez, null));
		JButton btGuVKto = new JButton("...");
		btGuVKto.addActionListener(new KontoAuswahlDialog(fibu, tfGuVKtoNr, tfGuVKtoBez, null));
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
	}
}

/*
 *  $Log: StammdatenForm.java,v $
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
