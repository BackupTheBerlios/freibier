/* Erzeugt am 30.08.2005 von tbayen
 * $Id: Drucktabelle.java,v 1.1 2005/08/30 21:05:53 tbayen Exp $
 */
package de.bayen.fibu.util;

import java.util.Map;

/**
 * Eine Drucktabelle enthält Informationen, um Daten in einer Tabellenform
 * formatiert ausgeben zu können.
 * 
 * @author tbayen
 */
public class Drucktabelle {
	// Daten über die einzelnen Spalten:
	private String spalten[];
	private int breiten[];
	private int align[];
	// Werte für align:
	static public final int LINKSBUENDIG = -1;
	static public final int RECHTSBUENDIG = 1;
	static public final int ZENTRIERT = 0;
	// Werte, die die ganze Tabelle betreffen:
	private String trenner = "|";

	/**
	 * Die Drucktabelle wird eingerichtet. Die Spalten der Tabelle bekommen
	 * Namen, die als erster Parameter angegeben werden und jede Spalte bekommt
	 * eine feste Breite für die Ausgabe mit einem fixed font.
	 */
	public Drucktabelle(String spalten[], int breiten[]) {
		this.spalten = spalten;
		this.breiten = breiten;
		this.align = new int[spalten.length];
		for (int i = 0; i < align.length; i++) {
			align[i] = LINKSBUENDIG;
		}
	}

	/**
	 * Die Drucktabelle wird eingerichtet. Neben Spaltennamen und Breite kann
	 * hier auch die Bündigkeit (Alignment) bei der Konstruktion angegeben werden.
	 * @param spalten
	 * @param breiten
	 */
	public Drucktabelle(String spalten[], int breiten[], int align[]) {
		this.spalten = spalten;
		this.breiten = breiten;
		this.align = align;
	}

	/**
	 * interne Methode, um zu einem Spaltennamen den Spaltenindex herauszufinden.
	 * @param name
	 * @return
	 */
	private int name2Nummer(String name) {
		for (int i = 0; i < spalten.length; i++) {
			if (spalten[i].equals(name))
				return i;
		}
		throw new IndexOutOfBoundsException(
				"Spalte existiert in der Drucktabelle nicht");
	}

	// Werte, die jeweils eine Spalte betreffen:
	/**
	 * Die Breite einer einzelnen Spalte wird geändert. Dabei wird die Nummer
	 * der Spalte angegeben (die erste Spalte links hat die Nummer 0).
	 * 
	 * @param spalte
	 * @param wert
	 */
	public void setColumnWidth(int spalte, int wert) {
		breiten[spalte] = wert;
	}

	/* Die Breite einer einzelnen Spalte wird geändert. Dabei wird der Name der
	 * Spalte angegeben.
	 * 
	 * @param name
	 * @param wert
	 */
	public void setColumnWidth(String name, int wert) {
		setColumnWidth(name2Nummer(name), wert);
	}

	/**
	 * Setzt die Ausrichtung der Spalte. Erlaubte Werte sind
	 * LINKSBUENDIG, RECHTSBUENDIG und ZENTRIERT.
	 * 
	 * @param spalte
	 * @param align
	 */
	public void setAlign(int spalte, int align) {
		this.align[spalte] = align;
	}

	/**
	 * Setzt die Ausrichtung der Spalte. Erlaubte Werte sind
	 * LINKSBUENDIG, RECHTSBUENDIG und ZENTRIERT.
	 * 
	 * @param name
	 * @param align
	 */
	public void setAlign(String name, int align) {
		setAlign(name2Nummer(name), align);
	}

	// globale Werte für die ganze Tabelle:
	/**
	 * Das Trennzeichen, das zwischen den einzelnen Spalten ausgegeben wird,
	 * kann hiermit geändert werden.
	 * 
	 * @param trenner
	 */
	public void setTrenner(String trenner) {
		this.trenner = trenner;
	}

	/**
	 * Gibt eine Zeile der Tabelle aus, die die angegebenen Strings als
	 * Werte enthält.
	 * 
	 * @param werte
	 * @return Textstring, der die Tabellenzeile enthält
	 */
	public String printZeile(String werte[]) {
		String erg = "";
		for (int i = 0; i < werte.length; i++) {
			String wert = werte[i];
			if (wert == null) {
				wert = StringUtil.left("", breiten[i]);
			} else if (align[i] == LINKSBUENDIG) {
				wert = StringUtil.left(wert, breiten[i]);
			} else if (align[i] == RECHTSBUENDIG) {
				wert = StringUtil.right(wert, breiten[i]);
			} else if (align[i] == ZENTRIERT) {
				wert = StringUtil.center(wert, breiten[i]);
			}
			erg += wert;
			if (i + 1 < werte.length)
				erg += trenner;
		}
		return erg;
	}

	/**
	 * Gibt eine Zeile der Tabelle aus. Die Werte für die Tabellenzellen stehen
	 * als String in einem Hash, dessen Schlüssel jeweils der Spaltenname ist.
	 */
	public String printZeile(Map map) {
		String erg = "";
		for (int i = 0; i < spalten.length; i++) {
			String wert = (String) map.get(spalten[i]);
			if (wert == null) {
				wert = StringUtil.left("", breiten[i]);
			} else if (align[i] == LINKSBUENDIG) {
				wert = StringUtil.left(wert, breiten[i]);
			} else if (align[i] == RECHTSBUENDIG) {
				wert = StringUtil.right(wert, breiten[i]);
			} else if (align[i] == ZENTRIERT) {
				wert = StringUtil.center(wert, breiten[i]);
			}
			erg += wert;
			if (i + 1 < spalten.length)
				erg += trenner;
		}
		return erg;
	}

	/**
	 * Gibt eine Überschriftszeile für die Tabelle aus. Ist der
	 * Parameter true, wird auch noch eine Trennzeile ausgegeben.
	 */
	public String printUeberschrift(boolean trennzeile) {
		String erg = printZeile(spalten);
		if (trennzeile) {
			erg+="\n";
			if (trenner.equals("|")) {
				// Bei dem Original-Trenner mache ich es besonders hübsch
				// und füge "+" ein.
				for (int i = 0; i < spalten.length; i++) {
					erg += StringUtil.times("-", breiten[i]);
					if (i + 1 < spalten.length)
						erg += "+";
				}
			} else {
				erg += StringUtil.times("-", erg.length());
			}
		}
		return erg;
	}
}
/*
 * $Log: Drucktabelle.java,v $
 * Revision 1.1  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 */