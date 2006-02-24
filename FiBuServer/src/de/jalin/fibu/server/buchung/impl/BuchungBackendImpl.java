// $Id: BuchungBackendImpl.java,v 1.2 2006/02/24 22:27:40 phormanns Exp $
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
package de.jalin.fibu.server.buchung.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchung.BuchungBackend;
import de.jalin.fibu.server.buchung.BuchungData;
import de.jalin.fibu.server.buchung.BuchungException;

public class BuchungBackendImpl implements BuchungBackend {
	
	private BuchungDAO buchungDAO;

	public BuchungBackendImpl() throws BuchungException {
		try {
			buchungDAO = new BuchungDAO();
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public Vector executeBuchungListCall(Connection dbConnection,
			XmlRpcSession session, BuchungData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungException {
		try {
			return buchungDAO.listBuchungs(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public void executeBuchungAddCall(Connection dbConnection,
			XmlRpcSession session, BuchungData writeData)
			throws BuchungException {
		try {
			int buchId = buchungDAO.nextId(dbConnection);
			writeData.setBuchid(new Integer(buchId));
			writeData.setErfassung(new Date());
			buchungDAO.addBuchung(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public void executeBuchungUpdateCall(Connection dbConnection,
			XmlRpcSession session, BuchungData writeData, BuchungData whereData)
			throws BuchungException {
		try {
			buchungDAO.updateBuchung(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}

	public void executeBuchungDeleteCall(Connection dbConnection,
			XmlRpcSession session, BuchungData whereData)
			throws BuchungException {
		try {
			buchungDAO.deleteBuchung(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungException(10501, e);
		}
	}
}

/*
 *  $Log: BuchungBackendImpl.java,v $
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
