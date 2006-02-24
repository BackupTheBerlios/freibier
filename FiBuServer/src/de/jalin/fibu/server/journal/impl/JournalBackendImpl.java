// $Id: JournalBackendImpl.java,v 1.2 2006/02/24 22:27:40 phormanns Exp $
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
package de.jalin.fibu.server.journal.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.journal.JournalBackend;
import de.jalin.fibu.server.journal.JournalData;
import de.jalin.fibu.server.journal.JournalException;

public class JournalBackendImpl implements JournalBackend {
	
	private JournalDAO journalDAO;
	private DisplayColumns display;

	public JournalBackendImpl() throws JournalException {
		try {
			this.journalDAO = new JournalDAO();
			this.display = new DisplayColumns();
			this.display.addColumnDefinition("jourid", 1);
			this.display.addColumnDefinition("journr", 1);
			this.display.addColumnDefinition("jahr", 1);
			this.display.addColumnDefinition("periode", 1);
			this.display.addColumnDefinition("since", 1);
			this.display.addColumnDefinition("lastupdate", 2);
			this.display.addColumnDefinition("absummiert", 1);
		} catch (XmlRpcTransactionException e) {
			throw new JournalException(10401, e);
		}
	}

	public Vector executeJournalListCall(Connection dbConnection,
			XmlRpcSession session, JournalData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws JournalException {
		try {
			return journalDAO.listJournals(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new JournalException(10401, e);
		}
	}

	public Vector executeJournalAddCall(Connection dbConnection,
			XmlRpcSession session, JournalData writeData)
			throws JournalException {
		try {
			int jourId = journalDAO.nextId(dbConnection);
			Integer jourIdInt = new Integer(jourId);
			writeData.setJourid(jourIdInt);
			writeData.setLastupdate(new Date());
			if (writeData.getJournr() == null) {
				writeData.setJournr(jourIdInt.toString());
			}
			journalDAO.addJournal(dbConnection, writeData);
			JournalData whereJour = new JournalData();
			whereJour.setJourid(jourIdInt);
			return journalDAO.listJournals(dbConnection, whereJour, display, null);
		} catch (XmlRpcTransactionException e) {
			throw new JournalException(10401, e);
		}
	}

	public void executeJournalUpdateCall(Connection dbConnection,
			XmlRpcSession session, JournalData writeData, JournalData whereData)
			throws JournalException {
		try {
			writeData.setLastupdate(new Date());
			journalDAO.updateJournal(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new JournalException(10401, e);
		}
	}

	public void executeJournalDeleteCall(Connection dbConnection,
			XmlRpcSession session, JournalData whereData)
			throws JournalException {
		try {
			journalDAO.deleteJournal(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new JournalException(10401, e);
		}
	}
}

/*
 *  $Log: JournalBackendImpl.java,v $
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
