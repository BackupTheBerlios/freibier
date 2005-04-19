/* Erzeugt am 21.02.2005 von tbayen
 * $Id: ServletDatabase.java,v 1.6 2005/04/19 17:17:04 tbayen Exp $
 */
package de.bayen.webframework;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import de.bayen.banking.ServletBanking;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.database.typedefinition.TypeDefinition;
import de.bayen.util.HttpMultipartRequest;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/**
 * Servlet für alle Datenbank-Webausgaben.
 * 
 * Dies ist das Basis-Servlet, das von allen konkreten Applikationen
 * abgeleitet werden sollte.
 * 
 * @author tbayen
 */
public abstract class ServletDatabase extends HttpServlet {
	static Logger logger = Logger.getLogger(ServletBanking.class.getName());
	private Properties props;
	private Configuration cfg;
	private URIParser uriParser;
	protected ActionDispatcher actionDispatcher;

	/**
	 * Hier wird das Servlet initialisiert.
	 * 
	 */
	public void init() {
		logger.debug("Servlet '" + this.getClass().getName()
				+ "' wird initialisiert.");
		cfg = new Configuration();
		TemplateLoader loaders[] = {
				new WebappTemplateLoader(getServletContext(),
						"WEB-INF/templates"),
				// mit folgender Zeile werden auch noch abgeleitete Klassen
				// durchsucht (TODO: aber keine mehrfach abgeleiteten).
				new ClassTemplateLoader(this.getClass(), "templates"),
				new ClassTemplateLoader(ServletDatabase.class, "templates")
		};
		cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
		// normalerweise gibts keine Euro-Zeichen, das stelle ich hier aber ein:
		cfg.setEncoding(new Locale("de","DE"),"ISO-8859-15");
		uriParser = new URIParserImpl();
		actionDispatcher = new ActionDispatcherClassLoader();
		readProperties();
	}

	/**
	 * Liest die Properties-Dateien mit den Grundeinstellungen des Programms.
	 * 
	 * Die Einstellungen werden aus mehreren Dateien zusammengetragen. Als
	 * erstes wird die Datei "config.properties" im webframework-Paket
	 * gelesen, die allgemeine Default-Werte enthält. Danach wird eine 
	 * gleichnamige Datei im Paket der abgeleiteten Applikations-Klasse
	 * gesucht, die Defaults für diese Applikation enthalten kann. Danach 
	 * wird eine Datei im Verzeichnis gesucht, dass in den bisherigen 
	 * Properties-Dateien durch den Schlüssel "configdir" angegeben ist 
	 * (vorgegeben ist "/etc/webdatabase/"). Die Datei hat den Namen des 
	 * letzten Teils des Paketnamens der Applikation, gefolgt von 
	 * ".properties", also z.B. für die Applikation im Paket 
	 * "de.bayen.kontaktdaten" den Namen "{configdir}/kontaktdaten.properties".
	 */
	private void readProperties() {
		// Properties einlesen
		props = new Properties();
		// zuerst die Properties der ServletDatabase-Klasse lesen:
		String path = ServletDatabase.class.getPackage().getName().replace('.',
				'/');
		InputStream stream = getClass().getClassLoader().getResourceAsStream(
				path + "/config.properties");
		try {
			if (stream != null)
				props.load(stream);
		} catch (IOException e) {}
		// dann die Properties der konkret abgeleiteten Klasse lesen:
		path = getClass().getPackage().getName().replace('.', '/');
		stream = getClass().getClassLoader().getResourceAsStream(
				path + "/config.properties");
		try {
			if (stream != null)
				props.load(stream);
		} catch (IOException e) {}
		// Dann die Properties im Config-Verzeichnis
		path = props.getProperty("configdir");
		if (path == null) {
			path = "";
			props.setProperty("configdir",path);
		} else if (!path.endsWith("/")){
			path += "/";
			props.setProperty("configdir",path);
		}
		String name = getClass().getPackage().getName();
		path += name.substring(name.lastIndexOf('.') + 1) + ".properties";
		try {
			stream = new FileInputStream(path);
			props.load(stream);
		} catch (IOException e) {}
	}

