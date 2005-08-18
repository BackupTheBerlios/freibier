// $Id: Settings.java,v 1.2 2005/08/18 11:24:12 phormanns Exp $

package de.bayen.fibu.gui;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import de.bayen.fibu.FibuService;
import de.willuhn.jameica.system.Application;

public class Settings {

	private static FibuService fibu;
	private static DecimalFormat jahr = new DecimalFormat("0000");
	private static DecimalFormat konto = new DecimalFormat("0000");
	private static DecimalFormat betrag = new DecimalFormat("#####0,00");
	private static DateFormat datum = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
	
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
	
	public static DateFormat getDateFormat() {
		return datum;
	}
}


//
// $Log: Settings.java,v $
// Revision 1.2  2005/08/18 11:24:12  phormanns
// Neue FiBu Version von Thomas
// Anzeige Journal-Listen
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//