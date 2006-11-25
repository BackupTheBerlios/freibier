// $Id: KontoBackendImpl.java,v 1.3 2006/11/25 12:59:38 phormanns Exp $
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
package de.jalin.fibu.server.konto.impl;

import java.sql.Connection;
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.konto.KontoBackend;
import de.jalin.fibu.server.konto.KontoData;
import de.jalin.fibu.server.konto.KontoException;

public class KontoBackendImpl implements KontoBackend {
	
	private KontoDAO kontoDAO;

	public KontoBackendImpl() throws KontoException {
		try {
			kontoDAO = new KontoDAO();
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public Vector executeKontoListCall(Connection dbConnection,
			XmlRpcSession session, KontoData whereData, DisplayColumns display,
			OrderByList orderBy) throws KontoException {
		try {
			return kontoDAO.listKontos(dbConnection, whereData, display, orderBy).getResult();
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public void executeKontoAddCall(Connection dbConnection,
			XmlRpcSession session, KontoData writeData) throws KontoException {
		try {
			kontoDAO.addKonto(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public void executeKontoUpdateCall(Connection dbConnection,
			XmlRpcSession session, KontoData writeData, KontoData whereData)
			throws KontoException {
		try {
			kontoDAO.updateKonto(dbConnection, writeData, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}

	public void executeKontoDeleteCall(Connection dbConnection,
			XmlRpcSession session, KontoData whereData) throws KontoException {
		try {
			kontoDAO.deleteKonto(dbConnection, whereData);
		} catch (XmlRpcTransactionException e) {
			throw new KontoException(10301, e);
		}
	}
}

/*
 *  $Log: KontoBackendImpl.java,v $
 *  Revision 1.3  2006/11/25 12:59:38  phormanns
 *  ResultVector in QueryResult umbenannt
 *  Refactoring: DAOs liefern QueryResult bei Select
 *
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
