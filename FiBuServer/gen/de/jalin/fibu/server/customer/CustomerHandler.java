// Generiert mit xmlrpcgen

package de.jalin.fibu.server.customer;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class CustomerHandler extends AbstractHandler {

	private CustomerBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;
	private ModuleProperties moduleDescription;

	public ModuleProperties getModuleProperties() {
		return moduleDescription;
	}

	public CustomerHandler(CustomerBackend backend) {
	    this.moduleDescription = new ModuleProperties("customer");
	    this.moduleDescription.addProperty("custid", "int", "implicit", "yes", "no", "auto");
	    this.moduleDescription.addProperty("firma", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("bilanzkonto", "int", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("guvkonto", "int", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("jahr", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("periode", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("since", "date", "explicit", "no", "no", "auto");
	    this.moduleDescription.addProperty("lastupdate", "date", "explicit", "no", "no", "auto");
	    this.moduleDescription.addFunction("customer", "list", true, true, true, false, true, true);
	    this.moduleDescription.addFunction("customer", "update", false, true, false, true, true, false);
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("custid", 1);
		this.display.addColumnDefinition("firma", 1);
		this.display.addColumnDefinition("bilanzkonto", 1);
		this.display.addColumnDefinition("guvkonto", 1);
		this.display.addColumnDefinition("jahr", 1);
		this.display.addColumnDefinition("periode", 1);
		this.display.addColumnDefinition("since", 2);
		this.display.addColumnDefinition("lastupdate", 2);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("custid");
		this.orderBy.addSelectableColumn("firma");
		this.orderBy.addSelectableColumn("bilanzkonto");
		this.orderBy.addSelectableColumn("guvkonto");
		this.orderBy.addSelectableColumn("jahr");
		this.orderBy.addSelectableColumn("periode");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callCustomerListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("update".equals(functionName)) {
	    		return callCustomerUpdateCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callCustomerListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		CustomerData whereData = new CustomerData();
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
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{custid}", whereData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeCustomerListCall(
					dbConnect
					, session
					, (CustomerData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}

	public Vector callCustomerUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		CustomerData writeData = new CustomerData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		CustomerData whereData = new CustomerData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{custid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeCustomerUpdateCall(
					dbConnect
					, session
					, writeData
					, (CustomerData) objIterator.next()
				);
		}
		return listResult;
	}
}
