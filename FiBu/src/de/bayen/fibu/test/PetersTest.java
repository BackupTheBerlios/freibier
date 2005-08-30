/* Erzeugt am 21.08.2005 von tbayen
 * $Id: PetersTest.java,v 1.3 2005/08/30 20:13:02 tbayen Exp $
 */
package de.bayen.fibu.test;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.TestCase;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.Konto;
import de.bayen.fibu.exceptions.FiBuException;

public class PetersTest extends TestCase {
	private static Log log = LogFactory.getLog(JournalTest.class);

	public static void testPetersMail050821() throws DatabaseException,
			FiBuException {
		Buchhaltung fibu = new Buchhaltung();
		Record firmenstammdaten = fibu.getFirmenstammdaten();
		fibu.setFirmenstammdaten(firmenstammdaten);
		fibu.setJahrAktuell("2005");
		fibu.setPeriodeAktuell("Q1");
		Konto bilanzkto = fibu.createKonto();
		bilanzkto.setKontonummer("0");
		bilanzkto.setBezeichnung("Bilanzkonto");
		bilanzkto.write();
		createKonto(fibu, bilanzkto, "Ausgaben", "1");
		Konto vermoegensKto = createKonto(fibu, bilanzkto, "Verm�gen", "2");
		createKonto(fibu, bilanzkto, "Einnahmen", "3");
		createKonto(fibu, vermoegensKto, "Bank", "21");
		createKonto(fibu, vermoegensKto, "Kasse", "22");
		log.info("Konten angelegt.");
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
 * Revision 1.3  2005/08/30 20:13:02  tbayen
 * Zugriff auf Standard-Datenbank, die auch alle anderen Tests benutzen
 *
 * Revision 1.2  2005/08/21 17:42:23  tbayen
 * Ausgaben von Test-Klassen nicht per println, sondern per Logging
 *
 * Revision 1.1  2005/08/21 17:26:12  tbayen
 * doppelte Variable in fast allen von AbstractObject abgeleiteten Klassen
 *
 */