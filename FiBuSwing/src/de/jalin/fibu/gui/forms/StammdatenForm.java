// $Id: StammdatenForm.java,v 1.2 2005/11/10 21:19:26 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
				"4dlu, pref, 2dlu, pref");
		JPanel editor = new JPanel(layout);
		CellConstraints constraints = new CellConstraints();
		editor.add(new JLabel("Firma:"), constraints.xy(2, 2));
		editor.add(tfFirma, constraints.xyw(4, 2, 3));
		editor.add(new JLabel("Periode/Jahr:"), constraints.xy(2, 4));
		editor.add(tfPeriodeAktuell, constraints.xy(4, 4));
		editor.add(tfJahrAktuell, constraints.xy(6, 4));
		return editor;
	}
}

/*
 *  $Log: StammdatenForm.java,v $
 *  Revision 1.2  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
