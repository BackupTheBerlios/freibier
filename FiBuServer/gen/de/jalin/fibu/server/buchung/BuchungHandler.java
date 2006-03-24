// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchung;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungHandler extends AbstractHandler {

	private BuchungBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungHandler(BuchungBackend backend) {
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("buchid", 1);
		this.display.addColumnDefinition("belegnr", 1);
		this.display.addColumnDefinition("buchungstext", 1);
		this.display.addColumnDefinition("jourid", 1);
		this.display.addColumnDefinition("valuta", 1);
		this.display.addColumnDefinition("erfassung", 2);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("buchid");
		this.orderBy.addSelectableColumn("belegnr");
		this.orderBy.addSelectableColumn("buchungstext");
		this.orderBy.addSelectableColumn("jourid");
		this.orderBy.addSelectableColumn("valuta");
		this.orderBy.addSelectableColumn("erfassung");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callBuchungListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("add".equals(functionName)) {
	    		return callBuchungAddCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("update".equals(functionName)) {
	    		return callBuchungUpdateCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("delete".equals(functionName)) {
	    		return callBuchungDeleteCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callBuchungListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungData whereData = new BuchungData();
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
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buchid}", whereData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeBuchungListCall(
					dbConnect
					, session
					, (BuchungData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}

	public Vector callBuchungAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungData writeData = new BuchungData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buchid}", writeData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungAddCall(
					dbConnect
					, session
					, (BuchungData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callBuchungUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungData writeData = new BuchungData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		BuchungData whereData = new BuchungData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buchid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungUpdateCall(
					dbConnect
					, session
					, writeData
					, (BuchungData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callBuchungDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungData whereData = new BuchungData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buchid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungDeleteCall(
					dbConnect
					, session
					, (BuchungData) objIterator.next()
				);
		}
		return listResult;
	}
}
