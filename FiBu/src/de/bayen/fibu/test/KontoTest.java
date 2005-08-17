/* Erzeugt am 13.08.2005 von tbayen
 * $Id: KontoTest.java,v 1.2 2005/08/17 20:28:04 tbayen Exp $
 */
package de.bayen.fibu.test;

import java.util.List;
import junit.framework.TestCase;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Konto;

public class KontoTest extends TestCase {
	boolean print = true;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(KontoTest.class);
	}

	public KontoTest(String name) {
		super(name);
		try {
			Buchhaltung bh = new Buchhaltung();
			bh.firstTimeInit();
			bh.close();
		} catch (Exception e) {
			// um das abzufangen sind andere Tests da...
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetterAndSetter() {
		try {
			Buchhaltung bh = new Buchhaltung();
			Konto konto = bh.createKonto();
			assertEquals("0.0", konto.getMwSt());
			assertEquals(null, konto.getOberkontoNummer());
			assertEquals(null, konto.getOberkonto());
			konto.setBezeichnung("Bilanz");
			konto.setKontonummer("00001");
			konto.write();
			Konto bilanz = bh.getKonto("00001");
			assertEquals("Bilanz", bilanz.getBezeichnung());
			if (TestConfig.print)
				System.out.println(bilanz);
			Konto aktiva = bh.createKonto();
			aktiva.setBezeichnung("Aktiva");
			aktiva.setKontonummer("10000");
			aktiva.setOberkontoNummer("00001");
			aktiva.write();
			if (TestConfig.print)
				System.out.println(aktiva);
			bh.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testUnterkonten() throws DatabaseException {
		Buchhaltung bh = new Buchhaltung();
			Konto oberkto = bh.createKonto();
			oberkto.setBezeichnung("Passiva");
			oberkto.setKontonummer("20000");
			oberkto.write();
		{
			Konto konto = bh.createKonto();
			konto.setBezeichnung("Eigenkapital");
			konto.setKontonummer("21000");
			konto.setOberkonto(oberkto);
			konto.write();
		}
		{
			Konto konto = bh.createKonto();
			konto.setBezeichnung("Fremdkapital");
			konto.setKontonummer("22000");
			konto.setOberkonto(oberkto);
			konto.write();
		}
		Konto oberkonto=bh.getKonto("20000");
		List konten=oberkonto.getUnterkonten();
		assertEquals(2,konten.size());
		assertEquals(0,bh.getKonto("22000").getUnterkonten().size());
	}
}
/*
 * $Log: KontoTest.java,v $
 * Revision 1.2  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */