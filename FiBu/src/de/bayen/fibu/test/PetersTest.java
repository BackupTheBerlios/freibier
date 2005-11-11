/* Erzeugt am 21.08.2005 von tbayen
 * $Id: PetersTest.java,v 1.4 2005/11/11 20:24:46 phormanns Exp $
 */
package de.bayen.fibu.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.TestCase;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.database.exception.UserDBEx.UserSQL_DBException;
import de.bayen.fibu.Betrag;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Buchungszeile;
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.Journal;
import de.bayen.fibu.Konto;
import de.bayen.fibu.exceptions.FiBuException;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;

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
		Konto vermoegensKto = createKonto(fibu, bilanzkto, "Vermögen", "2");
		createKonto(fibu, bilanzkto, "Einnahmen", "3");
		createKonto(fibu, vermoegensKto, "Bank", "21");
		createKonto(fibu, vermoegensKto, "Kasse", "22");
		log.info("Konten angelegt.");
		Konto konto = fibu.getKonto("0");
		List unterkonten = konto.getUnterkonten();
		GenericObject ktoObj = (GenericObject) unterkonten.get(0);
		assertEquals("Ausgaben", ktoObj.getAttribute("Bezeichnung"));
	}
	
	public static void testPetersMail051111() {
		try {
			Buchhaltung fibu = new Buchhaltung();
			Journal journal = fibu.createJournal();
			journal.write();
			Buchung buchung = journal.createBuchung();
			buchung.setBelegnummer("001");
			buchung.setBuchungstext("Erste Buchung");
			buchung.setValutadatum(new Date());
			Buchungszeile sollBuchung = buchung.createZeile();
			sollBuchung.setBetrag(new Betrag(new BigDecimal(10.0d), true));
			sollBuchung.setKonto(fibu.getKonto("1"));
			Buchungszeile habenBuchung = buchung.createZeile();
			habenBuchung.setBetrag(new Betrag(new BigDecimal(10.0d), false));
			habenBuchung.setKonto(fibu.getKonto("22"));
			buchung.write();
			Iterator buchungen = journal.getBuchungen().iterator();
			if (buchungen.hasNext()) {
				Buchung buchung2 = (Buchung) buchungen.next();
				Iterator zeilen = buchung2.getBuchungszeilen().iterator();
				if (!zeilen.hasNext()) {
					fail("Es müssten zwei Buchungszeilen vorhanden sein!");
				}
			} else {
				fail("Es müsste eine Buchung vorhanden sein!");
			}
		} catch (UserSQL_DBException e) {
			fail(e.getMessage());
		} catch (SQL_DBException e) {
			fail(e.getMessage());
		} catch (NotInitializedException e) {
			fail(e.getMessage());
		} catch (ParseErrorDBException e) {
			fail(e.getMessage());
		} catch (RecordNotExistsDBException e) {
			fail(e.getMessage());
		}
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
 * Revision 1.4  2005/11/11 20:24:46  phormanns
 * Testfall für das Lesen von Journalen
 *
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