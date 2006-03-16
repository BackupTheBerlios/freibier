package de.jalin.fibu.server.customer;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hostsharing.admin.runtime.*;

public class CustomerWebGUI extends AbstractWebGUI {

	private CustomerBackend backend;
	private DisplayColumns display;
	private OrderByList orderBy;

	public CustomerWebGUI(CustomerBackend backend) {
		this.backend = backend;
		this.display = new DisplayColumns();
		this.display.addColumnDefinition("custid", 1);
		this.display.addColumnDefinition("firma", 1);
		this.display.addColumnDefinition("bilanzkonto", 1);
		this.display.addColumnDefinition("guvkonto", 1);
		this.display.addColumnDefinition("jahr", 1);
		this.display.addColumnDefinition("periode", 1);
		this.display.addColumnDefinition("since", 2);
		this.display.addColumnDefinition("lastupdate", 2);
		this.orderBy = new OrderByList();
		this.orderBy.addSelectableColumn("custid");
		this.orderBy.addSelectableColumn("firma");
		this.orderBy.addSelectableColumn("bilanzkonto");
		this.orderBy.addSelectableColumn("guvkonto");
		this.orderBy.addSelectableColumn("jahr");
		this.orderBy.addSelectableColumn("periode");
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
	    		callCustomerListCall(
	    			dbConnect, 
	    			session,
	    			request,
	    			response);
			}
			if("update".equals(functionName)) {
	    		callCustomerUpdateCall(
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
		ModuleProperties props = new ModuleProperties("customer");
		props.addProperty("custid", "int", "implicit", "yes", "no", "auto");
		props.addProperty("firma", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("bilanzkonto", "int", "implicit", "yes", "yes", "mandatory");
		props.addProperty("guvkonto", "int", "implicit", "yes", "yes", "mandatory");
		props.addProperty("jahr", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("periode", "string", "implicit", "yes", "yes", "mandatory");
		props.addProperty("since", "date", "explicit", "no", "no", "auto");
		props.addProperty("lastupdate", "date", "explicit", "no", "no", "auto");
		return props;
	}

	public ModuleMenu getMenu() {
		ModuleMenu menu = new ModuleMenu("customer");
		menu.addItem("list");
		menu.addItem("update");
	    return menu;
	}

	public void callCustomerListCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		CustomerData whereData = (CustomerData) getWhereData(request, new CustomerData());
		display.reset();
		getDisplayColumns(request, display);
		orderBy.reset();
		getOrderByList(request, orderBy);
		Vector resultList = 
			backend.executeCustomerListCall(
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

	public void callCustomerUpdateCall(
		Connection dbConnect, 
		XmlRpcSession session,
		HttpServletRequest request, 
		HttpServletResponse response)
	   		throws XmlRpcTransactionException {
		CustomerData writeData = (CustomerData) getWriteData(request, new CustomerData());
		CustomerData whereData = (CustomerData) getWhereData(request, new CustomerData());
			backend.executeCustomerUpdateCall(
				dbConnect
				, session
				, writeData
				, whereData
			);
	}

}
