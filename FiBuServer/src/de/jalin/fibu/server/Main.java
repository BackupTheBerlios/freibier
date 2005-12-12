// $Id: Main.java,v 1.3 2005/12/12 21:10:27 phormanns Exp $
package de.jalin.fibu.server;

public class Main {
	
	public static void main(String[] args) {
		try {
			if (args.length == 1) {
				String opt = args[0];
				if ("server".equals(opt)) {
					Server.startServer();
					System.exit(0);
				}
				if ("initdb.model".equals(opt)) {
					InitDB init = new InitDB();
					init.initModel();
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 *  $Log: Main.java,v $
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
