/* Erzeugt am 03.04.2005 von tbayen
 * $Id: BankingUtils.java,v 1.2 2005/04/18 10:57:55 tbayen Exp $
 */
package de.bayen.banking.hbci;

import java.net.MalformedURLException;
import java.util.Map;
import org.kapott.hbci.manager.FileSystemClassLoader;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Hilfsfunktionen für den Umgang mit HBCI4Java.
 * 
 * Diese Klasse enthält nur statische Funktionen, die von Action-Klassen 
 * benutzt werden können, um HBCI-Verbindungen vorzubereiten.
 * 
 * @author tbayen
 */
public class BankingUtils {
	public static HBCIPassport makePassport(Map map,
			HBCICallbackWebinterface callback, WebDBDatabase db,
			ServletDatabase servlet) throws DatabaseException {
		try {
			String path = servlet.getProperty("configdir");
			// Parameter des HBCI-Kernels setzen
			HBCIUtils.init(new FileSystemClassLoader(),
			// "webdatabase" könnte man alternativ aus der URL extrahieren... 
					path + "banking-hbci.properties", callback);
			Integer konto = Integer.valueOf((String) map.get("konto"));
			if (konto != null) {
				Record record = db.getTable("Konten").getRecordByPrimaryKey(
						konto);
				if (record != null) {
					String medium = record.getFormatted("Sicherheitsmedium");
					HBCIUtils.setParam("client.passport.default", medium);
					if (medium.equals("PinTan")) {
						HBCIUtils.setParam("client.passport.PinTan.filename",
								path + "hbci4java-passports/"
										+ record.getFormatted("Kurzname")
										+ ".medium");
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// Nutzer-Passport initialisieren
		return AbstractHBCIPassport.getInstance();
	}

	public static HBCIHandler makeHandle(HBCIPassport passport) {
		// ein HBCI-Handle für einen Nutzer erzeugen
		String version = passport.getHBCIVersion();
		// Als Version wird in den HBCI4Java-Beispielen "210" genommen,
		// damit geht aber kein PinTan.
		return new HBCIHandler((version.length() != 0) ? version : "plus",
				passport);
	}
}
/*
 * $Log: BankingUtils.java,v $
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:09  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */