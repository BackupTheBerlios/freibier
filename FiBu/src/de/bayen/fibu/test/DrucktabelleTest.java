/* Erzeugt am 30.08.2005 von tbayen
 * $Id: DrucktabelleTest.java,v 1.1 2005/08/30 21:05:53 tbayen Exp $
 */
package de.bayen.fibu.test;

import de.bayen.fibu.util.Drucktabelle;
import junit.framework.TestCase;

public class DrucktabelleTest extends TestCase {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DrucktabelleTest.class);
	}

	/*
	 * Test method for 'de.bayen.fibu.util.Drucktabelle.Drucktabelle(String[], int[])'
	 */
	public void testDrucktabelle() {
		String spalten[]={"eins","zwei", "drei"};
		int breiten[]={5,10,10};
		Drucktabelle tab=new Drucktabelle(spalten,breiten);
		String texte[]={"abc","defgh","ijklm"};
		assertEquals("abc  |defgh     |ijklm     ",tab.printZeile(texte));
		tab.setAlign("zwei",Drucktabelle.RECHTSBUENDIG);
		tab.setAlign(2,Drucktabelle.ZENTRIERT);
		tab.setColumnWidth("eins",7);
		assertEquals("abc    |     defgh|  ijklm   ",tab.printZeile(texte));
		tab.setColumnWidth("drei",11);
		assertEquals("abc    |     defgh|   ijklm   ",tab.printZeile(texte));
	}
}

/*
 * $Log: DrucktabelleTest.java,v $
 * Revision 1.1  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 */