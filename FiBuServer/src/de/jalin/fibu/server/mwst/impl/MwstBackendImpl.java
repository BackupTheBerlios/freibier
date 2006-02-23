// $Id: MwstBackendImpl.java,v 1.2 2006/02/23 17:08:28 phormanns Exp $
package de.jalin.fibu.server.mwst.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.mwst.MwstBackend;
import de.jalin.fibu.server.mwst.MwstData;
import de.jalin.fibu.server.mwst.MwstException;

public class MwstBackendImpl implements MwstBackend {
	
	private MwstDAO mwstDAO;

	public MwstBackendImpl() {
		mwstDAO = new MwstDAO();
	}

	public Vector executeMwstListCall(Connection dbConnection,
			XmlRpcSession session, MwstData whereData, DisplayColumns display,
			OrderByList orderBy) throws MwstException {
		try {
			return mwstDAO.listMwsts(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}

	public void executeMwstAddCall(Connection dbConnection, XmlRpcSession session, MwstData writeData) throws MwstException {
		try {
			mwstDAO.addMwst(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}

	public void executeMwstUpdateCall(Connection dbConnection, XmlRpcSession session, MwstData writeData, MwstData whereData) throws MwstException {
		try {
			mwstDAO.updateMwst(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}

	public void executeMwstDeleteCall(Connection dbConnection, XmlRpcSession session, MwstData whereData) throws MwstException {
		try {
			mwstDAO.deleteMwst(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}
}

/*
 *  $Log: MwstBackendImpl.java,v $
 *  Revision 1.2  2006/02/23 17:08:28  phormanns
 *  neue Funktionen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
