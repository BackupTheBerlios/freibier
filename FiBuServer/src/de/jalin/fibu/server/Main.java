// $Id: Main.java,v 1.6 2006/02/24 22:27:40 phormanns Exp $
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
package de.jalin.fibu.server;

public class Main {
	
	public static void main(String[] args) {
		try {
			if (args.length == 1) {
				String opt = args[0];
				if ("server".equals(opt)) {
					Server.startServer();
				}
				if ("initdb.model".equals(opt)) {
					InitDB init = new InitDB();
					init.initModel();
					System.exit(0);
				}
				if ("initdb.data".equals(opt)) {
					InitDB init = new InitDB();
					init.initData();
					System.exit(0);
				}
			} else {
				System.out.println("Aufruf:");
				System.out.println("java de.jalin.fibu.Main server");
				System.out.println("java de.jalin.fibu.Main inidb.model");
				System.out.println("java de.jalin.fibu.Main inidb.data");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 *  $Log: Main.java,v $
 *  Revision 1.6  2006/02/24 22:27:40  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.5  2005/12/19 22:38:49  phormanns
 *  Init Data begonnen
 *
 *  Revision 1.4  2005/12/14 19:31:43  phormanns
 *  Start in Main-Klasse korrigiert
 *
 *  Revision 1.3  2005/12/12 21:10:27  phormanns
 *  Datenbank-Initialisierung begonnen
 *
 *  Revision 1.2  2005/12/12 21:09:05  phormanns
 *  Datenbank-Initialisierung begonnen
 *
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
