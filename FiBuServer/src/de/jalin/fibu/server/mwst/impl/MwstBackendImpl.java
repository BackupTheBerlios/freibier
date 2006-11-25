// $Id: MwstBackendImpl.java,v 1.4 2006/11/25 12:59:38 phormanns Exp $
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
package de.jalin.fibu.server.mwst.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.mwst.MwstBackend;
import de.jalin.fibu.server.mwst.MwstData;
import de.jalin.fibu.server.mwst.MwstException;

public class MwstBackendImpl implements MwstBackend {
	
	private MwstDAO mwstDAO;

	public MwstBackendImpl() throws MwstException {
		try {
			mwstDAO = new MwstDAO();
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}

	public Vector executeMwstListCall(Connection dbConnection,
			XmlRpcSession session, MwstData whereData, DisplayColumns display,
			OrderByList orderBy) throws MwstException {
		try {
			return mwstDAO.listMwsts(dbConnection, whereData, display, orderBy).getResult();
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}

	public void executeMwstAddCall(Connection dbConnection, XmlRpcSession session, MwstData writeData) throws MwstException {
		try {
			mwstDAO.addMwst(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}

	public void executeMwstUpdateCall(Connection dbConnection, XmlRpcSession session, MwstData writeData, MwstData whereData) throws MwstException {
		try {
			mwstDAO.updateMwst(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new MwstException(10201, e);
		}
	}
}

/*
 *  $Log: MwstBackendImpl.java,v $
 *  Revision 1.4  2006/11/25 12:59:38  phormanns
 *  ResultVector in QueryResult umbenannt
 *  Refactoring: DAOs liefern QueryResult bei Select
 *
 *  Revision 1.3  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.2  2006/02/23 17:08:28  phormanns
 *  neue Funktionen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
