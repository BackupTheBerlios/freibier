// $Id: BuchungsForm.java,v 1.8 2006/02/24 22:24:22 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.FiBuUserException;
import de.jalin.fibu.gui.dialogs.KontoAuswahlDialog;
import de.jalin.fibu.server.journal.JournalData;


public class BuchungsForm extends AbstractForm {
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private FiBuGUI gui;
	private JournalData journal;
	private JTextField tfBelegNr;
	private JTextField tfValutaDatum;
	private JTextField tfSollKontoNr;
	private JTextField tfHabenKontoNr;
	private JTextField tfBuchungstext;
	private JTextField tfBruttoBetrag;
	private JTextField tfSollKontoText;
	private JTextField tfHabenKontoText;
	private JTextField tfSollMWStSatz;
	private JTextField tfHabenMWStSatz;
	private JTextField tfSollBetrag;
	private JTextField tfHabenBetrag;
	private JTextField tfSollMWSt;
	private JTextField tfHabenMWSt;
	private JournalTable tabJournal;
	private BetragListener betragListener;

	public BuchungsForm(FiBuGUI gui, JournalData jour) {
		this.gui = gui;
		this.journal = jour;
	}

	public boolean save() {
		try {
			String belegNr = tfBelegNr.getText().trim();
			if (belegNr.length() < 1) {
				throw new FiBuUserException("Kein Beleg angegeben.");
			}
			String valutaDatum = tfValutaDatum.getText();
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateFormatter.parse(valutaDatum));
				if (cal.get(Calendar.YEAR) < 100) cal.add(Calendar.YEAR, 2000);
				tfValutaDatum.setText(dateFormatter.format(cal.getTime()));
			} catch (ParseException e) {
				throw new FiBuUserException("Kein gültiges Datum angegeben.");
			}
			String buchungstext = tfBuchungstext.getText();
			if (buchungstext.length() < 1) {
				throw new FiBuUserException("Kein Buchungstext angegeben.");
			}
			gui.getFiBuFacade().buchen(journal, belegNr, 
					buchungstext, 
					valutaDatum, 
					tfSollKontoNr.getText(), 
					tfHabenKontoNr.getText(), 
					tfBruttoBetrag.getText());
			tfBelegNr.setText("");
			tfBelegNr.requestFocus();
			return true;
		} catch (FiBuException e) {
			gui.handleException(e);
			return false;
		}
	}

	public Component getEditor() {
		FiBuFacade fibu = gui.getFiBuFacade();
		tfBelegNr = createTextField("", true);
		tfBuchungstext = createTextField("", true);
		tfValutaDatum = createTextField(dateFormatter.format(new Date()), true);
		tfSollKontoNr = createTextField("", true);
		tfHabenKontoNr = createTextField("", true);
		tfSollKontoText = createTextField("", false);
		tfHabenKontoText = createTextField("", false);
		tfBruttoBetrag = createTextField("0,00", true);
		tfBruttoBetrag.setHorizontalAlignment(JTextField.RIGHT);
		tfSollMWStSatz = createTextField("0", false);
		tfSollMWStSatz.setHorizontalAlignment(JTextField.RIGHT);
		tfHabenMWStSatz = createTextField("0", false);
		tfHabenMWStSatz.setHorizontalAlignment(JTextField.RIGHT);
		tfSollBetrag = createTextField("0,00", false);
		tfSollBetrag.setHorizontalAlignment(JTextField.RIGHT);
		tfHabenBetrag = createTextField("0,00", false);
		tfHabenBetrag.setHorizontalAlignment(JTextField.RIGHT);
		tfSollMWSt = createTextField("0,00", false);
		tfSollMWSt.setHorizontalAlignment(JTextField.RIGHT);
		tfHabenMWSt = createTextField("0,00", false);
		tfHabenMWSt.setHorizontalAlignment(JTextField.RIGHT);
		betragListener = new BetragListener(gui, tfBruttoBetrag,
				tfSollBetrag, tfHabenBetrag, 
				tfSollMWStSatz, tfHabenMWStSatz, 
				tfSollMWSt, tfHabenMWSt);
		tfSollKontoNr.addFocusListener(
				new KontoNrListener(fibu, tfSollKontoNr, tfSollKontoText, tfSollMWStSatz, betragListener));
		tfHabenKontoNr.addFocusListener(
				new KontoNrListener(fibu, tfHabenKontoNr, tfHabenKontoText, tfHabenMWStSatz, betragListener));
		tfBruttoBetrag.addFocusListener(betragListener);
		JButton btSelSollKto = new JButton("...");
		btSelSollKto.setFocusable(false);
		btSelSollKto.addActionListener(
				new KontoAuswahlDialog(gui, tfSollKontoNr, tfSollKontoText, tfSollMWStSatz, betragListener));
		JButton btSelHabenKto = new JButton("...");
		btSelHabenKto.setFocusable(false);
		btSelHabenKto.addActionListener(
				new KontoAuswahlDialog(gui, tfHabenKontoNr, tfHabenKontoText, tfHabenMWStSatz, betragListener));
		JButton save = new JButton("Buchen");
		save.addActionListener(new BuchungsAction(this));
		FormLayout layout = new FormLayout(
				"4dlu, 48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu, 8dlu, "
						+ "4dlu, 48dlu, 4dlu, pref:grow, 4dlu, 16dlu, 4dlu",
				"4dlu, pref, 4dlu, pref, 2dlu, pref, 2dlu, pref, 8dlu, "
						+ "pref, 4dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 8dlu, pref, 8dlu, "
						+ "pref, 4dlu, fill:16dlu:grow, 4dlu");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Neue Buchung", cc.xyw(2, 2, 13));
		builder.addLabel("&Beleg/Datum:", cc.xy(2, 4));
		tfBelegNr.setFocusAccelerator('b');
		builder.add(tfBelegNr, cc.xy(4, 4));
		tfValutaDatum.setFocusAccelerator('d');
		builder.add(tfValutaDatum, cc.xyw(6, 4, 9));
		builder.addLabel("Buchungs&text:", cc.xy(2, 6));
		builder.add(tfBuchungstext, cc.xyw(4, 6, 11));
		tfBuchungstext.setFocusAccelerator('t');
		builder.addLabel("Brutto-Betra&g:", cc.xy(2, 8));
		builder.add(tfBruttoBetrag, cc.xy(4, 8));
		tfBruttoBetrag.setFocusAccelerator('g');
		
		builder.addSeparator("&Soll-Konto", cc.xyw(2, 10, 5));
		builder.addSeparator("&Haben-Konto", cc.xyw(10, 10, 5));
		tfSollKontoNr.setFocusAccelerator('s');
		builder.add(tfSollKontoNr, cc.xy(2, 12));
		builder.add(tfSollKontoText, cc.xy(4, 12));
		builder.add(btSelSollKto, cc.xy(6, 12));
		tfHabenKontoNr.setFocusAccelerator('h');
		builder.add(tfHabenKontoNr, cc.xy(10, 12));
		builder.add(tfHabenKontoText, cc.xy(12, 12));
		builder.add(btSelHabenKto, cc.xy(14, 12));
		builder.addLabel("MWSt.-Satz:", cc.xy(2, 14));
		builder.add(tfSollMWStSatz, cc.xy(4, 14));
		builder.addLabel("%", cc.xy(6, 14));
		builder.addLabel("(Netto-)Betrag:", cc.xy(2, 16));
		builder.add(tfSollBetrag, cc.xy(4, 16));
		builder.addLabel("¤", cc.xy(6, 16));
		builder.addLabel("MWSt.:", cc.xy(2, 18));
		builder.add(tfSollMWSt, cc.xy(4, 18));
		builder.addLabel("¤", cc.xy(6, 18));
		builder.addLabel("MWSt.-Satz:", cc.xy(10, 14));
		builder.add(tfHabenMWStSatz, cc.xy(12, 14));
		builder.addLabel("%", cc.xy(14, 14));
		builder.addLabel("(Netto-)Betrag:", cc.xy(10, 16));
		builder.add(tfHabenBetrag, cc.xy(12, 16));
		builder.addLabel("¤", cc.xy(14, 16));
		builder.addLabel("MWSt.:", cc.xy(10, 18));
		builder.add(tfHabenMWSt, cc.xy(12, 18));
		builder.addLabel("¤", cc.xy(14, 18));
		builder.add(save, cc.xy(2, 20));
		builder.addSeparator("Bisherige Buchungen", cc.xyw(2, 22, 13));
		tabJournal = new JournalTable(gui, journal);
		builder.add(tabJournal.getEditor(), cc.xyw(2, 24, 13));
		return builder.getPanel();
	}

	class BuchungsAction implements ActionListener {
		
		private BuchungsForm form;

		public BuchungsAction(BuchungsForm form) {
			this.form = form;
		}
		
		public void actionPerformed(ActionEvent buchen) {
			form.save();
			tabJournal.reload();
		}
	}
}
/*
 *  $Log: BuchungsForm.java,v $
 *  Revision 1.8  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.7  2005/11/24 17:42:57  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.6  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.5  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.4  2005/11/15 21:20:36  phormanns
 *  Refactorings in FiBuGUI
 *  Focus und Shortcuts in BuchungsForm und StammdatenForm
 *
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
