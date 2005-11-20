package de.jalin.fibu.server;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.xmlrpc.XmlRpcServer;
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

public class Servlet extends HttpServlet {

	private static final long serialVersionUID = -6241033859600678799L;

	private XmlRpcServer xmlrpc;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		OutputStream out = response.getOutputStream();
		String msg = "<html><body>Methode GET ist nicht implementiert.</body></html>";
		out.write(msg.getBytes());
		out.flush();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		byte[] result = xmlrpc.execute(request.getInputStream());
		response.setContentType("text/xml");
		response.setContentLength(result.length);
		OutputStream out = response.getOutputStream();
		out.write(result);
		out.flush();
	}
	
	public void init() throws ServletException {
		try {
			xmlrpc = new XmlRpcServer();
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
			xmlrpc.addHandler(XmlRpcTransactionHandler.MODUL_NAME, rpcHandler);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
