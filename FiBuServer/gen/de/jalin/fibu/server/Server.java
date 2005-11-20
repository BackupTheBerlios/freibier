package de.jalin.fibu.server;

import org.apache.xmlrpc.*;
import net.hostsharing.admin.runtime.*;
import de.jalin.fibu.server.customer.*;
import de.jalin.fibu.server.customer.impl.*;
import de.jalin.fibu.server.mwst.*;
import de.jalin.fibu.server.mwst.impl.*;
import de.jalin.fibu.server.konto.*;
import de.jalin.fibu.server.konto.impl.*;
import de.jalin.fibu.server.journal.*;
import de.jalin.fibu.server.journal.impl.*;
import de.jalin.fibu.server.buchung.*;
import de.jalin.fibu.server.buchung.impl.*;
import de.jalin.fibu.server.buchungszeile.*;
import de.jalin.fibu.server.buchungszeile.impl.*;
import de.jalin.fibu.server.buchungsliste.*;
import de.jalin.fibu.server.buchungsliste.impl.*;

public class Server extends XmlRpcTransactionHandler {

	public Server() throws Exception {
		super();
	}
	
	public static void startServer() throws Exception {
		XmlRpc.debug = true;
		WebServer server = new WebServer(7777);
		XmlRpcTransactionHandler rpcHandler = new XmlRpcTransactionHandler();
		rpcHandler.addModule("customer", 
			new CustomerHandler(new CustomerBackendImpl()));
		rpcHandler.addModule("mwst", 
			new MwstHandler(new MwstBackendImpl()));
		rpcHandler.addModule("konto", 
			new KontoHandler(new KontoBackendImpl()));
		rpcHandler.addModule("journal", 
			new JournalHandler(new JournalBackendImpl()));
		rpcHandler.addModule("buchung", 
			new BuchungHandler(new BuchungBackendImpl()));
		rpcHandler.addModule("buchungszeile", 
			new BuchungszeileHandler(new BuchungszeileBackendImpl()));
		rpcHandler.addModule("buchungsliste", 
			new BuchungslisteHandler(new BuchungslisteBackendImpl()));
		server.addHandler(MODUL_NAME, rpcHandler);
		server.start();
	}
}
