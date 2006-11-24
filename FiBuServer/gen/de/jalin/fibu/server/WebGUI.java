// Generiert mit xmlrpcgen

package de.jalin.fibu.server;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;

import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.standardModules.impl.*;
import net.hostsharing.admin.runtime.standardModules.modules.*;
import net.hostsharing.admin.runtime.standardModules.properties.*;
import net.hostsharing.admin.runtime.standardModules.functions.*;
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

public class WebGUI extends AbstractWebGUIServlet {

	private static final long serialVersionUID = 1164399842915L;

	public void init() throws ServletException {
		super.init();
		try {
			guiModules = new Hashtable();
			menu = new Vector();
			SystemWebGUI systemWebGUI = new SystemWebGUI();
			systemWebGUI.setAuthenticator(authenticator);
			guiModules.put("system", systemWebGUI);
			menu.addElement(systemWebGUI.getMenu());
			Map modulesMap = new HashMap();
			CustomerBackendImpl customerBackend = new CustomerBackendImpl();
			modulesMap.put("customer", new CustomerHandler(customerBackend));
			CustomerWebGUI customerWebGUI = new CustomerWebGUI(customerBackend);
			guiModules.put("customer", customerWebGUI);
			menu.addElement(customerWebGUI.getMenu());
			MwstBackendImpl mwstBackend = new MwstBackendImpl();
			modulesMap.put("mwst", new MwstHandler(mwstBackend));
			MwstWebGUI mwstWebGUI = new MwstWebGUI(mwstBackend);
			guiModules.put("mwst", mwstWebGUI);
			menu.addElement(mwstWebGUI.getMenu());
			KontoBackendImpl kontoBackend = new KontoBackendImpl();
			modulesMap.put("konto", new KontoHandler(kontoBackend));
			KontoWebGUI kontoWebGUI = new KontoWebGUI(kontoBackend);
			guiModules.put("konto", kontoWebGUI);
			menu.addElement(kontoWebGUI.getMenu());
			JournalBackendImpl journalBackend = new JournalBackendImpl();
			modulesMap.put("journal", new JournalHandler(journalBackend));
			JournalWebGUI journalWebGUI = new JournalWebGUI(journalBackend);
			guiModules.put("journal", journalWebGUI);
			menu.addElement(journalWebGUI.getMenu());
			BuchungBackendImpl buchungBackend = new BuchungBackendImpl();
			modulesMap.put("buchung", new BuchungHandler(buchungBackend));
			BuchungWebGUI buchungWebGUI = new BuchungWebGUI(buchungBackend);
			guiModules.put("buchung", buchungWebGUI);
			menu.addElement(buchungWebGUI.getMenu());
			BuchungszeileBackendImpl buchungszeileBackend = new BuchungszeileBackendImpl();
			modulesMap.put("buchungszeile", new BuchungszeileHandler(buchungszeileBackend));
			BuchungszeileWebGUI buchungszeileWebGUI = new BuchungszeileWebGUI(buchungszeileBackend);
			guiModules.put("buchungszeile", buchungszeileWebGUI);
			menu.addElement(buchungszeileWebGUI.getMenu());
			BuchungslisteBackendImpl buchungslisteBackend = new BuchungslisteBackendImpl();
			modulesMap.put("buchungsliste", new BuchungslisteHandler(buchungslisteBackend));
			BuchungslisteWebGUI buchungslisteWebGUI = new BuchungslisteWebGUI(buchungslisteBackend);
			guiModules.put("buchungsliste", buchungslisteWebGUI);
			menu.addElement(buchungslisteWebGUI.getMenu());
			BuchungsmaschineBackendImpl buchungsmaschineBackend = new BuchungsmaschineBackendImpl();
			modulesMap.put("buchungsmaschine", new BuchungsmaschineHandler(buchungsmaschineBackend));
			BuchungsmaschineWebGUI buchungsmaschineWebGUI = new BuchungsmaschineWebGUI(buchungsmaschineBackend);
			guiModules.put("buchungsmaschine", buchungsmaschineWebGUI);
			menu.addElement(buchungsmaschineWebGUI.getMenu());
			PropertiesBackendImpl propertiesBackend = new PropertiesBackendImpl();
			modulesMap.put("properties", new PropertiesHandler(propertiesBackend));
			PropertiesWebGUI propertiesWebGUI = new PropertiesWebGUI(propertiesBackend);
			guiModules.put("properties", propertiesWebGUI);
			menu.addElement(propertiesWebGUI.getMenu());
			FunctionsBackendImpl functionsBackend = new FunctionsBackendImpl();
			modulesMap.put("functions", new FunctionsHandler(functionsBackend));
			FunctionsWebGUI functionsWebGUI = new FunctionsWebGUI(functionsBackend);
			guiModules.put("functions", functionsWebGUI);
			menu.addElement(functionsWebGUI.getMenu());
			ModulesBackendImpl modulesBackend = new ModulesBackendImpl();
			modulesMap.put("modules", new ModulesHandler(modulesBackend));
			modulesBackend.setModules(modulesMap);
			ModulesWebGUI modulesWebGUI = new ModulesWebGUI(modulesBackend);
			guiModules.put("modules", modulesWebGUI);
			menu.addElement(modulesWebGUI.getMenu());
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
