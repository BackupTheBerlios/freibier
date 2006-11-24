// Generiert mit xmlrpcgen

package de.jalin.fibu.server.journal;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class JournalHandler extends AbstractHandler {

	private JournalBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;
	private ModuleProperties moduleDescription;

	public ModuleProperties getModuleProperties() {
		return moduleDescription;
	}

	public JournalHandler(JournalBackend backend) {
	    this.moduleDescription = new ModuleProperties("journal");
	    this.moduleDescription.addProperty("jourid", "int", "implicit", "yes", "no", "auto");
	    this.moduleDescription.addProperty("journr", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("jahr", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("periode", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("since", "date", "implicit", "yes", "yes", "auto");
	    this.moduleDescription.addProperty("lastupdate", "date", "explicit", "yes", "no", "auto");
	    this.moduleDescription.addProperty("absummiert", "bool", "implicit", "yes", "yes", "auto");
	    this.moduleDescription.addFunction("journal", "list", true, true, true, false, true, true);
	    this.moduleDescription.addFunction("journal", "add", false, false, false, true, true, true);
	    this.moduleDescription.addFunction("journal", "update", false, true, false, true, true, false);
	    this.moduleDescription.addFunction("journal", "delete", false, true, false, false, true, false);
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("jourid", 1);
		this.display.addColumnDefinition("journr", 1);
		this.display.addColumnDefinition("jahr", 1);
		this.display.addColumnDefinition("periode", 1);
		this.display.addColumnDefinition("since", 1);
		this.display.addColumnDefinition("lastupdate", 2);
		this.display.addColumnDefinition("absummiert", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("jourid");
		this.orderBy.addSelectableColumn("journr");
		this.orderBy.addSelectableColumn("jahr");
		this.orderBy.addSelectableColumn("periode");
		this.orderBy.addSelectableColumn("since");
		this.orderBy.addSelectableColumn("lastupdate");
		this.orderBy.addSelectableColumn("absummiert");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callJournalListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("add".equals(functionName)) {
	    		return callJournalAddCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("update".equals(functionName)) {
	    		return callJournalUpdateCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("delete".equals(functionName)) {
	    		return callJournalDeleteCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callJournalListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		JournalData whereData = new JournalData();
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
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{jourid}", whereData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeJournalListCall(
					dbConnect
					, session
					, (JournalData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}

	public Vector callJournalAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		JournalData writeData = new JournalData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{jourid}", writeData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeJournalAddCall(
					dbConnect
					, session
					, (JournalData) objIterator.next()
					)
				);
		}
		return listResult;
	}

	public Vector callJournalUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		JournalData writeData = new JournalData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		JournalData whereData = new JournalData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{jourid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeJournalUpdateCall(
					dbConnect
					, session
					, writeData
					, (JournalData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callJournalDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		JournalData whereData = new JournalData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{jourid}", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeJournalDeleteCall(
					dbConnect
					, session
					, (JournalData) objIterator.next()
				);
		}
		return listResult;
	}
}
