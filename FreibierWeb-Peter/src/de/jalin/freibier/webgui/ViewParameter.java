// $Id: ViewParameter.java,v 1.2 2005/02/13 20:27:14 phormanns Exp $
package de.jalin.freibier.webgui;


public class ViewParameter {

	private String tableName;
	private int firstRowNumber;
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
		} else {
			ascending = true;
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
 * $Log: ViewParameter.java,v $
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