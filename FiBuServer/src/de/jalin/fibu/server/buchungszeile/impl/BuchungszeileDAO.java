// $Id: BuchungszeileDAO.java,v 1.2 2006/02/24 22:27:40 phormanns Exp $
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
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class BuchungszeileDAO extends
		de.jalin.fibu.server.buchungszeile.BuchungszeileDAO {
	
	private Sequence buzlidSEQ;

	public BuchungszeileDAO() throws XmlRpcTransactionException {
		super();
		buzlidSEQ = new Sequence(getTable(), "buzlid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return buzlidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		buzlidSEQ.createDatabaseObject(connect);
	}
}

/*
 *  $Log: BuchungszeileDAO.java,v $
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
