// Generiert mit xmlrpcgen

package de.jalin.fibu.server.mwst;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class MwstHandler extends AbstractHandler {

	private MwstBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;
	private ModuleProperties moduleDescription;

	public ModuleProperties getModuleProperties() {
		return moduleDescription;
	}

	public MwstHandler(MwstBackend backend) {
	    this.moduleDescription = new ModuleProperties("mwst");
	    this.moduleDescription.addProperty("mwstid", "int", "implicit", "yes", "no", "auto");
	    this.moduleDescription.addProperty("mwstsatz", "int", "implicit", "yes", "once", "mandatory");
	    this.moduleDescription.addProperty("mwsttext", "string", "implicit", "yes", "yes", "mandatory");
	    this.moduleDescription.addProperty("mwstkontosoll", "int", "implicit", "yes", "once", "mandatory");
	    this.moduleDescription.addProperty("mwstkontohaben", "int", "implicit", "yes", "once", "mandatory");
	    this.moduleDescription.addProperty("mwstsatzaktiv", "bool", "explicit", "yes", "yes", "auto");
	    this.moduleDescription.addFunction("mwst", "list", true, true, true, false, true, true);
	    this.moduleDescription.addFunction("mwst", "add", false, false, false, true, true, false);
	    this.moduleDescription.addFunction("mwst", "update", false, true, false, true, true, false);
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("mwstid", 1);
		this.display.addColumnDefinition("mwstsatz", 1);
		this.display.addColumnDefinition("mwsttext", 1);
		this.display.addColumnDefinition("mwstkontosoll", 1);
		this.display.addColumnDefinition("mwstkontohaben", 1);
		this.display.addColumnDefinition("mwstsatzaktiv", 2);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("mwstid");
		this.orderBy.addSelectableColumn("mwstsatz");
		this.orderBy.addSelectableColumn("mwsttext");
		this.orderBy.addSelectableColumn("mwstkontosoll");
		this.orderBy.addSelectableColumn("mwstkontohaben");
		this.orderBy.addSelectableColumn("mwstsatzaktiv");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callMwstListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("add".equals(functionName)) {
	    		return callMwstAddCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		if("update".equals(functionName)) {
	    		return callMwstUpdateCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callMwstListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		MwstData whereData = new MwstData();
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
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "mwstid", whereData).iterator();
		while (objIterator.hasNext()) {
			appendResult(listResult,
				backend.executeMwstListCall(
					dbConnect
					, session
					, (MwstData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}

	public Vector callMwstAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		MwstData writeData = new MwstData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "mwstid", writeData).iterator();
		while (objIterator.hasNext()) {
				backend.executeMwstAddCall(
					dbConnect
					, session
					, (MwstData) objIterator.next()
				);
		}
		return listResult;
	}

	public Vector callMwstUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		MwstData writeData = new MwstData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		MwstData whereData = new MwstData();
		readPropertiesVector(callParamLists.getWhereProps(), whereData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "mwstid", whereData).iterator();
		while (objIterator.hasNext()) {
				backend.executeMwstUpdateCall(
					dbConnect
					, session
					, writeData
					, (MwstData) objIterator.next()
				);
		}
		return listResult;
	}
}
