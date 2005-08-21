// $Id: FibuService.java,v 1.2 2005/08/21 17:09:50 tbayen Exp $

package de.bayen.fibu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SysDBEx;
import de.bayen.fibu.gui.FiBuPlugin;
import de.willuhn.datasource.Service;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;

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
		} catch (SysDBEx e) {
			throw new RemoteException("Fehler beim Schließen der Datenbank", e);
		}
	}

	public String getName() throws RemoteException {
		return "Buchhaltung";
	}

	public Buchhaltung getFiBu() {
		return fibu;
	}
}


//
// $Log: FibuService.java,v $
// Revision 1.2  2005/08/21 17:09:50  tbayen
// Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//