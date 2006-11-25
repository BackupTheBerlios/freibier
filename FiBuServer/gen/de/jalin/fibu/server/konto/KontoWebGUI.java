// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class KontoWebGUI extends AbstractWebGUI {

	private static final long serialVersionUID = 1164458221490L;

	private PostgresAccess pgAccess;
	private KontoBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public KontoWebGUI(KontoBackend backend) throws XmlRpcTransactionException {
		pgAccess = PostgresAccess.getInstance();
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("kontoid", 1);
		this.display.addColumnDefinition("kontonr", 1);
		this.display.addColumnDefinition("bezeichnung", 1);
		this.display.addColumnDefinition("mwstid", 1);
		this.display.addColumnDefinition("oberkonto", 1);
		this.display.addColumnDefinition("istsoll", 1);
		this.display.addColumnDefinition("isthaben", 1);
		this.display.addColumnDefinition("istaktiv", 1);
		this.display.addColumnDefinition("istpassiv", 1);
		this.display.addColumnDefinition("istaufwand", 1);
		this.display.addColumnDefinition("istertrag", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("kontoid");
		this.orderBy.addSelectableColumn("kontonr");
		this.orderBy.addSelectableColumn("bezeichnung");
		this.orderBy.addSelectableColumn("mwstid");
		this.orderBy.addSelectableColumn("oberkonto");
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
		    		callKontoListCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("add".equals(functionName)) {
		    		callKontoAddCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("update".equals(functionName)) {
		    		callKontoUpdateCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				if("delete".equals(functionName)) {
		    		callKontoDeleteCall(
		    			dbConnect, 
		    			session,
		    			request,
		    			response);
				}
				dbConnect.commit();
				dbConnect.setAutoCommit(true);
			} catch (KontoException e) {
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
		ModuleProperties props = new ModuleProperties("konto");
		props.addProperty("kontoid", "int", "implicit", "yes", "no", "auto");
		props.addProperty("kontonr", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("bezeichnung", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("mwstid", "int", "implicit", "yes", "yes", "mandatory");
		props.addProperty("oberkonto", "int", "implicit", "yes", "yes", "optional");
		props.addProperty("istsoll", "bool", "implicit", "yes", "yes", "auto");
		props.addProperty("isthaben", "bool", "implicit", "yes", "yes", "auto");
		props.addProperty("istaktiv", "bool", "implicit", "yes", "yes", "auto");
		props.addProperty("istpassiv", "bool", "implicit", "yes", "yes", "auto");
		props.addProperty("istaufwand", "bool", "implicit", "yes", "yes", "auto");
		props.addProperty("istertrag", "bool", "implicit", "yes", "yes", "auto");
		props.addFunction("konto", "list", true, true, true, false, true, true);
		props.addFunction("konto", "add", false, false, false, true, true, false);
		props.addFunction("konto", "update", false, true, false, true, true, false);
		props.addFunction("konto", "delete", false, true, false, false, true, false);
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("konto");
		menu.addItem("list");
		menu.addItem("add");
		menu.addItem("update");
		menu.addItem("delete");
	    return menu;
	}

	public void callKontoListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData whereData = (KontoData) getWhereData(request, new KontoData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		try {
			Vector resultList = 
				backend.executeKontoListCall(
					dbConnect
					, session
					, whereData
					, display
					, orderBy
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
			params.put("functionname", "list");
			params.put("headers", resultList.get(0));
			params.put("rows", resultList.get(1));
			templateName = "liste.vm";
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (KontoException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
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

	public void callKontoAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData writeData = (KontoData) getWriteData(request, new KontoData());
		try {
				backend.executeKontoAddCall(
					dbConnect
					, session
					, writeData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
			params.put("functionname", "add");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (KontoException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
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

	public void callKontoUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData writeData = (KontoData) getWriteData(request, new KontoData());
		KontoData whereData = (KontoData) getWhereData(request, new KontoData());
		try {
				backend.executeKontoUpdateCall(
					dbConnect
					, session
					, writeData
					, whereData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
			params.put("functionname", "update");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (KontoException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
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

	public void callKontoDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData whereData = (KontoData) getWhereData(request, new KontoData());
		try {
				backend.executeKontoDeleteCall(
					dbConnect
					, session
					, whereData
				);
			String templateName = "funct_ok.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
			params.put("functionname", "delete");
			try {
				response.getWriter().print(mergeTemplate(templateName, params));
			} catch (Exception e) {
				throw new ServerException(ErrorCode.TEMPLATE_ERROR_CODE);
			}
		} catch (KontoException e) {
			String templateName = "funct_err.vm";
			Map params = new HashMap();
			params.put("menu", request.getSession().getAttribute("menu"));
			params.put("modulename", "konto");
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
