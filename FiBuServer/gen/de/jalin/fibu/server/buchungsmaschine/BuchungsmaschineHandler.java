// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsmaschine;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungsmaschineHandler extends AbstractHandler {

	private BuchungsmaschineBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungsmaschineHandler(BuchungsmaschineBackend backend) {
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("buzlid", 1);
		this.display.addColumnDefinition("sollkontonr", 1);
		this.display.addColumnDefinition("habenkontonr", 1);
		this.display.addColumnDefinition("sollmwstid", 1);
		this.display.addColumnDefinition("habenmwstid", 1);
		this.display.addColumnDefinition("brutto", 1);
		this.display.addColumnDefinition("belegnr", 1);
		this.display.addColumnDefinition("buchungstext", 1);
		this.display.addColumnDefinition("jourid", 1);
		this.display.addColumnDefinition("valuta", 1);
		this.orderBy = new OrderByList();
	}

	public Vector call(Connection dbConnect, XmlRpcSession session,
			String functionName, XmlRpcTransactionParams callParamLists)
			throws XmlRpcTransactionException {
		if("add".equals(functionName)) {
	    		return callBuchungsmaschineAddCall(
	    			dbConnect, 
	    			session,
	    			callParamLists);
		}
		throw new ServerException(9003);
	}

	public Vector callBuchungsmaschineAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		XmlRpcTransactionParams callParamLists)
	   		throws XmlRpcTransactionException {
	   	Vector listResult = new Vector();
		BuchungsmaschineData writeData = new BuchungsmaschineData();
		readPropertiesVector(callParamLists.getSetProps(), writeData);
		Iterator objIterator = parseObjectIdList(callParamLists.getObjectIds(), "{buzlid}", writeData).iterator();
		while (objIterator.hasNext()) {
				backend.executeBuchungsmaschineAddCall(
					dbConnect
					, session
					, (BuchungsmaschineData) objIterator.next()
				);
		}
		return listResult;
	}
}
