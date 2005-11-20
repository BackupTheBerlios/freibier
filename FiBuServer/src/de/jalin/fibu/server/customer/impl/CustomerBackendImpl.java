// $Id: CustomerBackendImpl.java,v 1.1 2005/11/20 21:27:43 phormanns Exp $
package de.jalin.fibu.server.customer.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.customer.CustomerBackend;
import de.jalin.fibu.server.customer.CustomerData;
import de.jalin.fibu.server.customer.CustomerException;

public class CustomerBackendImpl implements CustomerBackend {
	
	private CustomerDAO custDAO;
	
	public CustomerBackendImpl() {
		custDAO = new CustomerDAO();
	}

	public Vector executeCustomerListCall(Connection dbConnection,
			XmlRpcSession session, CustomerData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws CustomerException {
		try {
			return custDAO.listCustomers(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new CustomerException(10101, e);
		}
	}

	public void executeCustomerUpdateCall(Connection dbConnection,
			XmlRpcSession session, CustomerData writeData,
			CustomerData whereData) throws CustomerException {
		try {
			custDAO.updateCustomer(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new CustomerException(10101, e);
		}
	}
}

/*
 *  $Log: CustomerBackendImpl.java,v $
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
