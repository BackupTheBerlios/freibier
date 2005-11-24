// $Id: BuchungsmaschineBackendImpl.java,v 1.1 2005/11/24 17:41:18 phormanns Exp $
package de.jalin.fibu.server.buchungsmaschine.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineBackend;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineData;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineException;

public class BuchungsmaschineBackendImpl implements BuchungsmaschineBackend {
	
	private BuchungsmaschineDAO buchungsmaschineDAO;
	
	public BuchungsmaschineBackendImpl() {
		buchungsmaschineDAO = new BuchungsmaschineDAO();
	}

	public void executeBuchungsmaschineAddCall(Connection dbConnection,
			XmlRpcSession session, BuchungsmaschineData writeData)
			throws BuchungsmaschineException {
		try {
			buchungsmaschineDAO.addBuchungsmaschine(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungsmaschineException(10801, e);
		}
	}
}

/*
 *  $Log: BuchungsmaschineBackendImpl.java,v $
 *  Revision 1.1  2005/11/24 17:41:18  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 */
