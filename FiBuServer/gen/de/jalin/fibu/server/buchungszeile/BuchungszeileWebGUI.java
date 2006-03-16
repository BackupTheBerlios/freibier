package de.jalin.fibu.server.buchungszeile;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungszeileWebGUI extends AbstractWebGUI {

	private BuchungszeileBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungszeileWebGUI(BuchungszeileBackend backend) {
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
		} catch (XmlRpcTransactionException e) {
			throw new ServletException(e);
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
		Vector resultList = 
			backend.executeBuchungszeileListCall(
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

	public void callBuchungszeileAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData writeData = (BuchungszeileData) getWriteData(request, new BuchungszeileData());
			backend.executeBuchungszeileAddCall(
				dbConnect
				, session
				, writeData
			);
	}

	public void callBuchungszeileUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData writeData = (BuchungszeileData) getWriteData(request, new BuchungszeileData());
		BuchungszeileData whereData = (BuchungszeileData) getWhereData(request, new BuchungszeileData());
			backend.executeBuchungszeileUpdateCall(
				dbConnect
				, session
				, writeData
				, whereData
			);
	}

	public void callBuchungszeileDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungszeileData whereData = (BuchungszeileData) getWhereData(request, new BuchungszeileData());
			backend.executeBuchungszeileDeleteCall(
				dbConnect
				, session
				, whereData
			);
	}

}
