/* Erzeugt am 21.08.2005 von tbayen
 * $Id: PetersTest.java,v 1.1 2005/08/21 17:26:12 tbayen Exp $
 */
package de.bayen.fibu.test;

import java.util.List;
import junit.framework.TestCase;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.Konto;
import de.bayen.fibu.exceptions.FiBuException;

public class PetersTest extends TestCase {
	public static void testPetersMail050821() throws DatabaseException,
			FiBuException {
		Buchhaltung fibu = new Buchhaltung("test", "localhost", "test", "");
		Record firmenstammdaten = fibu.getFirmenstammdaten();
		fibu.setFirmenstammdaten(firmenstammdaten);
		fibu.setJahrAktuell("2005");
		fibu.setPeriodeAktuell("Q1");
		Konto bilanzkto = fibu.createKonto();
		bilanzkto.setKontonummer("0");
		bilanzkto.setBezeichnung("Bilanzkonto");
		bilanzkto.write();
		createKonto(fibu, bilanzkto, "Ausgaben", "1");
		Konto vermoegensKto = createKonto(fibu, bilanzkto, "Vermögen", "2");
		createKonto(fibu, bilanzkto, "Einnahmen", "3");
		createKonto(fibu, vermoegensKto, "Bank", "21");
		createKonto(fibu, vermoegensKto, "Kasse", "22");
		//System.out.println("Konten angelegt.");
		Konto konto = fibu.getKonto("0");
		List unterkonten = konto.getUnterkonten();
		GenericObject ktoObj = (GenericObject) unterkonten.get(0);
		assertEquals("Ausgaben", ktoObj.getAttribute("Bezeichnung"));
	}

	private static Konto createKonto(Buchhaltung fibu, Konto bilanzkto,
			String text, String num) throws DatabaseException {
		Konto neuesKonto = fibu.createKonto();
		neuesKonto.setBezeichnung(text);
		neuesKonto.setKontonummer(num);
		neuesKonto.setOberkonto(bilanzkto);
		neuesKonto.write();
		return neuesKonto;
	}
}
/*
 * $Log: PetersTest.java,v $
 * Revision 1.1  2005/08/21 17:26:12  tbayen
 * doppelte Variable in fast allen von AbstractObject abgeleiteten Klassen
 *
 */