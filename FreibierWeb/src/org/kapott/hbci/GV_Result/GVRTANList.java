
/*  $Id: GVRTANList.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

/** Diese Klasse enthält Informationen über aktuelle TAN-Listen des Kunden. Dabei wird
    für jede TAN-Liste ein separates Objekt erzeugt. Innerhalb einer TAN-Listen-Informationen
    gibt es zu jeder verbrauchten TAN genauere Daten. */
public class GVRTANList 
    extends HBCIJobResultImpl
{
    /** Daten zu genau einer TAN */
    public static class TANInfo
    {
        /** Feld zum "Übersetzen" der Verwendungs-Codes ({@link #usage}) in einen Klartext. */
        public static final String[] usageCodes={
                                                "TAN ist frei",
                                                "Stornierung Überweisung",
                                                "PIN-Änderung",
                                                "Kontosperre aufheben",
                                                "Aktivieren neuer TAN-Liste",
                                                "Entwertete TAN (maschinell)",
                                                "Mitteilung mit TAN",
                                                "PC-Übertragung (Überweisung/Lastschrift)",
                                                "Wertpapierorder",
                                                "Dauerauftrag"};
        
        /** Code, wofür die TAN verwendet wurde, siehe auch {@link #usageCodes}). Gültige Codes sind:
            <ul>
              <li>0 - TAN wurde noch nicht verbraucht</li>
              <li>1 - Stornierung einer Überweisung</li>
              <li>2 - PIN-Änderung</li>
              <li>3 - Aufheben der Kontosperre</li>
              <li>4 - Aktivieren einer neuen TAN-Liste</li>
              <li>5 - TAN wurde maschinell entwertet</li>
              <li>6 - Mitteilungsversand</li>
              <li>7 - Überweisung/Lastschrift</li>
              <li>8 - Wertpapierverwaltung</li>
              <li>9 - Dauerauftragsverwaltung</li>
            </ul> */
        public int    usage;
        /** Die TAN selbst. Ist nur dann gesetzt, wenn die TAN tatsächlich bereits verbraucht
            wurde, sonst ist dieses Feld <code>null</code>*/
        public String tan;
        /** Zeitpunkt, wann die TAN verbraucht wurde. Diese Variable ist nur dann ungleich
            <code>null</code>, wenn die TAN tatsächlich bereits verbraucht wurde */
        public Date   timestamp;
        
        public String toString()
        {
            return "TAN:"+tan+" Verwendung:"+usage+" ("+usageCodes[usage]+")"+
                   (timestamp!=null?(" Zeitpunkt:"+HBCIUtils.date2String(timestamp)+" "+HBCIUtils.time2String(timestamp)):"");
        }
    }
    
    /** Informationen zu genau einer TAN-Liste. */
    public static class TANList
    {
        /** Typ der TAN-Liste. Gültige Codes sind:
            <ul>
              <li>A - aktive Liste</li>
              <li>S - Sperre der Liste</li>
              <li>V - vorherige Liste</li>
            </ul> */
        public  char      zustand;
        /** Listennummer */
        public  String    number;
        /** Erstellungsdatum der Liste, kann <code>null</code> sein. */
        public  Date      date;

        private List taninfos;
        
        public TANList()
        {
            taninfos=new ArrayList();
        }
    
        /** Gibt ein Feld mit Daten zu den einzelnen TANs dieser Liste zurück. 
            @return Array mit TAN-Informationen */
        public TANInfo[] getTANInfos()
        {
            return (TANInfo[])taninfos.toArray(new TANInfo[0]);
        }
        
        public void addTANInfo(TANInfo info)
        {
            taninfos.add(info);
        }
    
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
    
            ret.append("TANListe Nummer "+number+" Typ:"+zustand+linesep);
            for (Iterator i=taninfos.iterator();i.hasNext();) {
                ret.append("  "+((TANInfo)i.next()).toString()+linesep);
            }
    
            return ret.toString().trim();
        }
    }
    
    private List tanlists;
    
    public GVRTANList()
    {
        tanlists=new ArrayList();
    }
    
    public void addTANList(TANList list)
    {
        tanlists.add(list);
    }
    
    /** Gibt ein Array mit Informationen über jede verfügbare TAN-Liste zurück.
        @return Array mit TAN-Listen-Informationen */
    public TANList[] getTANLists()
    {
        return (TANList[])tanlists.toArray(new TANList[0]);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (Iterator i=tanlists.iterator();i.hasNext();) {
            ret.append(((TANList)i.next()).toString()+System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
}
