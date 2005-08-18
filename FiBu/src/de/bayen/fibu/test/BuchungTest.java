/* Erzeugt am 16.08.2005 von tbayen
 * $Id: BuchungTest.java,v 1.6 2005/08/18 14:14:04 tbayen Exp $
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
		if (TestConfig.print)
			System.out.println(buch);
	}

	public void testBuchungszeilen() throws DatabaseException {
		Journal j = bh.createJournal();
		assertEquals(0, j.getBuchungen().size());
		// Vorbereitung: Konto erzeugen
		Konto konto = bh.createKonto();
		konto.setBezeichnung("Bilanz");
		konto.setKontonummer("00001");
		konto.write();
		// Vorbereiten: Buchung erzeugen
		Buchung buch = j.createBuchung();
		buch.setBelegnummer("10001");
		buch.setBuchungstext("Testbuchung 2");
		// ab jetzt was neues: Buchungszeilen!
		Buchungszeile bz = buch.createZeile();
		bz.setBetrag(new Betrag(new BigDecimal("123.45"), "S"));
		bz.setKonto(bh.getKonto("00001"));
		Buchungszeile bz2 = buch.createZeile();
		bz2.setBetrag(new Betrag(new BigDecimal("100.00"), "H"));
		bz2.setKonto(bh.getKonto("00001"));
		assertTrue("Saldo stimmt nicht", buch.getSaldo().equals(
				new Betrag(new BigDecimal("23.45"), "S")));
		assertFalse("Saldo darf nicht Null sein", buch.isSaldoNull());
		Buchungszeile bz3 = buch.createZeile();
		bz3.setBetrag(buch.getSaldo().negate());
		bz3.setKonto(bh.getKonto("00001"));
		assertTrue("Saldo sollte Null sein", buch.isSaldoNull());
		buch.write();
		if (TestConfig.print)
			System.out.println(buch);
		// ein paar Tests zum Thema getBuchungen()
		assertEquals(1, j.getBuchungen().size());
		Buchung buch2 = j.createBuchung();
		buch2.setBelegnummer("10002");
		buch2.write();
		assertEquals(2, j.getBuchungen().size());
		assertEquals("10001", ((Buchung) j.getBuchungen().get(0))
				.getBelegnummer());
		assertEquals("10002", ((Buchung) j.getBuchungen().get(1))
				.getBelegnummer());
		if(TestConfig.print)
			System.out.println(j+"\n");
		// Sind die Buchungen auch auf dem Konto angekommen?
		assertEquals(3,bh.getKonto("00001").getBuchungszeilen().size());
		if(TestConfig.print)
			System.out.println(bh.getKonto("00001")+"\n");
	}
}
/*
 * $Log: BuchungTest.java,v $
 * Revision 1.6  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.5  2005/08/17 21:31:28  tbayen
 * Test etwas ausführlicher gemacht
 *
 * Revision 1.4  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 * Revision 1.3  2005/08/16 21:11:47  tbayen
 * Buchungszeilen werden gespeichert
 *
 * Revision 1.2  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 * Revision 1.1  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 */