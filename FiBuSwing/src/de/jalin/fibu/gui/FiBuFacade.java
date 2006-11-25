// $Id: FiBuFacade.java,v 1.14 2006/11/25 13:16:15 phormanns Exp $
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
package de.jalin.fibu.gui;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.hostsharing.admin.client.XmlRpcClientException;
import net.hostsharing.admin.client.XmlRpcClientTransaction;
import net.hostsharing.admin.client.XmlRpcTransactionClient;
import net.hostsharing.admin.runtime.AbstractCall;
import net.hostsharing.admin.runtime.QueryResult;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.BuchungData;
import de.jalin.fibu.server.buchung.BuchungDeleteCall;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungsliste.BuchungslisteListCall;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineAddCall;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineData;
import de.jalin.fibu.server.customer.CustomerData;
import de.jalin.fibu.server.customer.CustomerListCall;
import de.jalin.fibu.server.customer.CustomerUpdateCall;
import de.jalin.fibu.server.journal.JournalAddCall;
import de.jalin.fibu.server.journal.JournalData;
import de.jalin.fibu.server.journal.JournalListCall;
import de.jalin.fibu.server.journal.JournalUpdateCall;
import de.jalin.fibu.server.konto.KontoData;
import de.jalin.fibu.server.konto.KontoListCall;
import de.jalin.fibu.server.mwst.MwstData;
import de.jalin.fibu.server.mwst.MwstListCall;

public class FiBuFacade {
	
	private static final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
	private static final NumberFormat percentFormatter = new DecimalFormat("0.0");
	private static final NumberFormat currencyFormatter = new DecimalFormat("0.00");
	
	private XmlRpcTransactionClient client;
	private String ticket;
	private CustomerData customer;
	private KontoData bilanzKonto;
	private KontoData guvKonto;
	
	public FiBuFacade(String url, String ticket) throws FiBuException {
		try {
			client = new XmlRpcTransactionClient(url);
			this.ticket = ticket;
			customer = new CustomerData();
			bilanzKonto = new KontoData();
			guvKonto = new KontoData();
			initCustomerUndKonten();
		} catch (MalformedURLException e) {
			new FiBuSystemException("Fehler in Server-URL", e);
		}
	}
	
	public CustomerData getCustomer() {
		return customer;
	}
	
	public void setCustomer(CustomerData updatedCustomer, String bilanzKtoNr, String guvKtoNr) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			KontoData sampleBilanzKto = new KontoData();
			sampleBilanzKto.setKontoid(customer.getBilanzkonto());
			KontoData sampleGuVKonto = new KontoData();
			sampleGuVKonto.setKontoid(customer.getGuvkonto());
			tx.addCall(new KontoListCall(sampleBilanzKto));
			tx.addCall(new KontoListCall(sampleGuVKonto));
			Vector callResults = tx.perform();
			QueryResult resBilanzKto = new QueryResult((Vector) callResults, 0);
			QueryResult resGuVKto = new QueryResult((Vector) callResults, 1);
			bilanzKonto.readFromResult(resBilanzKto, 0);
			guvKonto.readFromResult(resGuVKto, 0);
			updatedCustomer.setCustid(customer.getCustid());
			updatedCustomer.setBilanzkonto(bilanzKonto.getKontoid());
			updatedCustomer.setGuvkonto(guvKonto.getKontoid());
			updateCustomer(updatedCustomer);
			customer = updatedCustomer;
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	public Vector getOffeneJournale() throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			JournalData queryJournal = new JournalData();
			queryJournal.setAbsummiert(Boolean.FALSE);
			JournalListCall journalListCall = new JournalListCall(queryJournal);
			journalListCall.addOrderByColumn("jourid", false);
			tx.addCall(journalListCall);
			QueryResult result = new QueryResult((Vector) tx.perform(), 0);
			Vector offeneJournale = new Vector();
			JournalData jour = null;
			for (int i=0; i<result.size(); i++) {
				jour = new JournalData();
				jour.readFromResult(result, i);
				offeneJournale.addElement(jour);
			}
			return offeneJournale;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public Vector getAlleJournale() throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			JournalData queryJournal = new JournalData();
			JournalListCall journalListCall = new JournalListCall(queryJournal);
			journalListCall.addOrderByColumn("jourid", false);
			tx.addCall(journalListCall);
			QueryResult result = new QueryResult((Vector) tx.perform(), 0);
			Vector alleJournale = new Vector();
			JournalData jour = null;
			for (int i=0; i<result.size(); i++) {
				jour = new JournalData();
				jour.readFromResult(result, i);
				alleJournale.addElement(jour);
			}
			return alleJournale;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public KontoData getBilanzKonto() throws FiBuException {
		return bilanzKonto;
	}
	
