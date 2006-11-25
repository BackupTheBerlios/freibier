// $Id: BuchungslisteDAO.java,v 1.4 2006/11/25 12:59:38 phormanns Exp $
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
package de.jalin.fibu.server.buchungsliste.impl;

import java.sql.Connection;

import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.QueryResult;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.CreateViewStatement;
import de.jalin.fibu.server.buchung.impl.BuchungDAO;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungszeile.impl.BuchungszeileDAO;
import de.jalin.fibu.server.journal.impl.JournalDAO;
import de.jalin.fibu.server.konto.impl.KontoDAO;

public class BuchungslisteDAO extends
		de.jalin.fibu.server.buchungsliste.BuchungslisteDAO {
	
	public void addBuchungsliste(Connection connect, BuchungslisteData writeData) throws XmlRpcTransactionException {
		// TODO Auto-generated method stub
		super.addBuchungsliste(connect, writeData);
	}

	private KontoDAO kontoDAO;
	private BuchungDAO buchungDAO;
	private BuchungszeileDAO buchungszeileDAO;
	private JournalDAO journalDAO;

	public BuchungslisteDAO() throws XmlRpcTransactionException {
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
		createStmt.addColumn(kontoDAO.getModuleName(), "istsoll");
		createStmt.addColumn(kontoDAO.getModuleName(), "isthaben");
		createStmt.addColumn(kontoDAO.getModuleName(), "istaktiv");
		createStmt.addColumn(kontoDAO.getModuleName(), "istpassiv");
		createStmt.addColumn(kontoDAO.getModuleName(), "istaufwand");
		createStmt.addColumn(kontoDAO.getModuleName(), "istertrag");
		createStmt.addJoin(kontoDAO.getModuleName(), "kontoid", 
				buchungszeileDAO.getModuleName(), "kontoid");
		createStmt.addJoin(buchungDAO.getModuleName(), "buchid",
				buchungszeileDAO.getModuleName(), "buchid");
		createStmt.addJoin(journalDAO.getModuleName(), "jourid", 
				buchungDAO.getModuleName(), "jourid");
		createStmt.createDatabaseObject(connect);
	}
	
	public void dropDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		// nichts tun!
	}

	public QueryResult listBuchungslistes(Connection connect, BuchungslisteData whereData, DisplayColumns display, OrderByList orderBy) throws XmlRpcTransactionException {
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
 *  Revision 1.4  2006/11/25 12:59:38  phormanns
 *  ResultVector in QueryResult umbenannt
 *  Refactoring: DAOs liefern QueryResult bei Select
 *
 *  Revision 1.3  2006/11/24 21:10:03  phormanns
 *  Datenmodellerweiterung bei Konto und Buchungsliste
 *
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/22 21:26:08  phormanns
 *  Modul Buchungsliste (noch nicht genutzt)
 *
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
