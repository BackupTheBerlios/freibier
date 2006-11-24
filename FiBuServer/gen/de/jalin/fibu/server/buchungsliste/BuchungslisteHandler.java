// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsliste;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungslisteHandler extends AbstractHandler {

	private BuchungslisteBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;
	private ModuleProperties moduleDescription;

	public ModuleProperties getModuleProperties() {
		return moduleDescription;
	}

	public BuchungslisteHandler(BuchungslisteBackend backend) {
	    this.moduleDescription = new ModuleProperties("buchungsliste");
	    this.moduleDescription.addProperty("buzlid", "int", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("betrag", "int", "implicit", "no", "no", "optional");
	    this.moduleDescription.addProperty("soll", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("haben", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("buchid", "int", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("belegnr", "string", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("buchungstext", "string", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("jourid", "int", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("journr", "string", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("jahr", "string", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("periode", "string", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("since", "date", "implicit", "no", "no", "optional");
	    this.moduleDescription.addProperty("absummiert", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("valuta", "date", "implicit", "no", "no", "optional");
	    this.moduleDescription.addProperty("erfassung", "date", "implicit", "no", "no", "optional");
	    this.moduleDescription.addProperty("kontoid", "int", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("kontonr", "string", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("bezeichnung", "string", "implicit", "no", "no", "optional");
	    this.moduleDescription.addProperty("istsoll", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("isthaben", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("istaktiv", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("istpassiv", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("istaufwand", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addProperty("istertrag", "bool", "implicit", "yes", "no", "optional");
	    this.moduleDescription.addFunction("buchungsliste", "list", true, true, true, false, true, true);
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("buzlid", 1);
		this.display.addColumnDefinition("betrag", 1);
		this.display.addColumnDefinition("soll", 1);
		this.display.addColumnDefinition("haben", 1);
		this.display.addColumnDefinition("buchid", 1);
		this.display.addColumnDefinition("belegnr", 1);
		this.display.addColumnDefinition("buchungstext", 1);
		this.display.addColumnDefinition("jourid", 1);
		this.display.addColumnDefinition("journr", 1);
		this.display.addColumnDefinition("jahr", 1);
		this.display.addColumnDefinition("periode", 1);
		this.display.addColumnDefinition("since", 1);
		this.display.addColumnDefinition("absummiert", 1);
		this.display.addColumnDefinition("valuta", 1);
		this.display.addColumnDefinition("erfassung", 1);
		this.display.addColumnDefinition("kontoid", 1);
		this.display.addColumnDefinition("kontonr", 1);
		this.display.addColumnDefinition("bezeichnung", 1);
		this.display.addColumnDefinition("istsoll", 1);
		this.display.addColumnDefinition("isthaben", 1);
		this.display.addColumnDefinition("istaktiv", 1);
		this.display.addColumnDefinition("istpassiv", 1);
		this.display.addColumnDefinition("istaufwand", 1);
		this.display.addColumnDefinition("istertrag", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("buzlid");
		this.orderBy.addSelectableColumn("soll");
		this.orderBy.addSelectableColumn("haben");
		this.orderBy.addSelectableColumn("buchid");
		this.orderBy.addSelectableColumn("belegnr");
		this.orderBy.addSelectableColumn("buchungstext");
		this.orderBy.addSelectableColumn("jourid");
		this.orderBy.addSelectableColumn("journr");
		this.orderBy.addSelectableColumn("jahr");
		this.orderBy.addSelectableColumn("periode");
		this.orderBy.addSelectableColumn("absummiert");
		this.orderBy.addSelectableColumn("kontoid");
		this.orderBy.addSelectableColumn("kontonr");
		this.orderBy.addSelectableColumn("istsoll");
		this.orderBy.addSelectableColumn("isthaben");
		this.orderBy.addSelectableColumn("istaktiv");
		this.orderBy.addSelectableColumn("istpassiv");
		this.orderBy.addSelectableColumn("istaufwand");
		this.orderBy.addSelectableColumn("istertrag");
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("list".equals(functionName)) {
	    		return callBuchungslisteListCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callBuchungslisteListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungslisteData whereData = new BuchungslisteData();
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
				backend.executeBuchungslisteListCall(
					dbConnect
					, session
					, (BuchungslisteData) objIterator.next()
					, display
					, orderBy
					)
				);
		}
		return listResult;
	}
}
