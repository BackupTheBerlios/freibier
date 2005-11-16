// $Id: KontoTable.java,v 1.2 2005/11/16 18:24:11 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.fibu.Betrag;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Buchungszeile;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.FiBuSystemException;
import de.jalin.fibu.gui.tree.Editable;

public class KontoTable implements Editable {
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private FiBuGUI gui;
	private Konto kto;
	private Vector columnTitles;

	public KontoTable(FiBuGUI gui, Konto kto) {
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
			// kontoLog.setEnabled(false);
			kontoLog.setCellSelectionEnabled(false);
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
		try {
			Iterator buchungsZeilen = kto.getBuchungszeilen().iterator();
			Buchungszeile buchungsZeile = null;
			Buchung buchung = null;
			Betrag betrag = null;
			String betragSoll = null;
			String betragHaben = null;
			Vector tableRow = null;
			while (buchungsZeilen.hasNext()) {
				buchungsZeile = (Buchungszeile) buchungsZeilen.next();
				buchung = buchungsZeile.getBuchung();
				tableRow = new Vector();
				tableRow.addElement(buchung.getBelegnummer());
				tableRow.addElement(buchung.getBuchungstext());
				tableRow.addElement(dateFormatter.format(buchung.getValutadatum()));
				betrag = buchungsZeile.getBetrag();
				if (betrag.isSoll()) {
					betragHaben = "0.00";
					betragSoll = betrag.getWert().toString();
				} else {
					betragSoll = "0.00";
					betragHaben = betrag.getWert().toString();
				}
				tableRow.addElement(betragSoll);
				tableRow.addElement(betragHaben);
				ktoList.addElement(tableRow);
			}
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht lesen.");
		}
		return ktoList;
	}
}

/*
 *  $Log: KontoTable.java,v $
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
