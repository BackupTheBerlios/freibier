//$Id: DatabaseImpl.java,v 1.13 2005/03/03 22:32:45 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.crossdb.sql.Column;
import com.crossdb.sql.CreateTableQuery;
import com.crossdb.sql.CrossdbResultSet;
import com.crossdb.sql.DeleteQuery;
import com.crossdb.sql.InsertQuery;
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
    private String jdbcConnectUrl = null;
    private String dbName = null;
    private String dbUser = null;
    private String dbPassword = null;
    private List tableNamesList = null;
    private Map tablesMap = null;

    public DatabaseImpl(String sqlFactoryClass, String jdbcDriverClass,
            String jdbcConnectUrl, 
			String dbName, String dbUser, String dbPassword)
            throws DatabaseException {
        log.trace("Konstruktor");
        this.sqlFactoryClass = sqlFactoryClass;
        this.jdbcDriverClass = jdbcDriverClass;
        this.jdbcConnectUrl = jdbcConnectUrl;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.tablesMap = new HashMap();
        openConnection();
        tableNamesList = new ArrayList();
        readTablesFromCatalog();
    }
    
    public String getName() {
    	return dbName;
    }
    
    public void createTestData() throws SystemDatabaseException {
    	CreateTableQuery createColorTableQuery = sqlFactory.getCreateTableQuery();
    	createColorTableQuery.setName("COLOR");
    	Column pkColor = new Column("ID", Types.INTEGER);
    	pkColor.setPrimaryKey(true);
    	pkColor.setAutoIncrement(true);
    	createColorTableQuery.addColumn(pkColor);
    	createColorTableQuery.addColumn(new Column("NAME", Types.VARCHAR));
    	try {
			createColorTableQuery.execute(conn);
		} catch (SQLException e) {
            throw new SystemDatabaseException("Konnte Testtabelle nicht anlegen.", e, log);
		}
		String[] colors = new String[] { "rot", "gelb", "blau" };
		InsertQuery insertColor = null;
		int len = colors.length;
		for (int i = 0; i<len; i++) {
			insertColor = sqlFactory.getInsertQuery();
			insertColor.setTable("COLOR");
			insertColor.addAutoIncrementColumn("ID");
			insertColor.addColumn("NAME", colors[i]);
			try {
				insertColor.execute(conn);
			} catch (SQLException e) {
                throw new SystemDatabaseException("Konnte Testtabelle nicht fuellen.", e, log);
			}
		}
        CreateTableQuery createMainTableQuery = sqlFactory.getCreateTableQuery();
        createMainTableQuery.setName("TABLE1");
        Column pkMain = new Column("ID", Types.INTEGER);
        pkMain.setPrimaryKey(true);
        pkMain.setAutoIncrement(true);
        createMainTableQuery.addColumn(pkMain);
        createMainTableQuery.addColumn(new Column("TEXT", Types.VARCHAR));
        createMainTableQuery.addColumn(new Column("DATUM", Types.DATE));
        createMainTableQuery.addColumn(new Column("ZAHL", Types.INTEGER));
        Column fkColor = new Column("ID_COLOR", Types.INTEGER);
        fkColor.setForeignKey(true);
        fkColor.setForeignTable("COLOR");
        fkColor.setForeignPrimaryKey("ID");
        createMainTableQuery.addColumn(fkColor);
        try {
            createMainTableQuery.execute(conn);
        } catch (SQLException e) {
            throw new SystemDatabaseException("Konnte Testtabelle nicht anlegen.", e, log);
        }
        InsertQuery insertQuery = null;
        for (int i = 0; i < 500; i++) {
        	insertQuery = sqlFactory.getInsertQuery();
	        insertQuery.setTable("TABLE1");
	        insertQuery.addAutoIncrementColumn("ID");
	        insertQuery.addColumn("TEXT", "Ein Text mit Nummer " + i);
	        insertQuery.addColumn("DATUM", new Date());
	        insertQuery.addColumn("ZAHL", 1700 - i);
	        insertQuery.addColumn("ID_COLOR", i % 3);
	        try {
                insertQuery.execute(conn);
            } catch (SQLException e) {
                throw new SystemDatabaseException("Konnte Testtabelle nicht fuellen.", e, log);
            }
        }
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

    public DBTable getTable(String name) throws DatabaseException {
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

    public CrossdbResultSet executeSelectQuery(SelectQuery query)
            throws SystemDatabaseException {
        try {
            return query.execute(conn);
        } catch (SQLException e) {
            throw new SystemDatabaseException("SQL Fehler", e, log);
        }
    }

    public void executeUpdateQuery(UpdateQuery query)
            throws SystemDatabaseException {
        int updateCount = 0;
        try {
            updateCount = query.execute(conn);
            if (updateCount != 1) {
                throw new SystemDatabaseException(
                        "Datensatz fuer Update nicht vorhanden", log);
            }
        } catch (SQLException e) {
            throw new SystemDatabaseException("SQL Fehler", e, log);
        }
    }

    public void executeInsertQuery(InsertQuery query)
    		throws SystemDatabaseException {
		int retValue = 0;
		try {
		    retValue = query.execute(conn);
		} catch (SQLException e) {
		    throw new SystemDatabaseException("SQL Fehler", e, log);
		}
    }

	public void executeDeleteQuery(DeleteQuery deleteQuery)
			throws SystemDatabaseException {
        int updateCount = 0;
		try {
			updateCount = deleteQuery.execute(conn);
            if (updateCount != 1) {
                throw new SystemDatabaseException(
                        "Datensatz fuer Delete nicht vorhanden", log);
            }
		} catch (SQLException e) {
		    throw new SystemDatabaseException("SQL Fehler", e, log);
		}
 	}

    private void readTablesFromCatalog() throws SystemDatabaseException {
        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[] {"TABLE"});
            while (rs.next()) {
                tableNamesList.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            throw new SystemDatabaseException(
                    "Kann Tabellenliste nicht lesen", e, log);
        }
    }

    private DBTableImpl initTableFromCatalog(String name)
            throws DatabaseException {
        DBTableImpl dbtab = new DBTableImpl(this, name);
        try {
        	DatabaseMetaData metaData = conn.getMetaData();
            Table tab = new Table(name);
            ResultSet columns = metaData.getColumns(null, null, name, "%");
            Column col = null;
            if (columns.next()) {
	            do {
	                col = new Column(columns.getString("COLUMN_NAME"));
	                col.setType(columns.getInt("DATA_TYPE"), 
	                        columns.getInt("COLUMN_SIZE"));
	                col.setNullable(columns.getInt("NULLABLE"));
	                tab.addColumn(col);
	            } while (columns.next());
            } else {
            	throw new UserDatabaseException("Tabelle nicht definiert.");
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
            ResultSet foreignKeys = metaData.getImportedKeys(null, null, name);
            String pkTableName = null;
            String pkColumnName = null;
            String fkTableName = null;
            String fkColumnName = null;
            Column fkCol = null;
            while (foreignKeys.next()) {
            	pkTableName = foreignKeys.getString("PKTABLE_NAME"); 
            	pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
            	fkTableName = foreignKeys.getString("FKTABLE_NAME"); 
            	fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            	fkCol = tab.getColumn(fkColumnName);
            	fkCol.setForeignKey(true);
            	fkCol.setForeignTable(pkTableName);
            	fkCol.setForeignPrimaryKey(pkColumnName);
            }
            dbtab.setTable(tab);
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
                    "SQL-Factory fuer die Datenbank kann nicht geladen werden",
                    e, log);
        } catch (InstantiationException e) {
            throw new SystemDatabaseException(
                    "SQL-Factory fuer die Datenbank kann nicht erzeugt werden",
                    e, log);
        } catch (IllegalAccessException e) {
            throw new SystemDatabaseException(
                    "SQL-Factory fuer die Datenbank kann nicht erzeugt werden",
                    e, log);
        }
        try {
            Class.forName(jdbcDriverClass);
        } catch (ClassNotFoundException e) {
            throw new SystemDatabaseException(
                    "JDBC-Treiber fuer die Datenbank kann nicht geladen werden",
                    e, log);
        }
        ;
        try {
            conn = DriverManager.getConnection(jdbcConnectUrl, dbUser,
                    dbPassword);
        } catch (SQLException e) {
            throw new UserDatabaseException(
                    "Verbindung zur SQL-Datenbank kann nicht geoeffnet werden",
                    e, log);
        }
    }

}
/*
 * $Log: DatabaseImpl.java,v $
 * Revision 1.13  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.12  2005/03/03 11:53:46  phormanns
 * deleteRecord implementiert
 *
 * Revision 1.11  2005/03/01 21:56:32  phormanns
 * Long immer als Value-Objekt zu Number-Typen
 * setRecord macht Insert, wenn PK = Default-Value
 *
 * Revision 1.10  2005/02/24 22:18:12  phormanns
 * Tests laufen mit HSQL und MySQL
 *
 * Revision 1.9  2005/02/24 13:52:12  phormanns
 * Mit Tests begonnen
 *
 * Revision 1.8  2005/02/21 22:55:25  phormanns
 * Hsqldb zugefuegt
 *
 * Revision 1.7  2005/02/14 21:24:43  phormanns
 * Kleinigkeiten
 * Revision 1.6 2005/02/13 20:27:14 phormanns
 * Funktioniert bis auf Filter
 * 
 * Revision 1.5 2005/02/11 16:46:02 phormanns MySQL geht wieder
 *  
 */