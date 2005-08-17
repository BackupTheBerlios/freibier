// $Id: Settings.java,v 1.1 2005/08/17 15:04:56 phormanns Exp $

package de.bayen.fibu.gui;

import java.text.DecimalFormat;

import de.bayen.fibu.FibuService;
import de.willuhn.jameica.system.Application;

public class Settings {

	private static FibuService fibu;
	private static DecimalFormat jahr = new DecimalFormat("0000");
	private static DecimalFormat konto = new DecimalFormat("0000");
	private static DecimalFormat betrag = new DecimalFormat("#####0,00");
	
	public Settings() {
	}

	public static FibuService getFibuService() throws Exception {
		if (fibu == null) {
			fibu = (FibuService) Application.getServiceFactory().lookup(FiBuPlugin.class, "buchhaltung");
		} 
		return fibu;
	}
	
	public static DecimalFormat getKontoFormat() {
		return konto;
	}
	
	public static DecimalFormat getJahrFormat() {
		return jahr;
	}
	
	public static DecimalFormat getMoneyFormat() {
		return betrag;
	}
}


//
// $Log: Settings.java,v $
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//