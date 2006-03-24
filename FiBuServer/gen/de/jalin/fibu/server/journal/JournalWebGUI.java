// Generiert mit xmlrpcgen

package de.jalin.fibu.server.journal;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class JournalWebGUI extends AbstractWebGUI {

	private JournalBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public JournalWebGUI(JournalBackend backend) {
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
			Connection dbConnect = PostgresAccess.getInstance().getConnection();
			XmlRpcSession session = new XmlRpcSession(request.getRemoteUser());
			if("list".equals(functionName)) {
	    		callJournalListCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
			if("add".equals(functionName)) {
	    		callJournalAddCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
			if("update".equals(functionName)) {
	    		callJournalUpdateCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
			if("delete".equals(functionName)) {
	    		callJournalDeleteCall(
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
		ModuleProperties props = new ModuleProperties("journal");
		props.addProperty("jourid", "int", "implicit", "yes", "no", "auto");
		props.addProperty("journr", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("jahr", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("periode", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("since", "date", "implicit", "yes", "yes", "auto");
		props.addProperty("lastupdate", "date", "explicit", "yes", "no", "auto");
		props.addProperty("absummiert", "bool", "implicit", "yes", "yes", "auto");
		props.addFunction("journal", "list", true, true, true, false, true, true);
		props.addFunction("journal", "add", false, false, false, true, true, true);
		props.addFunction("journal", "update", false, true, false, true, true, false);
		props.addFunction("journal", "delete", false, true, false, false, true, false);
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("journal");
		menu.addItem("list");
		menu.addItem("add");
		menu.addItem("update");
		menu.addItem("delete");
	    return menu;
	}

	public void callJournalListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		JournalData whereData = (JournalData) getWhereData(request, new JournalData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		Vector resultList = 
			backend.executeJournalListCall(
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
			throw new XmlRpcTransactionException(ErrorCode.TEMPLATE_ERROR_CODE, e.getMessage());
		}
	}

	public void callJournalAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		JournalData writeData = (JournalData) getWriteData(request, new JournalData());
		Vector resultList = 
			backend.executeJournalAddCall(
				dbConnect
				, session
				, writeData
			);
		Map params = new HashMap();
		params.put("headers", resultList.get(0));
		params.put("rows", resultList.get(1));
		params.put("menu", request.getSession().getAttribute("menu"));
		try {
			response.getWriter().print(mergeTemplate("liste.vm", params));
		} catch (Exception e) {
			throw new XmlRpcTransactionException(ErrorCode.TEMPLATE_ERROR_CODE, e.getMessage());
		}
	}

	public void callJournalUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		JournalData writeData = (JournalData) getWriteData(request, new JournalData());
		JournalData whereData = (JournalData) getWhereData(request, new JournalData());
			backend.executeJournalUpdateCall(
				dbConnect
				, session
				, writeData
				, whereData
			);
	}

	public void callJournalDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		JournalData whereData = (JournalData) getWhereData(request, new JournalData());
			backend.executeJournalDeleteCall(
				dbConnect
				, session
				, whereData
			);
	}

}
