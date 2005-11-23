// $Id: BuchungslisteDAO.java,v 1.1 2005/11/22 21:26:08 phormanns Exp $
package de.jalin.fibu.server.buchungsliste.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.CreateViewStatement;
import de.jalin.fibu.server.buchung.impl.BuchungDAO;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungszeile.impl.BuchungszeileDAO;
import de.jalin.fibu.server.journal.impl.JournalDAO;
import de.jalin.fibu.server.konto.impl.KontoDAO;

public class BuchungslisteDAO extends
		de.jalin.fibu.server.buchungsliste.BuchungslisteDAO {
	
	private KontoDAO kontoDAO;
	private BuchungDAO buchungDAO;
	private BuchungszeileDAO buchungszeileDAO;
	private JournalDAO journalDAO;

	public BuchungslisteDAO() {
		super();
		kontoDAO = new KontoDAO();
		journalDAO = new JournalDAO();
		buchungDAO = new BuchungDAO();
		buchungszeileDAO = new BuchungszeileDAO();
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateViewStatement createStmt = new CreateViewStatement(super.getTable());
		createStmt.addTable(kontoDAO.getTable());
		createStmt.addTable(journalDAO.getTable());
		createStmt.addTable(buchungDAO.getTable());
		createStmt.addTable(buchungszeileDAO.getTable());
		createStmt.addColumn(buchungszeileDAO.getModuleName(), "buzlid");
		createStmt.addColumn(buchungszeileDAO.getModuleName(), "betrag");
		createStmt.addColumn(buchungszeileDAO.getModuleName(), "soll");
		createStmt.addColumn(buchungszeileDAO.getModuleName(), "haben");
		createStmt.addColumn(buchungDAO.getModuleName(), "buchid");
		createStmt.addColumn(buchungDAO.getModuleName(), "belegnr");
		createStmt.addColumn(buchungDAO.getModuleName(), "buchungstext");
		createStmt.addColumn(buchungDAO.getModuleName(), "valuta");
		createStmt.addColumn(buchungDAO.getModuleName(), "erfassung");
		createStmt.addColumn(journalDAO.getModuleName(), "jourid");
		createStmt.addColumn(journalDAO.getModuleName(), "journr");
		createStmt.addColumn(journalDAO.getModuleName(), "jahr");
		createStmt.addColumn(journalDAO.getModuleName(), "periode");
		createStmt.addColumn(journalDAO.getModuleName(), "since");
		createStmt.addColumn(journalDAO.getModuleName(), "absummiert");
		createStmt.addColumn(kontoDAO.getModuleName(), "kontoid");
		createStmt.addColumn(kontoDAO.getModuleName(), "kontonr");
		createStmt.addColumn(kontoDAO.getModuleName(), "bezeichnung");
		createStmt.addJoin(kontoDAO.getModuleName(), "kontoid", 
				buchungszeileDAO.getModuleName(), "kontoid");
		createStmt.addJoin(buchungDAO.getModuleName(), "buchid",
				buchungszeileDAO.getModuleName(), "buchid");
		createStmt.addJoin(journalDAO.getModuleName(), "jourid", 
				buchungDAO.getModuleName(), "jourid");
		createStmt.createDatabaseObject(connect);
	}

	public Vector listBuchungslistes(Connection connect, BuchungslisteData whereData, DisplayColumns display, OrderByList orderBy) throws XmlRpcTransactionException {
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
 *  Revision 1.1  2005/11/22 21:26:08  phormanns
 *  Modul Buchungsliste (noch nicht genutzt)
 *
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */