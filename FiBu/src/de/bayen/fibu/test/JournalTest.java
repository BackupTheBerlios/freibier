/* Erzeugt am 16.08.2005 von tbayen
 * $Id: JournalTest.java,v 1.3 2005/08/17 18:54:32 tbayen Exp $
 */
package de.bayen.fibu.test;

import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Journal;
import junit.framework.TestCase;

public class JournalTest extends TestCase {
	Buchhaltung bh;
	private boolean print=true;

	protected void setUp() throws Exception {
		super.setUp();
		bh = new Buchhaltung();
		bh.firstTimeInit();
	}

	protected void tearDown() throws Exception {
		bh.close();
		super.tearDown();
	}
	
	public void testCreateJournal() throws DatabaseException{
		Journal jou = bh.createJournal();
		assertEquals("2005",jou.getBuchungsjahr());
		assertEquals("01",jou.getBuchungsperiode());
		if(print) System.out.println(jou);
		Journal j2=bh.createJournal();
		Journal j3=bh.createJournal();
		assertEquals(new Long(2),j2.getJournalnummer());
		assertEquals(new Long(3),j3.getJournalnummer());
		Journal gelesen=bh.getJournal(new Long(2));
		assertEquals(new Long(2),gelesen.getJournalnummer());
		gelesen.write();
		assertEquals(3,bh.getDatabase().getTable("Journale").getNumberOfRecords());
	}
}

/*
 * $Log: JournalTest.java,v $
 * Revision 1.3  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
 * Revision 1.2  2005/08/16 07:02:33  tbayen
 * Journal-Klasse steht als Grundgerüst
 *
 * Revision 1.1  2005/08/16 00:06:42  tbayen
 * grundlegende Journal-Eigenschaften implementiert
 *
 */