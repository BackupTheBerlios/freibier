package de.jalin.fibu.server.buchungsliste;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungslisteWebGUI extends AbstractWebGUI {

	private BuchungslisteBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungslisteWebGUI(BuchungslisteBackend backend) {
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
	}

	public void prepare(String function, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map params = new HashMap();
		params.put("menu", request.getSession().getAttribute("menu"));
		params.put("props", getModuleProperties());
		try {
			response.getWriter().print(mergeTemplate("params.vm", params));
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public void execute(String functionName, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			Connection dbConnect = PostgresAccess.getInstance().getConnection();
			XmlRpcSession session = new XmlRpcSession(request.getRemoteUser());
			if("list".equals(functionName)) {
	    		callBuchungslisteListCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
		} catch (XmlRpcTransactionException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	public ModuleProperties getModuleProperties() {
		ModuleProperties props = new ModuleProperties("buchungsliste");
		props.addProperty("buzlid", "int", "implicit", "yes", "no", "optional");
		props.addProperty("betrag", "int", "implicit", "no", "no", "optional");
		props.addProperty("soll", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("haben", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("buchid", "int", "implicit", "yes", "no", "optional");
		props.addProperty("belegnr", "string", "implicit", "yes", "no", "optional");
		props.addProperty("buchungstext", "string", "implicit", "yes", "no", "optional");
		props.addProperty("jourid", "int", "implicit", "yes", "no", "optional");
		props.addProperty("journr", "string", "implicit", "yes", "no", "optional");
		props.addProperty("jahr", "string", "implicit", "yes", "no", "optional");
		props.addProperty("periode", "string", "implicit", "yes", "no", "optional");
		props.addProperty("since", "date", "implicit", "no", "no", "optional");
		props.addProperty("absummiert", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("valuta", "date", "implicit", "no", "no", "optional");
		props.addProperty("erfassung", "date", "implicit", "no", "no", "optional");
		props.addProperty("kontoid", "int", "implicit", "yes", "no", "optional");
		props.addProperty("kontonr", "string", "implicit", "yes", "no", "optional");
		props.addProperty("bezeichnung", "string", "implicit", "no", "no", "optional");
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("buchungsliste");
		menu.addItem("list");
	    return menu;
	}

	public void callBuchungslisteListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungslisteData whereData = (BuchungslisteData) getWhereData(request, new BuchungslisteData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		Vector resultList = 
			backend.executeBuchungslisteListCall(
				dbConnect
				, session
				, whereData
				, display
				, orderBy
			);
		Map params = new HashMap();
		params.put("headers", resultList.get(0));
		params.put("rows", resultList.get(1));
		params.put("menu", request.getSession().getAttribute("menu"));
		try {
			response.getWriter().print(mergeTemplate("liste.vm", params));
		} catch (Exception e) {
			throw new XmlRpcTransactionException(ErrorCode.TEMPLATE_ERROR, e.getMessage());
		}
	}

}
