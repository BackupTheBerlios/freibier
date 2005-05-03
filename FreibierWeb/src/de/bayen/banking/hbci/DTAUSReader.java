/* Erzeugt am 03.04.2005 von tbayen
 * $Id: DTAUSReader.java,v 1.3 2005/05/03 11:54:53 tbayen Exp $
 */
package de.bayen.banking.hbci;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Diese Klasse kann Informationen aus DTAUS-Dateien extrahieren.
 * 
 * Um diese Dateien zu erzeugen, gibt es bereits eine Klasse in HBCI4Java,
 * deshalb habe ich hier nur die Methoden implementiert, die ich benötigte,
 * um mir Daten über den Inhalt der DTAUS-Datei zu extrahieren. Es gibt
 * Informationen aus dem A-Satz (am Anfang der Datei), dem E-Satz (am Ende
 * der Datei) und den C-Sätzen (den eigentlichen Transaktions-Daten).
 * 
 * @author tbayen
 */
public class DTAUSReader {
	private byte[] dtaus = null;
	private List csatzoffsets;

	public DTAUSReader(byte[] dtaus) throws Exception {
		this.dtaus = new byte[dtaus.length];
		for (int i = 0; i < dtaus.length; i++) {
			this.dtaus[i] = dtaus[i];
		}
		csatzoffsets = calculateOffsets();
	}

	/**
	 * Hier werden die Offsets der C-Datensätze neu berechnet. Mit Hilfe dieser
	 * kann dann besser direkt auf Datensätze zugegriffen werden, um deren 
	 * Daten zu extrahieren.
	 * @throws Exception
	 */
	private List calculateOffsets() throws Exception {
		List offsets = new ArrayList();
		int offset = 128;
		while (extract(offset + 4, 1).equals("C")) {
			offsets.add(new Integer(offset));
			// Die Länge ist laut Spezifikation nicht die physikalische
			// Datensatzlänge, sondern nur die belegten Daten. Dies ist
			// die Grundlänge von 187 zzgl. 29 für jeden Erweiterungsteil
			int laenge = Integer.valueOf(extract(offset, 4)).intValue();
			int anz_erweiterungen = (laenge - 187) / 29;
			if (anz_erweiterungen != Integer.valueOf(
					extract(offset + 128 + 27 + 27 + 1 + 2, 2)).intValue()) {
				throw new Exception("Fehler in der DTAUS-Datei (im C-Satz)");
			}
			// Ein Satzabschnitt ist immer 128 Bytes lang. Zwei Abschnitte
			// gibt es immer. Die ersten beiden Erweiterungsteile passen noch
			// in den zweiten Abschnitt. Dann kommen immer vier Erweiterungen
			// pro Abschnitt.
			int anz_satzabschnitte = 2 + ((anz_erweiterungen + 1) / 4);
			offset += 128 * anz_satzabschnitte;
		}
		if (!extract(offset + 4, 1).equals("E")) {
			throw new Exception(
					"Fehler in der DTAUS-Datei (E-Satz nicht gefunden)");
		}
		return offsets;
	}

	public String getA_BLZ() {
		return extract(7, 8);
	}

	public String getA_Kontonummer() {
		Perl5Util re = new Perl5Util();
		String erg = extract(60, 10);
		re.match("/^[0 ]*([1-9].*$)/", erg); // führende Leerstellen und Nullen abschneiden
		return re.group(1);
	}

	public BigDecimal getE_Summe() {
		int endsatzoffset = (((int) (dtaus.length / 128)) - 1) * 128;
		return (new BigDecimal(extract(endsatzoffset + 64, 13))).divide(
				new BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY);
	}

	/**
	 * ergibt "L" oder "G" für Last- oder Gutschriften
	 * 
	 * @return String "L" oder "G"
	 */
	public String getA_LastOrGut() {
		return extract(5, 1);
	}

	public int getC_count() {
		return csatzoffsets.size();
	}

	public String getC_BLZ(int satz) {
		int offset = ((Integer) csatzoffsets.get(satz)).intValue();
		return extract(offset + 4 + 1 + 8, 8);
	}

	public String getC_Kontonummer(int satz) {
		int offset = ((Integer) csatzoffsets.get(satz)).intValue();
		return extract(offset + 4 + 1 + 8 + 8, 10);
	}

	public String getC_Textschluessel(int satz) {
		int offset = ((Integer) csatzoffsets.get(satz)).intValue();
		return extract(offset + 4 + 1 + 8 + 8 + 10 + 13, 2);
	}

	public String getC_Betrag(int satz) {
		int offset = ((Integer) csatzoffsets.get(satz)).intValue();
		return extract(offset + 4 + 1 + 8 + 8 + 10 + 13 + 2 + 3 + 1 + 11 + 8
				+ 10, 9)
				+ ","
				+ extract(offset + 4 + 1 + 8 + 8 + 10 + 13 + 2 + 3 + 1 + 11 + 8
						+ 10 + 9, 2);
	}