	public KontoData getGuVKonto() {
		return guvKonto;
	}
	
	public void absummieren(JournalData whereJournal) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			JournalData setJournal = (JournalData) whereJournal.cloneData();
			setJournal.setAbsummiert(Boolean.TRUE);
			tx.addCall(new JournalUpdateCall(setJournal, whereJournal));
			tx.perform();
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	public JournalData neuesJournal() throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			CustomerData whereCust = new CustomerData();
			tx.addCall(new CustomerListCall(whereCust));
			Vector functionsResults = (Vector) tx.perform();
			QueryResult resultVector = new QueryResult(functionsResults, 0);
			CustomerData cust = new CustomerData();
			cust.readFromResult(resultVector, 0);
			tx = new XmlRpcClientTransaction(client, ticket);
			JournalData addJournal = new JournalData();
			addJournal.setAbsummiert(Boolean.FALSE);
			addJournal.setJahr(cust.getJahr());
			addJournal.setPeriode(cust.getPeriode());
			addJournal.setSince(new Date());
			tx.addCall(new JournalAddCall(addJournal));
			functionsResults = (Vector) tx.perform();
			resultVector = new QueryResult(functionsResults, 0);
			JournalData newJournal = new JournalData();
			newJournal.readFromResult(resultVector, 0);
			return newJournal;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public KontoData getKonto(String ktoNr) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			KontoData whereKto = new KontoData();
			whereKto.setKontonr(ktoNr);
			tx.addCall(new KontoListCall(whereKto));
			QueryResult resultVector = new QueryResult((Vector) tx.perform(), 0);
			KontoData kto = new KontoData();
			kto.readFromResult(resultVector, 0);
			return kto;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public void buchen(JournalData journal, String belegNr, String buchungstext, String valutaDatum, 
			String sollKtoNr, String habenKtoNr, String betrag) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			BuchungsmaschineData buchung = new BuchungsmaschineData();
			buchung.setBelegnr(belegNr);
			buchung.setBrutto(new Integer(Math.round(currencyFormatter.parse(betrag).floatValue() * 100.0f)));
			buchung.setBuchungstext(buchungstext);
			buchung.setHabenkontonr(habenKtoNr);
			buchung.setSollkontonr(sollKtoNr);
			buchung.setJourid(journal.getJourid());
			buchung.setValuta(dateFormatter.parse(valutaDatum));
			buchung.setHabenmwstid(getKonto(habenKtoNr).getMwstid());
			buchung.setSollmwstid(getKonto(sollKtoNr).getMwstid());
			tx.addCall(new BuchungsmaschineAddCall(buchung));
			tx.perform();
		} catch (NumberFormatException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (ParseException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public String getMWSt(KontoData kto) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			MwstData mwst = new MwstData();
			mwst.setMwstid(kto.getMwstid());
			tx.addCall(new MwstListCall(mwst));
			mwst = new MwstData();
			mwst.readFromResult(new QueryResult((Vector) tx.perform(), 0), 0);
			double mwstFloat = mwst.getMwstsatz().floatValue() / 100.0;
			return percentFormatter.format(mwstFloat);
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public List getKontenListe() throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			KontoData sampleKto = new KontoData();
			KontoListCall kontoListCall = new KontoListCall(sampleKto);
			kontoListCall.addOrderByColumn("kontonr", true);
			tx.addCall(kontoListCall);
			QueryResult resultVector = new QueryResult((Vector) tx.perform(), 0);
			List ktoList = new ArrayList();
			KontoData kto = null;
			for (int i=0; i<resultVector.size(); i++) {
				kto = new KontoData();
				kto.readFromResult(resultVector, i);
				ktoList.add(kto);
			}
			return ktoList;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public List getBuchungsliste(KontoData kto) throws FiBuException {
		BuchungslisteData sampleBuchung = new BuchungslisteData();
		sampleBuchung.setKontoid(kto.getKontoid());
		AbstractCall buchungslisteListCall = new BuchungslisteListCall(sampleBuchung);
		buchungslisteListCall.addOrderByColumn("buzlid", true);
		return callBuchungslisteList(buchungslisteListCall);
	}

	public List getBuchungsliste(JournalData journal) throws FiBuException {
		BuchungslisteData sampleBuchung = new BuchungslisteData();
		sampleBuchung.setJourid(journal.getJourid());
		AbstractCall buchungslisteListCall = new BuchungslisteListCall(sampleBuchung);
		buchungslisteListCall.addOrderByColumn("buzlid", false);
		return callBuchungslisteList(buchungslisteListCall);
	}
	
	public List getBuchungsliste(BuchungData buchung) throws FiBuException {
		BuchungslisteData sampleBuchung = new BuchungslisteData();
		sampleBuchung.setBuchid(buchung.getBuchid());
		AbstractCall buchungslisteListCall = new BuchungslisteListCall(sampleBuchung);
		buchungslisteListCall.addOrderByColumn("buzlid", false);
		return callBuchungslisteList(buchungslisteListCall);
	}
	
	private List callBuchungslisteList(AbstractCall buchungslisteListCall) throws FiBuUserException {
		// TODO Call mit zwei Aufrufen BuchungsListe, z.B. Aktiva - Passiva oder Auswand - Ertrag
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			tx.addCall(buchungslisteListCall);
			QueryResult resultVector = new QueryResult((Vector) tx.perform(), 0);
			List buchungsList = new ArrayList();
			BuchungslisteData buchungsListData = null;
			for (int i=0; i<resultVector.size(); i++) {
				buchungsListData = new BuchungslisteData();
				buchungsListData.readFromResult(resultVector, i);
				buchungsList.add(buchungsListData);
			}
			return buchungsList;
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	private void updateCustomer(CustomerData writeCust) throws FiBuUserException {
		try {
			CustomerData whereCust = new CustomerData();
			whereCust.setCustid(writeCust.getCustid());
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			tx.addCall(new CustomerUpdateCall(writeCust, whereCust));
			tx.perform();
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	private void initCustomerUndKonten() throws FiBuUserException {
		try {
			CustomerData sampleCust = new CustomerData();
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			tx.addCall(new CustomerListCall(sampleCust));
			QueryResult result = new QueryResult((Vector) tx.perform(), 0);
			customer.readFromResult(result, 0);
			tx = new XmlRpcClientTransaction(client, ticket);
			KontoData sampleBilanzKto = new KontoData();
			sampleBilanzKto.setKontoid(customer.getBilanzkonto());
			KontoData sampleGuVKonto = new KontoData();
			sampleGuVKonto.setKontoid(customer.getGuvkonto());
			tx.addCall(new KontoListCall(sampleBilanzKto));
			tx.addCall(new KontoListCall(sampleGuVKonto));
			Vector callResults = tx.perform();
			QueryResult resBilanzKto = new QueryResult((Vector) callResults, 0);
			QueryResult resGuVKto = new QueryResult((Vector) callResults, 1);
			bilanzKonto.readFromResult(resBilanzKto, 0);
			guvKonto.readFromResult(resGuVKto, 0);
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public void buchungLoeschen(Integer buchid) throws FiBuUserException {
		try {
			BuchungData buchung = new BuchungData();
			buchung.setBuchid(buchid);
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, ticket);
			tx.addCall(new BuchungDeleteCall(buchung));
			tx.perform();
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

}

/*
 *  $Log: FiBuFacade.java,v $
 *  Revision 1.14  2006/11/25 13:16:15  phormanns
 *  ResultVector in QueryResult umbenannt
 *  Refactoring: DAOs liefern QueryResult bei Select
 *
 *  Revision 1.13  2006/11/24 21:12:59  phormanns
 *  kleine Aenderungen
 *
 *  Revision 1.12  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.11  2006/01/05 13:09:40  phormanns
 *  Buchungen in offenen Journalen können gelöscht werden
 *
 *  Revision 1.10  2005/11/24 17:43:06  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.9  2005/11/23 23:16:49  phormanns
 *  Lesen Konto-Hierarchie und Buchungsliste optimiert
 *
 *  Revision 1.8  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.7  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.6  2005/11/15 21:20:36  phormanns
 *  Refactorings in FiBuGUI
 *  Focus und Shortcuts in BuchungsForm und StammdatenForm
 *
 *  Revision 1.5  2005/11/11 21:40:35  phormanns
 *  Einstiegskonten im Stammdaten-Form
 *
 *  Revision 1.4  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 *  Revision 1.3  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 *  Revision 1.2  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
