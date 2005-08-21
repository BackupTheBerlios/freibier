// $Id: FibuService.java,v 1.3 2005/08/21 20:18:09 phormanns Exp $

package de.bayen.fibu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.exceptions.FiBuException.NotInitializedException;
import de.bayen.fibu.gui.FiBuPlugin;
import de.willuhn.datasource.Service;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

public class FibuService implements Service {

	private Buchhaltung fibu;
	
	public FibuService() {
		super();
	}

	public void start() throws RemoteException {
		PluginResources resources = Application.getPluginLoader().getPlugin(FiBuPlugin.class).getResources();
		Properties mysqlProps = new Properties();
		try {
			mysqlProps.load(new FileInputStream(new File(resources.getPath() + "/db/mysql.properties")));
			fibu = new Buchhaltung(
					mysqlProps.getProperty("database"), 
					mysqlProps.getProperty("server"), 
					mysqlProps.getProperty("user"), 
					mysqlProps.getProperty("password"));
		} catch (FileNotFoundException e) {
			throw new RemoteException("mysql.properties nicht gefunden", e);
		} catch (IOException e) {
			throw new RemoteException("mysql.properties nicht lesbar", e);
		} catch (DatabaseException e) {
			throw new RemoteException("Kein Zugriff auf die Datenbank", e);
		}
	}

	public boolean isStarted() throws RemoteException {
		return fibu != null && fibu.ok();
	}

	public boolean isStartable() throws RemoteException {
		return true;
	}

	public void stop(boolean arg0) throws RemoteException {
		try {
			fibu.close();
			fibu = null;
		} catch (SQL_DBException e) {
			throw new RemoteException("Fehler beim Schlie�en der Datenbank", e);
		}
	}

	public String getName() throws RemoteException {
		return "Buchhaltung";
	}

	public Buchhaltung getFiBu() {
		return fibu;
	}
	
	public Konto getBilanzkonto() throws ApplicationException {
		try {
			String bilanz = fibu.getFirmenstammdaten().getFormatted("Bilanzkonto");
			return fibu.getKonto(bilanz);
		} catch (NotInitializedException e) {
			throw new ApplicationException("Fehler beim Lesen des Bilanzkontos", e);
		} catch (WrongTypeDBException e) {
			throw new ApplicationException("Fehler beim Lesen des Bilanzkontos", e);
		} catch (SQL_DBException e) {
			throw new ApplicationException("Fehler beim Lesen des Bilanzkontos", e);
		} catch (RecordNotExistsDBException e) {
			throw new ApplicationException("Fehler beim Lesen des Bilanzkontos", e);
		}
	}
}


//
// $Log: FibuService.java,v $
// Revision 1.3  2005/08/21 20:18:09  phormanns
// Erste Widgets f�r Buchen-Dialog
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//