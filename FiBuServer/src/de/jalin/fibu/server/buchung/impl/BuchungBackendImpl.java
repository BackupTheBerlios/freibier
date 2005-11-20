// $Id: BuchungBackendImpl.java,v 1.1 2005/11/20 21:27:43 phormanns Exp $
package de.jalin.fibu.server.buchung.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.BuchungBackend;
import de.jalin.fibu.server.buchung.BuchungData;
import de.jalin.fibu.server.buchung.BuchungException;

public class BuchungBackendImpl implements BuchungBackend {
	
	private BuchungDAO buchungDAO;

	public BuchungBackendImpl() {
		buchungDAO = new BuchungDAO();
	}

	public Vector executeBuchungListCall(Connection dbConnection,
			XmlRpcSession session, BuchungData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungException {
		try {
			return buchungDAO.listBuchungs(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public void executeBuchungAddCall(Connection dbConnection,
			XmlRpcSession session, BuchungData writeData)
			throws BuchungException {
		try {
			int buchId = buchungDAO.nextId(dbConnection);
			writeData.setBuchid(new Integer(buchId));
			writeData.setErfassung(new Date());
			buchungDAO.addBuchung(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public void executeBuchungUpdateCall(Connection dbConnection,
			XmlRpcSession session, BuchungData writeData, BuchungData whereData)
			throws BuchungException {
		try {
			buchungDAO.updateBuchung(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public void executeBuchungDeleteCall(Connection dbConnection,
			XmlRpcSession session, BuchungData whereData)
			throws BuchungException {
		try {
			buchungDAO.deleteBuchung(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}
}

/*
 *  $Log: BuchungBackendImpl.java,v $
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
