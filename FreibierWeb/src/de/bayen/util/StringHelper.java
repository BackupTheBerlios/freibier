package de.bayen.util;
/* Erzeugt am 18.01.2006 von tbayen
 * $Id: StringHelper.java,v 1.1 2006/01/21 23:10:10 tbayen Exp $
 */
/**
 * Diese Klasse sammelt einige Konvertierungsfunktionen, die man immer mal wieder benötigt,
 * , die es aber z.B. in der String-Klasse nicht gibt. 
 * 
 * @author tbayen
 */
import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * Diese Klasse enthält Hilfsfunktionen zum Umgang mit Strings wie z.B. 
 * Konvertierungsfunktionen, die die normalen Java-Klassen nicht hergeben.
 * 
 * @author tbayen
 */
public class StringHelper {
	/**
	 * Die toString()-Methode von BigInteger ist grauenhaft, was vernünftige Formatierung
	 * angeht. Diese Methode hier erlaubt, Ausgaben so zu formatieren, wie man das will.
	 * 
	 * Im Moment erzeugt sie nur deutsches Ausgabeformat. Dies sollte aber bei Bedarf 
	 * angepasst werden können.
	 * 
	 * @param zahl
	 * @param nachkommastellen
	 * @return String-Repräsentation im Format "#.##0,00"
	 */
	public static String BigDecimal2String(BigDecimal zahl,int nachkommastellen) {
		String result="";
		BigInteger zehn = new BigInteger("10");  // um das nicht in der Schleife zu erzeugen
		BigInteger zero = new BigInteger("0");
		BigInteger zahlenwert=zahl.movePointRight(nachkommastellen).toBigInteger().abs();
		do {
			result=zahlenwert.mod(zehn).toString()+result;
			zahlenwert=zahlenwert.divide(zehn);
		}while(zahlenwert.compareTo(zero)!=0);
		while(result.length()<=nachkommastellen)
			result="0"+result;
		String linksohnepunkte=result.substring(0, result.length()-nachkommastellen);
		String rechts=result.substring(result.length()-nachkommastellen);
		// jetzt die Tausenderpunkte setzen:
		String links="";
		while(linksohnepunkte.length()>3) {
			links="."+linksohnepunkte.substring(linksohnepunkte.length()-3)+links;
			linksohnepunkte=linksohnepunkte.substring(0,linksohnepunkte.length()-3);
		};
		links=linksohnepunkte+links;
		if(zahl.compareTo(new BigDecimal(0))<0)
			links="-"+links;
		// Ausgabe (ggf. mit/ohne Komma)
		if(nachkommastellen>0) {
			return links+","+rechts;
		}else{
			return links;
		}
	}
}

/*
 * $Log: StringHelper.java,v $
 * Revision 1.1  2006/01/21 23:10:10  tbayen
 * Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
 *
 */