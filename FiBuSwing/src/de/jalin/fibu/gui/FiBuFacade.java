// $Id: FiBuFacade.java,v 1.9 2005/11/23 23:16:49 phormanns Exp $
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
import net.hostsharing.admin.runtime.ResultVector;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.BuchungAddCall;
import de.jalin.fibu.server.buchung.BuchungData;
import de.jalin.fibu.server.buchung.BuchungListCall;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungsliste.BuchungslisteListCall;
import de.jalin.fibu.server.buchungszeile.BuchungszeileAddCall;
import de.jalin.fibu.server.buchungszeile.BuchungszeileData;
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
	
	public FiBuFacade() throws FiBuException {
		try {
			client = new XmlRpcTransactionClient();
		} catch (MalformedURLException e) {
			new FiBuSystemException("Fehler in Server-URL", e);
		}
	}
	
	public String getFirma() throws FiBuException {
		return getCustAttribute("firma");
	}

	public void setFirma(String value) throws FiBuException {
		setCustAttribute("firma", value);
	}

	public String getJahrAktuell() throws FiBuException {
		return getCustAttribute("jahr");
	}
	
	public void setJahrAktuell(String gjAktuell) throws FiBuException {
		setCustAttribute("jahr", gjAktuell);
	}
	
	public String getPeriodeAktuell() throws FiBuException {
		return getCustAttribute("periode");
	}
	
	public void setPeriodeAktuell(String periodeAktuell) throws FiBuException {
		setCustAttribute("periode", periodeAktuell);
	}
	
	public Vector getOffeneJournale() throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			JournalData queryJournal = new JournalData();
			queryJournal.setAbsummiert(Boolean.FALSE);
			tx.addCall(new JournalListCall(queryJournal));
			ResultVector result = new ResultVector((Vector) tx.perform().get(0));
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
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			JournalData queryJournal = new JournalData();
			tx.addCall(new JournalListCall(queryJournal));
			ResultVector result = new ResultVector((Vector) tx.perform().get(0));
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
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			CustomerData queryCust = new CustomerData();
			tx.addCall(new CustomerListCall(queryCust));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
			CustomerData cust = new CustomerData();
			cust.readFromResult(resultVector, 0);
			KontoData queryKto = new KontoData();
			queryKto.setKontoid(cust.getBilanzkonto());
			tx = new XmlRpcClientTransaction(client, "xxx");
			tx.addCall(new KontoListCall(queryKto));
			resultVector = new ResultVector((Vector) tx.perform().get(0));
			KontoData bilanzKto = new KontoData();
			bilanzKto.readFromResult(resultVector, 0);
			return bilanzKto;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	public void setBilanzKonto(String ktoNr) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			KontoData queryKto = new KontoData();
			queryKto.setKontonr(ktoNr);
			tx.addCall(new KontoListCall(queryKto));
			ResultVector result = new ResultVector((Vector) tx.perform().get(0));
			KontoData bilanzKto = new KontoData();
			bilanzKto.readFromResult(result, 0);
			tx = new XmlRpcClientTransaction(client, "xxx");
			CustomerData firma = new CustomerData();
			firma.setBilanzkonto(bilanzKto.getKontoid());
			tx.addCall(new CustomerUpdateCall(firma, new CustomerData()));
			tx.perform();
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	public KontoData getGuVKonto() throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			CustomerData queryCust = new CustomerData();
			tx.addCall(new CustomerListCall(queryCust));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
			CustomerData cust = new CustomerData();
			cust.readFromResult(resultVector, 0);
			KontoData queryKto = new KontoData();
			queryKto.setKontoid(cust.getGuvkonto());
			tx = new XmlRpcClientTransaction(client, "xxx");
			tx.addCall(new KontoListCall(queryKto));
			resultVector = new ResultVector((Vector) tx.perform().get(0));
			KontoData guvKto = new KontoData();
			guvKto.readFromResult(resultVector, 0);
			return guvKto;
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	public void setGuVKonto(String ktoNr) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			KontoData queryKto = new KontoData();
			queryKto.setKontonr(ktoNr);
			tx.addCall(new KontoListCall(queryKto));
			ResultVector result = new ResultVector((Vector) tx.perform().get(0));
			KontoData guvKto = new KontoData();
			guvKto.readFromResult(result, 0);
			tx = new XmlRpcClientTransaction(client, "xxx");
			CustomerData firma = new CustomerData();
			firma.setGuvkonto(guvKto.getKontoid());
			tx.addCall(new CustomerUpdateCall(firma, new CustomerData()));
			tx.perform();
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	public void absummieren(JournalData whereJournal) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
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
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			CustomerData whereCust = new CustomerData();
			tx.addCall(new CustomerListCall(whereCust));
			Vector functionsResults = (Vector) tx.perform().get(0);
			ResultVector resultVector = new ResultVector(functionsResults);
			CustomerData cust = new CustomerData();
			cust.readFromResult(resultVector, 0);
			tx = new XmlRpcClientTransaction(client, "xxx");
			JournalData addJournal = new JournalData();
			addJournal.setAbsummiert(Boolean.FALSE);
			addJournal.setJahr(cust.getJahr());
			addJournal.setPeriode(cust.getPeriode());
			addJournal.setSince(new Date());
			tx.addCall(new JournalAddCall(addJournal));
			functionsResults = (Vector) tx.perform().get(0);
			resultVector = new ResultVector(functionsResults);
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
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			KontoData whereKto = new KontoData();
			whereKto.setKontonr(ktoNr);
			tx.addCall(new KontoListCall(whereKto));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
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
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			BuchungData addBuchung = new BuchungData();
			addBuchung.setBelegnr(belegNr);
			addBuchung.setBuchungstext(buchungstext);
			addBuchung.setValuta(dateFormatter.parse(valutaDatum));
			addBuchung.setJourid(journal.getJourid());
			tx.addCall(new BuchungAddCall(addBuchung));
			tx.addCall(new BuchungListCall(addBuchung));
			KontoData sollKto = new KontoData();
			KontoData habenKto = new KontoData();
			sollKto.setKontonr(sollKtoNr);
			habenKto.setKontonr(habenKtoNr);
			tx.addCall(new KontoListCall(sollKto));
			tx.addCall(new KontoListCall(habenKto));
			Vector functResults = tx.perform();
			BuchungData buchung = new BuchungData();
			buchung.readFromResult(new ResultVector((Vector) functResults.get(1)), 0);
			tx = new XmlRpcClientTransaction(client, "xxx");
			sollKto.readFromResult(new ResultVector((Vector) functResults.get(2)), 0);
			habenKto.readFromResult(new ResultVector((Vector) functResults.get(3)), 0);
			BuchungszeileData zeileSoll = new BuchungszeileData();
			BuchungszeileData zeileHaben = new BuchungszeileData();
			zeileSoll.setKontoid(sollKto.getKontoid());
			zeileSoll.setBuchid(buchung.getBuchid());
			zeileHaben.setKontoid(habenKto.getKontoid());
			zeileHaben.setBuchid(buchung.getBuchid());
			float betragFloat = currencyFormatter.parse(betrag).floatValue() * 100.0f;
			Integer betragInt = new Integer((new Float(betragFloat)).intValue());
			zeileSoll.setBetrag(betragInt);
			zeileHaben.setBetrag(betragInt);
			zeileSoll.setHaben(Boolean.FALSE);
			zeileSoll.setSoll(Boolean.TRUE);
			zeileHaben.setHaben(Boolean.TRUE);
			zeileHaben.setSoll(Boolean.FALSE);
			tx.addCall(new BuchungszeileAddCall(zeileSoll));
			tx.addCall(new BuchungszeileAddCall(zeileHaben));
			tx.perform();
		} catch (NumberFormatException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (ParseException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

	public String getMWSt(KontoData kto) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			MwstData mwst = new MwstData();
			mwst.setMwstid(kto.getMwstid());
			tx.addCall(new MwstListCall(mwst));
			mwst = new MwstData();
			mwst.readFromResult(new ResultVector((Vector) tx.perform().get(0)), 0);
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
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			KontoData sampleKto = new KontoData();
			tx.addCall(new KontoListCall(sampleKto));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
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
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			BuchungslisteData sampleBuchung = new BuchungslisteData();
			sampleBuchung.setKontoid(kto.getKontoid());
			tx.addCall(new BuchungslisteListCall(sampleBuchung));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
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

	public List getBuchungsliste(JournalData journal) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			BuchungslisteData sampleBuchung = new BuchungslisteData();
			sampleBuchung.setJourid(journal.getJourid());
			tx.addCall(new BuchungslisteListCall(sampleBuchung));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
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
	
	public List getBuchungsliste(BuchungData buchung) throws FiBuException {
		try {
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			BuchungslisteData sampleBuchung = new BuchungslisteData();
			sampleBuchung.setBuchid(buchung.getBuchid());
			tx.addCall(new BuchungslisteListCall(sampleBuchung));
			ResultVector resultVector = new ResultVector((Vector) tx.perform().get(0));
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
	
	private void setCustAttribute(String property, String value) throws FiBuUserException {
		try {
			CustomerData whereCust = new CustomerData();
			CustomerData writeCust = new CustomerData();
			writeCust.setAttributeValue(property, value);
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			tx.addCall(new CustomerUpdateCall(writeCust, whereCust));
			tx.perform();
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		} catch (XmlRpcTransactionException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}
	
	private String getCustAttribute(String property) throws FiBuUserException {
		try {
			CustomerData cust = new CustomerData();
			XmlRpcClientTransaction tx = new XmlRpcClientTransaction(client, "xxx");
			tx.addCall(new CustomerListCall(cust));
			ResultVector result = new ResultVector((Vector) tx.perform().get(0));
			return (String) result.get(0, property);
		} catch (XmlRpcClientException e) {
			throw new FiBuUserException(e.getMessage());
		}
	}

}

/*
 *  $Log: FiBuFacade.java,v $
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
