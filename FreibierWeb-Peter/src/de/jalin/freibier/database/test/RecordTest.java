// $Id: RecordTest.java,v 1.3 2005/03/03 22:32:45 phormanns Exp $
package de.jalin.freibier.database.test;

import java.text.DateFormat;
import java.util.Date;
import junit.framework.TestCase;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.DatabaseFactory;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.exception.DatabaseException;


public class RecordTest extends TestCase {

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
	private Record rec;
	
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
		rec = db.getTable("TABLE1").getRecordByPrimaryKey(new Integer(123));
	}

	protected void tearDown() throws Exception {
		rec = null;
		db.close();
	}

	public void testGetPrintable() {
		try {
			Printable value = rec.getPrintable("ID");
			assertNotNull(value);
			assertNotNull(value.getName());
			assertNotNull(value.getValue());
			assertEquals("ID", value.getName());
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testPrintText() {
		try {
			assertEquals("123", rec.printText("ID"));
			assertEquals("Ein Text mit Nummer 123", rec.printText("TEXT"));
			DateFormat df = DateFormat.getDateInstance();
			assertEquals(df.format(new Date()), rec.printText("DATUM").substring(0, 10));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testSetField() {
		try {
			rec.setField("ID", "4711");
			rec.setField("TEXT", "Geänderter Text mit Nummer 4711");
			rec.setField("DATUM", "19.11.1962");
			assertEquals("4711", rec.printText("ID"));
			assertEquals("Geänderter Text mit Nummer 4711", rec.printText("TEXT"));
			assertEquals("19.11.1962 00:00:00", rec.printText("DATUM"));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}

	public void testGetTable() {
		assertEquals("TABLE1", rec.getTable().getName());
	}

	public void testGet() {
		try {
			assertEquals("123", rec.printText("ID"));
			assertEquals("Ein Text mit Nummer 123", rec.printText("TEXT"));
			DateFormat df = DateFormat.getDateInstance();
			assertEquals(df.format(new Date()), rec.printText("DATUM").substring(0, 10));
		} catch (DatabaseException e) {
			fail(e.getMessage());
		}
	}
}

/*
 *  $Log: RecordTest.java,v $
 *  Revision 1.3  2005/03/03 22:32:45  phormanns
 *  Arbeit an ForeignKeys
 *
 *  Revision 1.2  2005/02/24 22:18:13  phormanns
 *  Tests laufen mit HSQL und MySQL
 *
 *  Revision 1.1  2005/02/24 13:52:12  phormanns
 *  Mit Tests begonnen
 *
 */
