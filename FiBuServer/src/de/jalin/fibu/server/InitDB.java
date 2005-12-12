// $Id: InitDB.java,v 1.3 2005/12/12 21:09:05 phormanns Exp $
package de.jalin.fibu.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import net.hostsharing.admin.runtime.InitDatabase;
import net.hostsharing.admin.runtime.PostgresAccess;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.impl.BuchungDAO;
import de.jalin.fibu.server.buchungsliste.impl.BuchungslisteDAO;
import de.jalin.fibu.server.buchungszeile.impl.BuchungszeileDAO;
import de.jalin.fibu.server.customer.CustomerData;
import de.jalin.fibu.server.customer.impl.CustomerDAO;
import de.jalin.fibu.server.journal.impl.JournalDAO;
import de.jalin.fibu.server.konto.KontoData;
import de.jalin.fibu.server.konto.impl.KontoDAO;
import de.jalin.fibu.server.mwst.impl.MwstDAO;

public class InitDB {

	private Vector daoObjects;
	private CustomerDAO customerDAO;
	private KontoDAO kontoDAO;
	private JournalDAO journalDAO;
	private BuchungDAO buchungDAO;
	private BuchungszeileDAO buchungszeileDAO;
	private MwstDAO mwstDAO;
	private BuchungslisteDAO buchungslisteDAO;

	public InitDB() {
		daoObjects = new Vector();
		customerDAO = new CustomerDAO();
		daoObjects.addElement(customerDAO);
		kontoDAO = new KontoDAO();
		daoObjects.addElement(kontoDAO);
		journalDAO = new JournalDAO();
		daoObjects.addElement(journalDAO);
		buchungDAO = new BuchungDAO();
		daoObjects.addElement(buchungDAO);
		buchungszeileDAO = new BuchungszeileDAO();
		daoObjects.addElement(buchungszeileDAO);
		mwstDAO = new MwstDAO();
		daoObjects.addElement(mwstDAO);
		buchungslisteDAO = new BuchungslisteDAO();
		daoObjects.addElement(buchungslisteDAO);
	}

	public void initModel() {
		try {
			InitDatabase init = new InitDatabase();
			init.initDatabase(daoObjects);
			System.out.println("Datenbank initialisiert.");
		} catch (XmlRpcTransactionException e) {
			System.out.println("Fehler bei der Datenmodellinitialisierung.");
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public void initData() {
		try {
			// TODO: MWSt., Referenzen...
			PostgresAccess dbAccess = new PostgresAccess();
			Connection connection = dbAccess.getConnection();
			KontoData bilanzKonto = new KontoData();
			bilanzKonto.setKontoid(new Integer(kontoDAO.nextId(connection)));
			bilanzKonto.setKontonr("0000");
			bilanzKonto.setBezeichnung("Bilanzkonto");
			bilanzKonto.setIstsoll(Boolean.TRUE);
			bilanzKonto.setIsthaben(Boolean.TRUE);
			kontoDAO.addKonto(connection, bilanzKonto);
			CustomerData cust = new CustomerData();
			cust.setCustid(new Integer(customerDAO.nextId(connection)));
			cust.setFirma("Muster GmbH");
			cust.setJahr("2005");
			cust.setPeriode("01");
			Date now = new Date();
			cust.setLastupdate(now);
			cust.setSince(now);
			customerDAO.addCustomer(connection, cust);
			connection.close();
		} catch (XmlRpcTransactionException e) {
			System.out.println("Fehler bei der Dateninitialisierung.");
			System.out.println(e.getLocalizedMessage());
		} catch (SQLException e) {
			System.out.println("Fehler bei der Dateninitialisierung.");
			System.out.println(e.getLocalizedMessage());
		}
	}

	public static void main(String[] args) {
		InitDB initDB = new InitDB();
		initDB.initModel();
	}
}

/*
 * $Log: InitDB.java,v $
 * Revision 1.3  2005/12/12 21:09:05  phormanns
 * Datenbank-Initialisierung begonnen
 * Revision 1.2 2005/11/24 22:36:16 phormanns Anlegen der
 * Buchungsliste-View
 * 
 * Revision 1.1 2005/11/20 21:27:44 phormanns Import
 * 
 */
