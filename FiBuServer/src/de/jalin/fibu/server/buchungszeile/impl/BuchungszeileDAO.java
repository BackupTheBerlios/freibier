// $Id: BuchungszeileDAO.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server.buchungszeile.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class BuchungszeileDAO extends
		de.jalin.fibu.server.buchungszeile.BuchungszeileDAO {
	
	private Sequence buzlidSEQ;

	public BuchungszeileDAO() {
		super();
		buzlidSEQ = new Sequence(getTable(), "buzlid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return buzlidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		buzlidSEQ.createDatabaseObject(connect);
	}
}

/*
 *  $Log: BuchungszeileDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
