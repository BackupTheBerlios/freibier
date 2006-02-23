// $Id: InitDB.java,v 1.5 2006/02/23 17:08:11 phormanns Exp $
package de.jalin.fibu.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import net.hostsharing.admin.runtime.InitDatabase;
import net.hostsharing.admin.runtime.PostgresAccess;
import net.hostsharing.admin.runtime.ResultVector;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.impl.BuchungDAO;
import de.jalin.fibu.server.buchungsliste.impl.BuchungslisteDAO;
import de.jalin.fibu.server.buchungszeile.impl.BuchungszeileDAO;
import de.jalin.fibu.server.customer.CustomerData;
import de.jalin.fibu.server.customer.impl.CustomerDAO;
import de.jalin.fibu.server.journal.impl.JournalDAO;
import de.jalin.fibu.server.konto.KontoData;
import de.jalin.fibu.server.konto.impl.KontoDAO;
import de.jalin.fibu.server.mwst.MwstData;
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
	private Integer nullMWStId;
	private Integer ermMWStId;
	private Integer vollMWStId;

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
		Connection connection = null;
		try {
			// TODO: MWSt., Referenzen...
			PostgresAccess dbAccess = PostgresAccess.getInstance();
			connection = dbAccess.getConnection();
			connection.setAutoCommit(false);
			Integer kontoId = new Integer(kontoDAO.nextId(connection));
			nullMWStId = new Integer(mwstDAO.nextId(connection));
			ermMWStId = new Integer(mwstDAO.nextId(connection));
			vollMWStId = new Integer(mwstDAO.nextId(connection));
			MwstData nullMWSt= new MwstData();
			nullMWSt.setMwstid(nullMWStId);
			nullMWSt.setMwstkontohaben(kontoId);
			nullMWSt.setMwstkontosoll(kontoId);
			nullMWSt.setMwstsatz(new Integer(0));
			nullMWSt.setMwstsatzaktiv(Boolean.TRUE);
			nullMWSt.setMwsttext("keine MWSt.");
			mwstDAO.addMwst(connection, nullMWSt);
			MwstData ermMWSt = (MwstData) nullMWSt.cloneData();
			ermMWSt.setMwstid(ermMWStId);
			ermMWSt.setMwstsatz(new Integer(700));
			ermMWSt.setMwsttext("7% MWSt.");
			mwstDAO.addMwst(connection, ermMWSt);
			MwstData vollMWSt = (MwstData) nullMWSt.cloneData();
			vollMWSt.setMwstid(vollMWStId);
			vollMWSt.setMwstsatz(new Integer(1600));
			vollMWSt.setMwsttext("16% MWSt.");
			mwstDAO.addMwst(connection, vollMWSt);
			KontoData bilanzKonto = new KontoData();
			bilanzKonto.setKontoid(kontoId);
			bilanzKonto.setKontonr("_");
			bilanzKonto.setMwstid(nullMWStId);
			bilanzKonto.setBezeichnung("Finanzbuchhaltung");
			bilanzKonto.setIstsoll(Boolean.TRUE);
			bilanzKonto.setIsthaben(Boolean.TRUE);
			kontoDAO.addKonto(connection, bilanzKonto);
			CustomerData cust = new CustomerData();
			cust.setCustid(new Integer(customerDAO.nextId(connection)));
			cust.setBilanzkonto(kontoId);
			cust.setGuvkonto(kontoId);
			cust.setFirma("Firma");
			cust.setJahr("2006");
			cust.setPeriode("01");
			Date now = new Date();
			cust.setLastupdate(now);
			cust.setSince(now);
			customerDAO.addCustomer(connection, cust);
			readKontenFromFile(connection, "spec/kontenplan.csv");
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
		} catch (XmlRpcTransactionException e) {
			System.out.println("Fehler bei der Dateninitialisierung.");
			System.out.println(e.getLocalizedMessage());
		} catch (SQLException e) {
			System.out.println("Fehler bei der Dateninitialisierung.");
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen des Kontenplans.");
			System.out.println(e.getLocalizedMessage());
		} finally {
			if (connection != null) {
				try {
					connection.rollback();
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private void readKontenFromFile(Connection connection, String fileName) throws IOException, XmlRpcTransactionException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String kontoString = reader.readLine();
		String kontoNrString = null;
		String kontoBezString = null;
		KontoData konto = null;
		Integer kontoId = null;
		String[] tokens = null;
		while (kontoString != null) {
			tokens = kontoString.substring(0, kontoString.length()-1).split("\",");
			kontoNrString = tokens[0].substring(1, tokens[0].length());
			kontoBezString = tokens[1].substring(1, tokens[1].length());
			System.out.println(kontoNrString + ":" + kontoBezString);
			kontoId = new Integer(kontoDAO.nextId(connection));
			konto = new KontoData();
			konto.setKontoid(kontoId);
			konto.setKontonr(kontoNrString);
			konto.setBezeichnung(kontoBezString);
			konto.setIsthaben(Boolean.TRUE);
			konto.setIstsoll(Boolean.TRUE);
			konto.setMwstid(nullMWStId);
			konto.setOberkonto(findOberkonto(connection, kontoNrString));
			kontoDAO.addKonto(connection, konto);
			kontoString = reader.readLine();
		}
		reader.close();
	}

	private Integer findOberkonto(Connection connection, String kontoNrString) throws XmlRpcTransactionException {
		String oberKtoNr = "_";
		if (kontoNrString.length() > 1) {
			if (kontoNrString.length() > 2) {
				oberKtoNr = kontoNrString.substring(0, 2);
			} else {
				oberKtoNr = kontoNrString.substring(0, 1);
			}
		}
		KontoData queryOberKto = new KontoData();
		queryOberKto.setKontonr(oberKtoNr);
		KontoData oberKto = new KontoData();
		ResultVector resultVector = new ResultVector(kontoDAO.listKontos(connection, queryOberKto, null, null));
		oberKto.readFromResult(resultVector, 0);
		return oberKto.getKontoid();
	}

	public String stripDelimiter(String string) {
		if (string.charAt(0) == '"') {
			int len = string.length();
			if (len > 1 && string.charAt(len - 1) == '"') {
				return string.substring(1, len - 1);
			}
		}
		return string;
	}

	public static void main(String[] args) {
		InitDB initDB = new InitDB();
		initDB.initModel();
		initDB.initData();
	}
}

/*
 * $Log: InitDB.java,v $
 * Revision 1.5  2006/02/23 17:08:11  phormanns
 * Kontenplan für DBInit
 *
 * Revision 1.3  2005/12/12 21:09:05  phormanns
 * Datenbank-Initialisierung begonnen
 * Revision 1.2 2005/11/24 22:36:16 phormanns Anlegen der
 * Buchungsliste-View
 * 
 * Revision 1.1 2005/11/20 21:27:44 phormanns Import
 * 
 */
