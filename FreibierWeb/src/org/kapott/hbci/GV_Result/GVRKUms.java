
/*  $Id: GVRKUms.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

package org.kapott.hbci.GV_Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;

/** <p>Ergebnisse der Abfrage von Kontoumsatzinformationen.
    Ein Objekt dieser Klasse entspricht einen Kontoauszug.
    Ein Kontoauszug ist in einzelne Buchungstage unterteilt.
    Für jeden einzelnen Buchungstag wiederum gibt es eine Anzahl von Umsatzzeilen 
    (das entspricht je einem Eintrag auf dem "normalen" Kontoauszug auf Papier).
    Jede einzelne Umsatzzeile wiederum enthält die einzelnen Informationen zu genau
    einer Transaktion. </p>
    <p>Es können auch alle Umsatzzeilen in einer einzigen Liste abgefragt werden (also nicht
    in Buchungstage unterteilt .</p>*/
public class GVRKUms
    extends HBCIJobResultImpl
{
    /** Eine "Zeile" des Kontoauszuges (enthält Daten einer Transaktion) */
    public static class UmsLine
        implements Serializable
    {
        /** Datum der Wertstellung */
        public Date   valuta;
        /** Buchungsdatum */
        public Date   bdate;
        /** <p>Credit(H)/Debit(S) ("<code>C</code>"/"<code>D</code>"). 
            Vor dem "<code>C</code>" bzw. "<code>D</code>" kann optional noch
            ein "<code>R</code>" stehen, was eine Storno-Buchung anzeigt.</p>
            <p>Eine "<code>C</code>"-Buchung ist eine Haben-Buchung, d.h. eine Gutschrift
            auf das Kundenkonto. Eine "<code>D</code>"-Buchung ist eine Soll-Buchung,
            d.h. eine Belastung des Kundenkontos.</p> */
        public String cd;
        /** Gebuchter Betrag */
        public Value  value;
        /** Der Saldo <em>nach</em> dem Buchen des Betrages <code>value</code> */
        public Saldo  saldo;
        /** Kundenreferenz */
        public String customerref;
        /** Kreditinstituts-Referenz */
        public String instref;
        /** Ursprünglicher Betrag (bei ausländischen Buchungen; optional) */
        public Value  orig_value;
        /** Betrag für Gebühren des Geldverkehrs (optional) */
        public Value  charge_value;
        
        /** Art der Buchung (bankinterner Code) */
        public String   gvcode;
        /** <p>Zusatzinformationen im Rohformat. Wenn Zusatzinformationen zu dieser
            Transaktion in einem unbekannten Format vorliegen, dann enthält dieser
            String diese Daten.</p>
            <p>Wenn die Zusatzinformationen aber ausgewertet werden können, so ist dieser
            String <code>null</code>, und die Felder <code>text</code>, <code>primanota</code>,
            <code>usage</code>, <code>blz</code>, <code>number</code>, <code>name1</code>,
            <code>name2</code> und <code>addkey</code> enthalten die entsprechenden
            Werte</p> */
        public String   additional;
        /** Beschreibung der Art der Buchung (optional) */
        public String   text;
        /** Primanotakennzeichen (optional) */
        public String   primanota;
        /** Verwendungszweckzeilen (dieses Array ist niemals <code>null</code>, kann aber die Länge <code>0</code> haben) */
        public String[] usage;
        /** Gegenkonto der Buchung (optional) */
        public Konto    other;
        /** Erweiterte Informationen zur Art der Buchung (bankintern, optional) */
        public String   addkey;

        public UmsLine()
        {
            usage=new String[0];
        }

        public void addUsage(String st)
        {
            if (st!=null) {
                ArrayList l=new ArrayList(Arrays.asList(usage));
                l.add(st);
                usage=(String[])(l.toArray(usage));
            }
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String linesep=System.getProperty("line.separator");

            ret.append(HBCIUtils.date2String(valuta)+" "+HBCIUtils.date2String(bdate)+" ");
            ret.append(customerref+":"+instref+" ");
            ret.append(cd.charAt(0)=='R'?"S":" ");
            ret.append(cd.charAt(cd.length()-1)=='C'?"+":"-");
            ret.append(value.toString());
            if (orig_value!=null)
                ret.append(" (orig "+orig_value.toString()+")");
            if (charge_value!=null)
                ret.append(" (charge "+charge_value.toString()+")");
            ret.append(linesep);
            
            ret.append("    saldo: "+saldo.toString()+linesep);

            ret.append("    code "+gvcode+linesep);
            if (additional==null) {
                ret.append("    text:"+text+linesep);
                ret.append("    primanota:"+primanota+linesep);
                for (int i=0;i<usage.length;i++) {
                    ret.append("    usage:"+usage[i]+linesep);
                }
                if (other!=null)
                    ret.append("    konto:"+other.toString()+linesep);
                ret.append("    addkey:"+addkey);
            } 
            else ret.append("    "+additional);

            return ret.toString().trim();
        }
    }

    /** Enthält alle Transaktionen eines einzelnen Buchungstages. Dazu gehören
        das Datum des jeweiligen Tages, der Anfangs- und Endsaldo sowie die
        Menge aller dazugehörigen Umsatzeilen */
    public static class BTag
        implements Serializable
    {
        /** <p>Konto, auf das sich die Umsatzdaten beziehen (Kundenkonto). Einige
            Kreditinstitute geben fehlerhafte Kontoauszüge zurück, was zur Folge
            haben kann, dass dieses Feld nicht richtig belegt ist. Tritt ein solcher
            Fall ein, so kann es vorkommen, dass von dem <code>Konto</code>-Objekt
            nur das Feld <code>number</code> gefüllt ist, und zwar mit den
            Informationen, die das Kreditinstitut zur Identifizierung dieses Kontos
            zurückgibt.</p>
            <p>Normalerweise bestehen diese Informationen aus BLZ und
            Kontonummer, die dann auch korrekt in das <code>Konto</code>-Objekt
            eingetragen werden. Liegen diese Informationen aber gar nicht oder in 
            einem falschen bzw. unbekannten Format vor, so werden diese Daten 
            komplett in das <code>number</code>-Feld geschrieben.</p> */
        public Konto     my;
        /** Nummer des Kontoauszuges (optional) */
        public String    counter;
        /** Saldo zu Beginn des Buchungstages */
        public Saldo     start;
        /** Art des Saldos. <code>M</code> = Anfangssaldo; <code>F</code> = Zwischensaldo */
        public char      starttype;
        /** Die einzelnen Umsatzzeilen (dieses Array ist niemals <code>null</code>, kann aber die Länge <code>0</code> haben) */
        public UmsLine[] lines;
        /** Saldo am Ende des Buchungstages */
        public Saldo     end;
        /** Art des Endsaldos (siehe {@link #starttype}) */
        public char      endtype;

        public BTag()
        {
            lines=new UmsLine[0];
        }

        public void addLine(UmsLine line)
        {
            ArrayList l=new ArrayList(Arrays.asList(lines));
            l.add(line);
            lines=(UmsLine[])(l.toArray(lines));
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String linesep=System.getProperty("line.separator");

            ret.append("Konto "+my.toString()+" - Auszugsnummer "+counter+linesep);
            ret.append("  "+(starttype=='F'?"Anfangs":"Zwischen")+"saldo: "+start.toString()+linesep);

            for (int i=0;i<lines.length;i++) {
                ret.append("  "+(lines[i]).toString()+linesep);
            }
            
            ret.append("  "+(endtype=='F'?"Schluss":"Zwischen")+"saldo: "+end.toString());
            return ret.toString().trim();
        }
    }

    private BTag[] tage;
    
    /** Dieses Feld enthält einen String, der den nicht-auswertbaren Teil der Kontoauszüge
        enthält. Es dient nur zu Debugging-Zwecken und sollte eigentlich immer <code>null</code>
        bzw. einen leeren String enthalten. Wenn das nicht der Fall ist, dann konnten die 
        empfangenen Kontoauszüge nicht richtig geparst werden, und dieser String enthält den
        "Schwanz" der Kontoauszugsdaten, bei dem das Parsing-Problem aufgetreten ist.*/
    public  String rest;

    public GVRKUms()
    {
        tage=new BTag[0];
    }
    
    /** Gibt die Umsatzinformationen gruppiert nach Buchungstagen zurück.
        @return Array mit Informationen zu einzelnen Buchungstagen */
    public BTag[] getDataPerDay()
    {
        return tage;
    }
    
    /** Gibt alle Transaktionsdatensätze in einer "flachen" Struktur zurück.
        D.h. nicht in einzelne Buchungstage unterteilt, sondern in einer Liste
        analog zu einem "normalen" Kontoauszug.
        @return Array mit Transaktionsdaten */
    public UmsLine[] getFlatData()
    {
        List result=new ArrayList();
        
        for (int i=0;i<tage.length;i++) {
            BTag tag=tage[i];
            UmsLine[] lines=tag.lines;
            
            for (int j=0;j<lines.length;j++) {
                result.add(lines[j]);
            }
        }
        
        return (UmsLine[])result.toArray(new UmsLine[0]);
    }

    public void addTag(BTag tag)
    {
        ArrayList l=new ArrayList(Arrays.asList(tage));
        l.add(tag);
        tage=(BTag[])(l.toArray(tage));
    }

    public String toString()
    {
        StringBuffer ret=new StringBuffer();

        for (int i=0;i<tage.length;i++) {
            ret.append((tage[i]).toString()+System.getProperty("line.separator"));
        }

        ret.append("notbooked: "+getResultData().getProperty("notbooked")+System.getProperty("line.separator"));
        ret.append("rest: "+rest);
        return ret.toString().trim();
    }
}
