// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungszeile;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungszeileWebGUI extends AbstractWebGUI {

	private static final long serialVersionUID = 1164458225139L;

	private PostgresAccess pgAccess;
	private BuchungszeileBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungszeileWebGUI(BuchungszeileBackend backend) throws XmlRpcTransactionException {
		pgAccess = PostgresAccess.getInstance();
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("buzlid", 1);
		this.display.addColumnDefinition("buchid", 1);
		this.display.addColumnDefinition("kontoid", 1);
		this.display.addColumnDefinition("betrag", 1);
		this.display.addColumnDefinition("soll", 1);
		this.display.addColumnDefinition("haben", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("buzlid");
		this.orderBy.addSelectableColumn("buchid");
		this.orderBy.addSelectableColumn("kontoid");
		this.orderBy.addSelectableColumn("betrag");
		this.orderBy.addSelectableColumn("soll");
		this.orderBy.addSelectableColumn("haben");
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
		    		callBuchungszeileListCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("add".equals(functionName)) {
		    		callBuchungszeileAddCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("update".equals(functionName)) {
		    		callBuchungszeileUpdateCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("delete".equals(functionName)) {
		    		callBuchungszeileDeleteCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				dbConnect.commit();
				dbConnect.setAutoCommit(true);
			} catch (BuchungszeileException e) {
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
		ModuleProperties props = new ModuleProperties("buchungszeile");
		props.addProperty("buzlid", "int", "implicit", "yes", "no", "auto");
		props.addProperty("buchid", "int", "implicit", "yes", "once", "mandatory");
		props.addProperty("kontoid", "int", "implicit", "yes", "once", "mandatory");
		props.addProperty("betrag", "int", "implicit", "yes", "yes", "mandatory");
		props.addProperty("soll", "bool", "implicit", "yes", "yes", "mandatory");
		props.addProperty("haben", "bool", "implicit", "yes", "yes", "mandatory");
		props.addFunction("buchungszeile", "list", true, true, true, false, true, true);
		props.addFunction("buchungszeile", "add", false, false, false, true, true, false);
		props.addFunction("buchungszeile", "update", false, true, false, true, true, false);
		props.addFunction("buchungszeile", "delete", false, true, false, false, true, false);
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("buchungszeile");
		menu.addItem("list");
		menu.addItem("add");
		menu.addItem("update");
		menu.addItem("delete");
	    return menu;
	}

	public void callBuchungszeileListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData whereData = (BuchungszeileData) getWhereData(request, new BuchungszeileData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		try {
			Vector resultList = 
				backend.executeBuchungszeileListCall(
					dbConnect
					, session
					, whereData
					, display
					, orderBy
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
			params.put("functionname", "list");
			params.put("headers", resultList.get(0));
			params.put("rows", resultList.get(1));
			templateName = "liste.vm";
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungszeileException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
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

	public void callBuchungszeileAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData writeData = (BuchungszeileData) getWriteData(request, new BuchungszeileData());
		try {
				backend.executeBuchungszeileAddCall(
					dbConnect
					, session
					, writeData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
			params.put("functionname", "add");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungszeileException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
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

	public void callBuchungszeileUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData writeData = (BuchungszeileData) getWriteData(request, new BuchungszeileData());
		BuchungszeileData whereData = (BuchungszeileData) getWhereData(request, new BuchungszeileData());
		try {
				backend.executeBuchungszeileUpdateCall(
					dbConnect
					, session
					, writeData
					, whereData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
			params.put("functionname", "update");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungszeileException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
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

	public void callBuchungszeileDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData whereData = (BuchungszeileData) getWhereData(request, new BuchungszeileData());
		try {
				backend.executeBuchungszeileDeleteCall(
					dbConnect
					, session
					, whereData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
			params.put("functionname", "delete");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (BuchungszeileException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "buchungszeile");
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
