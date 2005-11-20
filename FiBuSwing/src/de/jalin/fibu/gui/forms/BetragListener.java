// $Id: BetragListener.java,v 1.3 2005/11/20 21:29:10 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
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

	public BetragListener(FiBuGUI gui,
			JTextField tfSollBetrag, JTextField tfHabenBetrag,
			JTextField tfSollMWStSatz, JTextField tfHabenMWStSatz,
			JTextField tfSollMWSt, JTextField tfHabenMWSt) {
		this.gui = gui;
		this.tfHabenBetrag = tfHabenBetrag;
		this.tfSollBetrag = tfSollBetrag;
		this.tfSollMWStSatz = tfSollMWStSatz;
		this.tfHabenMWStSatz = tfHabenMWStSatz;
		this.tfSollMWSt = tfSollMWSt;
		this.tfHabenMWSt = tfHabenMWSt;
	}

	public void focusGained(FocusEvent gotFocus) {
		tfHabenBetrag.setText("");
		tfSollMWSt.setText("");
		tfHabenMWSt.setText("");
	}

	public void focusLost(FocusEvent lostFocus) {
		double sollNetto = 0.0d;
		double sollMWstSatz = 0.0d;
		double habenMWStSatz = 0.0d;
		try {
			sollNetto = currencyFormatter.parse(tfSollBetrag.getText().trim()).doubleValue();
			sollMWstSatz = percentFormatter.parse(tfSollMWStSatz.getText().trim()).doubleValue();
			habenMWStSatz = percentFormatter.parse(tfHabenMWStSatz.getText().trim()).doubleValue();
		} catch (NumberFormatException e) {
			gui.handleException(new FiBuUserException("Kein gültiger Betrag angegeben."));
		} catch (ParseException e) {
			gui.handleException(new FiBuUserException("Kein gültiger Betrag angegeben."));
		}
		double sollMWSt = sollNetto * sollMWstSatz / 100.0d;
		double brutto = sollNetto + sollMWSt;
		double habenNetto = brutto * 100.0d / (100.0d + habenMWStSatz);
		double habenMWSt = brutto - habenNetto;
		tfSollBetrag.setText(currencyFormatter.format(sollNetto));
		tfHabenBetrag.setText(currencyFormatter.format(habenNetto));
		tfSollMWSt.setText(currencyFormatter.format(sollMWSt));
		tfHabenMWSt.setText(currencyFormatter.format(habenMWSt));
	}
	
	public void focusLost2(FocusEvent lostFocus) {
		BigDecimal sollNetto = new BigDecimal(0.00d);
		try {
			sollNetto = new BigDecimal(tfSollBetrag.getText().trim());
		} catch (NumberFormatException e) {
			gui.handleException(new FiBuUserException("Kein gültiger Betrag angegeben."));
		}
		BigDecimal sollMWSt = sollNetto.multiply(new BigDecimal(tfSollMWStSatz.getText().trim())).divide(new BigDecimal(100.0d), 2);
		BigDecimal brutto = sollNetto.add(sollMWSt);
		BigDecimal habenNetto = 
			brutto.multiply(
				new BigDecimal(100.0d)).divide(
					new BigDecimal(100.0d).add(
						new BigDecimal(tfHabenMWStSatz.getText().trim())), 
				    BigDecimal.ROUND_HALF_EVEN);
		tfSollBetrag.setText(sollNetto.toString());
		tfHabenBetrag.setText(habenNetto.toString());
		tfSollMWSt.setText(sollMWSt.toString());
		tfHabenMWSt.setText((brutto.subtract(habenNetto)).toString());
	}
}
/*
 *  $Log: BetragListener.java,v $
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
