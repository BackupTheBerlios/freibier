//$Id: DatabaseImpl.java,v 1.5 2005/02/11 16:46:02 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.io.BufferedReader;
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
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;

import com.crossdb.sql.SQLFactory;

import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.Table;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;

/**
 * @author tbayen
 * 
 * Ein Objekt dieser Klasse ist die Basis fuer alle Datenzugriffe. Es
 * repraesentiert z.B. eine MySQL-Datenbank. Aus diesem Objekt koennen dann
 * Tabellen (class TableImpl) extrahiert werden, in denen die eigentlichen Daten
 * stehen.
 */
public class DatabaseImpl implements Database {
	
	private static Log log = LogFactory.getLog(DatabaseImpl.class);
	
	private SQLFactory sqlFactory = null;
	private Connection conn = null;
	private String propertyPath = "";
	private String sqlFactoryClass = null;
	private String jdbcDriverClass = null;
	private String dbUser = null;
	private String jdbcConnectUrl = null;
	private String dbPassword = null;
	private List tableNamesList = null;
	private Map tablesMap = null;
                                
	/**
	 * Konstruktor
	 */
	public DatabaseImpl(String sqlFactoryClass, 
	        String jdbcDriverClass, 
	        String jdbcConnectUrl, 
	        String dbUser, 
	        String dbPassword)
			throws DatabaseException {
		log.trace("Konstruktor");
		this.sqlFactoryClass = sqlFactoryClass;
		this.jdbcDriverClass = jdbcDriverClass;
		this.jdbcConnectUrl = jdbcConnectUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.tablesMap = new HashMap();
		openConnection();
	}

	/**
     * Ergibt eine Liste von Strings, die die Namen der Tabellen enthaelt, die
     * in der Datenbank angelegt sind.
     * 
     * @return
     * @throws SystemDatabaseException
     */
    public List getTableNamesList() throws SystemDatabaseException {
        if (tableNamesList == null) {
            tableNamesList = new ArrayList();
            try {
                ResultSet rs 
                		= conn.getMetaData().getTables(null, null, "%", null);
                while (rs.next()) {
                    tableNamesList.add(rs.getString("TABLE_NAME"));
                }
                rs.close();
            } catch (SQLException e) {
                throw new SystemDatabaseException(
                        "Kann Tabellenliste nicht lesen", e, log);
            }
        }
        return tableNamesList;
    }

    public Table getTable(String name) throws SystemDatabaseException {
        TableImpl tab = (TableImpl) tablesMap.get(name);
        if (tab == null) {
		    	try {
		    		ResultSet columns 
		    			= conn.getMetaData().getColumns(null, null, name, "%");
		    		tab = new TableImpl(this, name);
		    		ResourceBundle resource = null;
		    		try {
		    			log.debug("Suche Property File: " + propertyPath + name);
		    			resource = ResourceBundle.getBundle(propertyPath + name);
		    			log.debug("Resource: " + resource);
		    		} catch (MissingResourceException e) {
		    			log.debug("Missing Resource: " + propertyPath + name);
		    		}
		    		while (columns.next()) {
		    			TypeDefinitionImpl typ = TypeDefinitionImpl.create(
		    					columns.getString("COLUMN_NAME"), 
		    					columns.getInt("DATA_TYPE"),
		    					columns.getInt("COLUMN_SIZE"),
		    					resource,this);
		    			tab.addColumn(typ);
		    		}
		    		columns.close();
		    		ResultSet primarykeys 
		    			= conn.getMetaData().getPrimaryKeys(null, null, name);
		    		if (primarykeys.next()) {
		    			tab.setPrimaryKey(primarykeys.getString("COLUMN_NAME"));
		    			if (primarykeys.next()) {
		    				throw new SystemDatabaseException(
		    						"Mehrere Primaerschluesselspalten sind nicht erlaubt",
		    						log);
		    			}
		    		} else {
		    			throw new SystemDatabaseException(
		    					"Keine Primaerschluesselspalte definiert", log);
		    		}
		    		primarykeys.close();
		    	} catch (SQLException e) {
		    	    throw new SystemDatabaseException("", e, log);
		    	}
        }
        return tab;
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

    private void openConnection() throws DatabaseException {
    	log.trace("openConnection");
    	Class sqlFactoryClassClass = null;
    	try {
            sqlFactoryClassClass = Class.forName(sqlFactoryClass);
            sqlFactory = (SQLFactory) sqlFactoryClassClass.newInstance();
        } catch (ClassNotFoundException e) {
    		throw new SystemDatabaseException(
    				"SQL-Factory fuer die Datenbank kann nicht geladen werden", e, log);
        } catch (InstantiationException e) {
    		throw new SystemDatabaseException(
    				"SQL-Factory fuer die Datenbank kann nicht erzeugt werden", e, log);
        } catch (IllegalAccessException e) {
    		throw new SystemDatabaseException(
    				"SQL-Factory fuer die Datenbank kann nicht erzeugt werden", e, log);
        }
    	try {
    		Class.forName(jdbcDriverClass);
    	} catch (ClassNotFoundException e) {
    		throw new SystemDatabaseException(
    				"JDBC-Treiber fuer die Datenbank kann nicht geladen werden", e, log);
    	};
    	try {
    		conn = DriverManager.getConnection(jdbcConnectUrl, dbUser, dbPassword);
    	} catch (SQLException e) {
    		throw new UserDatabaseException(
    		        "Verbindung zur SQL-Datenbank kann nicht geoeffnet werden", e, log);
    	}
    }

    protected void setPropertyPath(String path){
    	this.propertyPath=path;
    	if(propertyPath.charAt(propertyPath.length() - 1) != '.') {
    		this.propertyPath += ".";
    	}
    }

	protected boolean ok() {
		log.trace("ok");
		return (conn != null);
	}

	protected void executeSqlFile(String filename) throws SystemDatabaseException {
		log.trace("executeSqlFile");
		String sql = "";
		try {
			Statement st = conn.createStatement();
			InputStream datei = this.getClass().getClassLoader()
					.getResourceAsStream(filename);
			if (datei == null)
				throw new SystemDatabaseException("Kann Datei nicht lesen: "
						+ filename, log);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					datei));
			String zeile = buffer.readLine();
			while (zeile != null) {
				if (zeile.length() > 0 && zeile.charAt(0) != '#') { // Kommentarzeilen weglassen
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
					"Syntax-Fehler in der SQL-Datei (fehlendes Semikolon?)",
					log);
		}
	}

