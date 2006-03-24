// Generiert mit xmlrpcgen

package de.jalin.fibu.server.mwst;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class MwstWebGUI extends AbstractWebGUI {

	private MwstBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public MwstWebGUI(MwstBackend backend) {
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
	    		callMwstListCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
			if("add".equals(functionName)) {
	    		callMwstAddCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
			if("update".equals(functionName)) {
	    		callMwstUpdateCall(
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
		ModuleProperties props = new ModuleProperties("mwst");
		props.addProperty("mwstid", "int", "implicit", "yes", "no", "auto");
		props.addProperty("mwstsatz", "int", "implicit", "yes", "once", "mandatory");
		props.addProperty("mwsttext", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("mwstkontosoll", "int", "implicit", "yes", "once", "mandatory");
		props.addProperty("mwstkontohaben", "int", "implicit", "yes", "once", "mandatory");
		props.addProperty("mwstsatzaktiv", "bool", "explicit", "yes", "yes", "auto");
		props.addFunction("mwst", "list", true, true, true, false, true, true);
		props.addFunction("mwst", "add", false, false, false, true, true, false);
		props.addFunction("mwst", "update", false, true, false, true, true, false);
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("mwst");
		menu.addItem("list");
		menu.addItem("add");
		menu.addItem("update");
	    return menu;
	}

	public void callMwstListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		MwstData whereData = (MwstData) getWhereData(request, new MwstData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		Vector resultList = 
			backend.executeMwstListCall(
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

	public void callMwstAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		MwstData writeData = (MwstData) getWriteData(request, new MwstData());
			backend.executeMwstAddCall(
				dbConnect
				, session
				, writeData
			);
	}

	public void callMwstUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		MwstData writeData = (MwstData) getWriteData(request, new MwstData());
		MwstData whereData = (MwstData) getWhereData(request, new MwstData());
			backend.executeMwstUpdateCall(
				dbConnect
				, session
				, writeData
				, whereData
			);
	}

}
