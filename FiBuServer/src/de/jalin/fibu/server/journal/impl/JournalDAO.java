// $Id: JournalDAO.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server.journal.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class JournalDAO extends de.jalin.fibu.server.journal.JournalDAO {
	
	private Sequence jouridSEQ;

	public JournalDAO() {
		super();
		jouridSEQ = new Sequence(getTable(), "jourid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return jouridSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		jouridSEQ.createDatabaseObject(connect);
	}
}

/*
 *  $Log: JournalDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
