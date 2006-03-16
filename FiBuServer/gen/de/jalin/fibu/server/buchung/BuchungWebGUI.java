package de.jalin.fibu.server.buchung;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class BuchungWebGUI extends AbstractWebGUI {

	private BuchungBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public BuchungWebGUI(BuchungBackend backend) {
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
		} catch (XmlRpcTransactionException e) {
			throw new ServletException(e);
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
		Vector resultList = 
			backend.executeBuchungListCall(
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

	public void callBuchungAddCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData writeData = (BuchungData) getWriteData(request, new BuchungData());
			backend.executeBuchungAddCall(
				dbConnect
				, session
				, writeData
			);
	}

	public void callBuchungUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData writeData = (BuchungData) getWriteData(request, new BuchungData());
		BuchungData whereData = (BuchungData) getWhereData(request, new BuchungData());
			backend.executeBuchungUpdateCall(
				dbConnect
				, session
				, writeData
				, whereData
			);
	}

	public void callBuchungDeleteCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		BuchungData whereData = (BuchungData) getWhereData(request, new BuchungData());
			backend.executeBuchungDeleteCall(
				dbConnect
				, session
				, whereData
			);
	}

}
