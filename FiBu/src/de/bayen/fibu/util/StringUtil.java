/* Erzeugt am 29.08.2005 von tbayen
 * $Id: StringUtil.java,v 1.1 2005/08/30 21:05:53 tbayen Exp $
 */
package de.bayen.fibu.util;

import java.math.BigDecimal;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Hilfsfunktionen, um mit Strings zu arbeiten.
 * 
 * @author tbayen
 */
public class StringUtil {
	static private Perl5Util regex = new Perl5Util();
	static private String spacebuffer = " ";

	/**
	 * Gibt einen String aus, der aus der angegebenen Anzahl Leerstellen besteht.
	 */
	static public String spaces(int anzahl) {
		while (spacebuffer.length() < anzahl) {
			// reicht der Puffer nicht aus, wird er jeweils verdoppelt
			spacebuffer += spacebuffer;
		}
		return spacebuffer.substring(0, anzahl);
	}

	/**
	 * gibt einen String rechtsbündig mit der angegebenen Gesamtlänge aus.
	 * 
	 * @param str
	 * @param laenge
	 * @return formatierter String
	 */
	static public String right(String str, int laenge) {
		String erg = spaces(laenge) + str;
		return erg.substring(erg.length() - laenge);
	}

	/**
	 * gibt einen String linksbündig mit der angegebenen Gesamtlänge aus.
	 * 
	 */
	static public String left(String str, int laenge) {
		String erg = str + spaces(laenge);
		return erg.substring(0, laenge);
	}

	/**
	 * gibt einen String zentriert mit der angegebenen Gesamtlänge aus.
	 * 
	 */
	public static String center(String str, int laenge) {
		if (str.length() >= laenge)
			return str.substring(0, laenge);
		return spaces((laenge - str.length()) / 2) + str
				+ spaces((laenge - str.length() + 1) / 2);
	}

	/**
	 * formatiert eine Zahl mit Tausenderpunkten etc.
	 * @param number
	 * @return formatierter String
	 */
	static public String formatNumber(BigDecimal number) {
		String zahl = number.toString();
		String teile[] = zahl.split("\\.|,");
		String erg = regex.substitute("s/(\\d)(?=(?:\\d{3})+\\b)/$1./g",
				teile[0]);
		if (teile.length > 1) {
			erg += "," + (teile[1] + "00").substring(0, 2);
		} else {
			erg += ",00";
		}
		if (zahl.charAt(0) == '-')
			erg = "-" + erg;
		return erg;
	}

	/**
	 * Der angegebene String wird sooft vervielfältigt, wie angegeben und
	 * als ein neuer, langer String zurückgegeben. times('-',10) ergibt
	 * z.B. "----------".
	 * 
	 * @param string
	 * @param times
	 * @return vervielfältigter String
	 */
	public static String times(String string, int times) {
		String erg = "";
		for (int i = 0; i < times; i++) {
			erg += string;
		}
		return erg;
	}
}
/*
 * $Log: StringUtil.java,v $
 * Revision 1.1  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 */