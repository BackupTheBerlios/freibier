package de.jalin.fibu.server.buchungszeile;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungszeileHandler extends AbstractHandler {

	private BuchungszeileBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungszeileHandler(BuchungszeileBackend backend) {
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("buzlid", 1);
		this.display.addColumnDefinition("buchid", 1);
		this.display.addColumnDefinition("kontoid", 1);
		this.display.addColumnDefinition("betrag", 1);
		this.display.addColumnDefinition("soll", 1);
		this.display.addColumnDefinition("haben", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("buzlid");
		this.orderBy.addSelectableColumn("buchid");
		this.orderBy.addSelectableColumn("kontoid");
		this.orderBy.addSelectableColumn("betrag");
		this.orderBy.addSelectableColumn("soll");
		this.orderBy.addSelectableColumn("haben");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callBuchungszeileListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("add".equals(functionName)) {
	    		return callBuchungszeileAddCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("update".equals(functionName)) {
	    		return callBuchungszeileUpdateCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("delete".equals(functionName)) {
	    		return callBuchungszeileDeleteCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callBuchungszeileListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungszeileData whereData = new BuchungszeileData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		display.reset();
		String displayProp = null;
		Enumeration displayPropEnum = callParamLists.getDisplayColumns().elements();
		while (displayPropEnum.hasMoreElements()) {
			displayProp = (String) displayPropEnum.nextElement();
			display.addDisplayProperty((String) displayProp);
		}
		orderBy.reset();
		Enumeration orderByEnum = callParamLists.getOrderColumns().elements();
		Hashtable orderByHash = null;
		while (orderByEnum.hasMoreElements()) {
			orderByHash = (Hashtable) orderByEnum.nextElement();
			orderBy.addOrderByColumn(
					new OrderColumn(
							(String) orderByHash.get("property"), 
							((Boolean) orderByHash.get("ascending")).booleanValue()
					)
			);
		}
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buzlid}", whereData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeBuchungszeileListCall(
					dbConnect
					, session
					, (BuchungszeileData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}

	public Vector callBuchungszeileAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungszeileData writeData = new BuchungszeileData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buzlid}", writeData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungszeileAddCall(
					dbConnect
					, session
					, (BuchungszeileData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callBuchungszeileUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungszeileData writeData = new BuchungszeileData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		BuchungszeileData whereData = new BuchungszeileData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buzlid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungszeileUpdateCall(
					dbConnect
					, session
					, writeData
					, (BuchungszeileData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callBuchungszeileDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungszeileData whereData = new BuchungszeileData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buzlid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungszeileDeleteCall(
					dbConnect
					, session
					, (BuchungszeileData) objIterator.next()
				);
		}
		return listResult;
	}
}
