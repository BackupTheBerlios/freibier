// $Id: BuchungslisteBackendImpl.java,v 1.1 2005/11/20 21:27:43 phormanns Exp $
package de.jalin.fibu.server.buchungsliste.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import de.jalin.fibu.server.buchungsliste.BuchungslisteBackend;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungsliste.BuchungslisteException;

public class BuchungslisteBackendImpl implements BuchungslisteBackend {
	public BuchungslisteBackendImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Vector executeBuchungslisteListCall(Connection dbConnection,
			XmlRpcSession session, BuchungslisteData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungslisteException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
	}
}

/*
 *  $Log: BuchungslisteBackendImpl.java,v $
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
