/* Erzeugt am 16.08.2005 von tbayen
 * $Id: BuchungTest.java,v 1.9 2005/08/31 16:49:46 tbayen Exp $
 */
package de.bayen.fibu.test;

import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.TestCase;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Betrag;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Buchungszeile;
import de.bayen.fibu.Journal;
import de.bayen.fibu.Konto;
import de.bayen.fibu.exceptions.FiBuException;

public class BuchungTest extends TestCase {
	private static Log log = LogFactory.getLog(BuchungTest.class);
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

	public void testBuchung() throws DatabaseException, FiBuException {
		Journal j = bh.createJournal();
		Buchung buch = j.createBuchung();
		buch.setBelegnummer("10000");
		buch.setBuchungstext("Testbuchung 1");
		log.info(buch);
	}

	public void testBuchungszeilen() throws DatabaseException, FiBuException {
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
		log.info(buch);
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
		log.info(j+"\n");
		j.absummieren();
		// Sind die Buchungen auch auf dem Konto angekommen?
		assertEquals(3, bh.getKonto("00001").getBuchungszeilen().size());
		log.info(bh.getKonto("00001") + "\n");
	}
}
/*
 * $Log: BuchungTest.java,v $
 * Revision 1.9  2005/08/31 16:49:46  tbayen
 * In Auswertungen nach best. Kriterien ausw�hlen (Jahr, Periode, absummiert)
 *
 * Revision 1.8  2005/08/21 17:42:23  tbayen
 * Ausgaben von Test-Klassen nicht per println, sondern per Logging
 *
 * Revision 1.7  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und �berall eingef�hrt
 *
 * Revision 1.6  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.5  2005/08/17 21:31:28  tbayen
 * Test etwas ausf�hrlicher gemacht
 *
 * Revision 1.4  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch n�tig war
 *
 * Revision 1.3  2005/08/16 21:11:47  tbayen
 * Buchungszeilen werden gespeichert
 *
 * Revision 1.2  2005/08/16 12:22:09  tbayen
 * rudiment�res Arbeiten mit Buchungszeilen m�glich
 *
 * Revision 1.1  2005/08/16 08:52:32  tbayen
 * Grundger�st der Klasse Buchung (mit Test) steht
 *
 */