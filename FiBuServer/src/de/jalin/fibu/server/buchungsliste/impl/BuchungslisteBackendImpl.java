// $Id: BuchungslisteBackendImpl.java,v 1.4 2006/11/25 12:59:38 phormanns Exp $
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
import java.util.Vector;
import net.hostsharing.admin.runtime.DisplayColumns;
import net.hostsharing.admin.runtime.OrderByList;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungsliste.BuchungslisteBackend;
import de.jalin.fibu.server.buchungsliste.BuchungslisteData;
import de.jalin.fibu.server.buchungsliste.BuchungslisteException;

public class BuchungslisteBackendImpl implements BuchungslisteBackend {
	
	private BuchungslisteDAO buchungslisteDAO;

	public BuchungslisteBackendImpl() throws BuchungslisteException {
		try {
			buchungslisteDAO = new BuchungslisteDAO();
		} catch (XmlRpcTransactionException e) {
			throw new BuchungslisteException(10701, e);
		}
	}

	public Vector executeBuchungslisteListCall(Connection dbConnection,
			XmlRpcSession session, BuchungslisteData whereData,
			DisplayColumns display, OrderByList orderBy)
			throws BuchungslisteException {
		try {
			return buchungslisteDAO.listBuchungslistes(dbConnection, whereData, display, orderBy).getResult();
		} catch (XmlRpcTransactionException e) {
			throw new BuchungslisteException(10701, e);
		}
	}

}

/*
 *  $Log: BuchungslisteBackendImpl.java,v $
 *  Revision 1.4  2006/11/25 12:59:38  phormanns
 *  ResultVector in QueryResult umbenannt
 *  Refactoring: DAOs liefern QueryResult bei Select
 *
 *  Revision 1.3  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.2  2005/11/22 21:32:00  phormanns
 *  Modul Buchungsliste (noch nicht genutzt)
 *
 *  Revision 1.1  2005/11/22 21:26:08  phormanns
 *  Modul Buchungsliste (noch nicht genutzt)
 *
 *  Revision 1.1  2005/11/20 21:27:43  phormanns
 *  Import
 *
 */
