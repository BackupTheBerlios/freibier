/* Erzeugt am 03.04.2005 von tbayen
 * $Id: DTAUSReader.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.banking.hbci;

import java.math.BigDecimal;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Diese Klasse kann Informationen aus DTAUS-Dateien extrahieren.
 * 
 * Um diese Dateien zu erzeugen, gibt es bereits eine Klasse in HBCI4Java,
 * deshalb habe ich hier nur die Methoden implementiert, die ich benötigte,
 * um mir Daten über den Inhalt der DTAUS-Datei zu extrahieren.
 * 
 * @author tbayen
 */
public class DTAUSReader {
	private byte[] dtaus = null;

	public DTAUSReader(byte[] dtaus) {
		this.dtaus = new byte[dtaus.length];
		for (int i = 0; i < dtaus.length; i++) {
			this.dtaus[i] = dtaus[i];
		}
	}

	public String getBLZ() {
		return extract(7, 8);
	}

	public String getKontonummer() {
		Perl5Util re = new Perl5Util();
		String erg = extract(60, 10);
		re.match("/^[0 ]*([1-9].*$)/", erg); // führende Leerstellen und Nullen abschneiden
		return re.group(1);
	}

	public BigDecimal getSumme() {
		int endsatzoffset = (((int) (dtaus.length / 128)) - 1) * 128;
		return (new BigDecimal(extract(endsatzoffset + 64, 13))).divide(
				new BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY);
	}
	
	/**
	 * ergibt "L" oder "G" für Last- oder Gutschriften
	 * 
	 * @return String "L" oder "G"
	 */
	public String getLastOrGut(){
		return extract(5,1);
	}

	private String extract(int offset, int length) {
		if (offset + length > dtaus.length) {
			return null;
		}
		byte[] erg = new byte[length];
		for (int i = 0; i < erg.length; i++) {
			erg[i] = dtaus[i + offset];
		}
		return new String(erg);
	}
}
/*
 * $Log: DTAUSReader.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:09  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */