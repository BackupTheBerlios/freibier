// $Id: FiBuTable.java,v 1.1 2005/12/14 19:29:00 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public abstract class FiBuTable {

	protected TableColumn createTableColumn(int columnNum, String title, int defaultWidth, int alignment) {
		TableColumn column = new TableColumn(columnNum);
		column.setHeaderValue(title);
		column.setPreferredWidth(defaultWidth);
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(alignment);
		column.setCellRenderer(cellRenderer);
		return column;
	}
	
	public class FibuTableColumnModel extends DefaultTableColumnModel implements
			TableColumnModel {
		private static final long serialVersionUID = -2774963420748150729L;

		public FibuTableColumnModel(Vector columns) {
			super();
			Enumeration columnsEnum = columns.elements();
			while (columnsEnum.hasMoreElements()) {
				TableColumn column = (TableColumn) columnsEnum.nextElement();
				addColumn(column);
			}
		}
	}

	public class FibuTableModel extends AbstractTableModel implements
			TableModel {
		private static final long serialVersionUID = 9016673057910270828L;
		private Vector rows;
		private TableColumnModel tableColumnModel;

		public FibuTableModel(Vector rows, TableColumnModel tabelColumnModel) {
			super();
			this.rows = rows;
			this.tableColumnModel = tabelColumnModel;
		}

		public int getColumnCount() {
			return tableColumnModel.getColumnCount();
		}

		public int getRowCount() {
			return rows.size();
		}

		public Object getValueAt(int row, int column) {
			return ((Vector) rows.get(row)).get(column);
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}

/*
 *  $Log: FiBuTable.java,v $
 *  Revision 1.1  2005/12/14 19:29:00  phormanns
 *  Eigene TableModels für JournalTable, KontoTable
 *
 */
