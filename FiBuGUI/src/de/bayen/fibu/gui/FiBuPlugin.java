// $Id: FiBuPlugin.java,v 1.1 2005/08/17 15:04:56 phormanns Exp $

package de.bayen.fibu.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

public class FiBuPlugin extends AbstractPlugin {

	public FiBuPlugin(File file) {
		super(file);
	}

	public void init() throws ApplicationException {
	}

	public void install() throws ApplicationException {
		PluginResources resources = Application.getPluginLoader().getPlugin(FiBuPlugin.class).getResources();
		Properties mysqlProps = new Properties();
		try {
			mysqlProps.load(new FileInputStream(new File(resources.getPath() + "/db/mysql.properties")));
			Buchhaltung fibu = new Buchhaltung(
					mysqlProps.getProperty("database"), 
					mysqlProps.getProperty("server"), 
					mysqlProps.getProperty("user"), 
					mysqlProps.getProperty("password"));
			fibu.firstTimeInit();
			fibu.close();
		} catch (FileNotFoundException e) {
			throw new ApplicationException("mysql.properties nicht gefunden", e);
		} catch (IOException e) {
			throw new ApplicationException("mysql.properties nicht lesbar", e);
		} catch (DatabaseException e) {
			throw new ApplicationException("Kein Zugriff auf die Datenbank", e);
		}
	}

	public void update(double oldVersion) throws ApplicationException {
	}

	public void shutDown() {
	}

}


//
// $Log: FiBuPlugin.java,v $
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//