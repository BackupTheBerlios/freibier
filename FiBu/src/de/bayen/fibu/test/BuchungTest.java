/* Erzeugt am 16.08.2005 von tbayen
 * $Id: BuchungTest.java,v 1.1 2005/08/16 08:52:32 tbayen Exp $
 */
package de.bayen.fibu.test;

import junit.framework.TestCase;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Journal;

public class BuchungTest extends TestCase {
	private Buchhaltung bh;
	private boolean print=true;

	public BuchungTest(String arg0) {
		super(arg0);
		try {
			bh = new Buchhaltung();
			bh.firstTimeInit();
			bh.close();
		} catch (Exception e) {
			fail("Exception schon im Konstruktor: " + e.getMessage());
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		bh = new Buchhaltung();
	}

	protected void tearDown() throws Exception {
		bh.close();
		super.tearDown();
	}

	public void testBuchung() throws Exception {
		try {
			Journal j = bh.createJournal();
			Buchung buch = j.createBuchung();
			buch.setBelegnummer("10000");
			buch.setBuchungstext("Testbuchung 1");
			if(print) System.out.println(buch);
		} catch (DatabaseException e) {
			//fail("Exception im Test: " + e.getMessage());
			throw new Exception(e);
		}
		// TODO Methode schreiben
	}
}
/*
 * $Log: BuchungTest.java,v $
 * Revision 1.1  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 */