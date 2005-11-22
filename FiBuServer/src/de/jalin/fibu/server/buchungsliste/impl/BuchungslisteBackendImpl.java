// $Id: BuchungslisteBackendImpl.java,v 1.2 2005/11/22 21:32:00 phormanns Exp $
package de.jalin.fibu.server.buchungsliste.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungsliste.BuchungslisteBackend;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungsliste.BuchungslisteException;

public class BuchungslisteBackendImpl implements BuchungslisteBackend {
	
	private BuchungslisteDAO buchungslisteDAO;

	public BuchungslisteBackendImpl() {
		buchungslisteDAO = new BuchungslisteDAO();
	}

	public Vector executeBuchungslisteListCall(Connection dbConnection,
			XmlRpcSession session, BuchungslisteData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungslisteException {
		try {
			return buchungslisteDAO.listBuchungslistes(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungslisteException(10701, e);
		}
	}

}

/*
 *  $Log: BuchungslisteBackendImpl.java,v $
 *  Revision 1.2  2005/11/22 21:32:00  phormanns
 *  Modul Buchungsliste (noch nicht genutzt)
 *
 *  Revision 1.1  2005/11/22 21:26:08  phormanns
 *  Modul Buchungsliste (noch nicht genutzt)
 *
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
