// $Id: KontoTable.java,v 1.1 2005/11/12 11:44:46 phormanns Exp $
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
import de.jalin.fibu.gui.FiBuSystemException;
import de.jalin.fibu.gui.tree.Editable;

public class KontoTable implements Editable {
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private Konto kto;
	private Vector columnTitles;

	public KontoTable(Konto kto) {
		this.kto = kto;
		columnTitles = new Vector();
		columnTitles.addElement("Beleg");
		columnTitles.addElement("Buchungstext");
		columnTitles.addElement("Valuta");
		columnTitles.addElement("Soll");
		columnTitles.addElement("Haben");
	}

	public boolean validateAndSave() throws FiBuException {
		return true;
	}

	public Component getEditor() throws FiBuException {
		JPanel panel = new JPanel(new BorderLayout());
		JTable journalLog = new JTable(readJournal(), columnTitles);
		journalLog.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(journalLog);
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}
	
	private Vector readJournal() throws FiBuException {
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
 *  Revision 1.1  2005/11/12 11:44:46  phormanns
 *  KontoTabelle zeigt Buchungen zum Konto
 *
 *  Revision 1.1  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 */
