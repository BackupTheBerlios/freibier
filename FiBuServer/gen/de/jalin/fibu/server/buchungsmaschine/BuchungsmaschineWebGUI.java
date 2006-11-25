// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsmaschine;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungsmaschineWebGUI extends AbstractWebGUI {

	private static final long serialVersionUID = 1164458226962L;

	private PostgresAccess pgAccess;
	private BuchungsmaschineBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungsmaschineWebGUI(BuchungsmaschineBackend backend) throws XmlRpcTransactionException {
		pgAccess = PostgresAccess.getInstance();
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
				if("add".equals(functionName)) {
		    		callBuchungsmaschineAddCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				dbConnect.commit();
				dbConnect.setAutoCommit(true);
			} catch (BuchungsmaschineException e) {
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
		ModuleProperties props = new ModuleProperties("buchungsmaschine");
		props.addProperty("buzlid", "int", "implicit", "no", "no", "auto");
		props.addProperty("sollkontonr", "string", "implicit", "no", "once", "mandatory");
		props.addProperty("habenkontonr", "string", "implicit", "no", "once", "mandatory");
		props.addProperty("sollmwstid", "int", "implicit", "no", "once", "mandatory");
		props.addProperty("habenmwstid", "int", "implicit", "no", "once", "mandatory");
		props.addProperty("brutto", "int", "implicit", "no", "once", "optional");
		props.addProperty("belegnr", "string", "implicit", "no", "once", "optional");
		props.addProperty("buchungstext", "string", "implicit", "no", "once", "optional");
		props.addProperty("jourid", "int", "implicit", "no", "once", "optional");
		props.addProperty("valuta", "date", "implicit", "no", "once", "optional");
		props.addFunction("buchungsmaschine", "add", false, false, false, true, true, false);
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("buchungsmaschine");
		menu.addItem("add");
	    return menu;
	}

	public void callBuchungsmaschineAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungsmaschineData writeData = (BuchungsmaschineData) getWriteData(request, new BuchungsmaschineData());
		try {
				backend.executeBuchungsmaschineAddCall(
					dbConnect
					, session
					, writeData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungsmaschine");
			params.put("functionname", "add");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungsmaschineException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungsmaschine");
			params.put("functionname", "add");
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
