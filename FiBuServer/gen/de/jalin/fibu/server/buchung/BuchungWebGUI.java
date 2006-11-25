// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchung;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungWebGUI extends AbstractWebGUI {

	private static final long serialVersionUID = 1164458223981L;

	private PostgresAccess pgAccess;
	private BuchungBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungWebGUI(BuchungBackend backend) throws XmlRpcTransactionException {
		pgAccess = PostgresAccess.getInstance();
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("buchid", 1);
		this.display.addColumnDefinition("belegnr", 1);
		this.display.addColumnDefinition("buchungstext", 1);
		this.display.addColumnDefinition("jourid", 1);
		this.display.addColumnDefinition("valuta", 1);
		this.display.addColumnDefinition("erfassung", 2);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("buchid");
		this.orderBy.addSelectableColumn("belegnr");
		this.orderBy.addSelectableColumn("buchungstext");
		this.orderBy.addSelectableColumn("jourid");
		this.orderBy.addSelectableColumn("valuta");
		this.orderBy.addSelectableColumn("erfassung");
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
		    		callBuchungListCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("add".equals(functionName)) {
		    		callBuchungAddCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("update".equals(functionName)) {
		    		callBuchungUpdateCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("delete".equals(functionName)) {
		    		callBuchungDeleteCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				dbConnect.commit();
				dbConnect.setAutoCommit(true);
			} catch (BuchungException e) {
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
		ModuleProperties props = new ModuleProperties("buchung");
		props.addProperty("buchid", "int", "implicit", "yes", "no", "auto");
		props.addProperty("belegnr", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("buchungstext", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("jourid", "int", "implicit", "yes", "yes", "mandatory");
		props.addProperty("valuta", "date", "implicit", "yes", "yes", "mandatory");
		props.addProperty("erfassung", "date", "explicit", "yes", "no", "auto");
		props.addFunction("buchung", "list", true, true, true, false, true, true);
		props.addFunction("buchung", "add", false, false, false, true, true, false);
		props.addFunction("buchung", "update", false, true, false, true, true, false);
		props.addFunction("buchung", "delete", false, true, false, false, true, false);
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("buchung");
		menu.addItem("list");
		menu.addItem("add");
		menu.addItem("update");
		menu.addItem("delete");
	    return menu;
	}

	public void callBuchungListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData whereData = (BuchungData) getWhereData(request, new BuchungData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		try {
			Vector resultList = 
				backend.executeBuchungListCall(
					dbConnect
					, session
					, whereData
					, display
					, orderBy
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
			params.put("functionname", "list");
			params.put("headers", resultList.get(0));
			params.put("rows", resultList.get(1));
			templateName = "liste.vm";
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
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

	public void callBuchungAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData writeData = (BuchungData) getWriteData(request, new BuchungData());
		try {
				backend.executeBuchungAddCall(
					dbConnect
					, session
					, writeData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
			params.put("functionname", "add");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
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

	public void callBuchungUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData writeData = (BuchungData) getWriteData(request, new BuchungData());
		BuchungData whereData = (BuchungData) getWhereData(request, new BuchungData());
		try {
				backend.executeBuchungUpdateCall(
					dbConnect
					, session
					, writeData
					, whereData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
			params.put("functionname", "update");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
			params.put("functionname", "update");
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

	public void callBuchungDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData whereData = (BuchungData) getWhereData(request, new BuchungData());
		try {
				backend.executeBuchungDeleteCall(
					dbConnect
					, session
					, whereData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
			params.put("functionname", "delete");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchung");
			params.put("functionname", "delete");
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
