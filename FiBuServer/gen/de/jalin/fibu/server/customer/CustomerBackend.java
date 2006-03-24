// Generiert mit xmlrpcgen

package de.jalin.fibu.server.customer;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface CustomerBackend {

	public 
		Vector 
			executeCustomerListCall
		( Connection dbConnection
		, XmlRpcSession session
		, CustomerData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws CustomerException;

	public 
		void
			executeCustomerUpdateCall
		( Connection dbConnection
		, XmlRpcSession session
		, CustomerData writeData
		, CustomerData whereData
	) throws CustomerException;
}
