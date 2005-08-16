/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Betrag.java,v 1.2 2005/08/16 21:11:47 tbayen Exp $
 */
package de.bayen.fibu;

import java.math.BigDecimal;

/**
 * Ein Betrag in der Buchhaltung besteht aus einem (positiven oder negativen) 
 * Zahlenwert und der Angabe, ob es sich um einen Soll- oder einen Habenwert
 * handelt.
 * <p>
 * Diese Klasse enthält auch alle Methoden, um mit Beträgen umzugehen (rechnen,
 * saldieren, etc.)
 * </p>
 * 
 * @author tbayen
 */
public class Betrag {
	private BigDecimal wert;
	private boolean soll;

	// Konstruktoren
	public Betrag() {
		this.soll = true;
		this.wert = new BigDecimal("0.00");
	}

	public Betrag(Betrag original) {
		this.soll = original.soll;
		this.wert = new BigDecimal(original.wert.unscaledValue(), original.wert
				.scale());
	}

	public Betrag(BigDecimal wert, boolean soll) {
		this.soll = soll;
		this.wert = wert;
	}

	public Betrag(BigDecimal wert, char soll) {
		this.wert = wert;
		setSollHaben(soll);
	}

	public Betrag(BigDecimal wert, String soll) {
		this.wert = wert;
		char array[] = soll.toCharArray();
		setSollHaben(array[0]);
	}

	// Zugriffsfunktionen
	public boolean isSoll() {
		return soll;
	}

	public char getSollHaben() {
		return soll ? 'S' : 'H';
	}

	public void setSollHaben(char soll) {
		this.soll = soll == 'S';
	}

	public void setSoll(boolean soll) {
		this.soll = soll;
	}

	public BigDecimal getWert() {
		return wert;
	}

	public void setWert(BigDecimal wert) {
		this.wert = wert;
	}

	public String toString() {
		return getWert().toString() + getSollHaben();
	}

	// Rechenfunktionen
	/**
	 * ergibt die kaufmännische Summe der beiden Werte
	 */
	public Betrag add(Betrag wert) {
		if (isSoll() == wert.isSoll()) {
			return new Betrag(getWert().add(wert.getWert()), isSoll());
		} else {
			if (getWert().abs().compareTo(wert.getWert().abs()) > 0) {
				// abs(this) > abs(wert)
				return new Betrag(getWert().subtract(wert.getWert()), isSoll());
			} else {
				return new Betrag(wert.getWert().subtract(getWert()), !isSoll());
			}
		}
	}
	
	public Betrag negate(){
		Betrag val = new Betrag(this);
		val.setSoll(!val.isSoll());
		return val;
	}

	public boolean equals(Betrag wert) {
		return add(wert.negate()).getWert().unscaledValue().intValue()==0;
	}
}
/*
 * $Log: Betrag.java,v $
 * Revision 1.2  2005/08/16 21:11:47  tbayen
 * Buchungszeilen werden gespeichert
 *
 * Revision 1.1  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 */