/* Erzeugt am 27.03.2005 von tbayen
 * $Id: BLOB.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import java.io.IOException;
import sun.misc.BASE64Encoder;
import sun.misc.UUDecoder;
import sun.misc.UUEncoder;

/**
 * Ein "Binary Large Object", also ein Block von Binärdaten. Im Prinzip
 * handelt es sich um eine Klasse, die dem nativen Typ "byte[]" entspricht.
 * BLOBs können z.B. in einer Datenbank in einer BLOB-Spalte abgelegt
 * werden. Sie können Binärdaten wie Bilder, sonstige Dateien oder auch
 * Strings, die nicht konvertiert werden sollen, enthalten.
 * <p>
 * Ich benutze diesen Objekttyp, um BLOBs in meiner Datenbankklasse zu
 * verwalten. Da er dort durch eine Klasse TypeDefinitionBLOB dargestellt
 * wird, habe ich mich entschlossen, diese Klasse hier ins gleiche Paket
 * zu stecken, obwohl sie eigentlich auch ganz unabhängig für sich stehen
 * könnte.</p>
 * 
 * @author tbayen
 */
public class BLOB {
	private byte[] data;

	public BLOB() {
		data = new byte[0];
	}

	/**
	 * Konstruktor, dem ein Bytearray übergeben wird
	 */
	public BLOB(byte[] blobdata) {
		if (blobdata == null) {
			data = new byte[0];
		} else {
			data = new byte[blobdata.length];
			// TODO: gibts hier kein memcpy oder so?
			for (int i = 0; i < blobdata.length; i++) {
				data[i] = blobdata[i];
			}
		}
	}

	/**
	 * ergibt die Größe des BLOBs in Bytes.
	 * @return byteanzahl
	 */
	public int length() {
		return data.length;
	}

	/**
	 * Gibt den Inhalt des BLOBs in uucode-Format aus.
	 * 
	 * @return String (uucodiert)
	 */
	public String toUUCode() {
		// UUEncode und UUDecode sind undokumentierte Klassen im SDK von
		// Sun (ja, sowas gibts anscheinend!) Infos dazu unter:
		// - http://www.geocities.com/herong_yang/data/uuencode.html
		// - http://www.cs.duke.edu/csed/java/src1.3/sun/misc
		// Diese Klassen implementieren die "CharacterDecoder"- und
		// "CharacterEncoder"-Schnittstelle. Dort gibts also Doku dazu.
		UUEncoder encoder = new UUEncoder();
		return encoder.encode(data);
	}

	public void fromUUCode(String uutext) throws IOException {
		UUDecoder decode = new UUDecoder();
		data = decode.decodeBuffer(uutext);
	}

	private char[] byte2hex(byte b) {
		int val = b;
		if (val < 0) {
			val += 256;
		}
		char[] erg = new char[2];
		erg[0] = (char) (val / 16 + '0');
		if (erg[0] > '9') {
			erg[0] += (char) ('a' - 10 - '0');
		}
		erg[1] = (char) (val % 16 + '0');
		if (erg[1] > '9') {
			erg[1] += (char) ('a' - 10 - '0');
		}
		return erg;
	}

	public String toSQL() {
		StringBuffer sql = new StringBuffer(data.length * 2 + 2);
		if (data.length == 0) {
			return "''";
		} else {
			sql.append("0x");
			for (int i = 0; i < data.length; i++) {
				sql.append(byte2hex(data[i]));
			}
			return new String(sql);
		}
	}

	/**
	 * Da man ein BLOB eigentlich per Definition nicht als String
	 * ausgeben kann, habe ich mich entschieden, den BLOB hier
	 * uucode-kodiert auszugeben. Das bedeutet einerseits, daß der
	 * eventuelle Inhalt nun gar nicht mehr zu lesen ist, aber
	 * andererseits ist die Ausgabe immer ein String, immer eindeutig
	 * und im Zweifelsfall auch zurückzucodieren.
	 * <p>
	 * Statt uucode hätte ich auch Base64 wählen können. Das ist
	 * Geschmacksfrage (ich bin halt altmodisch) und kann geändert 
	 * werden, wenns einen Grund dafür gebe sollte.</p>
	 * @return String (uucodiert)
	 */
	public String toString() {
		return toUUCode();
	}

	/**
	 * Ergibt den Inhalt des BLOBs als byte[].
	 * 
	 * @return byte[]
	 */
	public byte[] toByteArray() {
		return data;
	}

	/**
	 * Gibt die Daten Base64-codiert aus.
	 * 
	 * @return String
	 */
	public String toBase64() {
		BASE64Encoder e = new BASE64Encoder();
		return e.encode(data);
	}

	/**
	 * Liest einen codierten String wieder ein. Es ist garantiert, daß
	 * ein mit toString() codierter String hier wieder zurückgelesen
	 * werden kann. Im Moment bedeutet das, daß hier uucode-kodierte
	 * Strings gelesen werden.
	 * <p>
	 * Im Prinzip könnte das aber auch erweitert werden, so daß andere
	 * Codierungen automatisch erkannt werden.</p>
	 * 
	 * @param coded
	 * @throws IOException - String kann nicht dekodiert werden
	 */
	public void parseString(String coded) throws IOException {
		fromUUCode(coded);
	}
}
/*
 * $Log: BLOB.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.4  2005/04/05 21:14:11  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/28 16:43:57  tbayen
 * Daten können jetzt auch null enthalten
 *
 * Revision 1.2  2005/03/28 15:53:03  tbayen
 * Boolean-Typen eingeführt
 *
 * Revision 1.1  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 */