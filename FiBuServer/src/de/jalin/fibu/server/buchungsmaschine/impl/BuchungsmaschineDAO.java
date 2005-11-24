// $Id: BuchungsmaschineDAO.java,v 1.1 2005/11/24 17:41:18 phormanns Exp $
package de.jalin.fibu.server.buchungsmaschine.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.ResultVector;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.BuchungData;
import de.jalin.fibu.server.buchung.impl.BuchungDAO;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineData;
import de.jalin.fibu.server.buchungszeile.BuchungszeileData;
import de.jalin.fibu.server.buchungszeile.impl.BuchungszeileDAO;
import de.jalin.fibu.server.konto.KontoData;
import de.jalin.fibu.server.konto.impl.KontoDAO;
import de.jalin.fibu.server.mwst.MwstData;
import de.jalin.fibu.server.mwst.impl.MwstDAO;

public class BuchungsmaschineDAO extends
		de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineDAO {
	
	private BuchungDAO buchungDAO;
	private BuchungszeileDAO buchungszeileDAO;
	private KontoDAO kontoDAO;
	private MwstDAO mwstDAO;

	public BuchungsmaschineDAO() {
		buchungDAO = new BuchungDAO();
		buchungszeileDAO = new BuchungszeileDAO();
		kontoDAO = new KontoDAO();
		mwstDAO = new MwstDAO();
	}

	public void addBuchungsmaschine(Connection connect, BuchungsmaschineData writeData) throws XmlRpcTransactionException {
		MwstData sollMwst = new MwstData();
		MwstData habenMwst = new MwstData();
		KontoData sollKto = new KontoData();
		KontoData habenKto = new KontoData();
		sollMwst.setMwstid(writeData.getSollmwstid());
		habenMwst.setMwstid(writeData.getHabenmwstid());
		sollKto.setKontonr(writeData.getSollkontonr());
		habenKto.setKontonr(writeData.getHabenkontonr());
		ResultVector resSollMwst = new ResultVector(mwstDAO.listMwsts(connect, sollMwst, null, null));
		sollMwst.readFromResult(resSollMwst, 0);
		ResultVector resHabenMwst = new ResultVector(mwstDAO.listMwsts(connect, habenMwst, null, null));
		habenMwst.readFromResult(resHabenMwst, 0);
		ResultVector resSollKto = new ResultVector(kontoDAO.listKontos(connect, sollKto, null, null));
		sollKto.readFromResult(resSollKto, 0);
		ResultVector resHabenKto = new ResultVector(kontoDAO.listKontos(connect, habenKto, null, null));
		habenKto.readFromResult(resHabenKto, 0);
		BuchungData buchung = new BuchungData();
		buchung.setBuchid(new Integer(buchungDAO.nextId(connect)));
		buchung.setBelegnr(writeData.getBelegnr());
		buchung.setBuchungstext(writeData.getBuchungstext());
		buchung.setErfassung(new Date());
		buchung.setJourid(writeData.getJourid());
		buchung.setValuta(writeData.getValuta());
		buchungDAO.addBuchung(connect, buchung);
		ResultVector resBuchung = new ResultVector(buchungDAO.listBuchungs(connect, buchung, null, null));
		buchung.readFromResult(resBuchung, 0);
		double brutto = writeData.getBrutto().doubleValue() / 100.0d;
		double sollMwstSatz = sollMwst.getMwstsatz().doubleValue() / 10000.0d;
		double habenMwstSatz = habenMwst.getMwstsatz().doubleValue() / 10000.0d;
		double sollNetto = brutto;
		double sollMwstBetrag = 0.0d;
		double habenNetto = brutto;
		double habenMwstBetrag = 0.0d;
		if (sollMwstSatz > 0.001d) {
			sollNetto = brutto / (1.0d + sollMwstSatz);
			sollMwstBetrag = brutto - sollNetto;
		}
		if (habenMwstSatz > 0.001d) {
			habenNetto = brutto / (1.0d + habenMwstSatz);
			habenMwstBetrag = brutto - habenNetto;
		}
		BuchungszeileData sollZeile = new BuchungszeileData();
		sollZeile.setBuzlid(new Integer(buchungszeileDAO.nextId(connect)));
		sollZeile.setBetrag(new Integer(Math.round((float) sollNetto * 100.0f)));
		sollZeile.setBuchid(buchung.getBuchid());
		sollZeile.setHaben(Boolean.FALSE);
		sollZeile.setSoll(Boolean.TRUE);
		sollZeile.setKontoid(sollKto.getKontoid());
		buchungszeileDAO.addBuchungszeile(connect, sollZeile);
		BuchungszeileData habenZeile = new BuchungszeileData();
		habenZeile.setBuzlid(new Integer(buchungszeileDAO.nextId(connect)));
		habenZeile.setBetrag(new Integer(Math.round((float) habenNetto * 100.0f)));
		habenZeile.setBuchid(buchung.getBuchid());
		habenZeile.setHaben(Boolean.TRUE);
		habenZeile.setSoll(Boolean.FALSE);
		habenZeile.setKontoid(habenKto.getKontoid());
		buchungszeileDAO.addBuchungszeile(connect, habenZeile);
		if (sollMwstBetrag > 0.001d) {
			BuchungszeileData sollMwstZeile = new BuchungszeileData();
			sollMwstZeile.setBuzlid(new Integer(buchungszeileDAO.nextId(connect)));
			sollMwstZeile.setBetrag(new Integer(Math.round((float) sollMwstBetrag * 100.0f)));
			sollMwstZeile.setBuchid(buchung.getBuchid());
			sollMwstZeile.setHaben(Boolean.FALSE);
			sollMwstZeile.setSoll(Boolean.TRUE);
			sollMwstZeile.setKontoid(sollMwst.getMwstkontosoll());
			buchungszeileDAO.addBuchungszeile(connect, sollMwstZeile);
		}
		if (habenMwstBetrag > 0.001d) {
			BuchungszeileData habenMwstZeile = new BuchungszeileData();
			habenMwstZeile.setBuzlid(new Integer(buchungszeileDAO.nextId(connect)));
			habenMwstZeile.setBetrag(new Integer(Math.round((float) habenMwstBetrag * 100.0f)));
			habenMwstZeile.setBuchid(buchung.getBuchid());
			habenMwstZeile.setHaben(Boolean.TRUE);
			habenMwstZeile.setSoll(Boolean.FALSE);
			habenMwstZeile.setKontoid(sollMwst.getMwstkontohaben());
			buchungszeileDAO.addBuchungszeile(connect, habenMwstZeile);
		}
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		// Nichts tun
	}

	public void deleteBuchungsmaschine(Connection connect, BuchungsmaschineData whereData) throws XmlRpcTransactionException {
		// Nichts tun
	}

	public Vector listBuchungsmaschines(Connection connect, BuchungsmaschineData whereData, DisplayColumns display, OrderByList orderBy) throws XmlRpcTransactionException {
		// Nichts tun
		return null;
	}

	public void updateBuchungsmaschine(Connection connect, BuchungsmaschineData writeData, BuchungsmaschineData whereData) throws XmlRpcTransactionException {
		// Nichts tun
	}

}

/*
 *  $Log: BuchungsmaschineDAO.java,v $
 *  Revision 1.1  2005/11/24 17:41:18  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 */
