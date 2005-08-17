/* Erzeugt am 16.08.2005 von tbayen
 * $Id: JournalTest.java,v 1.4 2005/08/17 20:28:04 tbayen Exp $
 */
package de.bayen.fibu.test;

import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Journal;
import junit.framework.TestCase;

public class JournalTest extends TestCase {
	Buchhaltung bh;

	protected void setUp() throws Exception {
		super.setUp();
		bh = new Buchhaltung();
		bh.firstTimeInit();
	}

	protected void tearDown() throws Exception {
		bh.close();
		super.tearDown();
	}

	public void testCreateJournal() throws DatabaseException {
		Journal jou = bh.createJournal();
		assertEquals("2005", jou.getBuchungsjahr());
		assertEquals("01", jou.getBuchungsperiode());
		if (TestConfig.print)
			System.out.println(jou);
		Journal j2 = bh.createJournal();
		Journal j3 = bh.createJournal();
		assertEquals(new Long(2), j2.getJournalnummer());
		assertEquals(new Long(3), j3.getJournalnummer());
		Journal gelesen = bh.getJournal(new Long(2));
		assertEquals(new Long(2), gelesen.getJournalnummer());
		gelesen.write();
		assertEquals(3, bh.getDatabase().getTable("Journale")
				.getNumberOfRecords());
	}

	public void testOffeneJournale() throws DatabaseException {
		assertEquals(0, bh.getAlleJournale().size());
		assertEquals(0, bh.getOffeneJournale().size());
		bh.createJournal();
		Journal jou2 = bh.createJournal();
		bh.createJournal();
		Journal jou4 = bh.createJournal();
		assertEquals(4, bh.getAlleJournale().size());
		assertEquals(4, bh.getOffeneJournale().size());
		jou2.absummieren();
		assertEquals(4, bh.getAlleJournale().size());
		assertEquals(3, bh.getOffeneJournale().size());
		jou4.absummieren();
		assertEquals(4, bh.getAlleJournale().size());
		assertEquals(2, bh.getOffeneJournale().size());
		bh.createJournal();
		assertEquals(5, bh.getAlleJournale().size());
		assertEquals(3, bh.getOffeneJournale().size());
	}
}
/*
 * $Log: JournalTest.java,v $
 * Revision 1.4  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch n�tig war
 *
 * Revision 1.3  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und k�rzer
 *
 * Revision 1.2  2005/08/16 07:02:33  tbayen
 * Journal-Klasse steht als Grundger�st
 *
 * Revision 1.1  2005/08/16 00:06:42  tbayen
 * grundlegende Journal-Eigenschaften implementiert
 *
 */