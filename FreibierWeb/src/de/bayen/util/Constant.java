/* Erzeugt am 25.03.2005 von tbayen
 * $Id: Constant.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $
 */
package de.bayen.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Diese Basis-Klasse stellt Konstanten-Objekte zur Verfügung. Sie "wrappt" die 
 * int-Werte, die ansonsten dafür benutzt werden und ermöglicht ein 
 * komfortableres Arbeiten wie z.B. die Konvertierung zu/von Strings, etc.
 * <p>
 * Hiervon abgeleitete Klassen können einfach statische Objekte innerhalb der
 * Klasse erzeugen, die die erlaubten Konstanten-Werte repräsentieren.</p>
 * <p>
 * Beispiel: <code>	public static MyConstant ONE=new MyConstant(1,"ONE");</code>
 * </p>
 * Folgendes gehört in jede abgeleitete Klasse:
 * <code>
 * static staticMaps maps=new staticMaps();
 * protected staticMaps getMaps(){return maps;}
 * public static Constant fromInt(int val){
 *   return (Constant)(maps.valMap.get(new Integer(val)));}
 * public static Constant fromString(String desc){
 *   return (Constant)maps.descMap.get(desc);}
 * </code>
 * @author tbayen
 */
public abstract class Constant {
	protected int value;
	protected String description;

	public static class staticMaps {
		public Map valMap=new HashMap();
		public Map descMap=new HashMap();
	}

	/**
	 * Diese Methode muss von jeder abgeleiteten Klasse implementiert werden.
	 * Sie gibt der Basisklasse Zugriff auf die gespeicherten Listen mit
	 * den möglichen Konstanten-Objekten. Abgeleitete Klassen sollten
	 * folgendes enthalten:
	 * <code>
	 * 	static staticMaps maps=new staticMaps();
     *  protected staticMaps getMaps(){return maps;}
     * </code>
	 * 
	 * @return staticMaps
	 */
	protected abstract staticMaps getMaps();

	/**
	 * privater Konstruktor sorgt dafür, daß von aussen keine neuen Objekte
	 * erzeugt werden können. Es existieren nur die in der jeweils abgeleiteten
	 * Klasse erzeugten "public static" Objekte.
	 * <p>
	 * Ein Konstruktor, der selbständig Zahlenwerte vergibt, ist auch denkbar,
	 * aber bisher noch nicht implementiert.</p>
	 */
	protected Constant(int value, String description) {
		this.value=value;
		this.description=description;
		getMaps().valMap.put(new Integer(value),this);
		getMaps().descMap.put(description,this);
	}
	
	/**
	 * ergibt den Integer-Wert, den diese Konstante hat.
	 * 
	 * @return Konstanten-Wert als int
	 */
	public int toInt(){
		return value;
	}
	
	/**
	 * ergibt den String-Wert, der diese Konstante beschreibt.
	 * 
	 * @return Konstanten-Wert als String
	 */
	public String toString(){
		return description;
	}
	
	/**
	 * Diese Factory-Methode ergibt die zugehörige Konstante, wenn man den 
	 * Integer-Wert der Konstante kennt.
	 * 
	 * @param val - Integer-Wert, der in eine Konstante gewandelt werden soll
	 * @return HBCICallbackConstant
	 */
//	public static Constant fromInt(int val){
//		return (Constant)getMaps().valMap.get(new Integer(val));
//	}
	
	/**
	 * Diese Factory-Methode ergibt die zugehörige Konstante, wenn man den 
	 * Beschreibungs-String der Konstante kennt.
	 * 
	 * @param desc - String-Wert, der in eine Konstante umgewandelt werden soll
	 * @return HBCICallbackConstant
	 */
//	public static Constant fromString(String desc){
//		return (Constant)descMap.get(desc);
//	}
}

/*
 * $Log: Constant.java,v $
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/26 03:10:44  tbayen
 * Banking-Applikation kann per Chipkarte
 * Auszüge abholen und anzeigen
 *
 */