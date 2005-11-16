// $Id: FiBuFacade.java,v 1.7 2005/11/16 18:24:11 phormanns Exp $
package de.jalin.fibu.gui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import de.bayen.database.exception.DatabaseException;
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

public class FiBuFacade {
	
	private static final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private Buchhaltung fibu;
	private FiBuGUI gui;
	
	public FiBuFacade(FiBuGUI gui) throws FiBuException {
		try {
			this.gui = gui;
			fibu = new Buchhaltung();
		} catch (UserSQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public String getFirma() throws FiBuException {
		try {
			return fibu.getFirma();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public void setFirma(String firma) throws FiBuException {
		try {
			fibu.getFirmenstammdaten().setField("Firma", firma);
		} catch (ParseErrorDBException e) {
			throw new FiBuUserException("Fehler in der Firmenbezeichnung.");
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public String getJahrAktuell() throws FiBuException {
		try {
			return fibu.getJahrAktuell();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public void setJahrAktuell(String gjAktuell) throws FiBuException {
		try {
			fibu.setJahrAktuell(gjAktuell);
		} catch (DatabaseException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public String getPeriodeAktuell() throws FiBuException {
		try {
			return fibu.getPeriodeAktuell();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public void setPeriodeAktuell(String periodeAktuell) throws FiBuException {
		try {
			fibu.setPeriodeAktuell(periodeAktuell);
		} catch (DatabaseException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public Vector getOffeneJournale() throws FiBuException {
		Vector offeneJournale = new Vector();
		try {
			offeneJournale.addAll(fibu.getOffeneJournale());
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
		return offeneJournale;
	}

	public Vector getAlleJournale() throws FiBuException {
		Vector alleJournale = new Vector();
		try {
			alleJournale.addAll(fibu.getAlleJournale());
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
		return alleJournale;
	}

	public Konto getBilanzKonto() throws FiBuException {
		try {
			return fibu.getBilanzKonto();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			return null;
		}
	}
	
	public void setBilanzKonto(String ktoNr) throws FiBuException {
		try {
			fibu.setBilanzkonto(fibu.getKonto(ktoNr));
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (RecordNotExistsDBException e) {
			throw new FiBuUserException("Konto nicht vorhanden.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public Konto getGuVKonto() throws FiBuException {
		try {
			return fibu.getGuVKonto();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			return null;
		}
	}
	
	public void setGuVKonto(String ktoNr) throws FiBuException {
		try {
			fibu.setGuVKonto(fibu.getKonto(ktoNr));
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (RecordNotExistsDBException e) {
			throw new FiBuUserException("Konto nicht vorhanden.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public void absummieren(Journal j) throws FiBuException {
		try {
			j.absummieren();
			gui.refreshJournale();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public Journal neuesJournal() throws FiBuException {
		try {
			Journal journal = fibu.createJournal();
			gui.refreshJournale();
			return journal;
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (NotInitializedException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}

	public Konto getKonto(String ktoNr) throws FiBuException {
		try {
			return fibu.getKonto(ktoNr);
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (RecordNotExistsDBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}

	public void buchen(Journal journal, String belegNr, String buchungstext, String valutaDatum, 
			String sollKtoNr, String habenKtoNr, String betrag) throws FiBuException {
		try {
			Buchung buchung = journal.createBuchung();
			buchung.setBelegnummer(belegNr);
			buchung.setBuchungstext(buchungstext);
			buchung.setValutadatum(dateFormatter.parse(valutaDatum));
			Buchungszeile sollBuchung = buchung.createZeile();
			sollBuchung.setBetrag(new Betrag(new BigDecimal(betrag), true));
			sollBuchung.setKonto(fibu.getKonto(sollKtoNr));
			Buchungszeile habenBuchung = buchung.createZeile();
			habenBuchung.setBetrag(new Betrag(new BigDecimal(betrag), false));
			habenBuchung.setKonto(fibu.getKonto(habenKtoNr));
			buchung.write();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (ParseErrorDBException e) {
			throw new FiBuUserException("Fehler im Eingabeformat.");
		} catch (ParseException e) {
			throw new FiBuUserException("Fehler im Eingabeformat.");
		} catch (RecordNotExistsDBException e) {
			throw new FiBuUserException("Fehler in der Konto-Angabe.");
		}
	}

	public String getMWSt(Konto kto) throws FiBuException {
		try {
			return kto.getMwSt();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		} catch (RecordNotExistsDBException e) {
			throw new FiBuUserException("Fehler in der Konto-Angabe (kein MWSt-Satz).");
		}
	}

	public List getUnterkonten(Konto kto) throws FiBuException {
		try {
			return kto.getUnterkonten();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}

}

/*
 *  $Log: FiBuFacade.java,v $
 *  Revision 1.7  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.6  2005/11/15 21:20:36  phormanns
 *  Refactorings in FiBuGUI
 *  Focus und Shortcuts in BuchungsForm und StammdatenForm
 *
 *  Revision 1.5  2005/11/11 21:40:35  phormanns
 *  Einstiegskonten im Stammdaten-Form
 *
 *  Revision 1.4  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 *  Revision 1.3  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 *  Revision 1.2  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
