// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsliste;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungslisteWebGUI extends AbstractWebGUI {

	private static final long serialVersionUID = 1164399841206L;

	private PostgresAccess pgAccess;
	private BuchungslisteBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungslisteWebGUI(BuchungslisteBackend backend) throws XmlRpcTransactionException {
		pgAccess = PostgresAccess.getInstance();
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

	public void prepare(String module, String function, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map params = new HashMap();
		ModuleProperties moduleProperties = getModuleProperties();
		params.put("menu", request.getSession().getAttribute("menu"));
		params.put("props", moduleProperties);
		params.put("modulename", module);
		params.put("functionname", function);
		params.put("function", moduleProperties.getFunction(function));
		try {
			response.getWriter().print(mergeTemplate("params.vm", params));
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public void execute(String functionName, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			Connection dbConnect = pgAccess.getConnection();
			try {
				dbConnect.setAutoCommit(false);
				XmlRpcSession session = getSession(request, response);
				if("list".equals(functionName)) {
		    		callBuchungslisteListCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				dbConnect.commit();
				dbConnect.setAutoCommit(true);
			} catch (BuchungslisteException e) {
				dbConnect.rollback();
				dbConnect.setAutoCommit(true);
			} catch (XmlRpcTransactionException e) {
				dbConnect.rollback();
				dbConnect.setAutoCommit(true);
				throw new ServletException(e);
			}
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
		props.addProperty("istsoll", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("isthaben", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("istaktiv", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("istpassiv", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("istaufwand", "bool", "implicit", "yes", "no", "optional");
		props.addProperty("istertrag", "bool", "implicit", "yes", "no", "optional");
		props.addFunction("buchungsliste", "list", true, true, true, false, true, true);
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
		try {
			Vector resultList = 
				backend.executeBuchungslisteListCall(
					dbConnect
					, session
					, whereData
					, display
					, orderBy
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungsliste");
			params.put("functionname", "list");
			params.put("headers", resultList.get(0));
			params.put("rows", resultList.get(1));
			templateName = "liste.vm";
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungslisteException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungsliste");
			params.put("functionname", "list");
			params.put("errorcode", new Integer(e.code));
			params.put("errormsg", e.getMessage());
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e1) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
			throw e;
		}
	}

}
