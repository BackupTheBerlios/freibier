// $Id: BuchungDAO.java,v 1.1 2005/11/20 21:27:43 phormanns Exp $
package de.jalin.fibu.server.buchung.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class BuchungDAO extends de.jalin.fibu.server.buchung.BuchungDAO {
	
	private Sequence buchidSEQ;

	public BuchungDAO() {
		super();
		buchidSEQ = new Sequence(getTable(), "buchid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return buchidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		buchidSEQ.createDatabaseObject(connect);
	}
}

/*
 *  $Log: BuchungDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
