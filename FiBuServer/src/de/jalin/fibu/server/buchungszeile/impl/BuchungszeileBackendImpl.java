// $Id: BuchungszeileBackendImpl.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server.buchungszeile.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungszeile.BuchungszeileBackend;
import de.jalin.fibu.server.buchungszeile.BuchungszeileData;
import de.jalin.fibu.server.buchungszeile.BuchungszeileException;

public class BuchungszeileBackendImpl implements BuchungszeileBackend {
	
	private BuchungszeileDAO buchungszeileDAO;

	public BuchungszeileBackendImpl() {
		buchungszeileDAO = new BuchungszeileDAO();
	}

	public Vector executeBuchungszeileListCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungszeileException {
		try {
			return buchungszeileDAO.listBuchungszeiles(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public void executeBuchungszeileAddCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData writeData)
			throws BuchungszeileException {
		try {
			int buzlid = buchungszeileDAO.nextId(dbConnection);
			writeData.setBuzlid(new Integer(buzlid));
			buchungszeileDAO.addBuchungszeile(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public void executeBuchungszeileUpdateCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData writeData,
			BuchungszeileData whereData) throws BuchungszeileException {
		try {
			buchungszeileDAO.updateBuchungszeile(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public void executeBuchungszeileDeleteCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData whereData)
			throws BuchungszeileException {
		try {
			buchungszeileDAO.deleteBuchungszeile(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}
}

/*
 *  $Log: BuchungszeileBackendImpl.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
