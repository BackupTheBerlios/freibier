// $Id: ViewParameter.java,v 1.1 2004/12/31 17:13:11 phormanns Exp $
package de.jalin.freibier.webgui;


public class ViewParameter {

	private String tableName;
	private int firstRowNumber;
	private int numberOfRows;
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
		if (this.firstRowNumber < 0) this.firstRowNumber = 0;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
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
			setFirstRowNumber(0);
			setOrderByColumn(null);
		}
		if (orderByColumn != null) {
			setOrderByColumn(orderByColumn);
		}
		if (page != null && page.equals("up")) {
			setFirstRowNumber(getFirstRowNumber() - getNumberOfRows());
		}
		if (page != null && page.equals("down")) {
			setFirstRowNumber(getFirstRowNumber() + getNumberOfRows());
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