
/*  $Id: Value.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

import org.kapott.hbci.manager.HBCIUtils;

/** Darstellung eines Geldbetrages. */
public final class Value
    implements Serializable
{
    /** Numerischer Wert des Betrages */
    public double value;
    /** Währung. Für EURO ist hier <code>EUR</code> zu benutzen. */
    public String curr;
    
    /** Anlegen eines neuen Objektes zur Aufnahme eines Geldbetrages. Die Währung wird
        mit <code>EUR</code> vorbelegt */
    public Value()
    {
        curr="EUR";
    }
    
    /** Anlegen eines Geldbetrag-Objektes. Die Währung wird mit <code>EUR</code> vorbelegt.
        @param value der Geldbetrag */
    public Value(double value)
    {
        this(value,"EUR");
    }
    
    /** Anlegen eines Geldbetrag-Objektes. Die Währung wird mit <code>EUR</code> vorbelegt.
        @param value der Geldbetrag als String */
    public Value(String value)
    {
        this(value,"EUR");
    }
    
    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag 
        @param curr die Währung des Geldbetrages */
    public Value(double value,String curr)
    {
        this.value=value;
        this.curr=curr;
    }
    
    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag als String 
        @param curr die Währung des Geldbetrages */
    public Value(String value,String curr)
    {
        this(HBCIUtils.string2Value(value),curr);
    }

    /** Erstellt eine neue Instanz eines Geldbetrag-Objektes als Kopie
        eines bestehenden Objektes.
        @param v ein Objekt, welches geklont werden soll */
    public Value(Value v)
    {
        this(v.value,v.curr);
    }

    /** Umwandeln in einen String. Die Rückgabe erfolgt im Format 
        <pre>&lt;value> " " &lt;curr></pre>
        @return Stringdarstellung des Geldbetrages */
    public String toString()
    {
        return HBCIUtils.value2String(value)+" "+curr;
    }
}
