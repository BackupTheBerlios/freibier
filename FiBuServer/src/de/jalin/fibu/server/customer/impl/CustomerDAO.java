// $Id: CustomerDAO.java,v 1.1 2005/11/20 21:27:43 phormanns Exp $
package de.jalin.fibu.server.customer.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class CustomerDAO extends de.jalin.fibu.server.customer.CustomerDAO {

	private Sequence custidSEQ;

	public CustomerDAO() {
		super();
		custidSEQ = new Sequence(getTable(), "custid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return custidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		custidSEQ.createDatabaseObject(connect);
	}
	
}

/*
 *  $Log: CustomerDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
