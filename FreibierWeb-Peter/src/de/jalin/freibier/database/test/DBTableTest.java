// $Id: DBTableTest.java,v 1.6 2005/03/03 12:31:39 phormanns Exp $
package de.jalin.freibier.database.test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import com.crossdb.sql.IWhereClause;
import com.crossdb.sql.WhereClause;
import junit.framework.TestCase;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.DatabaseFactory;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.TypeDefinition;
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
		try {
			DBTable tab = db.getTable("TABLE1");
			List colNames = new ArrayList();
			colNames.add("ID");
			colNames.add("TEXT");
			List multipleRecords = tab.getGivenColumns(colNames, 10);
			assertEquals(10, multipleRecords.size());
			Record firstRec = (Record) multipleRecords.get(0);
			assertEquals("Ein Text mit Nummer 0", (String) firstRec.get("TEXT"));
			Record lastRec = (Record) multipleRecords.get(9);
			assertEquals("Ein Text mit Nummer 9", (String) lastRec.get("TEXT"));
			assertEquals("", lastRec.get("DATUM"));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testSetRecordUpdate() {
		try {
			DBTable tab = db.getTable("TABLE1");
			Record rec = tab.getRecordByPrimaryKey(new Integer(255));
			rec.setField("TEXT", "Ein geaenderter Text mit Nummer 254");
			rec.setField("DATUM", "14.02.1964");
			rec.setField("ZAHL", "9999");
			tab.setRecord(rec);
			IWhereClause where = new WhereClause();
			where.addWhereLikeLeft("TEXT", "Ein geaenderter Text");
			assertEquals(1, tab.getNumberOfRecords(where));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testSetRecordInsert() {
		try {
			DBTable tab = db.getTable("TABLE1");
			int numberOfRecords = tab.getNumberOfRecords(null);
			assertEquals(500, numberOfRecords);
			Record rec = tab.getEmptyRecord();
			rec.setField("TEXT", "Ein neuer Text mit Nummer 254");
			rec.setField("DATUM", "14.02.1964");
			rec.setField("ZAHL", "9999");
			tab.setRecord(rec);
			numberOfRecords = tab.getNumberOfRecords(null);
			assertEquals(501, numberOfRecords);
			IWhereClause where = new WhereClause();
			where.addWhereLikeLeft("TEXT", "Ein neuer Text");
			assertEquals(1, tab.getNumberOfRecords(where));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testDeleteRecord() {
		try {
			DBTable tab = db.getTable("TABLE1");
			Record rec = tab.getRecordByPrimaryKey(new Integer(255));
			tab.deleteRecord(rec);
			int numberOfRecords = tab.getNumberOfRecords(null);
			assertEquals(500, numberOfRecords);
			try {
				tab.deleteRecord(rec);
			} catch (DatabaseException e1) {
				assertEquals("Datensatz fuer Delete nicht vorhanden", e1.getMessage());
			}
			numberOfRecords = tab.getNumberOfRecords(null);
			assertEquals(500, numberOfRecords);
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetName() {
		try {
			DBTable tab = db.getTable("TABLE1");
			assertEquals("TABLE1", tab.getName());
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetFieldDef() {
		try {
			DBTable tab = db.getTable("TABLE1");
			TypeDefinition def = tab.getFieldDef("ID");
			assertEquals("ID", def.getName());
			assertEquals(Types.INTEGER, def.getSQLType());
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetPrimaryKey() {
		try {
			DBTable tab = db.getTable("TABLE1");
			assertEquals("ID", tab.getPrimaryKey());
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetFieldsList() {
		try {
			DBTable tab = db.getTable("TABLE1");
			List fieldsList = tab.getFieldsList();
			assertEquals(4, fieldsList.size());
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}
}

/*
 *  $Log: DBTableTest.java,v $
 *  Revision 1.6  2005/03/03 12:31:39  phormanns
 *  getGivenColumns implementiert
 *
 *  Revision 1.5  2005/03/03 11:53:46  phormanns
 *  deleteRecord implementiert
 *
 *  Revision 1.4  2005/03/01 21:56:32  phormanns
 *  Long immer als Value-Objekt zu Number-Typen
 *  setRecord macht Insert, wenn PK = Default-Value
 *
 *  Revision 1.3  2005/02/28 21:52:38  phormanns
 *  SaveAction begonnen
 *
 *  Revision 1.2  2005/02/24 22:18:13  phormanns
 *  Tests laufen mit HSQL und MySQL
 *
 *  Revision 1.1  2005/02/24 13:52:12  phormanns
 *  Mit Tests begonnen
 *
 */
