// $Id: Main.java,v 1.5 2005/12/19 22:38:49 phormanns Exp $
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
