// $Id: FiBuTable.java,v 1.2 2006/02/24 22:24:22 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */
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
 *  Revision 1.2  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/12/14 19:29:00  phormanns
 *  Eigene TableModels für JournalTable, KontoTable
 *
 */
