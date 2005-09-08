// $Id: FibuService.java,v 1.8 2005/09/08 06:27:46 tbayen Exp $

package de.bayen.fibu;

import java.rmi.RemoteException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.UserSQL_DBException;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;
import de.willuhn.datasource.Service;
import de.willuhn.util.ApplicationException;

public class FibuService implements Service {

	private Buchhaltung fibu;
	
	public FibuService() {
		super();
	}

	public void start() throws RemoteException {
		try {
			fibu=new Buchhaltung(System.getProperty("user.home")+"/.fibu.properties");
		} catch (UserSQL_DBException e) {
			throw new RemoteException("Kein Zugriff auf die Datenbank", e);
		}
	}

	public boolean isStarted() throws RemoteException {
		return fibu != null && fibu.ok();
	}

	public boolean isStartable() throws RemoteException {
		return true;
	}

	public void stop(boolean stop) throws RemoteException {
		try {
			fibu.close();
		} catch (SQL_DBException e) {
			throw new RemoteException("Kein Zugriff auf die Datenbank", e);
		}
		fibu = null;
	}

	public String getName() throws RemoteException {
		return "Buchhaltung";
	}

	public Buchhaltung getFiBu() {
		return fibu;
	}
	
	public Konto getBilanzkonto() throws ApplicationException {
		try {
			// ersetzt durch Methode der Buchhaltung-Klasse
			//DataObject bilanz = fibu.getFirmenstammdaten().getField("Bilanzkonto");
			//return new Konto(fibu.getDatabase().getTable("Konten"), (Long) bilanz.getValue());
			return fibu.getBilanzKonto();
		} catch (WrongTypeDBException e) {
			throw new ApplicationException("Kein Zugriff auf die Datenbank", e);
		} catch (SQL_DBException e) {
			throw new ApplicationException("Kein Zugriff auf die Datenbank", e);
//		} catch (RecordNotExistsDBException e) {
//			throw new ApplicationException("Kein Zugriff auf die Datenbank", e);
		} catch (NotInitializedException e) {
			throw new ApplicationException("Kein Zugriff auf die Datenbank", e);
//		} catch (ParseErrorDBException e) {
//			throw new ApplicationException("Kein Zugriff auf die Datenbank", e);
		}
	}
}


//
// $Log: FibuService.java,v $
// Revision 1.8  2005/09/08 06:27:46  tbayen
// Buchhaltung.getBilanzkonto() überarbeitet
//
// Revision 1.7  2005/08/26 20:48:47  phormanns
// Erste Buchung in der Datenbank
//
// Revision 1.6  2005/08/23 19:37:11  phormanns
// Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
//
// Revision 1.4  2005/08/21 20:38:51  tbayen
// Datenbankparameter werden nacheinander an verschiedenen Quellen gesucht
//
// Revision 1.3  2005/08/21 20:18:09  phormanns
// Erste Widgets für Buchen-Dialog
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//