// $Id: BuchungsForm.java,v 1.1 2005/11/10 21:19:26 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.bayen.fibu.Journal;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.dialogs.KontoAuswahlDialog;
import de.jalin.fibu.gui.tree.Editable;

public class BuchungsForm implements Editable {
	
	private static final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private FiBuFacade fibu;
	private Journal journal;

	public BuchungsForm(FiBuFacade fibu, Journal jour) {
		this.fibu = fibu;
		this.journal = jour;
	}

	public boolean validateAndSave() throws FiBuException {
		// TODO Auto-generated method stub
		return true;
	}

	public Component getEditor() throws FiBuException {
		JTextField tfBelegNummer = new JTextField("");
		JTextField tfBuchungsText = new JTextField("");
		JTextField tfValutaDatum = new JTextField(dateFormatter.format(new Date()));
		JTextField tfSollKonto = new JTextField(""); 
		JTextField tfHabenKonto = new JTextField("");
		JTextField tfSollMWStSatz = new JTextField("0");
		tfSollMWStSatz.setEditable(false);
		tfSollMWStSatz.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfHabenMWStSatz = new JTextField("0");
		tfHabenMWStSatz.setEditable(false);
		tfHabenMWStSatz.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfSollBetrag = new JTextField("0,00");
		tfSollBetrag.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfHabenBetrag = new JTextField("0,00");
		tfHabenBetrag.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfSollMWSt = new JTextField("0,00");
		tfSollMWSt.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfHabenMWSt = new JTextField("0,00");
		tfHabenMWSt.setHorizontalAlignment(JTextField.RIGHT);
		JButton btSelSollKto = new JButton("...");
		btSelSollKto.addActionListener(new KontoAuswahlDialog(fibu, tfSollKonto));
		JButton btSelHabenKto = new JButton("...");
		btSelHabenKto.addActionListener(new KontoAuswahlDialog(fibu, tfHabenKonto));
		FormLayout layout = new FormLayout(
				"4dlu, right:48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu, 8dlu, " +
				"4dlu, right:48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu",
				"4dlu, pref, 2dlu, pref, 2dlu, pref, 8dlu, " +
				"pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Buchung",                 cc.xyw(2, 2, 13));
		builder.addLabel("Beleg/Datum:",                cc.xy(2, 4));
		builder.add(tfBelegNummer,                      cc.xy(4, 4));
		builder.add(tfValutaDatum,                      cc.xyw(6, 4, 9));
		builder.addLabel("Buchungstext:",               cc.xy(2, 6));
		builder.add(tfBuchungsText,                     cc.xyw(4, 6, 11));
		builder.addSeparator("Soll-Konto",              cc.xyw(2, 8, 5));
		builder.addSeparator("Haben-Konto",             cc.xyw(10, 8, 5));
		builder.add(tfSollKonto,                        cc.xyw(2, 10, 3));
		builder.add(btSelSollKto,                       cc.xy(6, 10));
		builder.add(tfHabenKonto,                       cc.xyw(10, 10, 3));
		builder.add(btSelHabenKto,                      cc.xy(14, 10));
		builder.addLabel("MWSt.-Satz:",                 cc.xy(2, 12));
		builder.add(tfSollMWStSatz,                     cc.xy(4, 12));
		builder.addLabel("%",                           cc.xy(6, 12));
		builder.addLabel("(Netto-)Betrag:",             cc.xy(2, 14));
		builder.add(tfSollBetrag,                       cc.xy(4, 14));
		builder.addLabel("¤",                           cc.xy(6, 14));
		builder.addLabel("MWSt.:",                      cc.xy(2, 16));
		builder.add(tfSollMWSt,                         cc.xy(4, 16));
		builder.addLabel("¤",                           cc.xy(6, 16));
		builder.addLabel("MWSt.-Satz:",                 cc.xy(10, 12));
		builder.add(tfHabenMWStSatz,                    cc.xy(12, 12));
		builder.addLabel("%",                           cc.xy(14, 12));
		builder.addLabel("(Netto-)Betrag:",             cc.xy(10, 14));
		builder.add(tfHabenBetrag,                      cc.xy(12, 14));
		builder.addLabel("¤",                           cc.xy(14, 14));
		builder.addLabel("MWSt.:",                      cc.xy(10, 16));
		builder.add(tfHabenMWSt,                        cc.xy(12, 16));
		builder.addLabel("¤",                           cc.xy(14, 16));
		return builder.getPanel();
	}
}

/*
 *  $Log: BuchungsForm.java,v $
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
