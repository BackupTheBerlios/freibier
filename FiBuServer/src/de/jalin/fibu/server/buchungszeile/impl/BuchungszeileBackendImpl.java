// $Id: BuchungszeileBackendImpl.java,v 1.2 2006/02/24 22:27:40 phormanns Exp $
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
package de.jalin.fibu.server.buchungszeile.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungszeile.BuchungszeileBackend;
import de.jalin.fibu.server.buchungszeile.BuchungszeileData;
import de.jalin.fibu.server.buchungszeile.BuchungszeileException;

public class BuchungszeileBackendImpl implements BuchungszeileBackend {
	
	private BuchungszeileDAO buchungszeileDAO;

	public BuchungszeileBackendImpl() throws BuchungszeileException {
		try {
			buchungszeileDAO = new BuchungszeileDAO();
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public Vector executeBuchungszeileListCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungszeileException {
		try {
			return buchungszeileDAO.listBuchungszeiles(dbConnection, whereData, display, orderBy);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public void executeBuchungszeileAddCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData writeData)
			throws BuchungszeileException {
		try {
			int buzlid = buchungszeileDAO.nextId(dbConnection);
			writeData.setBuzlid(new Integer(buzlid));
			buchungszeileDAO.addBuchungszeile(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public void executeBuchungszeileUpdateCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData writeData,
			BuchungszeileData whereData) throws BuchungszeileException {
		try {
			buchungszeileDAO.updateBuchungszeile(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}

	public void executeBuchungszeileDeleteCall(Connection dbConnection,
			XmlRpcSession session, BuchungszeileData whereData)
			throws BuchungszeileException {
		try {
			buchungszeileDAO.deleteBuchungszeile(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungszeileException(10501, e);
		}
	}
}

/*
 *  $Log: BuchungszeileBackendImpl.java,v $
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
