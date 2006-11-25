// $Id: InitDB.java,v 1.8 2006/11/25 12:59:38 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */
package de.jalin.fibu.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import net.hostsharing.admin.runtime.InitDatabase;
import net.hostsharing.admin.runtime.PostgresAccess;
import net.hostsharing.admin.runtime.QueryResult;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.standardModules.impl.FunctionsDAO;
import net.hostsharing.admin.runtime.standardModules.impl.ModulesDAO;
import net.hostsharing.admin.runtime.standardModules.impl.PropertiesDAO;
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
	private ModulesDAO modulesDAO;
	private FunctionsDAO functionsDAO;
	private PropertiesDAO propertiesDAO;

	public InitDB() throws XmlRpcTransactionException {
		daoObjects = new Vector();
		
		modulesDAO = new ModulesDAO();
		daoObjects.addElement(modulesDAO);
		functionsDAO = new FunctionsDAO();
		daoObjects.addElement(functionsDAO);
		propertiesDAO = new PropertiesDAO();
		daoObjects.addElement(propertiesDAO);
		
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
	
	public void dropData() {
		try {
			InitDatabase init = new InitDatabase();
			init.dropDatabase(daoObjects);
			System.out.println("Datenbanktabellen gelöscht.");
		} catch (XmlRpcTransactionException e) {
			System.out.println("Fehler beim Löschen des Datenmodells.");
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
			bilanzKonto.setIstaktiv(Boolean.TRUE);
			bilanzKonto.setIstpassiv(Boolean.TRUE);
			bilanzKonto.setIstaufwand(Boolean.TRUE);
			bilanzKonto.setIstertrag(Boolean.TRUE);
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
		reader.readLine();
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
			konto.setIstsoll(new Boolean("J".equals(tokens[2].substring(1, tokens[2].length()))));
			konto.setIsthaben(new Boolean("J".equals(tokens[3].substring(1, tokens[3].length()))));
			konto.setIstaktiv(new Boolean("J".equals(tokens[4].substring(1, tokens[4].length()))));
			konto.setIstpassiv(new Boolean("J".equals(tokens[5].substring(1, tokens[5].length()))));
			konto.setIstaufwand(new Boolean("J".equals(tokens[6].substring(1, tokens[6].length()))));
			konto.setIstertrag(new Boolean("J".equals(tokens[7].substring(1, tokens[7].length()))));
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
		QueryResult resultVector = kontoDAO.listKontos(connection, queryOberKto, null, null);
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
}

/*
 * $Log: InitDB.java,v $
 * Revision 1.8  2006/11/25 12:59:38  phormanns
 * ResultVector in QueryResult umbenannt
 * Refactoring: DAOs liefern QueryResult bei Select
 *
 * Revision 1.7  2006/11/24 21:10:03  phormanns
 * Datenmodellerweiterung bei Konto und Buchungsliste
 *
 * Revision 1.6  2006/02/24 22:27:40  phormanns
 * Copyright
 * diverse Verbesserungen
 *
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
