
/*  $Id: GVRWPDepotList.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Ergebnisdaten f�r die Abfrage einer Depotaufstellung.
    Diese Klasse enth�lt f�r jedes Depot ein separates
    Datenobjekt. Innerhalb eines Depots werden f�r jede
    in diesem Depot vorhandene Wertpapiergattung separate
    Datenobjekte gehalten. F�r jede Wertpapiergattung wiederum
    gibt es u.U. mehrere Objekte, die Saldeninformationen
    enthalten. */
public final class GVRWPDepotList 
    extends HBCIJobResultImpl
{
    /** Ein Eintrag zu genau einem Depot */
    public static final class Entry
    {
        public static final int SALDO_TYPE_STCK=1;
        public static final int SALDO_TYPE_WERT=2;

        /** Enh�lt Informationen zu einer Wertpapiergattung */
        public static final class Gattung
        {
            /** Untersaldoinformationen, das hei�t Informationen �ber die Zusammensetzung
                des Saldos einer Wertpapiergattung. */
            public static final class SubSaldo
            {
                /** Beschreibung des Saldowertes */
                public String  qualifier;
                /** Gibt den Typ des Saldos {@link #saldo} an (optional).
                    <ul>
                      <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry.Gattung#PRICE_TYPE_PRCT} - Saldo ist ein Prozentsatz</li>
                      <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry.Gattung#PRICE_TYPE_VALUE} - Saldo ist ein Geldbetrag</li>
                    </ul> */
                public int     saldo_type;
                /** Gibt an, ob das Papier f�r einen Verkauf zur Verf�gung steht.
                    <code>true</code> gibt an, dass das Papier gesperrt ist und somit
                    nicht zur Verf�gung steht, bei <code>false</code> kann es verkauft werden */
                public boolean locked;
                /** Saldobetrag. Das W�hrungsfeld <code>curr</code> ist hier zur Zeit
                    immer der leere String. */
                public Value   saldo;
                /** Lagerland der Depotstelle (optional). Der L�ndercode ist
                    der ISO-3166-L�ndercode (z.B. DE f�r Deutschland) */
                public String  country;
                /** Art der Verwahrung (optional).
                    <ul>
                      <li>0 - nicht gesetzt</li>
                      <li>1 - Girosammelverwahrung</li>
                      <li>2 - Streifbandverwahrung</li>
                      <li>3 - Haussammelverwahrung</li>
                      <li>4 - Wertpapierrechnung</li>
                      <li>9 - Sonstige</li>
                    </ul>*/
                public int     verwahrung;
                /** Beschreibung der Lagerstelle (optional) */
                public String  lager;
                /** Sperre bis zu diesem Datum (optional) */
                public Date    lockeduntil;
                /** Sperr- oder Zusatzvermerke der Bank (optional) */
                public String  comment;
                
                public String toString()
                {
                    StringBuffer ret=new StringBuffer();
                    String       linesep=System.getProperty("line.separator");
                    
                    ret.append(qualifier+": ");
                    ret.append(saldo.toString()+" ("+((saldo_type==SALDO_TYPE_STCK)?"STCK":"WERT")+")"+linesep);
                    
                    ret.append("Lagerland: "+country+linesep);
                    ret.append("Verwahrung Typ: "+verwahrung+linesep);
                    ret.append("Lager: "+lager+linesep);
                    
                    if (locked) {
                        ret.append("locked");
                        if (lockeduntil!=null)
                            ret.append(" until "+DateFormat.getDateTimeInstance().format(lockeduntil));
                    } else {
                        ret.append("not locked");
                    }
                    
                    if (comment!=null)
                        ret.append(linesep+"Bemerkungen: "+comment);
                    
                    return ret.toString().trim();
                }
            }
            
            public final static int PRICE_TYPE_PRCT=1;
            public final static int PRICE_TYPE_VALUE=2;
            public final static int PRICE_QUALIF_MRKT=1;
            public final static int PRICE_QUALIF_HINT=2;
            public final static int SOURCE_LOC=1;
            public final static int SOURCE_THEOR=2;
            public final static int SOURCE_SELLER=3;
            
            /** ISIN des Wertpapiers (optional) */
            public String     isin;
            /** WKN des Wertpapiers (optional) */
            public String     wkn;
            /** Wertpapierbezeichnung */
            public String     name;
            /** Gibt den Typ des Preises {@link #price} an (optional).
                <ul>
                  <li>{@link #PRICE_TYPE_PRCT} - Preis ist ein Prozentsatz</li>
                  <li>{@link #PRICE_TYPE_VALUE} - Preis ist ein Geldbetrag</li>
                </ul> */
            public int        pricetype;
            /** Hinweise zum Preis {@link #price} (optional).
                <ul>
                  <li>{@link #PRICE_QUALIF_MRKT} - Marktpreis (z.B. aktueller B�rsenkurs)</li>
                  <li>{@link #PRICE_QUALIF_HINT} - Hinweispreis (rechnerischer bzw. ermittelter Preis)</li>
               </ul>*/
            public int        pricequalifier;
            /** Preis pro Einheit (optional). Die W�hrung ist bei
                {@link #pricetype}={@link #PRICE_TYPE_PRCT} auf "%" gesetzt. */
            public Value      price;
            /** Herkunft von Preis/Kurs (optional).
                <ul>
                  <li>{@link #SOURCE_LOC} - lokale B�rse</li>
                  <li>{@link #SOURCE_THEOR} - theoretischer Wert</li>
                  <li>{@link #SOURCE_SELLER} - Verk�ufer als Quelle</li>
                </ul> */
            public int        source;
            /** Bemerkungen zur Herkunft von Preis/Kurs {@link #source} (optional).
                Bei {@link #source}={@link #SOURCE_LOC} kann der Name der B�rse
                als MIC angegeben werden */
            public String     source_comment;
            /** Zeitpunkt, wann {@link #price} notiert wurde (optional). */
            public Date       timestamp_price;
            /** Typ des Gesamtsaldos.
                <ul>
                  <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry#SALDO_TYPE_STCK} - Saldo ist eine St�ckzahl</li> 
                  <li>{@link org.kapott.hbci.GV_Result.GVRWPDepotList.Entry#SALDO_TYPE_WERT} - Saldo ist ein Betrag</li>
                </ul>*/ 
            public int        saldo_type;
            /** Gesamtsaldo dieser Gattung. Das W�hrungsfeld ist in jedem
                Fall ein leerer String! (TODO). */
            public Value      saldo;
            private ArrayList saldi;
            /** Anzahl der aufgelaufenen Tage (optional) */
            public int        days;
            /** Kurswert zum Gesamtsaldo {@link #saldo} (optional) */
            public Value      depotwert;
            /** Betrag der   St�ckzinsen (optional) */
            public Value      stueckzinsbetrag;
            // *** dafuer muessen depotwert2 und stueckzinsbetrag2 eingefuehrt werden
            public String     xchg_cur1;
            public String     xchg_cur2;
            public double     xchg_kurs;
            /** Depotw�hrung (optional) */
            public String     curr;
            /** Wertpapierart gem�� WM GD 195 (optional) */
            public String     wptype;
            /** Branchenschl�ssel gem�� WM GD 200 (optional) */
            public String     branche;
            /** Land des Emittenten (Country-Code wie in Kontodaten) (optional) */
            public String     countryEmittent;
            /** Kaufdatum (optional) */
            public Date       kauf;
            /** F�lligkeitsdatum (optional) */
            public Date       faellig;
            /** Einstandspreis/-kurs (optional). Die W�hrung ist "%", 
                wenn es sich um eine Prozentabgabe handelt */
            public Value      einstandspreis;
            /** Zinssatz als Prozentangabe bei verzinslichen Papieren (optional) */
            public double     zinssatz;
            // *** das ist noch nicht gemacht
            public int        kontrakttype; 
            public Date       kontraktverfall;
            public int        kontraktversion;
            public int        kontraktsize;
            public String     symbol;
            public String     underlyingwkn;
            public String     underlyingisin;
            public Value      kontraktbasispreis;
            
            public Gattung()
            {
                saldi=new ArrayList();
            }
            
            public void addSubSaldo(SubSaldo subsaldo)
            {
                saldi.add(subsaldo);
            }
            
            /** Gibt alle Unter-Saldoinformationen in einem Array zur�ck.
                Der R�ckgabewert ist niemals <code>null</code>, das Array kann aber
                die L�nge <code>0</code> haben.
                @return Array mit Untersaldoinformationen */
            public SubSaldo[] getEntries()
            {
                return (SubSaldo[])saldi.toArray(new SubSaldo[0]);
            }
            
            public String toString()
            {
                StringBuffer ret=new StringBuffer();
                String       linesep=System.getProperty("line.separator");
                
                ret.append("Gattung "+name+"(ISIN:"+isin+" WKN:"+wkn+" CURR:"+curr+")"+linesep);
                if (price!=null)
                    ret.append("Preis: "+price.toString()+" ("+(pricetype==PRICE_TYPE_PRCT?"Prozent":"Betrag")+
                               "; "+(pricequalifier==PRICE_QUALIF_MRKT?"Marktpreis":"Hinweispreis")+")"+linesep);
                
                if (source!=0) {
                    ret.append("Quelle: ");
                    switch (source) {
                        case SOURCE_LOC:
                            ret.append("lokale Boerse");
                            break;
                        case SOURCE_THEOR:
                            ret.append("theoretischer Wert");
                            break;
                        case SOURCE_SELLER:
                            ret.append("Verkaeufer");
                            break;
                        default:
                            ret.append("(unknown)");
                    }
                    if (source_comment!=null)
                        ret.append(" ("+source_comment+")");
                    ret.append(linesep);
                }
                
                if (timestamp_price!=null)
                    ret.append("Zeitpunkt: "+DateFormat.getDateTimeInstance().format(timestamp_price)+linesep);
                
                if (depotwert!=null)
                    ret.append("Depotwert: "+depotwert.toString()+linesep);
                if (stueckzinsbetrag!=null)
                    ret.append("Stueckzins: "+stueckzinsbetrag.toString()+linesep);
                if (einstandspreis!=null)
                    ret.append("Einstandspreis: "+einstandspreis.toString()+linesep);
                if (zinssatz!=0)
                    ret.append("Zinssatz: "+HBCIUtilsInternal.double2String(zinssatz)+linesep);
                ret.append("Typ:"+wptype+" Branche:"+branche+" Emittent:"+countryEmittent+linesep);
                if (kauf!=null)
                    ret.append("Kauf: "+HBCIUtils.date2String(kauf)+linesep);
                if (faellig!=null)
                    ret.append("Faelligkeit: "+HBCIUtils.date2String(faellig)+linesep);
                if (days!=0)
                    ret.append("Anzahl abgelaufener Tage: "+days+linesep);
                ret.append("Saldo: "+saldo.toString()+" ("+((saldo_type==SALDO_TYPE_STCK)?"STCK":"WERT")+")"+linesep);
                
                for (int i=0;i<saldi.size();i++) {
                    ret.append("SubSaldo:"+linesep);
                    ret.append(saldi.get(i).toString());
                    if (i<saldi.size()-1) {
                        ret.append(linesep+linesep);
                    }
                }
                
                return ret.toString().trim();
            }
        }
        
        /** Zeitpunkt der Erstellung dieser Daten */
        public  Date      timestamp;
        /** Depotkonto, auf das sich der Eintrag bezieht. */
        public  Konto     depot;
        private ArrayList gattungen;
        /** Gesamtwert des Depots (optional!) */
        public  Value     total;
        
        /* *** Zusatzinformationen aus Feld 72 */
        
        public Entry()
        {
            gattungen=new ArrayList();
        }
        
        public void addEntry(Gattung gattung)
        {
            gattungen.add(gattung);
        }
        
        /** Gibt ein Array mit Informationen �ber alle Wertpapiergattungen
            zur�ck, die in diesem Depot gehalten werden. Der R�ckgabewert ist
            niemals <code>null</code>, die Gr��e des Arrays kann aber 0 sein.
            @return Array mit Informationen �ber Wertpapiergattungen */
        public Gattung[] getEntries()
        {
            return (Gattung[])gattungen.toArray(new Gattung[0]);
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            ret.append("Depot "+depot.toString()+" "+DateFormat.getDateTimeInstance().format(timestamp)+linesep);
            for (int i=0;i<gattungen.size();i++) {
                ret.append("Gattung:"+linesep);
                ret.append(((Gattung)gattungen.get(i)).toString()+linesep+linesep);
            }
            if (total!=null)
                ret.append("Total: "+total.toString());
            
            return ret.toString().trim();
        }
    }
    
    private List entries;
    /** Dieses Feld enth�lt einen String, der den nicht-auswertbaren Teil der gelieferten Informationen
        enth�lt. Es dient nur zu Debugging-Zwecken und sollte eigentlich immer <code>null</code>
        bzw. einen leeren String enthalten. Wenn das nicht der Fall ist, dann konnten die 
        empfangenen Daten nicht richtig geparst werden, und dieser String enth�lt den
        "Schwanz" der Daten, bei dem das Parsing-Problem aufgetreten ist.*/
    public String rest;
    
    public GVRWPDepotList()
    {
        entries=new ArrayList();
    }
    
    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }
    
    /** Gibt ein Array mit Depotdaten zur�ck, wobei jeder Eintrag
        Informationen zu genau einem Depot enth�lt.
        @return Array mit Depotinformationen */
    public Entry[] getEntries()
    {
        return (Entry[])entries.toArray(new Entry[0]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
            
        for (int i=0;i<entries.size();i++) {
            Entry e=(Entry)entries.get(i);
            ret.append("Entry #"+(i+1)+":"+linesep);
            ret.append(e.toString()+linesep+linesep);
        }
        
        ret.append("rest: "+rest);
            
        return ret.toString().trim();
    }
}
