
/*  $Id: Saldo.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2004  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.structures;

import java.io.Serializable;
import java.util.Date;

import org.kapott.hbci.manager.HBCIUtils;

/** Darstellung eines Saldos. Anders als bei der Darstellung als
    einfacher Wert wird hier der <em>absolute</em> Betrag des Wertes
    gespeichert. Es gibt ein separates Kennzeichen für die
    Unterscheidung zwischen Soll und Haben. */
public final class Saldo
    implements Serializable
{
    /** Indikator für Credit/Debit (Haben/Soll). Der Wert "<code>C</code>"
        steht für <code>Credit</code> (Haben, also aus Sicht des
        Kontoinhabers ein Guthaben), der Wert "<code>D</code>" steht
        für <code>Debit</code> (aus Sicht des Kontoinhabers eine
        Schuld) */
    public String cd;
    /** Absoluter Betrag des Saldos. Unabhängig davon, ob es sich
        um ein Guthaben oder eine Schuld handelt, ist der in diesem
        Objekt gespeicherte Wert immer positiv (oder 0). */
    public Value  value;
    /** Zeitpunkt der Gültigkeit dieses Saldos. */
    public Date   timestamp;

    /** Anlegen eines neuen Saldo-Objektes */
    public Saldo()
    {
        value=new Value();
    }

    /** Umwandeln des Saldos in eine String-Darstellung. Das Format ist dabei folgendes:
        <pre>&lt;timestamp> ["+"|"-"] &lt;value></pre>
        @return Stringdarstellung des Saldos */
    public String toString()
    {
        return HBCIUtils.date2String(timestamp)+" "+
               HBCIUtils.time2String(timestamp)+" "+
               (cd==null?"":(cd.charAt(0)=='C'?"+":"-"))+value.toString();
    }
}
