
/*  $Id: GVRStatus.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.status.HBCIRetVal;

/** <p>Ergebnisse einer Statusprotokoll-Abfrage.
    Ein Statusprotokoll enth�lt zu allen eingereichten Auftr�gen
    den Bearbeitungsstatus. Die einzelnen Auftr�ge werden durch
    die HBCI-Daten identifiziert, mit denen sie eingereicht wurden
    (Dialog-ID, Nachrichtennummer, Segmentnummer). Um diese Daten
    nicht manuell verwalten zu m�ssen, werden sie in der sogenannten
    Job-ID (siehe {@link org.kapott.hbci.GV_Result.HBCIJobResultImpl#getJobId()})
    zusammengefasst. </p>
    <p>In dieser Klasse werden die Antwortdaten f�r eine Statusprotokollabfrage
    gespeichert. Dabei handelt es sich in der Regel um mehr als einen
    Protokolleintrag. Es kann der Protokolleintrag f�r eine gegebene Job-ID
    extrahiert werden.</p> */
public final class GVRStatus
    extends HBCIJobResultImpl
{
    /** Daten f�r einen einzelnen Eintrag im Statusprotokoll. Ein Eintrag enth�lt
        Informationen zu genau einem eingereichten Auftrag */
    public static class Entry
    {
        /** Dialog-ID, mit der der Auftrag eingereicht wurde */
        public String     dialogid;
        /** Nachrichtennummer innerhalb des Dialoges, in dem der Auftrag eingereicht wurde */
        public String     msgnum;
        /** Zeitpunkt der Einreichung */
        public Date       timestamp;
        /** Status (ein HBCI-Returncode) des Auftrages */
        public HBCIRetVal retval;
        
        /** Gibt die Job-ID des Jobs zur�ck, zu dem dieser Statusprotokolleintrag geh�rt. 
            @return Job-ID */
        public String getJobId()
        {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
            return format.format(timestamp)+"/"+dialogid+"/"+msgnum+"/"+retval.segref;
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            
            ret.append(HBCIUtils.date2String(timestamp)+" "+HBCIUtils.time2String(timestamp));
            ret.append(" ");
            ret.append(dialogid);
            ret.append("/");
            ret.append(msgnum);
            ret.append("/");
            ret.append(retval.toString());
            
            return ret.toString();
        }
    }
    
    private List entries;
    
    public GVRStatus()
    {
        entries=new ArrayList();
    }
    
    public void addEntry(Entry entry)
    {
        entries.add(entry);
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (Iterator i=entries.iterator();i.hasNext();) {
            Entry e=(Entry)i.next();
            ret.append(e.toString());
            ret.append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
    
    /** Gibt alle Eintr�ge des Statusprotokolls in einem Array zur�ck.
        @return Array mit Statusprotokolleintr�gen */
    public Entry[] getStatusData()
    {
        return (Entry[])entries.toArray(new Entry[0]);
    }
    
    /** Gibt den Protokoll-Eintrag zu einem bestimmten Job zur�ck.
        Liefert <code>null</code>, wenn der Eintrag f�r die angegebene Job-ID
        nicht im Statusprotokoll vorhanden ist. 
        @param jobId die Job-ID, f�r die Informationen zur�ckgegeben werden sollen
        @return Eintrag im Statusprotokoll, der zu dem entsprechenden Auftrag geh�rt;
                <code>null</code>, wenn kein solcher Auftrag gefunden wurde */
    public Entry getJobEntry(String jobId)
    {
        Entry ret=null;
        
        for (Iterator i=entries.iterator();i.hasNext();) {
            Entry entry=(Entry)i.next();
            
            if (entry.getJobId().equals(jobId)) {
                ret=entry;
                break;
            }
        }
        
        return ret;
    }
}