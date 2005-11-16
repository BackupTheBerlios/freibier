// $Id: JournalTable.java,v 1.2 2005/11/16 18:24:11 phormanns Exp $
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
import de.bayen.fibu.Journal;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.FiBuSystemException;
import de.jalin.fibu.gui.tree.Editable;

public class JournalTable implements Editable {
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private FiBuGUI gui;
	private Journal journal;
	private Vector columnTitles;

	public JournalTable(FiBuGUI gui, Journal journal) {
		this.gui = gui;
		this.journal = journal;
		columnTitles = new Vector();
		columnTitles.addElement("Beleg");
		columnTitles.addElement("Buchungstext");
		columnTitles.addElement("Valuta");
		columnTitles.addElement("Soll");
		columnTitles.addElement("Haben");
		columnTitles.addElement("Kto.-Nr.");
		columnTitles.addElement("Kto.-Bezeichnung");
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		JPanel panel = new JPanel(new BorderLayout());
		try {
			JTable journalLog = new JTable(readJournal(), columnTitles);
			// journalLog.setEnabled(false);
			journalLog.setCellSelectionEnabled(false);
			journalLog.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane scroll = new JScrollPane(journalLog);
			panel.add(scroll, BorderLayout.CENTER);
		} catch (FiBuException e) {
			gui.handleException(e);
		}
		return panel;
	}
	
	private Vector readJournal() throws FiBuException {
		Vector journalList = new Vector();
		try {
			Iterator buchungen = journal.getBuchungen().iterator();
			Buchung buchung = null;
			String belegNr = null;
			String buchungstext = null;
			String valutaDatum = null;
			Buchungszeile buchungsZeile = null;
			Betrag betrag = null;
			String betragSoll = null;
			String betragHaben = null;
			Konto kto = null;
			String ktoNr = null;
			String ktoBez = null;
			Vector tableRow = null;
			while (buchungen.hasNext()) {
				buchung = (Buchung) buchungen.next();
				belegNr = buchung.getBelegnummer();
				buchungstext = buchung.getBuchungstext();
				valutaDatum = dateFormatter.format(buchung.getValutadatum());
				Iterator buchungsZeilen = buchung.getBuchungszeilen().iterator();  // Hier kommen keine Zeilen!
				while (buchungsZeilen.hasNext()) {
					tableRow = new Vector();
					tableRow.addElement(belegNr);
					tableRow.addElement(buchungstext);
					tableRow.addElement(valutaDatum);
					buchungsZeile = (Buchungszeile) buchungsZeilen.next();
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
					kto = buchungsZeile.getKonto();
					ktoNr = kto.getKontonummer();
					ktoBez = kto.getBezeichnung();
					tableRow.addElement(ktoNr);
					tableRow.addElement(ktoBez);
					journalList.addElement(tableRow);
				}
			}
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht lesen.");
		}
		return journalList;
	}
}

/*
 *  $Log: JournalTable.java,v $
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 */
