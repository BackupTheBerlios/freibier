// $Id: DialogState.java,v 1.1 2005/02/18 22:17:42 phormanns Exp $
package de.jalin.freibier.webgui;


public class DialogState {

	private String tableName;
	private int firstRowNumber;
	private int maxRowNumber;
	private int rowsPerPage;
	private String editRecordKey;
	private String orderByColumn;
	private boolean ascending = true;
	private boolean editMode = false;
	
	public boolean isAscending() {
		return ascending;
	}
	
	public int getFirstRowNumber() {
		return firstRowNumber;
	}

	public void setFirstRowNumber(int firstRowNumber) {
		this.firstRowNumber = firstRowNumber;
		if (this.firstRowNumber < 1) this.firstRowNumber = 1;
		if (this.maxRowNumber < this.firstRowNumber) this.firstRowNumber = this.maxRowNumber;
	}

	public int getMaxRowNumber() {
		return maxRowNumber;
	}
	
	public void setMaxRowNumber(int maxRowNumber) {
		this.maxRowNumber = maxRowNumber;
		if (this.firstRowNumber > this.maxRowNumber) {
			this.setFirstRowNumber(this.maxRowNumber - rowsPerPage);
		}
	}
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowPerPage) {
		this.rowsPerPage = rowPerPage;
	}

	public String getOrderByColumn() {
		return orderByColumn;
	}

	public void setOrderByColumn(String orderBy) {
		if (orderBy != null && orderBy.equals(orderByColumn)) {
			ascending = !ascending;
			if (ascending) {
				this.setFirstRowNumber(1);
			} else {
				this.setFirstRowNumber(this.getMaxRowNumber());
			}
		} else {
			ascending = true;
			this.setFirstRowNumber(1);
		}
		this.orderByColumn = orderBy;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void updateFromRequest(String tableName, String orderByColumn, String page, String edit) {
		if (tableName != null) {
			setTableName(tableName);
			setFirstRowNumber(1);
			setOrderByColumn(null);
		}
		if (orderByColumn != null) {
			setOrderByColumn(orderByColumn);
		}
		if (ascending) {
			if (page != null && page.equals("up")) {
				setFirstRowNumber(getFirstRowNumber() - getRowsPerPage());
			}
			if (page != null && page.equals("down")) {
				setFirstRowNumber(getFirstRowNumber() + getRowsPerPage());
			}
		} else {
			if (page != null && page.equals("up")) {
				setFirstRowNumber(getFirstRowNumber() + getRowsPerPage());
			}
			if (page != null && page.equals("down")) {
				setFirstRowNumber(getFirstRowNumber() - getRowsPerPage());
			}
		}
		editRecordKey = edit;
		editMode = editRecordKey != null;
	}

	public boolean isEditModeOn() {
		return editMode;
	}
	
	public void setEditModeOff() {
		editRecordKey = null;
		editMode = false;
	}

	public String getRecordKey() {
		return editRecordKey;
	}
}

/*
 * $Log: DialogState.java,v $
 * Revision 1.1  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.3  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.2  2005/02/13 20:27:14  phormanns
 * Funktioniert bis auf Filter
 *
 * Revision 1.1  2004/12/31 17:13:11  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.2  2004/12/19 13:24:06  phormanns
 * Web-Version des Tabellen-Browsers mit Edit, Filter (1x)
 *
 * Revision 1.1  2004/12/17 22:31:17  phormanns
 * Erste Web-Version des Tabellen-Browsers
 *
 */