/* Erzeugt am 16.08.2005 von tbayen
 * $Id: JournalTest.java,v 1.1 2005/08/16 00:06:42 tbayen Exp $
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
	}
}

/*
 * $Log: JournalTest.java,v $
 * Revision 1.1  2005/08/16 00:06:42  tbayen
 * grundlegende Journal-Eigenschaften implementiert
 *
 */