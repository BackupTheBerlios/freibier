// $Id: KontoTable.java,v 1.5 2005/11/24 17:43:04 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.konto.KontoData;

public class KontoTable implements Editable {
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	private static final NumberFormat currencyFormatter = new DecimalFormat("0.00");
	
	private FiBuGUI gui;
	private KontoData kto;
	private Vector columnTitles;

	public KontoTable(FiBuGUI gui, KontoData kto) {
		this.gui = gui;
		this.kto = kto;
		columnTitles = new Vector();
		columnTitles.addElement("Beleg");
		columnTitles.addElement("Buchungstext");
		columnTitles.addElement("Valuta");
		columnTitles.addElement("Soll");
		columnTitles.addElement("Haben");
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		JPanel panel = new JPanel(new BorderLayout());
		try {
			JTable kontoLog = new JTable(readKonto(), columnTitles);
			kontoLog.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane scroll = new JScrollPane(kontoLog);
			panel.add(scroll, BorderLayout.CENTER);
		} catch (FiBuException e) {
			gui.handleException(e);
		}
		return panel;
	}
	
	private Vector readKonto() throws FiBuException {
		Vector ktoList = new Vector();
		Iterator buchungsZeilen = gui.getFiBuFacade().getBuchungsliste(kto).iterator();
		BuchungslisteData buchungsZeile = null;
		Integer betrag = null;
		String betragSoll = null;
		String betragHaben = null;
		Vector tableRow = null;
		while (buchungsZeilen.hasNext()) {
			buchungsZeile = (BuchungslisteData) buchungsZeilen.next();
			tableRow = new Vector();
			tableRow.addElement(buchungsZeile.getBelegnr());
			tableRow.addElement(buchungsZeile.getBuchungstext());
			tableRow.addElement(dateFormatter.format(buchungsZeile.getValuta()));
			betrag = buchungsZeile.getBetrag();
			if (buchungsZeile.getSoll().booleanValue()) {
				betragHaben = "0,00";
				betragSoll = currencyFormatter.format(betrag.floatValue() / 100.0);
			} else {
				betragSoll = "0,00";
				betragHaben = currencyFormatter.format(betrag.floatValue() / 100.0);
			}
			tableRow.addElement(betragSoll);
			tableRow.addElement(betragHaben);
			ktoList.addElement(tableRow);
		}
		return ktoList;
	}
}

/*
 *  $Log: KontoTable.java,v $
 *  Revision 1.5  2005/11/24 17:43:04  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.4  2005/11/23 23:16:49  phormanns
 *  Lesen Konto-Hierarchie und Buchungsliste optimiert
 *
 *  Revision 1.3  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/12 11:44:46  phormanns
 *  KontoTabelle zeigt Buchungen zum Konto
 *
 *  Revision 1.1  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 */
