// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class KontoHandler extends AbstractHandler {

	private KontoBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public KontoHandler(KontoBackend backend) {
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("kontoid", 1);
		this.display.addColumnDefinition("kontonr", 1);
		this.display.addColumnDefinition("bezeichnung", 1);
		this.display.addColumnDefinition("mwstid", 1);
		this.display.addColumnDefinition("oberkonto", 1);
		this.display.addColumnDefinition("istsoll", 1);
		this.display.addColumnDefinition("isthaben", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("kontoid");
		this.orderBy.addSelectableColumn("kontonr");
		this.orderBy.addSelectableColumn("bezeichnung");
		this.orderBy.addSelectableColumn("mwstid");
		this.orderBy.addSelectableColumn("oberkonto");
		this.orderBy.addSelectableColumn("istsoll");
		this.orderBy.addSelectableColumn("isthaben");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callKontoListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("add".equals(functionName)) {
	    		return callKontoAddCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("update".equals(functionName)) {
	    		return callKontoUpdateCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("delete".equals(functionName)) {
	    		return callKontoDeleteCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callKontoListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		KontoData whereData = new KontoData();
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
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{kontoid}", whereData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeKontoListCall(
					dbConnect
					, session
					, (KontoData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}

	public Vector callKontoAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		KontoData writeData = new KontoData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{kontoid}", writeData).iterator();
		while (objIterator.hasNext()) {
				backend.executeKontoAddCall(
					dbConnect
					, session
					, (KontoData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callKontoUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		KontoData writeData = new KontoData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		KontoData whereData = new KontoData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{kontoid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeKontoUpdateCall(
					dbConnect
					, session
					, writeData
					, (KontoData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callKontoDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		KontoData whereData = new KontoData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{kontoid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeKontoDeleteCall(
					dbConnect
					, session
					, (KontoData) objIterator.next()
				);
		}
		return listResult;
	}
}
