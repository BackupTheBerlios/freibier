// $Id: JournalTable.java,v 1.6 2005/12/14 19:29:00 phormanns Exp $
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
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.journal.JournalData;

public class JournalTable extends FiBuTable implements Editable {
	
	private static final DateFormat dateFormatter = 
		DateFormat.getDateInstance(DateFormat.MEDIUM);
	private static final NumberFormat currencyFormatter = new DecimalFormat("0.00");
	
	private FiBuGUI gui;
	private JournalData journal;
	private Vector columns;
	private JTable journalLog;
	private JPanel panel;
	private Vector readJournal;

	public JournalTable(FiBuGUI gui, JournalData journal) {
		this.gui = gui;
		this.journal = journal;
		this.journalLog = null;
		columns = new Vector();
		columns.addElement(createTableColumn(0, "Beleg", 50, SwingConstants.CENTER));
		columns.addElement(createTableColumn(1, "Buchungstext", 200, SwingConstants.LEFT));
		columns.addElement(createTableColumn(2, "Valuta", 75, SwingConstants.CENTER));
		columns.addElement(createTableColumn(3, "Soll", 50, SwingConstants.RIGHT));
		columns.addElement(createTableColumn(4, "Haben", 50, SwingConstants.RIGHT));
		columns.addElement(createTableColumn(5, "Kto.-Nr.", 50, SwingConstants.RIGHT));
		columns.addElement(createTableColumn(6, "Kto.-Bezeichnung", 200, SwingConstants.LEFT));
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		panel = new JPanel(new BorderLayout());
		try {
			readJournal = readJournal();
			TableColumnModel tableColumnModel = new FibuTableColumnModel(columns);
			journalLog = new JTable(new FibuTableModel(readJournal, tableColumnModel), tableColumnModel);
			journalLog.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			journalLog.setColumnSelectionAllowed(false);
			journalLog.setRowSelectionAllowed(true);
			JScrollPane scroll = new JScrollPane(journalLog);
			panel.removeAll();
			panel.add(scroll, BorderLayout.CENTER);
		} catch (FiBuException e) {
			gui.handleException(e);
		}
		return panel;
	}

	public void reload() {
		try {
			readJournal.removeAllElements();
			readJournal.addAll(readJournal());
			journalLog.revalidate();
			journalLog.repaint();
		} catch (FiBuException e) {
			gui.handleException(e);
		}
	}
	
	private Vector readJournal() throws FiBuException {
		Vector journalList = new Vector();
		FiBuFacade fiBuFacade = gui.getFiBuFacade();
		Iterator buchungen = fiBuFacade.getBuchungsliste(journal).iterator();
		BuchungslisteData buchung = null;
		String belegNr = null;
		String buchungstext = null;
		String valutaDatum = null;
		Integer betrag = null;
		String betragSoll = null;
		String betragHaben = null;
		String ktoNr = null;
		String ktoBez = null;
		Vector tableRow = null;
		while (buchungen.hasNext()) {
			buchung = (BuchungslisteData) buchungen.next();
			belegNr = buchung.getBelegnr();
			buchungstext = buchung.getBuchungstext();
			valutaDatum = dateFormatter.format(buchung.getValuta());
			tableRow = new Vector();
			tableRow.addElement(belegNr);
			tableRow.addElement(buchungstext);
			tableRow.addElement(valutaDatum);
			betrag = buchung.getBetrag();
			if (buchung.getSoll().booleanValue()) {
				betragHaben = "0,00";
				betragSoll = currencyFormatter.format(betrag.floatValue() / 100.0);
			} else {
				betragSoll = "0,00";
				betragHaben = currencyFormatter.format(betrag.floatValue() / 100.0);
			}
			tableRow.addElement(betragSoll);
			tableRow.addElement(betragHaben);
			ktoNr = buchung.getKontonr();
			ktoBez = buchung.getBezeichnung();
			tableRow.addElement(ktoNr);
			tableRow.addElement(ktoBez);
			journalList.addElement(tableRow);
		}
		return journalList;
	}

}

/*
 *  $Log: JournalTable.java,v $
 *  Revision 1.6  2005/12/14 19:29:00  phormanns
 *  Eigene TableModels für JournalTable, KontoTable
 *
 *  Revision 1.5  2005/11/24 17:43:05  phormanns
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
 *  Revision 1.1  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 */
