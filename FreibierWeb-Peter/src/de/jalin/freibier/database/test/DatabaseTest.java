// $Id: DatabaseTest.java,v 1.1 2005/02/24 13:52:12 phormanns Exp $
package de.jalin.freibier.database.test;

import java.util.List;
import junit.framework.TestCase;
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
			assertEquals(1, tableNames.size());
			assertEquals("table1".toUpperCase(), ((String) tableNames.get(0)).toUpperCase());
		} catch (SystemDatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetTable() {}
}
/*
 *  $Log: DatabaseTest.java,v $
 *  Revision 1.1  2005/02/24 13:52:12  phormanns
 *  Mit Tests begonnen
 *
 */
