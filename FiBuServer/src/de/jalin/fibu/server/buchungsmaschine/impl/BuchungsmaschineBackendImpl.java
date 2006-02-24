// $Id: BuchungsmaschineBackendImpl.java,v 1.2 2006/02/24 22:27:40 phormanns Exp $
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
package de.jalin.fibu.server.buchungsmaschine.impl;

import java.sql.Connection;
import net.hostsharing.admin.runtime.XmlRpcSession;
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineBackend;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineData;
import de.jalin.fibu.server.buchungsmaschine.BuchungsmaschineException;

public class BuchungsmaschineBackendImpl implements BuchungsmaschineBackend {
	
	private BuchungsmaschineDAO buchungsmaschineDAO;
	
	public BuchungsmaschineBackendImpl() throws BuchungsmaschineException {
		try {
			buchungsmaschineDAO = new BuchungsmaschineDAO();
		} catch (XmlRpcTransactionException e) {
			throw new BuchungsmaschineException(10801, e);
		}
	}

	public void executeBuchungsmaschineAddCall(Connection dbConnection,
			XmlRpcSession session, BuchungsmaschineData writeData)
			throws BuchungsmaschineException {
		try {
			buchungsmaschineDAO.addBuchungsmaschine(dbConnection, writeData);
		} catch (XmlRpcTransactionException e) {
			throw new BuchungsmaschineException(10801, e);
		}
	}
}

/*
 *  $Log: BuchungsmaschineBackendImpl.java,v $
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/24 17:41:18  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 */
