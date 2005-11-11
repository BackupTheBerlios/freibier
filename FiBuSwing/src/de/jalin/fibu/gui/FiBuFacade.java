// $Id: FiBuFacade.java,v 1.3 2005/11/11 13:25:55 phormanns Exp $
package de.jalin.fibu.gui;

import java.util.Vector;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.database.exception.UserDBEx.UserSQL_DBException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.Journal;
import de.bayen.fibu.Konto;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;

public class FiBuFacade {
	
	private Buchhaltung fibu;
	
	public FiBuFacade() throws FiBuException {
		try {
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
			throw new FiBuUserException("Fehler in der Firmanbezeichnung.");
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
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public void absummieren(Journal j) throws FiBuException {
		try {
			j.absummieren();
		} catch (SQL_DBException e) {
			throw new FiBuSystemException("Konnte Datenbank nicht �ffnen.");
		}
	}
	
	public Journal neuesJournal() throws FiBuException {
		try {
			return fibu.createJournal();
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

}

/*
 *  $Log: FiBuFacade.java,v $
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
