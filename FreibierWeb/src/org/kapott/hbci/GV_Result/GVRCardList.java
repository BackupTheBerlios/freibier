
/*  $Id: GVRCardList.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.structures.Value;

/** Klasse mit den Ergebissen der Abfrage von Informationen zu 
    ausgegebenen Karten. Für jede Karte, für die Informationen
    verfügbar sind, wird eine separates Informationsobjekt
    {@link org.kapott.hbci.GV_Result.GVRCardList.CardInfo}
    erzeugt. */
public class GVRCardList 
    extends HBCIJobResultImpl
{
    /** Informationen über genau eine Karte */
    public static class CardInfo
    {
        /** Kartenart aus den BPD */
        public int    cardtype;
        /** Kartennummer */
        public String cardnumber;
        /** Kartenfolgenummer (optional) */
        public String cardordernumber;
        /** Name des Karteninhabers (optional) */
        public String owner;
        /** Karte gültig von (optional) */
        public Date   validFrom;
        /** Karte gültig bis (optional) */
        public Date   validUntil;
        /** Kartenlimit (optional) */
        public Value  limit;
        /** Bemerkungen (optional) */
        public String comment;
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            ret.append("Karte "+cardnumber+
                       " (typ "+cardtype+
                       ", Folgenummer "+cardordernumber+"): "+
                       owner+linesep);
            ret.append("Gültig von "+(validFrom!=null?HBCIUtils.date2String(validFrom):"unknown")+
                       " bis "+(validUntil!=null?HBCIUtils.date2String(validUntil):"unknown")+linesep);
            if (limit!=null)
                ret.append("Kartenlimit: "+limit+linesep);
            if (comment!=null)
                ret.append("Bemerkungen: "+comment+linesep);
            
            return ret.toString().trim();
        }
    }
    
    private List entries;
    
    public GVRCardList()
    {
        entries=new ArrayList();
    }
    
    public void addEntry(CardInfo info)
    {
        entries.add(info);
    }
    
    /** Gibt eine Liste aller empfangenen Karteninformations-Einträge zurück.
        @return Array mit Karteninformationsdaten. Das Array selbst ist niemals
        <code>null</code>, kann aber die Länge <code>0</code> haben */
    public CardInfo[] getEntries()
    {
        return (CardInfo[])entries.toArray(new CardInfo[0]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
        
        int num=0;
        for (Iterator i=entries.iterator();i.hasNext();) {
            num++;
            ret.append("Karteninfo #"+num+linesep);
            ret.append(((CardInfo)i.next()).toString()+linesep);
        }
        
        return ret.toString().trim();
    }
}
