/* Erzeugt am 25.03.2005 von tbayen
 * $Id: HBCICallbackWebinterface.java,v 1.1 2006/01/24 00:26:00 tbayen Exp $
 */
package de.bayen.banking.hbci;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import de.bayen.util.Constant;

/**
 * Diese Klasse fängt alle Callbacks ab, die HBCI4Java erzeugt und 
 * beantwortet sie adäquat. HBCI4Java kommuniziert mit dem Benutzer
 * ausschließlich über diese Callbacks, die z.B. nach dem Passwort
 * fragen, etc. Alle diese Fragen werden in dieser Klasse sinnvoll
 * beantwortet und Statusmeldungen werden geloggt.
 * <p>
 * Die Klasse ist also in Wirklichkeit nicht für ein Webinterface
 * optimiert, sondern für "gar kein" Interface (obwohl sich das noch
 * ändern kann).</p>
 * 
 * @author tbayen
 */
public class HBCICallbackWebinterface implements HBCICallback {
	static Logger logger = Logger.getLogger(HBCICallbackWebinterface.class
			.getName());
	// um meine eigenen von den Logs von HBCI4Java unterscheiden zu können, 
	// benutze ich einen zweiten Logger:
	static Logger hbcilogger = Logger.getLogger(HBCICallback.class.getName());
	private String logCache = "";
	private String pin = null;
	private String tan = null;
	private Map parameter;
	private Map logLevelMapping;

	/**
	 * Der Konstruktor initialisiert einige statische Werte.
	 */
	public HBCICallbackWebinterface(Map parameter) {
		super();
		this.parameter = parameter;
		// Die folgenden Maps sollten sowas wie statische Hashes sein, aber 
		// das gibt's in Java nicht.
		// in diesem Hash lege ich fest, welche HBCI-Loglevel ich mit
		// welchem Loglevel in Log4J ausgebe.
		logLevelMapping = new HashMap();
		logLevelMapping.put(new Integer(HBCIUtils.LOG_DEBUG), Level.DEBUG);
		logLevelMapping.put(new Integer(HBCIUtils.LOG_DEBUG2), Level.DEBUG);
		logLevelMapping.put(new Integer(HBCIUtils.LOG_ERR), Level.ERROR);
		logLevelMapping.put(new Integer(HBCIUtils.LOG_INFO), Level.INFO);
		logLevelMapping.put(new Integer(HBCIUtils.LOG_NONE), Level.OFF);
		logLevelMapping.put(new Integer(HBCIUtils.LOG_WARN), Level.WARN);
	}

	/**
	 * Die Log-Meldungen, die HBCI5Java ausgibt, werden auf mein Logging
	 * per Log4j umgeleitet (also im Normalfall nach 
	 * <code>/var/log/tomcat4/webdatabase.log</code> geschrieben). 
	 * Zusätzlich werden sie intern gecached und können am Ende eines
	 * HBCI-Requests als ein String abgefragt werden, um sie z.B. in einem
	 * Logfenster innerhalb der HTML-Ausgabe ausgeben zu können.
	 * 
	 * @see org.kapott.hbci.callback.HBCICallback#log(java.lang.String, int, java.util.Date, java.lang.StackTraceElement)
	 */
	public void log(String message, int level, Date date,
			StackTraceElement trace) {
		try {
			hbcilogger.log((Level) (logLevelMapping.get(new Integer(level))),
					message);
			logCache += "Message: " + message + "\n";
		} catch (Exception e) {
			// Das muss unbedingt abgefangen werden, da sonst die Exception
			// evtl. an den JNI-Kartentreiber zurückgeht, der diese nicht
			// richtig verarbeitet und mit seinem "Signal 11" den ganzen
			// Tomcat kommentarlos in den Abgrund reisst. :-(
			logger.error("Exception im Callback");
		}
	}

	/**
	 * Hiermit kann die PIN angegeben werden, die ggf. benutzt wird, wenn
	 * danach gefragt wird.
	 * 
	 * @param pin The pin to set.
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * Hiermit kann eine TAN angegeben werden, die ggf. benutzt wird, sobald
	 * eine benötigt wird.
	 * 
	 * @param tan The tan to set.
	 */
	public void setTan(String tan) {
		this.tan = tan;
	}

	/**
	 * Hier kann abgefragt werden, ob bereits eine TAN vorliegt bzw. ob diese
	 * schon benutzt wurde.
	 * 
	 * @return true bedeutet gültige TAN liegt vor
	 */
	public boolean isTan() {
		return tan != null;
	}

