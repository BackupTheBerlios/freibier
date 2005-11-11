// $Id: StammdatenForm.java,v 1.3 2005/11/11 19:46:26 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import javax.swing.JTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.tree.Editable;

public class StammdatenForm implements Editable {

	private FiBuFacade fibu;
	
	private JTextField tfJahrAktuell;
	private JTextField tfPeriodeAktuell;
	private JTextField tfFirma;

	public StammdatenForm(FiBuFacade fibu) throws FiBuException {
		this.fibu = fibu;
	}

	public boolean validateAndSave() throws FiBuException {
		try {
			fibu.setFirma(tfFirma.getText());
			fibu.setJahrAktuell(tfJahrAktuell.getText());
			fibu.setPeriodeAktuell(tfPeriodeAktuell.getText());
		} catch (FiBuException e) {
			return false;
		}
		return true;
	}

	public Component getEditor() throws FiBuException {
		tfJahrAktuell = new JTextField(fibu.getJahrAktuell());
		tfPeriodeAktuell = new JTextField(fibu.getPeriodeAktuell());
		tfFirma = new JTextField(fibu.getFirma());
		FormLayout layout = new FormLayout(
				"4dlu, right:pref, 4dlu, 24dlu, 4dlu, pref:grow, 4dlu",
				"4dlu, pref, 4dlu, pref, 2dlu, pref, 2dlu");
		CellConstraints constraints = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.addSeparator("Stammdaten", constraints.xyw(2, 2, 5));
		builder.addLabel("Firma:", constraints.xy(2, 4));
		builder.add(tfFirma, constraints.xyw(4, 4, 3));
		builder.addLabel("Periode/Jahr:", constraints.xy(2, 6));
		builder.add(tfPeriodeAktuell, constraints.xy(4, 6));
		builder.add(tfJahrAktuell, constraints.xy(6, 6));
		return builder.getPanel();
	}
}

/*
 *  $Log: StammdatenForm.java,v $
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
