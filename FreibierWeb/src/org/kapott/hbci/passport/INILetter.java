
/*  $Id: INILetter.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.passport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;

/** Hilfsklasse f�r das Erzeugen von INI-Briefen (f�r RDH-Zug�nge). Diese Klasse
    erm�glicht das Erzeugen von INI-Briefen. Dazu werden Methoden bereitgestellt,
    mit deren Hilfe die f�r einen INI-Brief ben�tigten Daten ermittelt werden
    k�nnen. Au�erdem liefert die {@link #toString()}-Methode einen vorgefertigten
    INI-Brief (kann als Vorlage benutzt werden). */
public class INILetter
{
    /** INI-Brief f�r Institutsschl�ssel (wird f�r Vergleich mit tats�chlichem
        INI-Brief von der Bank ben�tigt) */
    public static final int TYPE_INST=1;
    /** INI-Brief f�r Nutzerschl�ssel erzeugen (muss nach dem Erstellen neuer
        Schl�ssel an die Bank versandt werden) */
    public static final int TYPE_USER=2;
    
    private HBCIPassport passport;
    private HBCIKey      hbcikey;
    
    /** Anlegen eines neuen INI-Brief-Objektes.
        @param passport das Passport-Objekt (entspricht einem HBCI-Zugang), f�r
               den ein INI-Brief ben�tigt wird
        @param type gibt an, f�r welche Schl�ssel aus dem <code>passport</code> 
               der INI-Brief ben�tigt wird ({@link #TYPE_INST} f�r die Bankschl�ssel,
               {@link #TYPE_USER} f�r die Schl�ssel des Nutzers) */
    public INILetter(HBCIPassport passport,int type)
    {
        this.passport=passport;
        
        if (type==TYPE_INST) {
            hbcikey=passport.getInstSigKey();
            if (hbcikey==null)
                hbcikey=passport.getInstEncKey();
        } else {
            hbcikey=passport.getMyPublicSigKey();
        }
    }
    
    private byte[] getKeyData(BigInteger x)
    {
        byte[] xArray=x.toByteArray();
        byte[] retArray=new byte[128];
        System.arraycopy(xArray,xArray.length-Math.min(128,xArray.length),
                         retArray,128-Math.min(128,xArray.length),
                         Math.min(128,xArray.length));
        return retArray;
    }
    
    /** Gibt den Exponenten des �ffentlichen Schl�ssels zur�ck. Das byte-Array hat
        dabei immer eine Gr��e von 128 Bytes (also mit f�hrenden Nullen aufgef�llt).
        @return Exponent des �ffentlichen Schl�ssels */
    public byte[] getKeyExponent()
    {
        return getKeyData(((RSAPublicKey)hbcikey.key).getPublicExponent());
    }
    
    /** Gibt den Modulus des �ffentlichen Schl�ssels zur�ck. Das byte-Array hat
        dabei immer eine Gr��e von 128 Bytes (also mit f�hrenden Nullen aufgef�llt).
        @return Modulus des �ffentlichen Schl�ssels */
    public byte[] getKeyModulus()
    {
        return getKeyData(((RSAPublicKey)hbcikey.key).getModulus());
    }
    
    /** Gibt den Hashwert des �ffentlichen Schl�ssels zur�ck. Das byte-Array hat
        dabei immer eine Gr��e von 20 Bytes.
        @return Hashwert des �ffentlichen Schl�ssels */
    public byte[] getKeyHash()
    {
        try {
            byte[] retArray=new byte[256];
            System.arraycopy(getKeyExponent(),0,retArray,0,128);
            System.arraycopy(getKeyModulus(),0,retArray,128,128);
            
            MessageDigest dig=MessageDigest.getInstance("RIPEMD160","HBCIProvider");
            return dig.digest(retArray);
        } catch (Exception e) {
            throw new HBCI_Exception("*** error while calculating hash value",e);
        }
    }
    
    /** Gibt einen "fertigen" INI-Brief zur�ck.
        @return INI-Brief */
    public String toString()
    {
        StringWriter ret=new StringWriter();
        PrintWriter out=new PrintWriter(ret);
        
        Date date=new Date();
        
        out.println();
        out.println("INI-Brief HBCI");
        out.println();
        out.println();
        out.println("Datum:                       "+HBCIUtils.date2String(date));
        out.println();
        out.println("Uhrzeit:                     "+HBCIUtils.time2String(date));
        out.println();
        out.println("Empf�nger BLZ:               "+passport.getBLZ());
        out.println();
        out.println("Benutzerkennung:             "+passport.getUserId());
        out.println();
        out.println("Schl�sselnummer:             "+hbcikey.num);
        out.println();
        out.println("Schl�sselversion:            "+hbcikey.version);
        out.println();
        out.println("HBCI-Version:                "+passport.getHBCIVersion());
        out.println();
        out.println();
        out.println("�ffentlicher Schl�ssel f�r die elektronische Signatur");
        out.println();
        out.println("  Exponent");
        out.println();
        String st=HBCIUtils.data2hex(getKeyExponent());
        for (int line=0;line<8;line++) {
            out.println("    "+st.substring(line*(16*3),(line+1)*16*3));
        }
        
        out.println();
        out.println("  Modulus");
        out.println();
        st=HBCIUtils.data2hex(getKeyModulus());
        for (int line=0;line<8;line++) {
            out.println("    "+st.substring(line*(16*3),(line+1)*16*3));
        }

        out.println();
        out.println("  Hashwert");
        out.println();
        st=HBCIUtils.data2hex(getKeyHash());
        for (int line=0;line<2;line++) {
            out.println("    "+st.substring(line*(10*3),(line+1)*10*3));
        }
        
        out.println();
        out.println("Ich best�tige hiermit den obigen �ffentlichen Schl�ssel");
        out.println("f�r meine elektronische Signatur");
        out.println();
        out.println();
        out.println();
        out.println();
        out.println();
        out.println();
        out.println();
        out.println("Ort/Datum                                       Unterschrift");
        
        out.close();
        return ret.toString();
    }
}
