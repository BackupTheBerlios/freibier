// $Id: BuchungslisteDAO.java,v 1.1 2005/11/20 21:27:43 phormanns Exp $
package de.jalin.fibu.server.buchungsliste.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;

public class BuchungslisteDAO extends
		de.jalin.fibu.server.buchungsliste.BuchungslisteDAO {
	
	public BuchungslisteDAO() {
		super();
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		// TODO Create View!
		
	}

	public Vector listBuchungslistes(Connection connect, BuchungslisteData whereData, DisplayColumns display, OrderByList orderBy) throws XmlRpcTransactionException {
		// TODO Select from View!
		return super.listBuchungslistes(connect, whereData, display, orderBy);
	}

	public void deleteBuchungsliste(Connection connect, BuchungslisteData whereData) throws XmlRpcTransactionException {
		// nichts tun!
	}

	public void updateBuchungsliste(Connection connect, BuchungslisteData writeData, BuchungslisteData whereData) throws XmlRpcTransactionException {
		// nichts tun!
	}
}

/*
 *  $Log: BuchungslisteDAO.java,v $
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