	public String getC_Empfaenger(int satz) {
		int offset = ((Integer) csatzoffsets.get(satz)).intValue();
		return extract(offset + 4 + 1 + 8 + 8 + 10 + 13 + 2 + 3 + 1 + 11 + 8
				+ 10 + 11 + 3, 27);
	}

	public List getC_Verwendungszweck(int satz) {
		int offset = ((Integer) csatzoffsets.get(satz)).intValue();
		List vwz = new ArrayList();
		vwz.add(extract(offset + 128 + 27, 27));
		int erweiterungsteile = Integer.valueOf(
				extract(offset + 128 + 27 + 27 + 1 + 2, 2)).intValue();
		if (erweiterungsteile > 0) {
			if (extract(offset + 128 + 27 + 27 + 1 + 2 + 2, 2).equals("02")) {
				// Diese Erweiterung ist ein Verwendungszweck
				vwz.add(extract(offset + 128 + 27 + 27 + 1 + 2 + 2 + 2, 27));
			}
		}
		if (erweiterungsteile > 1) {
			if (extract(offset + 128 + 27 + 27 + 1 + 2 + 2 + 27, 2)
					.equals("02")) {
				// Diese Erweiterung ist ein Verwendungszweck
				vwz.add(extract(
						offset + 128 + 27 + 27 + 1 + 2 + 2 + 2 + 27 + 2, 27));
			}
		}
		if (erweiterungsteile > 2) {
			if (extract(offset + 128 + 128, 2).equals("02")) {
				// Diese Erweiterung ist ein Verwendungszweck
				vwz.add(extract(offset + 128 + 128 + 2, 27));
			}
		}
		if (erweiterungsteile > 3) {
			if (extract(offset + 128 + 128 + 2 + 27, 2).equals("02")) {
				// Diese Erweiterung ist ein Verwendungszweck
				vwz.add(extract(offset + 128 + 128 + 2 + 27 + 2, 27));
			}
		}
		if (erweiterungsteile > 4) {
			if (extract(offset + 128 + 128 + 2 + 27 + 2 + 27, 2).equals("02")) {
				// Diese Erweiterung ist ein Verwendungszweck
				vwz.add(extract(offset + 128 + 128 + 2 + 27 + 2 + 27 + 2, 27));
			}
		}
		if (erweiterungsteile > 5) {
			if (extract(offset + 128 + 128 + 2 + 27 + 2 + 27 + 2 + 27, 2)
					.equals("02")) {
				// Diese Erweiterung ist ein Verwendungszweck
				vwz.add(extract(offset + 128 + 128 + 2 + 27 + 2 + 27 + 2 + 27
						+ 2, 27));
			}
		}
		// Ich dekodiere hier nur 7 VWZ-Zeilen. Im Moment reicht mir das.
		// Man kann das aber gerne erweitern, wenn das jemand braucht. Das
		// ginge bestimmt auch eleganter, als hier 15 gleichlautende Teile
		// hintereinanderzumachen, aber im Moment läufts so und fertig. :-)
		return vwz;
	}

	private String extract(int offset, int length) {
		if (offset + length > dtaus.length) {
			return null;
		}
		byte[] erg = new byte[length];
		for (int i = 0; i < erg.length; i++) {
			erg[i] = dtaus[i + offset];
			// Konvertierung in DIN 66003-Zeichensatz (wie es sich gehört):
			if (((int) erg[i]) + 256 == (int) '\133')
				erg[i] = (byte) 'Ä';
			int i1 = ((int) erg[i]) + 256;
			int i2 = (int) '\134';
			if (((int) erg[i]) + 256 == (int) '\134')
				erg[i] = (byte) 'Ö';
			if (((int) erg[i]) + 256 == (int) '\135')
				erg[i] = (byte) 'Ü';
			if (((int) erg[i]) + 256 == (int) '\173')
				erg[i] = (byte) 'ä';
			if (((int) erg[i]) + 256 == (int) '\174')
				erg[i] = (byte) 'ö';
			if (((int) erg[i]) + 256 == (int) '\175')
				erg[i] = (byte) 'ü';
			if (((int) erg[i]) + 256 == (int) '\176')
				erg[i] = (byte) 'ß';
			// Konvertierung des von DURST verwendeten Zeichensatzes:
			// (empirisch ermittelt)
			if (((int) erg[i]) + 256 == 9 * 16 + 9)
				erg[i] = (byte) 'Ö';
			if (((int) erg[i]) + 256 == 8 * 16 + 14)
				erg[i] = (byte) 'Ä';
			if (((int) erg[i]) + 256 == 9 * 16 + 10)
				erg[i] = (byte) 'Ü';
		}
		return new String(erg);
	}
}
/*
 * $Log: DTAUSReader.java,v $
 * Revision 1.3  2005/05/03 11:54:53  tbayen
 * Bei Dtausparse den Textschlüssel in den VWZ schreiben
 *
 * Revision 1.2  2005/04/19 17:17:04  tbayen
 * DTAUS-Dateien wieder einlesen in die Datenbank
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:09  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 */