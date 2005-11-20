// $Id: KontoDAO.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server.konto.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class KontoDAO extends de.jalin.fibu.server.konto.KontoDAO {
	
	private Sequence kontoidSEQ;

	public KontoDAO() {
		super();
		kontoidSEQ = new Sequence(getTable(), "kontoid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return kontoidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		kontoidSEQ.createDatabaseObject(connect);
	}
}

/*
 *  $Log: KontoDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