	/**
	 * Hier werden Properties ausgelesen, die irgendwo in den verteilten
	 * Proiperties-Dateien stehen können.
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * Dies ist die eigentliche Hauptfunktion, die vom Tomcat aufgerufen
	 * wird, wenn die zugehörige URL aufgerufen wird. (Was zugehörig ist,
	 * wird in web/WEB-INF/web.xml festgelegt.
	 * <p>
	 * Es wird ein root-Objekt erzeugt, dass Werte enthalten kann. Dann
	 * wird die sich aus der URL ergebende Action aufgerufen, die ggf. 
	 * Aktionen ausführt und weitere Daten in das root-Objekt schreibt.
	 * Dann wird das entsprechende Template aufgerufen.
	 * <p>
	 * Es gibt zwei besondere views:
	 * <ul><li>
	 *   binarydata - es werden Binärdaten direkt ausgegeben, nicht
	 *   per Template und nicht als MIME-Type HTML
	 * </li><li>
	 *   redirect-* - hinter dem Minus-Zeichen steht eine URL, auf die
	 *   ein redirect durchgeführt wird (nachdem die Action ausgeführt 
	 *   wurde). Dadurch können natürlich auch mehrere Actions 
	 *   hintereinander durch eine einzige URL ausgeführt werden.
	 * </li></ul>
	 * 
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.info("Servlet '" + this.getClass().getName() + "' - '"
				+ req.getRequestURI() + "'");
		Map root = new HashMap();
		Map uri = null;
		String theme = null;
		String table = null;
		try {
			// Die Datenbank wird in der Session gespeichert, damit sie nicht
			// bei jedem Request neu aufgemacht werden muss
			WebDBDatabase db = null;
			//db = (WebDBDatabase) req.getSession().getAttribute("de.bayen.database");
			if (db == null) {
				db = connectDatabase();
				req.getSession().setAttribute("de.bayen.database", db);
			}
			root.putAll(populateContextRoot(req, db));
			uri = (Map) root.get("uri");
			String action = (String) uri.get("action");
			actionDispatcher.executeAction(action, req, root, db, this);
			String view = (String) uri.get("view");
			theme = (String) uri.get("theme");
			table = (String) uri.get("table");
			if (view.startsWith("redirect-")) {
				// redirect
				resp.sendRedirect(view.substring(9));
			} else if (view.equals("binarydata")) {
				// Das ist ein besonderes View, für das keine 
				// Template-Engine gestartet wird
				executeBinaryData(root, view, theme, table, resp);
			} else {
				executeTemplate(root, view, theme, table, resp);
			}
		} catch (Exception e1) {
			// Falls eine Exception entsteht, die nicht abgefangen werden kann,
			// werden die Informationen über diese Exception jetzt in das 
			// model-root gepackt und dann das View "exception" aufgerufen.
			logger.error(e1.toString());
			if (root.get("uri") == null) {
				root.put("uri", uriParser.parseURI(req));
			}
			List exc = new ArrayList();
			root.put("exceptions", exc);
			Throwable thisexception = e1;
			while (thisexception != null) {
				Map ex = new HashMap();
				exc.add(ex);
				ex.put("class", thisexception.getClass());
				ex.put("message", thisexception.getMessage());
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				thisexception.printStackTrace(new PrintStream(stream));
				ex.put("stacktrace", stream.toString());
				thisexception = thisexception.getCause();
			}
			executeTemplate(root, "exception", theme, table, resp);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Bei POST könnte es sich um einen Multipart-Request handeln
		// (nötig bei File-Upload, aber auch möglich bei ganz normalen
		// Formularen). Dafür muss ich hier die Request-Klasse wrappen:
		doGet(new HttpMultipartRequest(req), resp);
	}

	protected Map populateContextRoot(HttpServletRequest req, WebDBDatabase db)
			throws SystemDatabaseException {
		Map uri = uriParser.parseURI(req);
		// context-root-hash erzeugen und mit einigen Grundwerten füllen
		Map root = new HashMap();
		root.put("uri", uri);
		root.put("databasename", db.getName());
		if (uri.get("table") == null)
			return root; // keine Tabellen vorhanden, also ist hier Schluß
		Table tab = db.getTable((String) uri.get("table"));
		root.put("primarykey", tab.getRecordDefinition().getPrimaryKey());
		List fields = new ArrayList();
		root.put("fields", fields);
		List felderdef = tab.getRecordDefinition().getFieldsList();
		for (int i = 0; i < felderdef.size(); i++) {
			// fields-Hash füllen
			String feldname = ((TypeDefinition) felderdef.get(i)).getName();
			if (!feldname.equals(tab.getRecordDefinition().getPrimaryKey())) {
				fields.add(feldname);
			}
		}
		// Daten, die erst nach dem Einlesen der Typdefinitionen festgestellt werden können
		String order = req.getParameter("order");
		if (order == null)
			order = (String) ((List) root.get("fields")).get(0);
		root.put("order", order);
		String orderdir = req.getParameter("orderdir");
		if (orderdir == null)
			orderdir = (String) "ASC";
		root.put("orderdir", orderdir);
		return root;
	}

	/**
	 * Der Template-Loader wird in init() so eingerichtet, daß zuerst ein 
	 * Verzeichnis im Web-Ordner der Applikation durchsucht wird, dann die 
	 * templates, die im Klassenverzeichnis der Applikation, dann die im 
	 * Klassenverzeichnis dieser Basisklasse hier liegen (als Default-Templates).
	 * 
	 * Eine Applikation kann diese so in Ihrem eigenen Package "überschreiben", 
	 * muss dies aber nicht tun. Ein Anwender kann diese in WEB-INF überschreiben.
	 *  
	 * Übrigens können Templates auch für einzelne Tabellen überschrieben
	 * werden, indem sie in ein Unterverzeichnis mit dem Tabellennamen
	 * gepackt werden.
	 */
	protected void executeTemplate(Map root, String view, String theme,
			String tabname, HttpServletResponse resp) throws IOException,
			ServletException {
		// Template finden, ausführen und ausgeben
		String pfad = theme;
		if (tabname != null) {
			pfad += "/" + tabname;
		}
		Template t = cfg.getTemplate(pfad + "/*/" + view + ".ftl");
		// Man kann mittels folgender Befehle das Problem auch sehr schnell
		// lösen. Allerdings kann man dann aus dem Template heraus nicht den
		// Content-Type ändern, und das halte ich für wichtig:
		//   resp.setContentType("text/html; charset=" + t.getEncoding());
		//   Writer out = resp.getWriter();
		//   t.process(root, out);
		//
		// Also führe ich das Template hier auf die etwas umständliche Art aus:
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(os);
			Environment env = t.createProcessingEnvironment(root, out);
			env.setOutputEncoding("ISO-8859-15");
			env.process();
			String contenttype = null;
			TemplateModel tm = env.getVariable("contenttype");
			if ((tm != null) && (tm instanceof TemplateScalarModel)) {
				contenttype = ((TemplateScalarModel) tm).getAsString();
			} else {
				contenttype = "text/html; charset=" + t.getEncoding();
			}
			resp.setContentType(contenttype);
			resp.getOutputStream().write(os.toByteArray());
		} catch (TemplateException e) {
			throw new ServletException("Fehler bei der Template-Bearbeitung: "
					+ e.getMessage(), e);
		}
	}

	protected void executeBinaryData(Map root, String view, String theme,
			String tabname, HttpServletResponse resp) throws IOException,
			ServletException {
		logger.debug("sende Binärdaten");
		resp.setContentType((String) root.get("contenttype"));
		resp.getOutputStream().write(
				((BLOB) root.get("binarydata")).toByteArray());
	}

	/**
	 * Diese Methode stellt der Applikation eine Datenbank zur Verfügung.
	 * Die Parameter liegen in *.property-Dateien vor.
	 * 
	 * Falls man die Datenbank ggf. automatisch einrichten will, kann man
	 * diese Methode entsprechend überladen.
	 */
	protected WebDBDatabase connectDatabase() throws DatabaseException {
		ResourceBundle resource = null;
		try {
			resource = ResourceBundle.getBundle(getClass().getPackage()
					.getName()
					+ "." + "database");
			WebDBDatabase db = new WebDBDatabase(resource.getString("name"),
					resource.getString("host"), resource.getString("user"),
					resource.getString("password"));
			db.setPropertyPath(getClass().getPackage().getName());
			return db;
		} catch (MissingResourceException e) {
			throw new UserDatabaseException(
					"Datenbank-Beschreibung in database.properties nicht vorhanden",
					e);
		}
	}
}
/*
 * $Log: ServletDatabase.java,v $
 * Revision 1.6  2005/04/19 17:17:04  tbayen
 * DTAUS-Dateien wieder einlesen in die Datenbank
 *
 * Revision 1.5  2005/04/18 13:11:53  tbayen
 * Sonderzeichen wie ":" im Verwendungszweck erlaubt
 *
 * Revision 1.4  2005/04/18 11:02:27  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.3  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.2  2005/04/06 21:14:10  tbayen
 * Anwenderprobleme behoben,
 * redirect-view implementiert
 * allgemeine Verbesserungen der Oberfläche
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:08  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.20  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 * Revision 1.19  2005/03/25 00:16:59  tbayen
 * Log4J konfiguriert und Logging eingerichtet
 * HBCI4Java eingebunden
 * erster Anfang der Banking-Applikation
 *
 * Revision 1.18  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.17  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 * Revision 1.16  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 * Revision 1.15  2005/03/19 12:55:04  tbayen
 * Zwischenmodell entfernt, stattdessen
 * direkter Zugriff auf Freibier-Klassen
 *
 * Revision 1.14  2005/02/28 11:18:28  tbayen
 * Layout verfeinert
 *
 * Revision 1.13  2005/02/28 01:28:48  tbayen
 * Import-Funktion eingebaut
 * Diese Version erstmalig auf dem Produktionsserver installiert
 *
 * Revision 1.12  2005/02/27 18:38:22  tbayen
 * Kunden-Formuar wie mit Günther besprochen
 *
 * Revision 1.11  2005/02/26 16:31:02  tbayen
 * versciedene Verbesserungen in den Templates
 *
 * Revision 1.10  2005/02/26 08:52:25  tbayen
 * Probleme mit Character Encoding behoben
 *
 * Revision 1.9  2005/02/25 21:44:07  tbayen
 * Löschen von Datensätzen
 * Formulare per HTTP-POST-Methode
 *
 * Revision 1.8  2005/02/24 21:20:28  tbayen
 * Umlaute können benutzt werden
 *
 * Revision 1.7  2005/02/24 15:47:43  tbayen
 * Probleme mit der Neuanlage bei ForeignKeys behoben
 *
 * Revision 1.6  2005/02/24 13:24:59  tbayen
 * Referenzen und Listen funktionieren jetzt!
 *
 * Revision 1.5  2005/02/24 12:15:46  tbayen
 * Anzeigen von Referenzen (Editieren noch nicht)
 *
 * Revision 1.4  2005/02/24 11:48:33  tbayen
 * automatische Aktivierung des ersten Eingabefeldes
 *
 * Revision 1.3  2005/02/24 00:35:28  tbayen
 * Listen funktionieren, Datensätze anlegen funktioniert
 *
 * Revision 1.2  2005/02/23 12:26:04  tbayen
 * sortieren von Listen geht jetzt
 *
 * Revision 1.1  2005/02/23 11:40:58  tbayen
 * recht taugliche Version mit Authentifizierung und
 * Trennung von allgem. und applik.-spezifischen Dingen
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 */