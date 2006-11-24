// $Id: MwstDAO.java,v 1.3 2006/11/24 21:10:03 phormanns Exp $
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
import net.hostsharing.admin.runtime.XmlRpcTransactionException;
import net.hostsharing.admin.runtime.sql.Sequence;

public class MwstDAO extends de.jalin.fibu.server.mwst.MwstDAO {
	
	private Sequence mwstidSEQ;

	public MwstDAO() throws XmlRpcTransactionException {
		super();
		mwstidSEQ = new Sequence(getTable(), "mwstid", 1);
	}

	public int nextId(Connection conn) throws XmlRpcTransactionException {
		return mwstidSEQ.nextValue(conn);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.createDatabaseObject(connect);
		mwstidSEQ.createDatabaseObject(connect);
	}

	public void dropDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		super.dropDatabaseObject(connect);
		mwstidSEQ.dropDatabaseObject(connect);
	}
	
}

/*
 *  $Log: MwstDAO.java,v $
 *  Revision 1.3  2006/11/24 21:10:03  phormanns
 *  Datenmodellerweiterung bei Konto und Buchungsliste
 *
 *  Revision 1.2  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
