/* Erzeugt am 29.08.2005 von tbayen
 * $Id: KontenrahmenTest.java,v 1.1 2005/08/30 21:05:53 tbayen Exp $
 */
package de.bayen.fibu.test;

import java.math.BigDecimal;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.database.exception.UserDBEx.UserSQL_DBException;
import de.bayen.fibu.Betrag;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Buchung;
import de.bayen.fibu.Buchungszeile;
import de.bayen.fibu.Journal;
import de.bayen.fibu.Konto;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;
import de.bayen.fibu.kontenimport.ImportGNUCash;

public class KontenrahmenTest extends TestCase {
	private static Log log = LogFactory.getLog(KontenrahmenTest.class);

	public static void main(String[] args) {
		junit.textui.TestRunner.run(KontenrahmenTest.class);
	}

	/*
	 * Test method for 'de.bayen.fibu.kontenimport.ImportGNUCash.importSKR04(Buchhaltung)'
	 */
	public void testImportSKR04() throws Exception {
		Buchhaltung buch = new Buchhaltung();
		ImportGNUCash.importSKR04(buch);
		assertEquals(280, buch.getDatabase().getTable("Konten")
				.getNumberOfRecords());
	}

	public void testSaldenliste() throws UserSQL_DBException, SQL_DBException,
			NotInitializedException {
		Buchhaltung buch = new Buchhaltung();
		String saldenliste = buch.printSaldenliste();
		//System.out.print(saldenliste);
		assertEquals(21996, saldenliste.length());
		assertEquals(-1925540257, saldenliste.hashCode());
	}

	public void testKontoAusgabe() throws UserSQL_DBException, SQL_DBException,
			NotInitializedException, ParseErrorDBException,
			RecordNotExistsDBException {
		Buchhaltung buch = new Buchhaltung();
		Konto bilanz;
		try {
			bilanz = buch.getKonto("bilanz");
		} catch (RecordNotExistsDBException e) {
			throw new NotInitializedException(
					"Bilanzkonto kann nicht gefunden werden", e, log);
		}
		Journal j = buch.createJournal();
		Buchung buchung = j.createBuchung();
		buchung.setBelegnummer("4711");
		buchung.setBuchungstext("Testbuchung auf Bilanzkonto");
		Buchungszeile zeile;
		// erste Zeile
		zeile = buchung.createZeile();
		zeile.setKonto(buch.getKonto("bilanz"));
		zeile.setBetrag(new Betrag(new BigDecimal("1234567.89"), 'S'));
		// zweite Zeile
		zeile = buchung.createZeile();
		zeile.setKonto(buch.getKonto("guv"));
		zeile.setBetrag(new Betrag(new BigDecimal("1234567.89"), 'H'));
		buchung.write();
		//System.out.print(bilanz.ausgabe(20, true, true));
		assertEquals(
				"    Nr|Bezeichnung                             |          Soll|         Haben\n"
						+ "------+----------------------------------------+--------------+--------------\n"
						+ "bilanz|Bilanz                                  |          0,00|              \n"
						+ "   guv|  Gewinn- und Verlustrechnung           |              |  1.234.567,89\n",
				bilanz.ausgabe(20, true, true));
		//		List unterkonten = bilanz.getUnterkonten();
	}
}
/*
 * $Log: KontenrahmenTest.java,v $
 * Revision 1.1  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 */