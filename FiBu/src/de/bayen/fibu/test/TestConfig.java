/* Erzeugt am 17.08.2005 von tbayen
 * $Id: TestConfig.java,v 1.2 2005/08/18 14:14:04 tbayen Exp $
 */
package de.bayen.fibu.test;

/*
 * In dieser Klasse konfiguriere ich das Verhalten meiner Tests.
 */
public class TestConfig {
	/*
	 * Um meine toString()-Funktionen zu testen, habe ich einige
	 * println()-Aufrufe in meine Tests eingebaut. Deren Ausgaben sind
	 * allerdings nicht Teil des offiziellen Test-Programms (ich "teste"
	 * sie nur ab und zu, indem ich sie persönlich in Augenschein nehme).
	 * Diese Ausgaben kann ich hier ein- und ausschalten. Wenn sie
	 * ausgeschaltet sind, nerven sie nicht in der Ausgabe herum und 
	 * Eclipse wechselt nicht vom JUnit-View in das Konsole-View.
	 */
	static public boolean print = true;
}

/*
 * $Log: TestConfig.java,v $
 * Revision 1.2  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.1  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 */