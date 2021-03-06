/* Erzeugt am 21.02.2005 von tbayen
 * $Id: ServletDatabase.java,v 1.15 2007/11/05 13:47:31 tbayen Exp $
 */
package de.bayen.webframework;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import de.bayen.database.Database;
import de.bayen.database.Table;
import de.bayen.database.exception.DBRuntimeException;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx;
import de.bayen.database.exception.UserDBEx;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.database.typedefinition.TypeDefinition;
import de.bayen.util.FreemarkerClassTemplateLoader;
import de.bayen.util.HttpMultipartRequest;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/**
 * Servlet f�r alle Datenbank-Webausgaben.
 * 
 * Dies ist das Basis-Servlet, das von allen konkreten Applikationen
 * abgeleitet werden sollte.
 * 
 * <h1>Allgemeines zu Datenbanken:</h1>
 * <p>
 * Jede Webapplikation hat automatischen Zugriff auf genau eine Datenbank. Die Logik hierzu
 * ist in der Klasse webframework.ServletDatabase verankert. Es wird zuerst die Datei
 * config.properties im webframework-Paket gelesen. Diese enth�lt die absoluten Default-
 * Einstellungen (Username und Passwort sind "freibierweb"). Ist dort kein Datenbankname
 * angegeben, wird der Paketname der Applikation als Datenbankname verwendet. Dann wird im 
 * Paket der Applikation eine config.properties gelesen, falls dort andere Voreinstellungen 
 * stehen. So kann eine einzelne Applikation z.B. den Default-Datenbanknamen oder den 
 * Usernamen speziell anpassen.
 * </p><p>
 * Dann werden die eigentlichen Einstellungen aus dem Config-Dir "/etc/freibierweb/" 
 * gelesen. Dort wird zuerst die Datei "config.properties" und dann die Datei
 * "appname.properties" gelesen. Hier kann der Benutzer konkrete Parameter zum Zugriff
 * auf die Datenbank angeben.
 * </p>
 * @author tbayen
 */
public abstract class ServletDatabase extends HttpServlet {
	static Logger logger = Logger.getLogger(ServletDatabase.class.getName());
	private Properties props;
	private Configuration cfg;
	private URIParser uriParser;
	protected ActionDispatcher actionDispatcher;
	protected Database database;

	/**
	 * Hier wird das Servlet initialisiert.
	 * 
	 */
	public void init() {
		logger.debug("Servlet '" + this.getClass().getName()
				+ "' wird initialisiert.");
		cfg = new Configuration();
		TemplateLoader loaders[] = {
				//new WebappTemplateLoader(getServletContext(), "WEB-INF/templates"),
				// mit folgender Zeile werden auch noch abgeleitete Klassen
				// durchsucht (TO DO: aber keine mehrfach abgeleiteten).
				new FreemarkerClassTemplateLoader(this.getClass(), "templates"),
				new FreemarkerClassTemplateLoader(ServletDatabase.class,
						"templates") };
		cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
		// normalerweise gibts keine Euro-Zeichen, das stelle ich hier aber ein:
		cfg.setEncoding(new Locale("de", "DE"), "ISO-8859-15");
		uriParser = new URIParserImpl();
		actionDispatcher = new ActionDispatcherClassLoader(this.getClass());
		readProperties();
		try {
			connectDatabase();
		} catch (DatabaseException e) {
			logger.error("Kann die Datenbank nicht �ffnen.", e);
			throw new RuntimeException("Kann die Datenbank nicht �ffnen.");
		}
	}

	/**
	 * Aufr�umen der Ressourcen, die ich in init() belegt habe
	 */
	public void destroy() {
		try {
			database.close();
		} catch (SQL_DBException e) {}
		super.destroy();
	}

