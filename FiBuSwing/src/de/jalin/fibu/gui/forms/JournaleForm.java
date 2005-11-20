// $Id: JournaleForm.java,v 1.3 2005/11/20 21:29:10 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.server.journal.JournalData;

public class JournaleForm implements Editable {

	private static final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);

	private FiBuGUI gui;
	private FiBuFacade fibu;
	private JTable table;
	private boolean nurOffene;
	
	public JournaleForm(FiBuGUI gui, boolean nurOffene) {
		this.gui = gui;
		this.fibu = gui.getFiBuFacade();
		this.nurOffene = nurOffene;
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		final Vector journaleVector = new Vector();
		try {
			if (nurOffene) {
				journaleVector.addAll(fibu.getOffeneJournale());
			} else {
				journaleVector.addAll(fibu.getAlleJournale());
			}
		} catch (FiBuException e) {
			gui.handleException(e);
		}
		JPanel panel = new JPanel(new BorderLayout());
		JournalTableModel tableModel = new JournalTableModel(journaleVector);
		table = new JTable(tableModel);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(table);
		panel.add(scroll, BorderLayout.CENTER);
		if (nurOffene) {
			JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEADING));
			JButton btnNew = new JButton("Neues Journal");
			JButton btnClose = new JButton("Journal schließen");
			btnNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent pressedNew) {
					try {
						journaleVector.addElement(fibu.neuesJournal());
						table.revalidate();
						table.repaint();
						gui.refreshJournale();
					} catch (FiBuException e) {
						gui.handleException(e);
					}
				}
			});
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent pressedClose) {
					int selRow = table.getSelectedRow();
					if (selRow >= 0) {
						JournalData jour = (JournalData) journaleVector.get(journaleVector.size() - 1 - selRow);
						try {
							fibu.absummieren(jour);
							journaleVector.remove(jour);
							table.revalidate();
							table.repaint();
							gui.refreshJournale();
						} catch (FiBuException e) {
							gui.handleException(e);
						}
					}
				}
			});
			btnBar.add(btnNew);
			btnBar.add(btnClose);
			panel.add(btnBar, BorderLayout.SOUTH);
		}
		return panel;
	}
	
	class JournalTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1390366082879281639L;

		private Vector journaleVector;
		
		JournalTableModel(Vector journaleVector) {
			this.journaleVector = journaleVector;
		}
		
		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return journaleVector.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			JournalData journ = (JournalData) journaleVector.get(getRowCount() - 1 - rowIndex);
			Object value = "";
			switch (columnIndex) {
				case 0: value = journ.getJournr().toString(); break;
				case 1: value = journ.getPeriode(); break;
				case 2: value = journ.getJahr(); break;
				case 3: value = dateFormatter.format(journ.getSince()); break;
				default: value = ""; break;
			}
			return value;
		}

		public String getColumnName(int columnIndex) {
			String value = "";
			switch (columnIndex) {
				case 0: value = "Nr."; break;
				case 1: value = "Periode"; break;
				case 2: value = "Jahr"; break;
				case 3: value = "ab"; break;
				default: value = ""; break;
			}
			return value;
		}

	}
}

/*
 *  $Log: JournaleForm.java,v $
 *  Revision 1.3  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
