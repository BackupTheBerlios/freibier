
/*  $Id: GVRAccInfo.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** Klasse für die Ergebnisdaten einer Kontostammdaten-Abfrage */
public class GVRAccInfo 
    extends HBCIJobResultImpl
{
    /** Informationen zu genau einem Konto */
    public static class AccInfo
    {
        public static final int DELIVER_TYPE_NONE=0;
        public static final int DELIVER_TYPE_POST=1;
        public static final int DELIVER_TYPE_KAD=2;
        public static final int DELIVER_TYPE_OFFICE=3;
        public static final int DELIVER_TYPE_EDV=4;
        
        public static final int TURNUS_DAILY=1;
        public static final int TURNUS_WEEKLY=2;
        public static final int TURNUS_MONTHLY=3;
        public static final int TURNUS_QUARTER=4;
        public static final int TURNUS_HALF=5;
        public static final int TURNUS_ANNUAL=6;
        
        /** Konto, auf das sich diese Daten beziehen */
        public Konto   account;
        /** Kontoart - Folgende Wertebereiche sind definiert:.
            <ul>
              <li>1-9 - Kontokorrent-/Girokonto</li>
              <li>10-19 - Sparkonto</li>
              <li>20-29 - Festgeldkonto (Termineinlagen) </li>
              <li>30-39 - Wertpapierdepot</li>
              <li>40-49 - Kredit-/Darlehenskonto</li>
              <li>50-59 - Kreditkartenkonto</li>
              <li>60-69 - Fondsdepot bei einer Kapitalanlagegesellschaft</li>
              <li>70-79 - Bausparvertrag</li>
              <li>80-89 - Versicherungsvertrag</li>
              <li>90-99 - Sonstige</li>
            </ul>
            <code>-1</code>, wenn diese Information nicht von der Bank 
            bereitgestellt wird*/
        public int     type;
        /** Eröffnungsdatum (optional) */
        public Date    created;
        /** Sollzins (optional) */
        public double  sollzins;
        /** Habenzins (optional) */
        public double  habenzins;
        /** Überziehungszins (optional) */
        public double  ueberzins;
        /** Kreditlinie (optional) */
        public Value   kredit;
        /** Referenzkonto (zB für Kreditkartenkonten) (optional) */
        public Konto   refAccount;
        /** Versandart für Kontoauszüge. Folgende Werte sind definiert:
            <ul>
              <li><code>DELIVER_TYPE_NONE</code> - kein Auszug</li>
              <li><code>DELIVER_TYPE_POST</code> - Postzustellung</li>
              <li><code>DELIVER_TYPE_KAD</code> - Kontoauszugsdrucker</li>
              <li><code>DELIVER_TYPE_OFFICE</code> - Abholung in Geschäftsstelle</li>
              <li><code>DELIVER_TYPE_EDV</code> - elektronische Übermittlung</li>
            </ul>*/
        public int     versandart;
        /** Turnus für Kontoauszugszustellung (nur bei Postzustellung) (optional).
         * Folgende Werte sind definiert:
         * <ul>
         *   <li><code>TURNUS_DAILY</code> - täglicher Kontoauszug</li>
         *   <li><code>TURNUS_WEEKLY</code> - wöchentlicher Kontoauszug</li>
         *   <li><code>TURNUS_MONTHLY</code> - monatlicher Kontoauszug</li>
         *   <li><code>TURNUS_QUARTER</code> - vierteljährlicher Kontoauszug</li>
         *   <li><code>TURNUS_HALF</code> - halbjährlicher Kontoauszug</li>
         *   <li><code>TURNUS_ANNUAL</code> - jährlicher Kontoauszug</li>
         * </ul> */
        public int     turnus;
        /** Weitere Informationen (optional) */
        public String  comment;
        
        // *** public Berechtigter[] berechtigte; 
        // /** *** Briefanschrift (optional) */
        // public Address address;
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            String       linesep=System.getProperty("line.separator");
            
            ret.append("Konto "+account.toString()+" (art: "+type+")"+linesep);
            if (created!=null)
                ret.append("Eröffnungsdatum: "+HBCIUtils.date2String(created)+linesep);
            ret.append("Sollzins:"+HBCIUtilsInternal.double2String(sollzins)+
                       " Habenzins:"+HBCIUtilsInternal.double2String(habenzins)+
                       " Überziehungszins:"+HBCIUtilsInternal.double2String(ueberzins)+
                       " Kredit: "+kredit+linesep);
            if (refAccount!=null)
                ret.append("Referenzkonto: "+refAccount.toString()+linesep);
            ret.append("Kontoauszug Versandart:"+versandart+" Turnus:"+turnus+linesep);
            if (comment!=null)
                ret.append("Bemerkungen: "+comment+linesep);
                
            // *** Adresse, Berechtigte
            
            return ret.toString().trim();
        }
    }
    
    private List entries;
    
    public GVRAccInfo()
    {
        entries=new ArrayList();
    }
    
    public void addEntry(AccInfo info)
    {
        entries.add(info);
    }

    /** Holen aller empfangenen Kontostammdaten.
        @return Array mit einzelnen Konto-Informationen. Das Array ist niemals
        <code>null</code>, kann aber die Länge <code>0</code> haben */
    public AccInfo[] getEntries()
    {
        return (AccInfo[])entries.toArray(new AccInfo[0]);
    }

    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");

        int num=0;
        for (Iterator i=entries.iterator();i.hasNext();) {
            num++;
            ret.append("Kontoinfo #"+num+linesep);
            ret.append(((AccInfo)i.next()).toString()+linesep);
        }

        return ret.toString().trim();
    }
}