	/**
	 * Liest die Properties-Dateien mit den Grundeinstellungen des Programms.
	 * 
	 * Die Einstellungen werden aus mehreren Dateien zusammengetragen. Als
	 * erstes wird die Datei "config.properties" im webframework-Paket
	 * gelesen, die allgemeine Default-Werte enth�lt. Danach wird eine 
	 * gleichnamige Datei im Paket der abgeleiteten Applikations-Klasse
	 * gesucht, die Defaults f�r diese Applikation enthalten kann. Danach 
	 * werden Dateien im Verzeichnis gesucht, dass in den bisherigen 
	 * Properties-Dateien durch den Schl�ssel "configdir" angegeben ist 
	 * (vorgegeben ist "/etc/freibierweb/"). Die erste Datei ist 
	 * "config.properties", die zweite hat den Namen des letzten Teils des 
	 * Paketnamens der Applikation, gefolgt von ".properties", also z.B. 
	 * f�r die Applikation im Paket "de.bayen.kontaktdaten" den Namen 
	 * "{configdir}/kontaktdaten.properties".
	 */
	private void readProperties() {
		String appname = getClass().getPackage().getName();
		appname = appname.substring(appname.lastIndexOf('.') + 1);
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
		// Falls kein Datenbankname, diesen auf den Paketnamen der Applikation setzen
		if (!props.containsKey("database.name")) {
			props.setProperty("database.name", appname);
		}
		if (!props.containsKey("database.datasource")) {
			props.setProperty("database.datasource", appname);
		}
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
			props.setProperty("configdir", path);
		} else if (!path.endsWith("/")) {
			path += "/";
			props.setProperty("configdir", path);
		}
		try {
			stream = new FileInputStream(path + "config.properties");
			props.load(stream);
		} catch (IOException e) {}
		try {
			stream = new FileInputStream(path + appname + ".properties");
			props.load(stream);
		} catch (IOException e) {}
	}

	/**
	 * Hier werden Properties ausgelesen, die irgendwo in den verteilten
	 * Proiperties-Dateien stehen k�nnen.
	 * 
	 * @param key
	 * @return Property-value
	 */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * Dies ist die eigentliche Hauptfunktion, die vom Tomcat aufgerufen
	 * wird, wenn die zugeh�rige URL aufgerufen wird. (Was zugeh�rig ist,
	 * wird in web/WEB-INF/web.xml festgelegt.
	 * <p>
	 * Es wird ein root-Objekt erzeugt, dass Werte enthalten kann. Dann
	 * wird die sich aus der URL ergebende Action aufgerufen, die ggf. 
	 * Aktionen ausf�hrt und weitere Daten in das root-Objekt schreibt.
	 * Dann wird das entsprechende Template aufgerufen.
	 * <p>
	 * Es gibt zwei besondere views:
	 * <ul><li>
	 *   binarydata - es werden Bin�rdaten direkt ausgegeben, nicht
	 *   per Template und nicht als MIME-Type HTML
	 * </li><li>
	 *   redirect-* - hinter dem Minus-Zeichen steht eine URL, auf die
	 *   ein redirect durchgef�hrt wird (nachdem die Action ausgef�hrt 
	 *   wurde). Dadurch k�nnen nat�rlich auch mehrere Actions 
	 *   hintereinander durch eine einzige URL ausgef�hrt werden.
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
			//			// Die Datenbank wird in der Session gespeichert, damit sie nicht
			//			// bei jedem Request neu aufgemacht werden muss
			//			WebDBDatabase db = null;
			//			//db = (WebDBDatabase) req.getSession().getAttribute("de.bayen.database");
			//			if (db == null) {
			//				db = connectDatabase();
			//				req.getSession().setAttribute("de.bayen.database", db);
			//			}
			root.putAll(populateContextRoot(req, database));
			uri = (Map) root.get("uri");
			String action = (String) uri.get("action");
			actionDispatcher.executeAction(action, req, root, database, this);
			String view = (String) uri.get("view");
			theme = (String) uri.get("theme");
			table = (String) uri.get("table");
			if (view.startsWith("redirect-")) {
				// redirect
				resp.sendRedirect(view.substring(9));
			} else if (view.equals("binarydata")) {
				// Das ist ein besonderes View, f�r das keine 
				// Template-Engine gestartet wird
				executeBinaryData(root, view, theme, table, resp);
			} else {
				executeTemplate(root, view, theme, table, resp);
			}
		} catch (Exception e1) {
			// Falls eine Exception entsteht, die nicht abgefangen werden kann,
			// werden die Informationen �ber diese Exception jetzt in das 
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
		// Bei POST k�nnte es sich um einen Multipart-Request handeln
		// (n�tig bei File-Upload, aber auch m�glich bei ganz normalen
		// Formularen). Daf�r muss ich hier die Request-Klasse wrappen:
		doGet(new HttpMultipartRequest(req), resp);
	}

	protected Map populateContextRoot(HttpServletRequest req, Database db)
			throws SysDBEx {
		Map uri = uriParser.parseURI(req);
		// context-root-hash erzeugen und mit einigen Grundwerten f�llen
		Map root = new HashMap();
		root.put("uri", uri);
		root.put("databasename", db.getName());
		if (uri.get("table") == null)
			return root; // keine Tabellen vorhanden, also ist hier Schlu�
		Table tab = db.getTable((String) uri.get("table"));
		root.put("primarykey", tab.getRecordDefinition().getPrimaryKey());
		List fields = new ArrayList();
		root.put("fields", fields);
		List felderdef = tab.getRecordDefinition().getFieldsList();
		for (int i = 0; i < felderdef.size(); i++) {
			// fields-Hash f�llen
			String feldname = ((TypeDefinition) felderdef.get(i)).getName();
			if (!feldname.equals(tab.getRecordDefinition().getPrimaryKey())) {
				fields.add(feldname);
			}
		}
		// Daten, die erst nach dem Einlesen der Typdefinitionen festgestellt werden k�nnen
		String order = req.getParameter("order");
		if (order == null)
			order = (String) ((List) root.get("fields")).get(0);
		root.put("order", order);
		String orderdir = req.getParameter("orderdir");
		if (orderdir == null)
			orderdir = "ASC";
		root.put("orderdir", orderdir);
		return root;
	}

	/**
	 * Der Template-Loader wird in init() so eingerichtet, da� zuerst ein 
	 * Verzeichnis im Web-Ordner der Applikation durchsucht wird, dann die 
	 * templates, die im Klassenverzeichnis der Applikation, dann die im 
	 * Klassenverzeichnis dieser Basisklasse hier liegen (als Default-Templates).
	 * 
	 * Eine Applikation kann diese so in Ihrem eigenen Package "�berschreiben", 
	 * muss dies aber nicht tun. Ein Anwender kann diese in WEB-INF �berschreiben.
	 *  
	 * �brigens k�nnen Templates auch f�r einzelne Tabellen �berschrieben
	 * werden, indem sie in ein Unterverzeichnis mit dem Tabellennamen
	 * gepackt werden.
	 */
	protected void executeTemplate(Map root, String view, String theme,
			String tabname, HttpServletResponse resp) throws IOException,
			ServletException {
		// Template finden, ausf�hren und ausgeben
		String pfad = theme;
		if (tabname != null) {
			pfad += "/" + tabname;
		}
		cfg.setDefaultEncoding("ISO-8859-15");  // Encoding der Template-Quelltextdateien
		Template t = cfg.getTemplate(pfad + "/*/" + view + ".ftl");
		// Man kann mittels folgender Befehle das Problem auch sehr schnell
		// l�sen. Allerdings kann man dann aus dem Template heraus nicht den
		// Content-Type �ndern, und das halte ich f�r wichtig:
		//   resp.setContentType("text/html; charset=" + t.getEncoding());
		//   Writer out = resp.getWriter();
		//   t.process(root, out);
		//
		// Also f�hre ich das Template hier auf die etwas umst�ndliche Art aus:
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(os, "ISO-8859-15");  // Encoding der Ausgabe
			Environment env = t.createProcessingEnvironment(root, out);
			env.setOutputEncoding("ISO-8859-15");  // Encoding der Ausgabe
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
		logger.debug("sende Bin�rdaten");
		resp.setContentType((String) root.get("contenttype"));
		Object output = root.get("binarydata");
		if (output.getClass().equals(BLOB.class)) {
			resp.getOutputStream().write(((BLOB) output).toByteArray());
		} else if (output.getClass().equals(byte[].class)) {
			resp.getOutputStream().write((byte[]) output);
		} else {
			throw new RuntimeException("ung�ltiger Bin�rdatentyp");
		}
	}

	/**
	 * Diese Methode stellt der Applikation eine Datenbank zur Verf�gung.
	 * Die Parameter liegen in *.property-Dateien vor.
	 * 
	 * Falls man die Datenbank ggf. automatisch einrichten will, kann man
	 * diese Methode entsprechend �berladen.
	 */
	protected void connectDatabase() throws DatabaseException {
		try {
			String datasource = getProperty("database.datasource");
			try {
				Context initCtx = new InitialContext();
				DataSource ds = (DataSource) initCtx
						.lookup("java:comp/env/jdbc/" + datasource);
				database = new Database(ds, getProperty("database.name"));
			} catch (Exception e) {
				// Wenns keine DataSource gibt, nehme ich den herk�mmlichen Weg
				database = new Database(getProperty("database.name"),
						getProperty("database.host"),
						getProperty("database.user"),
						getProperty("database.password"));
			}
			database.setPropertyPath(getClass().getPackage().getName());
			// falls die Datenbank noch nicht existiert, initialisiere ich sie
			if (database.getTableNamesList().size() == 0) {
				try {
					String path = getClass().getPackage().getName().replace(
							'.', '/');
					database.executeSqlFile(path + "/db_definition.sql");
				} catch (IOException e) {
					throw new DBRuntimeException(
							"Kann Definitionsdatei nicht lesen", e);
				}
			}
		} catch (MissingResourceException e) {
			throw new UserDBEx(
					"Datenbank-Beschreibung in database.properties nicht vorhanden",
					e);
		}
	}
}
/*
 * $Log: ServletDatabase.java,v $
 * Revision 1.15  2007/11/05 13:47:31  tbayen
 * Encoding der Templates fest angeben
 *
 * Revision 1.14  2007/11/04 15:51:39  tbayen
 * Anpassung an moderneres System (MySQL 5.1, Tomcat 5.5, System mit UTF-8)
 *
 * Revision 1.13  2006/01/29 00:41:52  tbayen
 * Datenbankverbindung per DataSource m�glich
 *
 * Revision 1.12  2006/01/22 19:44:24  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unn�tig, wurde gel�scht
 *
 * Revision 1.11  2006/01/21 23:10:09  tbayen
 * Komplette �berarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
 *
 * Revision 1.10  2005/11/25 08:59:52  tbayen
 * kleinere Verbesserungen und Fehlerabfragen
 *
 * Revision 1.9  2005/08/12 21:00:16  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.8  2005/08/12 20:59:02  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.7  2005/08/07 16:56:14  tbayen
 * Produktionsversion 1.5
 *
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
 * allgemeine Verbesserungen der Oberfl�che
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:08  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.20  2005/03/28 03:09:45  tbayen
 * Bin�rdaten (BLOBS) in der Datenbank und im Webinterface
 *
 * Revision 1.19  2005/03/25 00:16:59  tbayen
 * Log4J konfiguriert und Logging eingerichtet
 * HBCI4Java eingebunden
 * erster Anfang der Banking-Applikation
 *
 * Revision 1.18  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.17  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 * Revision 1.16  2005/03/21 02:06:16  tbayen
 * Komplette �berarbeitung des Web-Frameworks
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
 * Kunden-Formuar wie mit G�nther besprochen
 *
 * Revision 1.11  2005/02/26 16:31:02  tbayen
 * versciedene Verbesserungen in den Templates
 *
 * Revision 1.10  2005/02/26 08:52:25  tbayen
 * Probleme mit Character Encoding behoben
 *
 * Revision 1.9  2005/02/25 21:44:07  tbayen
 * L�schen von Datens�tzen
 * Formulare per HTTP-POST-Methode
 *
 * Revision 1.8  2005/02/24 21:20:28  tbayen
 * Umlaute k�nnen benutzt werden
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
 * Listen funktionieren, Datens�tze anlegen funktioniert
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