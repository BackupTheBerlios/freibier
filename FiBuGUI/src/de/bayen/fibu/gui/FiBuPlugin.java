// $Id: FiBuPlugin.java,v 1.4 2005/08/23 19:40:14 phormanns Exp $

package de.bayen.fibu.gui;

import java.io.File;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.util.ApplicationException;

public class FiBuPlugin extends AbstractPlugin {

	public FiBuPlugin(File file) {
		super(file);
	}

	public void init() throws ApplicationException {
	}

	public void install() throws ApplicationException {
		try {
			Buchhaltung fibu=new Buchhaltung(System.getProperty("user.home")+"/.fibu.properties");
			fibu.firstTimeInit();
			fibu.close();
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
// Revision 1.4  2005/08/23 19:40:14  phormanns
// Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
//
// Revision 1.3  2005/08/21 20:39:55  tbayen
// Datenbankparameter werden nacheinander an verschiedenen Quellen gesucht
//
// Revision 1.2  2005/08/21 17:11:11  tbayen
// kleinere Warnungen beseitigt
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//