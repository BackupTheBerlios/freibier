package de.jalin.fibu.server;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import de.jalin.fibu.server.buchungsmaschine.*;
import de.jalin.fibu.server.buchungsmaschine.impl.*;

public class WebGUI extends HttpServlet {

	private static final long serialVersionUID = -1L;

	private Hashtable guiModules;
	private Vector menu;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getSession().setAttribute("menu", menu);
		String module = request.getParameter("module");
		String function = request.getParameter("function");
		AbstractWebGUI guiModule = (AbstractWebGUI) guiModules.get(module);
		guiModule.prepare(function, request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getSession().setAttribute("menu", menu);
		String module = request.getParameter("module");
		String function = request.getParameter("function");
		AbstractWebGUI guiModule = (AbstractWebGUI) guiModules.get(module);
		guiModule.execute(function, request, response);
	}
	
	public void init() throws ServletException {
		try {
		    guiModules = new Hashtable();
		    menu = new Vector();
			CustomerWebGUI customerWebGUI = new CustomerWebGUI(new CustomerBackendImpl());
			guiModules.put("customer", customerWebGUI);
			menu.addElement(customerWebGUI.getMenu());
			MwstWebGUI mwstWebGUI = new MwstWebGUI(new MwstBackendImpl());
			guiModules.put("mwst", mwstWebGUI);
			menu.addElement(mwstWebGUI.getMenu());
			KontoWebGUI kontoWebGUI = new KontoWebGUI(new KontoBackendImpl());
			guiModules.put("konto", kontoWebGUI);
			menu.addElement(kontoWebGUI.getMenu());
			JournalWebGUI journalWebGUI = new JournalWebGUI(new JournalBackendImpl());
			guiModules.put("journal", journalWebGUI);
			menu.addElement(journalWebGUI.getMenu());
			BuchungWebGUI buchungWebGUI = new BuchungWebGUI(new BuchungBackendImpl());
			guiModules.put("buchung", buchungWebGUI);
			menu.addElement(buchungWebGUI.getMenu());
			BuchungszeileWebGUI buchungszeileWebGUI = new BuchungszeileWebGUI(new BuchungszeileBackendImpl());
			guiModules.put("buchungszeile", buchungszeileWebGUI);
			menu.addElement(buchungszeileWebGUI.getMenu());
			BuchungslisteWebGUI buchungslisteWebGUI = new BuchungslisteWebGUI(new BuchungslisteBackendImpl());
			guiModules.put("buchungsliste", buchungslisteWebGUI);
			menu.addElement(buchungslisteWebGUI.getMenu());
			BuchungsmaschineWebGUI buchungsmaschineWebGUI = new BuchungsmaschineWebGUI(new BuchungsmaschineBackendImpl());
			guiModules.put("buchungsmaschine", buchungsmaschineWebGUI);
			menu.addElement(buchungsmaschineWebGUI.getMenu());
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
