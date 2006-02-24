// $Id: CustomerBackendImpl.java,v 1.2 2006/02/24 22:27:40 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */
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
	
	public CustomerBackendImpl() throws CustomerException {
		try {
			custDAO = new CustomerDAO();
		} catch (XmlRpcTransactionException e) {
			throw new CustomerException(10101, e);
		}
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
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
