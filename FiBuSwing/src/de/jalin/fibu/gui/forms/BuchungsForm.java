// $Id: BuchungsForm.java,v 1.3 2005/11/11 19:46:26 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JButton;
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
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	
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
		JTextField tfValutaDatum = new JTextField(dateFormatter
				.format(new Date()));
		JTextField tfSollKontoNr = new JTextField("");
		JTextField tfHabenKontoNr = new JTextField("");
		JTextField tfSollKontoText = new JTextField("");
		tfSollKontoText.setEditable(false);
		JTextField tfHabenKontoText = new JTextField("");
		tfHabenKontoText.setEditable(false);
		JTextField tfSollMWStSatz = new JTextField("0");
		tfSollMWStSatz.setEditable(false);
		tfSollMWStSatz.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfHabenMWStSatz = new JTextField("0");
		tfHabenMWStSatz.setEditable(false);
		tfHabenMWStSatz.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfSollBetrag = new JTextField("0.00");
		tfSollBetrag.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfHabenBetrag = new JTextField("0.00");
		tfHabenBetrag.setEditable(false);
		tfHabenBetrag.setHorizontalAlignment(JTextField.RIGHT);
		JTextField tfSollMWSt = new JTextField("0.00");
		tfSollMWSt.setHorizontalAlignment(JTextField.RIGHT);
		tfSollMWSt.setEditable(false);
		JTextField tfHabenMWSt = new JTextField("0.00");
		tfHabenMWSt.setEditable(false);
		tfHabenMWSt.setHorizontalAlignment(JTextField.RIGHT);
		tfSollKontoNr.addFocusListener(new KontoNrListener(fibu, tfSollKontoNr,
				tfSollKontoText, tfSollMWStSatz));
		tfHabenKontoNr.addFocusListener(new KontoNrListener(fibu,
				tfHabenKontoNr, tfHabenKontoText, tfHabenMWStSatz));
		tfSollBetrag.addFocusListener(new BetragListener(tfSollBetrag, 
				tfHabenBetrag, tfSollMWStSatz, tfHabenMWStSatz, tfSollMWSt, tfHabenMWSt));
		JButton btSelSollKto = new JButton("...");
		btSelSollKto.addActionListener(new KontoAuswahlDialog(fibu,
				tfSollKontoNr, tfSollKontoText, tfSollMWStSatz));
		JButton btSelHabenKto = new JButton("...");
		btSelHabenKto.addActionListener(new KontoAuswahlDialog(fibu,
				tfHabenKontoNr, tfHabenKontoText, tfHabenMWStSatz));
		JButton save = new JButton("Buchen");
		save.addActionListener(new BuchungsAction(fibu, journal, tfBelegNummer, tfBuchungsText,
				tfValutaDatum, tfSollKontoNr, tfHabenKontoNr,tfSollBetrag));
		FormLayout layout = new FormLayout(
				"4dlu, 48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu, 8dlu, "
						+ "4dlu, 48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu",
				"4dlu, pref, 4dlu, pref, 2dlu, pref, 8dlu, "
						+ "pref, 4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 8dlu, pref, 2dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Buchung", cc.xyw(2, 2, 13));
		builder.addLabel("Beleg/Datum:", cc.xy(2, 4));
		builder.add(tfBelegNummer, cc.xy(4, 4));
		builder.add(tfValutaDatum, cc.xyw(6, 4, 9));
		builder.addLabel("Buchungstext:", cc.xy(2, 6));
		builder.add(tfBuchungsText, cc.xyw(4, 6, 11));
		builder.addSeparator("Soll-Konto", cc.xyw(2, 8, 5));
		builder.addSeparator("Haben-Konto", cc.xyw(10, 8, 5));
		builder.add(tfSollKontoNr, cc.xy(2, 10));
		builder.add(tfSollKontoText, cc.xy(4, 10));
		builder.add(btSelSollKto, cc.xy(6, 10));
		builder.add(tfHabenKontoNr, cc.xy(10, 10));
		builder.add(tfHabenKontoText, cc.xy(12, 10));
		builder.add(btSelHabenKto, cc.xy(14, 10));
		builder.addLabel("MWSt.-Satz:", cc.xy(2, 12));
		builder.add(tfSollMWStSatz, cc.xy(4, 12));
		builder.addLabel("%", cc.xy(6, 12));
		builder.addLabel("(Netto-)Betrag:", cc.xy(2, 14));
		builder.add(tfSollBetrag, cc.xy(4, 14));
		builder.addLabel("¤", cc.xy(6, 14));
		builder.addLabel("MWSt.:", cc.xy(2, 16));
		builder.add(tfSollMWSt, cc.xy(4, 16));
		builder.addLabel("¤", cc.xy(6, 16));
		builder.addLabel("MWSt.-Satz:", cc.xy(10, 12));
		builder.add(tfHabenMWStSatz, cc.xy(12, 12));
		builder.addLabel("%", cc.xy(14, 12));
		builder.addLabel("(Netto-)Betrag:", cc.xy(10, 14));
		builder.add(tfHabenBetrag, cc.xy(12, 14));
		builder.addLabel("¤", cc.xy(14, 14));
		builder.addLabel("MWSt.:", cc.xy(10, 16));
		builder.add(tfHabenMWSt, cc.xy(12, 16));
		builder.addLabel("¤", cc.xy(14, 16));
		builder.add(save, cc.xy(2, 18));
		return builder.getPanel();
	}

	class BuchungsAction implements ActionListener {
		
		private FiBuFacade fibu;
		private Journal journal;
		private JTextField tfBelegNr;
		private JTextField tfBuchungstext;
		private JTextField tfValutaDatum;
		private JTextField tfSollKtoNr;
		private JTextField tfHabenKtoNr;
		private JTextField tfBetrag;

		public BuchungsAction(FiBuFacade fibu, Journal journal, JTextField tfBelegNr, JTextField tfBuchungstext,
				JTextField tfValutaDatum, JTextField tfSollKtoNr, JTextField tfHabenKtoNr, JTextField tfBetrag) {
			this.fibu = fibu;
			this.journal = journal;
			this.tfBelegNr = tfBelegNr;
			this.tfBuchungstext = tfBuchungstext;
			this.tfValutaDatum = tfValutaDatum;
			this.tfSollKtoNr = tfSollKtoNr;
			this.tfHabenKtoNr = tfHabenKtoNr;
			this.tfBetrag = tfBetrag;
		}
		
		public void actionPerformed(ActionEvent save) {
			try {
				fibu.buchen(journal, tfBelegNr.getText(), 
						tfBuchungstext.getText(), tfValutaDatum.getText(), 
						tfSollKtoNr.getText(), tfHabenKtoNr.getText(), 
						tfBetrag.getText());
			} catch (FiBuException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
/*
 *  $Log: BuchungsForm.java,v $
 *  Revision 1.3  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 *  Revision 1.2  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
