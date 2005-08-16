/* Erzeugt am 16.08.2005 von tbayen
 * $Id: BuchungTest.java,v 1.2 2005/08/16 12:22:09 tbayen Exp $
 */
package de.bayen.fibu.test;

import java.math.BigDecimal;
import junit.framework.TestCase;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Betrag;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Buchungszeile;
import de.bayen.fibu.Journal;
import de.bayen.fibu.Konto;

public class BuchungTest extends TestCase {
	private Buchhaltung bh;
	private boolean print = true;

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

	public void testBuchung() throws DatabaseException {
		Journal j = bh.createJournal();
		Buchung buch = j.createBuchung();
		buch.setBelegnummer("10000");
		buch.setBuchungstext("Testbuchung 1");
		if (print)
			System.out.println(buch);
	}

	public void testBuchungszeilen() throws DatabaseException {
		Journal j = bh.createJournal();
		// Vorbereitung: Konto erzeugen
		Konto konto = bh.createKonto();
		konto.setBezeichnung("Bilanz");
		konto.setKontonummer("00001");
		konto.write();

		Buchung buch = j.createBuchung();
		buch.setBelegnummer("10001");
		buch.setBuchungstext("Testbuchung 2");
		Buchungszeile bz=buch.createZeile();
		bz.setBetrag(new Betrag(new BigDecimal("123.45"),"S"));
		bz.setKonto(bh.getKonto("00001"));
		Buchungszeile bz2=buch.createZeile();
		bz2.setBetrag(new Betrag(new BigDecimal("100.00"),"H"));
		bz2.setKonto(bh.getKonto("00001"));
		if (print)
			System.out.println(buch);
	}

}
/*
 * $Log: BuchungTest.java,v $
 * Revision 1.2  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 * Revision 1.1  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 */