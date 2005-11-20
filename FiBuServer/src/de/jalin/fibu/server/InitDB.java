// $Id: InitDB.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server;

import java.util.Vector;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.XmlRpcTransactionManager;
import de.jalin.fibu.server.buchung.impl.BuchungDAO;
import de.jalin.fibu.server.buchungszeile.impl.BuchungszeileDAO;
import de.jalin.fibu.server.customer.impl.CustomerDAO;
import de.jalin.fibu.server.journal.impl.JournalDAO;
import de.jalin.fibu.server.konto.impl.KontoDAO;
import de.jalin.fibu.server.mwst.impl.MwstDAO;

public class InitDB {

	private Vector daoObjects;
	
	public InitDB() {
		daoObjects = new Vector();
		daoObjects.addElement(new CustomerDAO());
		daoObjects.addElement(new KontoDAO());
		daoObjects.addElement(new JournalDAO());
		daoObjects.addElement(new BuchungDAO());
		daoObjects.addElement(new BuchungszeileDAO());
		daoObjects.addElement(new MwstDAO());
	}
	
	public void initDB() {
		try {
			XmlRpcTransactionManager tx = new XmlRpcTransactionManager();
			tx.initDatabase(daoObjects);
			System.out.println("Datenbank initialisiert.");
		} catch (XmlRpcTransactionException e) {
			System.out.println("Fehler bei der Datenbankinitialisierung.");
			System.out.println(e.getLocalizedMessage());
		}
	}

	public static void main(String[] args) {
		InitDB initDB = new InitDB();
		initDB.initDB();
	}
}

/*
 *  $Log: InitDB.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
