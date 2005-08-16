/* Erzeugt am 16.08.2005 von tbayen
 * $Id: Betrag.java,v 1.1 2005/08/16 12:22:09 tbayen Exp $
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

	public Betrag(BigDecimal wert,boolean soll) {
		super();
		this.soll = soll;
		this.wert = wert;
	}

	public Betrag(BigDecimal wert,char soll) {
		super();
		this.wert = wert;
		setSollHaben(soll);
	}
	public Betrag(BigDecimal wert,String soll) {
		super();
		this.wert = wert;
		char array[]=soll.toCharArray();
		setSollHaben(array[0]);
	}
	
	public boolean isSoll() {
		return soll;
	}

	public char getSollHaben() {
		return soll ? 'S' : 'H';
	}

	public void setSollHaben(char soll) {
		this.soll = soll == 'S';
	}

	public BigDecimal getWert() {
		return wert;
	}

	public void setWert(BigDecimal wert) {
		this.wert = wert;
	}
	
	public String toString(){
		return getWert().toString()+getSollHaben();
	}
}
/*
 * $Log: Betrag.java,v $
 * Revision 1.1  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 */