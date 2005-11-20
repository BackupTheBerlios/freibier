// $Id: MwstDAO.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server.mwst.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class MwstDAO extends de.jalin.fibu.server.mwst.MwstDAO {
	
	private Sequence mwstidSEQ;

	public MwstDAO() {
		super();
		mwstidSEQ = new Sequence(getTable(), "mwstid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return mwstidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		mwstidSEQ.createDatabaseObject(connect);
	}
}

/*
 *  $Log: MwstDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
