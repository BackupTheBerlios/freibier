//$Id: DatabaseImpl.java,v 1.6 2005/02/13 20:27:14 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.crossdb.sql.Column;
import com.crossdb.sql.CrossdbResultSet;
import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.crossdb.sql.Table;
import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;

/**
 * @author tbayen
 * 
 * Ein Objekt dieser Klasse ist die Basis fuer alle Datenzugriffe. Es
 * repraesentiert z.B. eine MySQL-Datenbank. Aus diesem Objekt koennen dann
 * Tabellen (Interface DBTable) extrahiert werden, in denen die eigentlichen 
 * Daten stehen.
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
		tableNamesList = new ArrayList();
		readTablesFromCatalog();
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
            readTablesFromCatalog();
        }
        return tableNamesList;
    }

	public DBTable getTable(String name) throws SystemDatabaseException {
        DBTableImpl tab = (DBTableImpl) tablesMap.get(name);
        if (tab == null) {
	    	tab = initTableFromCatalog(name);
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

	public SQLFactory getSQLFactory() {
		return sqlFactory;
	}
	
	public CrossdbResultSet executeSelectQuery(SelectQuery query) throws SystemDatabaseException {
		try {
			return query.execute(conn);
		} catch (SQLException e) {
    		throw new SystemDatabaseException(
    				"SQL Fehler", e, log);
		}
	}
    
	public void executeUpdateQuery(UpdateQuery query) throws SystemDatabaseException {
		int updateCount = 0;
		try {
			updateCount = query.execute(conn);
			if (updateCount != 1) {
				throw new SystemDatabaseException(
						"DAtensatz fuer Update nicht vorhanden", log);
			}
		} catch (SQLException e) {
    		throw new SystemDatabaseException(
    				"SQL Fehler", e, log);
		}
	}

    private void readTablesFromCatalog() throws SystemDatabaseException {
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

	private DBTableImpl initTableFromCatalog(String name) throws SystemDatabaseException {
		DBTableImpl dbtab = new DBTableImpl(this, name);
		try {
			Table tab = new Table(name); 
			dbtab.setTable(tab);
			ResultSet columns 
				= conn.getMetaData().getColumns(null, null, name, "%");
			Column col = null;
			while (columns.next()) {
				col = new Column(columns.getString("COLUMN_NAME"));
				col.setType(columns.getInt("DATA_TYPE"), columns.getInt("COLUMN_SIZE"));
				col.setNullable(columns.getInt("NULLABLE"));
				tab.addColumn(col);
			}
			columns.close();
			ResultSet primarykeys 
				= conn.getMetaData().getPrimaryKeys(null, null, name);
			if (primarykeys.next()) {
				String pkName = primarykeys.getString("COLUMN_NAME");
				if (primarykeys.next()) {
					throw new SystemDatabaseException(
							"Mehrere Primaerschluesselspalten sind nicht erlaubt",
							log);
				}
				tab.getColumn(pkName).setPrimaryKey(true);
			} else {
				throw new SystemDatabaseException(
						"Keine Primaerschluesselspalte definiert", log);
			}
			primarykeys.close();
		} catch (SQLException e) {
		    throw new SystemDatabaseException("", e, log);
		}
		return dbtab;
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

}
/*
 * $Log: DatabaseImpl.java,v $
 * Revision 1.6  2005/02/13 20:27:14  phormanns
 * Funktioniert bis auf Filter
 *
 * Revision 1.5  2005/02/11 16:46:02  phormanns
 * MySQL geht wieder
 *
 */