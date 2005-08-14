/* Erzeugt am 01.10.2004 von tbayen
 * $Id: Database.java,v 1.9 2005/08/14 20:06:21 tbayen Exp $
 */
package de.bayen.database;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;
import de.bayen.database.typedefinition.TypeDefinition;

/**
 * @author tbayen
 * 
 * Ein Objekt dieser Klasse ist die Basis für alle Datenzugriffe. Es
 * repräsentiert z.B. eine MySQL-Datenbank. Aus diesem Objekt können dann
 * Tabellen (class Table) extrahiert werden, in denen die eigentlichen Daten
 * stehen.
 */
public class Database {
	private static Log log = LogFactory.getLog(Database.class);
	private Connection conn;
	private String name, server, user, password;
	private String propertyPath = "";

	/**
	 * Konstruktor
	 */
	public Database(String name, String server, String user, String password)
			throws DatabaseException {
		super();
		log.trace("Konstruktor");
		this.name = name;
		this.server = server;
		this.user = user;
		this.password = password;
		openConnection();
	}

	public void setPropertyPath(String path) {
		this.propertyPath = path;
		if (propertyPath.charAt(propertyPath.length() - 1) != '.') {
			this.propertyPath += ".";
		}
	}

	private void openConnection() throws DatabaseException {
		log.trace("openConnection");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			/*
			 * Ist der JDBC-Treiber nicht, installiert, kann man dies z.B. durch
			 * das Debian-Paket "libmysql-java" nachholen. Dann muss die
			 * Mysql-JDBC-Bibliothek in den Pfad für Bibliotheken eingebunden
			 * werden. Dies geht bei Eclipse z.B. unter dem Menüpunkt
			 * Project/Properties auf der Karte Java Build Path/Libraries.
			 */
			throw new SystemDatabaseException(
					"JDBC-Treiber für MySQL kann nicht geladen werden", e, log);
		};
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + server + "/"
					+ name, user, password);
		} catch (SQLException e2) {
			throw new UserDatabaseException("Verbindung zur SQL-Datenbank '"
					+ name + "' kann nicht geöffnet werden", e2);
		}
	}

	public boolean ok() {
		log.trace("ok");
		return (conn != null);
	}

	public String getName() {
		return name;
	}

	/**
	 * Ergibt eine Liste von Strings, die die Namen der Tabellen enthält, die
	 * in der Datenbank angelegt sind.
	 * 
	 * @return List, die Strings enthält
	 * @throws SystemDatabaseException
	 */
	public List getTableNamesList() throws SystemDatabaseException {
		List liste = new ArrayList();
		try {
			ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);
			while (rs.next()) {
				liste.add(rs.getString("TABLE_NAME"));
			}
			rs.close();
		} catch (SQLException e) {
			throw new SystemDatabaseException("Kann Tabellenliste nicht lesen",
					e, log);
		}
		return liste;
	}

	/**
	 * Besorgt ein Tabellenobjekt aus der Datenbank. Über dieses kann dann 
	 * auf die eigentlichen Daten zugegriffen werden, die als Zeilen in der
	 * Tabelle stehen.
	 *  
	 * @param name
	 * @return
	 * @throws SystemDatabaseException
	 */
	public Table getTable(String name) throws SystemDatabaseException {
		try {
			ResultSet columns = conn.getMetaData().getColumns(null, null, name,
					"%");
			RecordDefinition def = new RecordDefinition();
			ResourceBundle resource = null;
			try {
				log.debug("Suche Property File: " + propertyPath + name);
				resource = ResourceBundle.getBundle(propertyPath + name);
				//log.debug("Resource: "+resource);
			} catch (MissingResourceException e) {}
			while (columns.next()) {
				TypeDefinition typ = TypeDefinition.create(columns
						.getString("COLUMN_NAME"), columns.getInt("DATA_TYPE"),
						columns.getInt("COLUMN_SIZE"), resource, this);
				def.addColumn(typ);
			}
			columns.close();
			ResultSet primarykeys = conn.getMetaData().getPrimaryKeys(null,
					null, name);
			if (primarykeys.next()) {
				def.setPrimaryKey(primarykeys.getString("COLUMN_NAME"));
				if (primarykeys.next()) {
					throw new SystemDatabaseException(
							"Mehrere Primärschlüsselspalten sind nicht erlaubt",
							log);
				}
			} else {
				throw new SystemDatabaseException(
						"Keine Primärschlüsselspalte definiert", log);
			}
			primarykeys.close();
			// Zugriff auf Informationen über Unterlisten
			if (resource != null) {
				try {
					String liste = resource.getString("_lists");
					String listennamen[] = liste.split("\\s*,\\s*");
					if (listennamen.length > 0) {
						List lists = new ArrayList();
						for (int i = 0; i < listennamen.length; i++) {
							Map listendaten = new HashMap();
							lists.add(listendaten);
							listendaten.put("name", listennamen[i]);
							listendaten.put("indexcolumn", resource
									.getString(listennamen[i]
											+ ".list.indexcolumn"));
							listendaten.put("table", resource
									.getString(listennamen[i] + ".list.table"));
						}
						def.setSublists(lists);
					}
				} catch (MissingResourceException e) {}
			}
			return new Table(this, name, def);
		} catch (SQLException e) {
			throw new SystemDatabaseException(
					"Tabelle kann nicht angesprochen werden: '" + name + "'",
					e, log);
		}
	}

	/**
	 * löscht eine gesamte Tabelle (Inhalt und Struktur) aus der Datenbank.
	 * 
	 * @throws UserDatabaseException
	 *
	 */
	public void dropTable(String name) throws UserDatabaseException {
		try {
			executeSql("DROP TABLE `" + name + "`;");
		} catch (DatabaseException e) {
			throw new UserDatabaseException(
					"Tabelle kann nicht gelöscht werden", e, log);
		}
	}

	/**
	 * Löscht die gesamte Datenbank mit allen Tabellen. (GEFÄHRLICH!)
	 * @throws SystemDatabaseException 
	 * @throws DatabaseException 
	 */
	public void wipeOutDatabase() throws DatabaseException {
		List tables = getTableNamesList();
		for (Iterator iter = tables.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			dropTable(element);
		}
	}

	/**
	 * Führt einen String als SQL-Befehl (oder mehrere Befehle) aus.
	 * 
	 * @param sqltext
	 * @throws SystemDatabaseException
	 */
	public void executeSql(String sqltext) throws SystemDatabaseException {
		executeSqlFile(new ByteArrayInputStream(sqltext.getBytes()));
	}

	public void executeSqlFile(String filename) throws SystemDatabaseException {
		InputStream datei = this.getClass().getClassLoader()
				.getResourceAsStream(filename);
		if (datei == null)
			throw new SystemDatabaseException("Kann Datei nicht lesen: "
					+ filename, log);
		executeSqlFile(datei);
	}

	public void executeSqlFile(InputStream datei)
			throws SystemDatabaseException {
		log.trace("executeSqlFile");
		String sql = "";
		String zeile;
		try {
			Statement st = conn.createStatement();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					datei));
			zeile = buffer.readLine();
			while (zeile != null) {
				if (zeile.length() > 0 && zeile.charAt(0) != '#'
						&& !zeile.startsWith("--")) { // Kommentarzeilen weglassen
					sql += zeile;
					Perl5Util regex = new Perl5Util();
					// Langer RegEx kurzer Sinn: Haben wir ein Semikolon?
					if (regex.match("/^(.+)\\s*;(\\s|\\n)*(?:#.*)?$/", sql)) {
						st.addBatch(regex.group(1)); // Inhalt der ersten Klammer
						//log.debug("SQL: " + sql);
						sql = "";
					}
				}
				zeile = buffer.readLine();
			};
			st.executeBatch();
			st.close();
		} catch (IOException e) {
			throw new SystemDatabaseException(
					"SQL-Datei kann nicht gelesen werden", e, log);
		} catch (SQLException e) {
			throw new SystemDatabaseException("SQL-Exception", e, log);
		}
		if (!sql.equals("")) {
			throw new SystemDatabaseException(
					"Syntax-Fehler in der SQL-Datei (fehlendes Semikolon?):"
							+ zeile, log);
		}
	}

	public Map executeSelectSingleRow(String sql) throws DatabaseException {
		log.trace("executeSelectSingleRow");
		Map hash = new HashMap();
		try {
			Statement st = conn.createStatement();
			log.debug("SQL SELECT: " + sql);
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			if (rs.next()) {
				for (int i = 0; i < metadata.getColumnCount(); i++) {
					hash
							.put(metadata.getColumnName(i + 1), rs
									.getObject(i + 1));
				}
			} else {
				throw new UserDatabaseException(
						"angegebener Datensatz existiert nicht", log);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new SystemDatabaseException(
					"SQL-Query kann die Datenbank nicht erreichen", e, log);
		}
		return hash;
	}

	public List executeSelectMultipleRows(String sql) throws DatabaseException {
		log.trace("executeSelectMultipleRows");
		List resultList = new ArrayList();
		Map hash = null;
		try {
			Statement st = conn.createStatement();
			log.debug("SQL SELECT: " + sql);
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			while (rs.next()) {
				hash = new HashMap();
				for (int i = 0; i < metadata.getColumnCount(); i++) {
					hash
							.put(metadata.getColumnName(i + 1), rs
									.getObject(i + 1));
				}
				resultList.add(hash);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new SystemDatabaseException(
					"SQL-Query kann die Datenbank nicht erreichen", e, log);
		}
		return resultList;
	}

	/**
	 * Führt einen String als SQL-Befehl (oder mehrere) aus.
	 * 
	 * @param sqltext
	 * @throws SystemDatabaseException
	 */
	public void executeUpdate(String sql) throws DatabaseException {
		log.trace("executeUpdate");
		try {
			Statement st = conn.createStatement();
			log.debug("SQL UPDATE: " + sql);
			int updateCount = st.executeUpdate(sql);
			if (updateCount < 1) {
				throw new UserDatabaseException(
						"angegebener Datensatz existiert nicht", log);
			}
			st.close();
		} catch (SQLException e) {
			throw new SystemDatabaseException(
					"SQL-Update kann die Datenbank nicht erreichen", e, log);
		}
	}

	public void close() throws SystemDatabaseException {
		log.trace("close");
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			throw new SystemDatabaseException(
					"Fehler beim schliessen der Datenbank", e, log);
		}
	}
}
/*
 * $Log: Database.java,v $
 * Revision 1.9  2005/08/14 20:06:21  tbayen
 * Verbesserungen an den ForeignKeys, die sich aus der FiBu ergeben haben
 *
 * Revision 1.8  2005/08/13 00:11:08  tbayen
 * SQL darf jetzt auch Kommentare enthalten, die PHPMyAdmin erzeugt
 *
 * Revision 1.7  2005/08/12 23:49:18  tbayen
 * wipeOutDatabase() neu erstellt
 *
 * Revision 1.6  2005/08/12 23:39:58  tbayen
 * Table.dropTable() neu und einige Methoden besser dokumentiert
 *
 * Revision 1.5  2005/08/12 23:37:21  tbayen
 * Table.dropTable() neu und einige Methoden besser dokumentiert
 *
 * Revision 1.4  2005/08/12 22:39:37  tbayen
 * executeSql(String), um SQL-Text direkt angeben zu können
 *
 * Revision 1.3  2005/08/12 19:39:47  tbayen
 * kleine Nachbesserung...
 *
 * Revision 1.2  2005/08/12 19:37:18  tbayen
 * unnötige TO DO-Kommentare entfernt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.5  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 * Revision 1.4  2005/02/26 08:52:25  tbayen
 * Probleme mit Character Encoding behoben
 *
 * Revision 1.3  2005/02/23 14:25:14  tbayen
 * automatisches Anlegen der Datenbank geht nun wieder
 *
 * Revision 1.2  2005/02/23 11:40:58  tbayen
 * recht taugliche Version mit Authentifizierung und
 * Trennung von allgem. und applik.-spezifischen Dingen
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.30  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.29  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.28  2004/10/23 12:13:54  tbayen
 * Debugausgaben etwas bereinigt
 *
 * Revision 1.27  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.26  2004/10/18 12:10:14  tbayen
 * Test für den Zugriff auf Properties
 *
 * Revision 1.25  2004/10/16 22:17:16  phormanns
 * Datenbank-Ressourcen werden wieder freigegeben.
 *
 * Revision 1.24  2004/10/16 12:28:14  tbayen
 * setzen des Property Path möglich (für Properties von Datentypen)
 *
 * Revision 1.23  2004/10/15 19:41:49  phormanns
 * MissingRessource abgefangen
 *
 * Revision 1.22  2004/10/15 19:27:08  tbayen
 * Properties für Datentypen
 *
 * Revision 1.21  2004/10/15 16:14:00  phormanns
 * Optimierung TableCache liest 20 Records
 *
 * Revision 1.20  2004/10/14 21:37:36  phormanns
 * Weitere Datentypen
 *
 * Revision 1.19  2004/10/14 21:05:29  tbayen
 * Methode getTableNamesList() umbenannt
 *
 * Revision 1.18  2004/10/14 21:04:52  tbayen
 * Methode getTableNamesList() umbenannt
 *
 * Revision 1.17  2004/10/14 21:03:23  tbayen
 * TableGUI kann nun richtig sortieren (d.h. direkt in SQL)
 *
 * Revision 1.16  2004/10/14 12:51:34  tbayen
 * TableGUI kann jetzt auch vom Benutzer sortiert werden
 *
 * Revision 1.15  2004/10/13 21:40:50  phormanns
 * Typ VARCHAR eingefügt
 *
 * Revision 1.14  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu Überarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.13  2004/10/11 12:55:11  tbayen
 * Table.setRecord implementiert
 *
 * Revision 1.12  2004/10/11 11:18:50  tbayen
 * Dokumentation
 *
 * Revision 1.11  2004/10/10 15:03:26  tbayen
 * Parsen von SQL-Befehlen mit RegEx verbessert
 *
 * Revision 1.10  2004/10/09 20:51:16  tbayen
 * Editorformatierung justiert und Revisionskommentare neu formatiert
 *
 * Revision 1.9 2004/10/09 20:18:54 tbayen 
 * Aufteilung der TypeDefinition in Unterklassen
 * 
 * Revision 1.8 2004/10/09 20:17:31 tbayen 
 * Aufteilung der TypeDefinition in Unterklassen
 * 
 * Revision 1.7 2004/10/09 13:25:45 tbayen 
 * Membervariablen sind private
 *  
 * Revision 1.6 2004/10/09 11:28:24 phormanns 
 * Implementierung der Select- und Delete-Operationen 
 * 
 * Revision 1.5 2004/10/07 17:15:33 tbayen 
 * Datenbankklassen bis auf Table fertig für weitere Tests
 * 
 * Revision 1.4 2004/10/07 14:08:24 tbayen 
 * Logging eingebaut und executeSQL fertiggestellt
 * 
 * Revision 1.3 2004/10/07 13:02:06 tbayen 
 * Funktion executeSqlFile
 * 
 * Revision 1.2 2004/10/06 22:57:37 tbayen 
 * Erste Version eines JUnit-Tests
 * 
 * Revision 1.1 2004/10/04 12:39:00 tbayen 
 * Projekt neu erstellt und einige Exception-Klassen erzeugt
 * 
 */