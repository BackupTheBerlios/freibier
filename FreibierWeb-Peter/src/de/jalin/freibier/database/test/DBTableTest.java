// $Id: DBTableTest.java,v 1.2 2005/02/24 22:18:13 phormanns Exp $
package de.jalin.freibier.database.test;

import java.util.List;
import com.crossdb.sql.IWhereClause;
import com.crossdb.sql.WhereClause;
import junit.framework.TestCase;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.DatabaseFactory;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.exception.DatabaseException;

public class DBTableTest extends TestCase {
	
	static {
		Database db;
		try {
			db = DatabaseFactory.getDatabaseInstance(
					"com.spaceprogram.sql.hsqldb.HsqldbFactory",
					"org.hsqldb.jdbcDriver",
					"jdbc:hsqldb:mem:test",
					"test", "sa", "");
//			db = DatabaseFactory.getDatabaseInstance(
//					"com.spaceprogram.sql.mysql.MySQLFactory",
//					"com.mysql.jdbc.Driver",
//					"jdbc:mysql://localhost/demo",
//					"demo", "demo", "demo");
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
//		db = DatabaseFactory.getDatabaseInstance(
//				"com.spaceprogram.sql.mysql.MySQLFactory",
//				"com.mysql.jdbc.Driver",
//				"jdbc:mysql://localhost/demo",
//				"demo", "demo", "demo");
	}

	protected void tearDown() throws Exception {
		db.close();
	}

	public void testGetMultipleRecords() {
		try {
			DBTable tab = db.getTable("TABLE1");
			List multipleRecords = tab.getMultipleRecords(21, 10, null, true);
			assertEquals(10, multipleRecords.size());
			Record firstRec = (Record) multipleRecords.get(0);
			assertEquals("Ein Text mit Nummer 20", (String) firstRec.get("TEXT"));
			Record secondRec = (Record) multipleRecords.get(1);
			assertEquals("Ein Text mit Nummer 21", (String) secondRec.get("TEXT"));
			multipleRecords = tab.getMultipleRecords(1, 30, "TEXT", true);
			assertEquals(30, multipleRecords.size());
			firstRec = (Record) multipleRecords.get(0);
			assertEquals("Ein Text mit Nummer 0", (String) firstRec.get("TEXT"));
			secondRec = (Record) multipleRecords.get(1);
			assertEquals("Ein Text mit Nummer 1", (String) secondRec.get("TEXT"));
			Record thirdRec = (Record) multipleRecords.get(2);
			assertEquals("Ein Text mit Nummer 10", (String) thirdRec.get("TEXT"));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetRecordsFromQuery() {
		try {
			DBTable tab = db.getTable("TABLE1");
			IWhereClause where = new WhereClause();
			where.addWhereLikeRight("TEXT", "200");
			List multipleRecords = tab.getRecordsFromQuery(where, "TEXT", true);
			assertEquals(1, multipleRecords.size());
			Record firstRec = (Record) multipleRecords.get(0);
			assertEquals("Ein Text mit Nummer 200", (String) firstRec.get("TEXT"));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetRecords() {
		try {
			DBTable tab = db.getTable("TABLE1");
			IWhereClause where = new WhereClause();
			where.addWhereLikeLeft("TEXT", "Ein Text mit Nummer 20");
			List multipleRecords = tab.getRecords(where, "TEXT", true, 1, 50);
			assertEquals(11, multipleRecords.size());
			Record firstRec = (Record) multipleRecords.get(0);
			assertEquals("Ein Text mit Nummer 20", (String) firstRec.get("TEXT"));
			Record lastRec = (Record) multipleRecords.get(10);
			assertEquals("Ein Text mit Nummer 209", (String) lastRec.get("TEXT"));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetNumberOfRecords() {
		try {
			DBTable tab = db.getTable("TABLE1");
			IWhereClause where = new WhereClause();
			where.addWhereLikeLeft("TEXT", "Ein Text mit Nummer 20");
			assertEquals(11, tab.getNumberOfRecords(where));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetRecordByPrimaryKey() {
		try {
			DBTable tab = db.getTable("TABLE1");
			Record rec = tab.getRecordByPrimaryKey(new Integer(255));
			assertEquals("Ein Text mit Nummer 255", (String) rec.get("TEXT"));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetGivenColumns() {
	//TODO Implement getGivenColumns().
	}

	public void testSetRecord() {
		try {
			DBTable tab = db.getTable("TABLE1");
			Record rec = tab.getRecordByPrimaryKey(new Integer(255));
			rec.setField("TEXT", "Ein gešnderter Text mit Nummer 254");
			rec.setField("DATUM", "14.02.1964");
			tab.setRecord(rec);
			IWhereClause where = new WhereClause();
			where.addWhereLikeLeft("TEXT", "Ein gešnderter Text");
			assertEquals(1, tab.getNumberOfRecords(where));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testDeleteRecord() {
	//TODO Implement deleteRecord().
	}

	public void testGetName() {
	//TODO Implement getName().
	}

	public void testGetFieldDef() {
	//TODO Implement getFieldDef().
	}

	public void testGetPrimaryKey() {
	//TODO Implement getPrimaryKey().
	}

	public void testGetFieldsList() {
	//TODO Implement getFieldsList().
	}
}

/*
 *  $Log: DBTableTest.java,v $
 *  Revision 1.2  2005/02/24 22:18:13  phormanns
 *  Tests laufen mit HSQL und MySQL
 *
 *  Revision 1.1  2005/02/24 13:52:12  phormanns
 *  Mit Tests begonnen
 *
 */
