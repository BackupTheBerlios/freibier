// $Id: DatabaseTest.java,v 1.3 2005/03/18 19:01:00 phormanns Exp $
package de.jalin.freibier.database.test;

import java.util.List;
import junit.framework.TestCase;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.DatabaseFactory;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

public class DatabaseTest extends TestCase {
	
	static {
		Database db;
		try {
			db = DatabaseFactory.getDatabaseInstance(
					"com.spaceprogram.sql.hsqldb.HsqldbFactory",
					"org.hsqldb.jdbcDriver",
					"jdbc:hsqldb:mem:test",
					"test", "sa", "");
			db.createTestData();
			db.close();
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private Database db;
	
	protected void setUp() throws Exception {
		db = DatabaseFactory.getDatabaseInstance(
				"com.spaceprogram.sql.hsqldb.HsqldbFactory",
				"org.hsqldb.jdbcDriver",
				"jdbc:hsqldb:mem:test",
				"test", "sa", "");
	}

	protected void tearDown() throws Exception {
		db.close();
	}

	public void testGetName() {
		String dbName = db.getName();
		assertEquals("test", dbName);
	}

	public void testGetTableNamesList() {
		try {
			List tableNames = db.getTableNamesList();
			assertEquals(2, tableNames.size());
			assertEquals("table1".toUpperCase(), ((String) tableNames.get(1)).toUpperCase());
		} catch (SystemDatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetTable() {
		try {
			DBTable table = db.getTable(("TABLE1"));
			assertEquals("TABLE1", table.getName());
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}
}
/*
 *  $Log: DatabaseTest.java,v $
 *  Revision 1.3  2005/03/18 19:01:00  phormanns
 *  DatabaseTest an mehrere Tests angepasst
 *
 *  Revision 1.2  2005/03/03 11:53:46  phormanns
 *  deleteRecord implementiert
 *
 *  Revision 1.1  2005/02/24 13:52:12  phormanns
 *  Mit Tests begonnen
 *
 */
