// $Id: JournaleForm.java,v 1.1 2005/11/10 21:19:26 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import de.bayen.fibu.Journal;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.tree.Editable;

public class JournaleForm implements Editable {
	
	private FiBuFacade fibu;
	private JTable table;
	private boolean nurOffene;
	
	public JournaleForm(FiBuFacade fibu, boolean nurOffene) {
		this.fibu = fibu;
		this.nurOffene = nurOffene;
	}

	public boolean validateAndSave() throws FiBuException {
		return true;
	}

	public Component getEditor() throws FiBuException {
		final Vector journaleVector = new Vector();
		if (nurOffene) {
			journaleVector.addAll(fibu.getOffeneJournale());
		} else {
			journaleVector.addAll(fibu.getAlleJournale());
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
			JButton btnClose = new JButton("Journal schlie�en");
			btnNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent pressedNew) {
					try {
						journaleVector.addElement(fibu.neuesJournal());
						table.revalidate();
						table.repaint();
					} catch (FiBuException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent pressedClose) {
					int selRow = table.getSelectedRow();
					if (selRow >= 0) {
						Journal jour = (Journal) journaleVector.get(selRow);
						try {
							fibu.absummieren(jour);
							journaleVector.remove(jour);
							table.revalidate();
							table.repaint();
						} catch (FiBuException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
			Journal journ = (Journal) journaleVector.get(rowIndex);
			Object value = "";
			switch (columnIndex) {
				case 0:
					value = journ.getJournalnummer().toString();
					break;
				case 1:
					value = journ.getBuchungsperiode();
					break;
				case 2:
					value = journ.getBuchungsjahr();
					break;
				case 3:
					value = journ.getStartdatum();
					break;
				default:
					value = "";
					break;
			}
			return value;
		}

		public String getColumnName(int columnIndex) {
			String value = "";
			switch (columnIndex) {
				case 0:
					value = "Nr.";
					break;
				case 1:
					value = "Periode";
					break;
				case 2:
					value = "Jahr";
					break;
				case 3:
					value = "ab";
					break;
				default:
					value = "";
					break;
			}
			return value;
		}

	}
}

/*
 *  $Log: JournaleForm.java,v $
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */