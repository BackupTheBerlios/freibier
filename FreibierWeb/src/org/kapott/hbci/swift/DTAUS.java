
/*  $Id: DTAUS.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

package org.kapott.hbci.swift;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.kapott.hbci.datatypes.SyntaxDTAUS;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** <p>Hilfsklasse zum Erzeugen von DTAUS-Datensätzen für die Verwendung in
    Sammelüberweisungen und Sammellastschriften. Diese Klasse kann verwendet
    werden, um den DTAUS-Datenstrom zu erzeugen, der für Sammellastschriften
    und -überweisungen als Job-Parameter angegeben werden muss.</p>
    <p>In einem DTAUS-Objekt werden ein oder mehrere Transaktionen gespeichert. 
    Dabei müssen alle Transaktionen entweder Lastschriften oder Überweisungen sein. 
    Außerdem wird für alle Transaktionen das gleiche "Auftraggeberkonto" 
    angenommen (bei Überweisungen also das Belastungskonto, bei Lastschriften 
    das Konto, auf das der Betrag gutgeschrieben wird).</p>
    <p>In der Regel wird zunächst ein <code>DTAUS</code>-Objekt erzeugt. Dazu
    wird der Konstruktor {@link #DTAUS(Konto,int)}
    verwendet, womit gleichzeit das zu verwendende Auftraggeberkonto und der
    Typ des Sammelauftrages (<code>TYPE_CREDIT</code> für Sammelüberweisungen,
    <code>TYPE_DEBIT</code> für Sammellastschriften) festgelegt wird.
    Anschließend können beliebig viele {@link DTAUS.Transaction}-Objekte
    erzeugt werden, welche jeweils eine Transaktion darstellen. Jedes so erzeugte
    Objekt kann mit {@link #addEntry(DTAUS.Transaction)}
    zum Sammelauftrag hinzugefügt werden. Die Methode {@link #toString()} 
    liefert schließlich den so erzeugten Sammelauftrag im DTAUS-Format.</p> */
public class DTAUS 
{
    /** Daten einer einzelnen Transaktion, die in einen Sammelauftrag
        übernommen werden soll. Vor dem Hinzufügen dieser Transaktion zum
        Sammelauftrag müssen alle Felder dieses Transaktions-Objektes mit den
        jeweiligen Auftragsdaten gefüllt werden. */
    public class Transaction
    {
        /** <p>Konto des Zahlungsempfängers bzw. des Zahlungspflichtigen. Soll
            dieser Einzelauftrag in eine Sammelüberweisung eingestellt werden,
            so muss in diesem Feld die Kontoverbindung des Zahlungsempfängers
            eingestellt werden. Bei Sammellastschriften ist hier die 
            Kontoverbindung des Zahlungspflichtigen einzustellen.</p>
            <p>Von dem verwendeten {@link Konto}-Objekt müssen mindestens die
            Felder <code>blz</code>, <code>number</code> und <code>name</code>
            richtig belegt sein.</p> */
        public Konto otherAccount;
        
        /** interne Kunden-ID. Wie die verwendet wird weiß ich leider nicht
            genau, kann im Prinzip leer gelassen werden (ansonsten Maximallänge
            11 Zeichen). */
        public String internalCustomerId;
        
        /** Textschlüssel für den Auftrag. Bei Sammelüberweisungen ist dieses
            Feld mit '51' vorbelegt, bei Sammellastschriften mit '05'. Dieser
            Wert kann überschrieben werden, gültige Werte finden sich in den 
            Job-Restrictions 
            (siehe {@link org.kapott.hbci.GV.HBCIJob#getJobRestrictions()}). */
        public String key;
        
        /** Zusätzlicher Textschlüssel (wird i.d.R. bankintern verwendet).
            Dieser Wert muss aus drei Ziffern bestehen und ist mit '000'
            vorbelegt. Das manuelle Setzen dieses Wertes ist in den meisten
            Fällen nicht nötig (außer für Leute, die wissen was sie tun ;-) ). */
        public String addkey;
        
        /** Geldbetrag, der bei diesem Einzelauftrag überwiesen (Sammelüberweisungen)
            bzw. eingezogen (Sammellastschriften) werden soll */
        public Value value;
        
        private ArrayList usage;
        
        /** Erzeugen eine neuen Objektes für die Aufnahme von Daten für eine
            Transaktion */
        public Transaction()
        {
            addkey="000";
            key=(type==TYPE_CREDIT?"51":"05");
            usage=new ArrayList();
        }
        
        /** Hinzufügen einer Verwendungszweckzeile zu diesem Auftrag. */
        public void addUsage(String st)
        {
            usage.add(st);
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            
            ret.append("0000C");
            ret.append(expand(myAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
            ret.append(expand(otherAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
            ret.append(expand(otherAccount.number,10,(byte)0x30,ALIGN_RIGHT));
            
            sumBLZ+=Long.parseLong(otherAccount.blz);
            sumNumber+=Long.parseLong(otherAccount.number);
            
            if (internalCustomerId==null) {
                ret.append(expand("",13,(byte)0x30,ALIGN_LEFT));
            } else {
                ret.append((char)0);
                ret.append(expand(SyntaxDTAUS.check(internalCustomerId),11,(byte)0x30,ALIGN_LEFT));
                ret.append((char)0);
            }
            
            ret.append(expand(key,2,(byte)0x30,ALIGN_RIGHT));
            ret.append(expand(addkey,3,(byte)0x30,ALIGN_RIGHT));
            ret.append((char)0x20);
            ret.append(expand(Integer.toString(value.curr.equals("DEM")?(int)(100*value.value+0.5):0),11,(byte)0x30,ALIGN_RIGHT));
            ret.append(expand(myAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
            ret.append(expand(myAccount.number,10,(byte)0x30,ALIGN_RIGHT));
            ret.append(expand(Integer.toString(value.curr.equals("EUR")?(int)(100*value.value+0.5):0),11,(byte)0x30,ALIGN_RIGHT));
            ret.append(expand("",3,(byte)0x20,ALIGN_LEFT));
            ret.append(expand(SyntaxDTAUS.check(otherAccount.name),27,(byte)0x20,ALIGN_LEFT));
            ret.append(expand("",8,(byte)0x20,ALIGN_LEFT));
            
            if (value.curr.equals("EUR"))
                sumEUR+=(long)(100*value.value+0.5);
            else if (value.curr.equals("DEM"))
                sumDM+=(long)(100*value.value+0.5); 
            
            ret.append(expand(SyntaxDTAUS.check(myAccount.name),27,(byte)0x20,ALIGN_LEFT));
            
            String st="";
            if (usage.size()!=0)
                st=SyntaxDTAUS.check((String)usage.get(0));
            ret.append(expand(st,27,(byte)0x20,ALIGN_LEFT));
            
            ret.append((char)curr);
            ret.append(expand("",2,(byte)0x20,ALIGN_LEFT));
            
            int posForNumOfExt=ret.length();
            ret.append("00");
            
            int basicLenOfCSet=128+27+27+5;
            int realLenOfCSet=basicLenOfCSet;
            int numOfExt=0;
            
            // erweiterungsteile
            // *** name2 für myAccount und otherAccount vorerst weggelassen
            
            for (int i=1;i<usage.size();i++) {
                st=SyntaxDTAUS.check((String)usage.get(i));
                
                if (((realLenOfCSet%128)+29)>128) {
                    int diff=128-(realLenOfCSet%128);
                    ret.append(expand("",diff,(byte)0x20,ALIGN_LEFT));
                    realLenOfCSet+=diff;
                }
                
                ret.append("02");
                ret.append(expand(st,27,(byte)0x20,ALIGN_LEFT));
                realLenOfCSet+=29;
                numOfExt++;
            }
            
            if ((realLenOfCSet%128)!=0) {
                int diff=128-(realLenOfCSet%128);
                ret.append(expand("",diff,(byte)0x20,ALIGN_LEFT));
                realLenOfCSet+=diff;
            }
                
            ret.replace(posForNumOfExt,posForNumOfExt+2,expand(Integer.toString(numOfExt),2,(byte)0x30,ALIGN_RIGHT));
            ret.replace(0,4,expand(Integer.toString(basicLenOfCSet+29*numOfExt),4,(byte)0x30,ALIGN_RIGHT));
            
            return ret.toString();
        }
    }
    
    /** Typ des Sammelauftrages: Sammelüberweisung */
    public static final int TYPE_CREDIT=1;
    /** Typ des Sammelauftrages: Sammellastschrift */
    public static final int TYPE_DEBIT=2;
    
    private static final byte CURR_DM=0x20;  
    private static final byte CURR_EUR=0x31;
    private static final byte ALIGN_LEFT=1;
    private static final byte ALIGN_RIGHT=2;
    
    private Konto     myAccount;
    private int       type;
    private Date      execdate;
    private byte      curr;
    private ArrayList entries;
    
    private long sumDM;
    private long sumEUR;
    private long sumBLZ;
    private long sumNumber;
    
    /** Entspricht {@link #DTAUS(Konto, int, Date) DTAUS(myAccount,type,null)} */
    public DTAUS(Konto myAccount,int type)
    {
        this(myAccount,type,null);
    }
    
    /** Erzeugen eines neuen Objektes für die Aufnahme von
     Sammelaufträgen. <code>myAccount</code> ist dabei das "eigene" Konto, 
     welches bei Sammelüberweisungen als Belastungskonto und bei 
     Sammellastschriften als Gutschriftkonto verwendet wird. Von dem
     {@link Konto}-Objekt müssen mindestens die Felder <code>blz</code>,
     <code>number</code>, <code>curr</code> und <code>name</code> richtig
     gesetzt sein.  <br/>
     <code>execdate</code> gibt das Datum an, wann dieser Sammelauftrag 
     ausgeführt werden soll. ACHTUNG: <code>execdate</code> wird zur Zeit noch
     nicht ausgewertet! 
     @param myAccount Gegenkonto für die enthaltenen Aufträge 
     @param type <ul><li><code>TYPE_CREDIT</code> für Sammelüberweisungen,</li>
     <li><code>TYPE_DEBIT</code> für Sammellastschriften</li></ul>
     @param execdate Ausführungsdatum für diesen Sammelauftrag; <code>null</code>,
     wenn kein Ausführungsdatum gesetzt werden soll (sofortige Ausführung) */
    public DTAUS(Konto myAccount,int type,Date execdate)
    {
        this.myAccount=myAccount;
        this.type=type;
        this.execdate=execdate;
        
        entries=new ArrayList();
        
        if (myAccount.curr.equals("EUR"))
            this.curr=CURR_EUR;
        else if (myAccount.curr.equals("DEM"))
            this.curr=CURR_DM;
        else
            throw new InvalidUserDataException("*** invalid currency of this account: "+myAccount.curr);
    }
    
    /** Hinzufügen eines einzelnen Auftrages zu diesem Sammelauftrag. Das
        {@link DTAUS.Transaction}-Objekt, welches hier als Argument benötigt wird,
        muss mit '<code>dtaus.new&nbsp;Transaction()</code>' erzeugt werden 
        ('<code>dtaus</code>' ist dabei das aktuelle <code>DTAUS</code>-Objekt).
        @param entry Hinzuzufügender Einzelauftrag */ 
    public void addEntry(Transaction entry)
    {
        entries.add(entry);
    }
    
    /** Rückgabe des Sammelauftrages im DTAUS-Format. Der Rückgabewert dieser
        Methode kann direkt als Parameterwert für den Parameter '<code>data</code>'
        bei Sammelaufträgen verwendet werden (für eine Parameterbeschreibung
        siehe Paketbeschreibung des Paketes <code>org.kapott.hbci.GV</code>).
        @return DTAUS-Datenstrom für diesen Sammelauftrag */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        sumBLZ=0;
        sumNumber=0;
        sumDM=0;
        sumEUR=0;
        
        // A-set
        ret.append("0128A");
        switch (type) {
            case TYPE_CREDIT:
                ret.append("GK");
                break;
            case TYPE_DEBIT:
                ret.append("LK");
                break;
            default:
                throw new InvalidUserDataException("*** type of DTAUS order not set (DEBIT/CREDIT)");
        }
        
        ret.append(expand(myAccount.blz,8,(byte)0x20,ALIGN_RIGHT));
        ret.append(expand("",8,(byte)0x30,ALIGN_LEFT));
        ret.append(expand(SyntaxDTAUS.check(myAccount.name),27,(byte)0x20,ALIGN_LEFT));
        
        SimpleDateFormat form=new SimpleDateFormat("ddMMyy");
        ret.append(form.format(new Date()));
        
        ret.append(expand("",4,(byte)0x20,ALIGN_LEFT));
        ret.append(expand(myAccount.number,10,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand("",10,(byte)0x30,ALIGN_LEFT));
        ret.append(expand("",15,(byte)0x20,ALIGN_LEFT));
        
        if (execdate==null) {
            ret.append(expand("",8,(byte)0x20,ALIGN_LEFT));
        } else {
            form=new SimpleDateFormat("ddMMyyyy");
            ret.append(form.format(execdate));
        }
        
        ret.append(expand("",24,(byte)0x20,ALIGN_LEFT));
        ret.append((char)curr);
        
        // C-sets
        for (Iterator i=entries.iterator();i.hasNext();) {
            Transaction entry=(Transaction)i.next();
            ret.append(entry.toString());
        }
        
        // E-set
        ret.append("0128E");
        ret.append(expand("",5,(byte)0x20,ALIGN_LEFT));
        ret.append(expand(Integer.toString(entries.size()),7,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(curr==CURR_DM?sumDM:0),
                        13,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(sumNumber),17,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(sumBLZ),17,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand(Long.toString(curr==CURR_EUR?sumEUR:0),
                        13,(byte)0x30,ALIGN_RIGHT));
        ret.append(expand("",51,(byte)0x20,ALIGN_LEFT));
        
        return ret.toString();
    }
    
    private String expand(String st,int len,byte filler,int align)
    {
        if (st.length()<len) {
            try {
                byte[] fill=new byte[len-st.length()];
                Arrays.fill(fill,filler);
                String fillst=new String(fill,"ISO-8859-1");
                
                if (align==ALIGN_LEFT)
                    st=st+fillst;
                else if (align==ALIGN_RIGHT)
                    st=fillst+st;
                else
                    throw new HBCI_Exception("*** invalid align type: "+align);
            } catch (Exception e) {
                throw new HBCI_Exception(e);
            }
        } else if (st.length()>len) {
            throw new InvalidArgumentException("string too long: \""+st+"\" has "+st.length()+" chars, but max is "+len);
        }
        
        return st;
    }
}