	protected Map executeSelectSingleRow(String sql, int recordNr) throws DatabaseException {
		log.trace("executeSelectSingleRow");
		Map hash = new HashMap();
		try {
			Statement st = conn.createStatement();
			log.debug("SQL SELECT: " + sql);
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();
			if (rs.absolute(recordNr + 1)) {
				for (int i = 0; i < metadata.getColumnCount(); i++) {
					hash.put(metadata.getColumnName(i + 1), 
								rs.getObject(i + 1));
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

	protected List executeSelectMultipleRows(String sql, int startRecordNum, int numberOfRecords) throws DatabaseException {
		log.trace("executeSelectMultipleRows");
		List resultList = new ArrayList();
		Map hash = null;
		if (numberOfRecords <= 0) {
			numberOfRecords = 10000;
		}
		try {
			Statement st = conn.createStatement();
			log.debug("SQL SELECT: " + sql);
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData metadata = rs.getMetaData();  
			int count = 0;
			if (rs.absolute(startRecordNum + 1)) {
				while (count < numberOfRecords) {
					hash = new HashMap();
					for (int i = 0; i < metadata.getColumnCount(); i++) {
						hash.put(metadata.getColumnName(i + 1), 
									rs.getObject(i + 1));
					}
					resultList.add(hash);
					if (rs.next()) {
						count++;
					} else {
						count = numberOfRecords;
					}
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
		return resultList;
	}

	protected void executeUpdate(String sql) throws DatabaseException {
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
}
/*
 * $Log: DatabaseImpl.java,v $
 * Revision 1.5  2005/02/11 16:46:02  phormanns
 * MySQL geht wieder
 *
 * Revision 1.4  2005/02/11 15:25:45  phormanns
 * Zwischenstand, nicht funktionierend
 *
 * Revision 1.3  2005/01/29 22:10:02  phormanns
 * SQL zum Teil nach MysqlTableImpl verschoben
 * MySQL LIMIT-Statement durch JDBC 3.0 ersetzt
 *
 * Revision 1.2  2005/01/29 20:21:59  phormanns
 * RecordDefinition in TableImpl integriert
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste �ffentliche Version
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
 * Test f�r den Zugriff auf Properties
 *
 * Revision 1.25  2004/10/16 22:17:16  phormanns
 * Datenbank-Ressourcen werden wieder freigegeben.
 *
 * Revision 1.24  2004/10/16 12:28:14  tbayen
 * setzen des Property Path m�glich (f�r Properties von Datentypen)
 *
 * Revision 1.23  2004/10/15 19:41:49  phormanns
 * MissingRessource abgefangen
 *
 * Revision 1.22  2004/10/15 19:27:08  tbayen
 * Properties f�r Datentypen
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
 * Typ VARCHAR eingef�gt
 *
 * Revision 1.14  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu �berarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.13  2004/10/11 12:55:11  tbayen
 * TableImpl.setRecord implementiert
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
 * Datenbankklassen bis auf TableImpl fertig f�r weitere Tests
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