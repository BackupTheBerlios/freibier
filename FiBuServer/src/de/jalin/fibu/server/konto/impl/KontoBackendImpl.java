// $Id: KontoBackendImpl.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server.konto.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.konto.KontoBackend;
import de.jalin.fibu.server.konto.KontoData;
import de.jalin.fibu.server.konto.KontoException;

public class KontoBackendImpl implements KontoBackend {
	
	private KontoDAO kontoDAO;

	public KontoBackendImpl() {
		kontoDAO = new KontoDAO();
	}

	public Vector executeKontoListCall(Connection dbConnection,
			XmlRpcSession session, KontoData whereData, DisplayColumns display,
			OrderByList orderBy) throws KontoException {
		try {
			return kontoDAO.listKontos(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public void executeKontoAddCall(Connection dbConnection,
			XmlRpcSession session, KontoData writeData) throws KontoException {
		try {
			kontoDAO.addKonto(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public void executeKontoUpdateCall(Connection dbConnection,
			XmlRpcSession session, KontoData writeData, KontoData whereData)
			throws KontoException {
		try {
			kontoDAO.updateKonto(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public void executeKontoDeleteCall(Connection dbConnection,
			XmlRpcSession session, KontoData whereData) throws KontoException {
		try {
			kontoDAO.deleteKonto(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}
}

/*
 *  $Log: KontoBackendImpl.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
