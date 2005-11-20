// $Id: Main.java,v 1.1 2005/11/20 21:27:44 phormanns Exp $
package de.jalin.fibu.server;

public class Main {
	
	public static void main(String[] args) {
		try {
			Server.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 *  $Log: Main.java,v $
 *  Revision 1.1  2005/11/20 21:27:44  phormanns
 *  Import
 *
 */
