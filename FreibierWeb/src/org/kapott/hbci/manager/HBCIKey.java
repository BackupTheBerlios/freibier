
/*  $Id: HBCIKey.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.manager;

import java.io.Serializable;
import java.security.Key;

/** Diese Klasse repräsentiert einen von <em>HBCI4Java</em>verwendeten Schlüssel.
    Ein solcher HBCI-Schlüssel besteht aus
    administrativen Daten zu diesem Schlüssel (Besitzer, Version) sowie den eigentlichen
    kryptographischen Daten. Bei Verwendung von asymmetrischen Sicherheitsverfahren
    (RDH) werden sowohl für den öffentlichen als auch für den privaten Schlüsselteil
    intern je ein <code>HBCIKey</code>-Objekt verwendet!
    Bei einigen Sicherheitsverfahren (DDV, PinTan) werden
    die kryptografischen Daten nicht in diesem Objekt gespeichert, sondern nur die
    administrativen.*/
public final class HBCIKey
     implements Serializable
{
    /** Ländercode des Schlüsselbesitzers */
    public String country;
    /** Bankleitzahl des Schlüsselbesitzers */
    public String blz;
    /** Nutzerkennung des Schlüsselbesitzers. Wenn der Schlüssel
        einem "richtigen" Nutzer gehört, so wird hier seine HBCI-Userkennung eingestellt;
        gehört der Schlüssel der Bank, so steht hier eine bankinterne
        ID (u.U. die Bankleitzahl o.ä.) */
    public String userid;
    /** Schlüsselnummer */
    public String num;
    /** Schlüsselversion */
    public String version;
    /** kryptographische Schlüsseldaten (kann <code>null</code> sein)*/
    public Key key;

    /** Neues <code>HBCIKey</code>-Objekt erzeugen */
    public HBCIKey()
    {
    }

    public HBCIKey(String country, String blz, String userid, String num, String version, Key key)
    {
        this.country = country;
        this.blz = blz;
        this.userid = userid;
        this.num = num;
        this.version = version;
        this.key = key;
    }
}
