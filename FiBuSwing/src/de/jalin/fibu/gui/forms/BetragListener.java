// $Id: BetragListener.java,v 1.4 2005/11/24 17:43:05 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JTextField;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.FiBuUserException;

public class BetragListener implements FocusListener {

	private static final NumberFormat currencyFormatter = new DecimalFormat("0.00");
	private static final NumberFormat percentFormatter = new DecimalFormat("0.0");

	private FiBuGUI gui;
	private JTextField tfSollBetrag;
	private JTextField tfHabenBetrag;
	private JTextField tfSollMWStSatz;
	private JTextField tfHabenMWStSatz;
	private JTextField tfSollMWSt;
	private JTextField tfHabenMWSt;
	private JTextField tfBruttoBetrag;

	public BetragListener(FiBuGUI gui, JTextField tfBruttoBetrag,
			JTextField tfSollBetrag, JTextField tfHabenBetrag,
			JTextField tfSollMWStSatz, JTextField tfHabenMWStSatz,
			JTextField tfSollMWSt, JTextField tfHabenMWSt) {
		this.gui = gui;
		this.tfBruttoBetrag = tfBruttoBetrag;
		this.tfHabenBetrag = tfHabenBetrag;
		this.tfSollBetrag = tfSollBetrag;
		this.tfSollMWStSatz = tfSollMWStSatz;
		this.tfHabenMWStSatz = tfHabenMWStSatz;
		this.tfSollMWSt = tfSollMWSt;
		this.tfHabenMWSt = tfHabenMWSt;
	}

	public void focusGained(FocusEvent gotFocus) {
		tfSollBetrag.setText("");
		tfHabenBetrag.setText("");
		tfSollMWSt.setText("");
		tfHabenMWSt.setText("");
	}

	public void focusLost(FocusEvent lostFocus) {
		berechneMWSt();
	}

	public void berechneMWSt() {
		double sollNetto = 0.0d;
		double sollMWStSatz = 0.0d;
		double habenNetto = 0.0d;
		double habenMWStSatz = 0.0d;
		double sollMWSt = 0.0d;
		double habenMWSt = 0.0d;
		double brutto = 0.0d;
		try {
			brutto = currencyFormatter.parse(tfBruttoBetrag.getText().trim()).doubleValue();
			sollMWStSatz = percentFormatter.parse(tfSollMWStSatz.getText().trim()).doubleValue() / 100.0d;
			habenMWStSatz = percentFormatter.parse(tfHabenMWStSatz.getText().trim()).doubleValue() / 100.0d;
		} catch (NumberFormatException e) {
			gui.handleException(new FiBuUserException("Kein gültiger Betrag angegeben."));
		} catch (ParseException e) {
			gui.handleException(new FiBuUserException("Kein gültiger Betrag angegeben."));
		}
		sollNetto = brutto;
		habenNetto = brutto;
		if (sollMWStSatz > 0.001d) {
			sollNetto = brutto / (1.0d + sollMWStSatz);
			sollMWSt = brutto - sollNetto;
		}
		if (habenMWStSatz > 0.001d) {
			habenNetto = brutto / (1.0d + habenMWStSatz);
			habenMWSt = brutto - habenNetto;
		}
		tfBruttoBetrag.setText(currencyFormatter.format(brutto));
		tfSollBetrag.setText(currencyFormatter.format(sollNetto));
		tfHabenBetrag.setText(currencyFormatter.format(habenNetto));
		tfSollMWSt.setText(currencyFormatter.format(sollMWSt));
		tfHabenMWSt.setText(currencyFormatter.format(habenMWSt));
	}
	
}
/*
 *  $Log: BetragListener.java,v $
 *  Revision 1.4  2005/11/24 17:43:05  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.3  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 *  Revision 1.1  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 */