	/*
	 * @see org.kapott.hbci.callback.HBCICallback#callback(org.kapott.hbci.passport.HBCIPassport, int, java.lang.String, int, java.lang.StringBuffer)
	 */
	public void callback(HBCIPassport passport, int reason, String message,
			int datatype, StringBuffer retData) {
		try {
			Constant cbreason = HBCICallbackConstant.fromInt(reason);
			logger.debug("callback: " + cbreason.toString() + " - " + message);
			logCache += "callback: " + cbreason.toString() + " - " + message
					+ "\n";
			// Zuerstmal Meldungen aussortieren, die ich in einer Webapplikation
			// definitiv uninteressant finde:
			switch (reason) {
			case NEED_CHIPCARD:
			case HAVE_CHIPCARD:
			case NEED_CONNECTION:
			case CLOSE_CONNECTION:
				return;
			default:
				break;
			}
			if (reason == NEED_PASSPHRASE_LOAD
					|| reason == NEED_PASSPHRASE_SAVE) {
				// mit _SAVE werden neue Passports angelegt.
				// Bei Chipkarten ist das sinnvoll, das auch von alleine zu machen,
				// weil diese Passports gar keine wichtigen Informationen enthalten
				retData.delete(0, retData.length());
				retData.append(parameter.get("passportpassword"));
			} else if (reason == NEED_PT_PIN) {
				retData.delete(0, retData.length());
				retData.append(pin);
			} else if (reason == NEED_PT_TAN) {
				if (tan != null) {
					retData.delete(0, retData.length());
					retData.append(tan);
					tan = null;
				}
			} else {
				logger.error("unbekannter Callback aufgerufen: "
						+ cbreason.toString() + " - " + message);
			}
		} catch (Exception e) {
			// Das muss unbedingt abgefangen werden, da sonst die Exception
			// evtl. an den JNI-Kartentreiber zurückgeht, der diese nicht
			// richtig verarbeitet und mit seinem "Signal 11" den ganzen
			// Tomcat kommentarlos in den Abgrund reisst. :-(
			logger.error("Exception im Callback");
		}
	}

	/*
	 * @see org.kapott.hbci.callback.HBCICallback#status(org.kapott.hbci.passport.HBCIPassport, int, java.lang.Object[])
	 */
	public void status(HBCIPassport passport, int statusTag, Object[] o) {
		try {
			Constant status = HBCIStatusConstant.fromInt(statusTag);
			//String info = (o == null || o.length == 0 || o[0] == null ? ""
			//		: o[0].toString());
			logger.info("status '" + status.toString() + "'");
			logCache += "status '" + status.toString() + "'" + "\n";
			//logger.debug("Info: " + info + "...");  // Achtung, macht Binärdaten!
		} catch (Exception e) {
			// Das muss unbedingt abgefangen werden, da sonst die Exception
			// evtl. an den JNI-Kartentreiber zurückgeht, der diese nicht
			// richtig verarbeitet und mit seinem "Signal 11" den ganzen
			// Tomcat kommentarlos in den Abgrund reisst. :-(
			logger.error("Exception im Callback");
		}
	}

	/*
	 * @see org.kapott.hbci.callback.HBCICallback#status(org.kapott.hbci.passport.HBCIPassport, int, java.lang.Object)
	 */
	public void status(HBCIPassport passport, int statusTag, Object o) {
		try {
			Constant status = HBCIStatusConstant.fromInt(statusTag);
			//String info = (o == null ? "" : o.toString());
			logger.info("status '" + status.toString() + "'");
			logCache += "status '" + status.toString() + "'" + "\n";
			//logger.debug("Info: " + info + "...");  // Achtung, macht Binärdaten!
		} catch (Exception e) {
			// Das muss unbedingt abgefangen werden, da sonst die Exception
			// evtl. an den JNI-Kartentreiber zurückgeht, der diese nicht
			// richtig verarbeitet und mit seinem "Signal 11" den ganzen
			// Tomcat kommentarlos in den Abgrund reisst. :-(
			logger.error("Exception im Callback");
		}
	}

	public String getLog() {
		return logCache;
	}
}
/*
 * $Log: HBCICallbackWebinterface.java,v $
 * Revision 1.1  2006/01/24 00:26:00  tbayen
 * Erste eigenständige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
 * Revision 1.2  2005/08/12 22:57:11  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.3  2005/04/05 21:14:09  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.2  2005/03/26 18:52:58  tbayen
 * Kontoauszug per PinTan abgeholt
 *
 * Revision 1.1  2005/03/26 03:10:44  tbayen
 * Banking-Applikation kann per Chipkarte
 * Auszüge abholen und anzeigen
 *
 */