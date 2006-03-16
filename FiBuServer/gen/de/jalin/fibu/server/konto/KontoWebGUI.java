package de.jalin.fibu.server.konto;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class KontoWebGUI extends AbstractWebGUI {

	private KontoBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public KontoWebGUI(KontoBackend backend) {
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("kontoid", 1);
		this.display.addColumnDefinition("kontonr", 1);
		this.display.addColumnDefinition("bezeichnung", 1);
		this.display.addColumnDefinition("mwstid", 1);
		this.display.addColumnDefinition("oberkonto", 1);
		this.display.addColumnDefinition("istsoll", 1);
		this.display.addColumnDefinition("isthaben", 1);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("kontoid");
		this.orderBy.addSelectableColumn("kontonr");
		this.orderBy.addSelectableColumn("bezeichnung");
		this.orderBy.addSelectableColumn("mwstid");
		this.orderBy.addSelectableColumn("oberkonto");
		this.orderBy.addSelectableColumn("istsoll");
		this.orderBy.addSelectableColumn("isthaben");
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
		} catch (XmlRpcTransactionException e) {
			throw new ServletException(e);
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
		Vector resultList = 
			backend.executeKontoListCall(
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

	public void callKontoAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData writeData = (KontoData) getWriteData(request, new KontoData());
			backend.executeKontoAddCall(
				dbConnect
				, session
				, writeData
			);
	}

	public void callKontoUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData writeData = (KontoData) getWriteData(request, new KontoData());
		KontoData whereData = (KontoData) getWhereData(request, new KontoData());
			backend.executeKontoUpdateCall(
				dbConnect
				, session
				, writeData
				, whereData
			);
	}

	public void callKontoDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		KontoData whereData = (KontoData) getWhereData(request, new KontoData());
			backend.executeKontoDeleteCall(
				dbConnect
				, session
				, whereData
			);
	}

}
