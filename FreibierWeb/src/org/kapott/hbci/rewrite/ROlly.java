
/*  $Id: ROlly.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.rewrite;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.status.HBCIMsgStatus;

/** Fehlerkorrekturen für falsche Sequenznummern, fehlende Nachrichtenreferenzen (MsgRef)
    und unerwartete Segmente in der Dialoginitialisierung.
    Bis jetzt ist nur die Baden-Württembergische Bank für diese Fehler bekannt. Dabei
    handelt es sich zum einen um das Problem, dass bei Fehlermeldungen, die unerwartete
    HBCI-Nachrichten betreffen, manchmal die Datenelementgruppe "Nachrichtenbezug" (MsgRef)
    im Nachrichtenkopf fehlt. Des weiteren werden die einzelnen Segmente einer HBCI-Nachricht
    falsch nummeriert. Die falschen Sequenznummern werden mit diesem
    Modul korrigiert. Außerdem wird in den Dialoginitialisierungs-Antwortnachrichten
    ein ungültiges Segment ("IIDIA") eingestellt, welches durch dieses Modul entfernt wird. */
public final class ROlly
    extends Rewrite
{
    // *** msgsize muss angepasst werden
    public String incomingCrypted(String st, MsgGen gen) 
    {
        int idx=st.indexOf("'");
        if (idx!=-1) {
            try {
                String msghead_st=st.substring(0,idx);
                int plusidx=0;
                for (int i=0;i<5;i++)
                    plusidx=msghead_st.indexOf("+",plusidx+1);
                if (plusidx==-1) {
                    HBCIUtils.log("*** MsgRef is missing, adding it", HBCIUtils.LOG_WARN);
                    String[] des={"dialogid","msgnum"};
                    for (int i=0;i<2;i++) {
                        HBCIMsgStatus msgStatus=(HBCIMsgStatus)getData("msgStatus");
                        String        msgName=(String)getData("msgName");
                        String        temp=(msgStatus.getData().getProperty("orig_"+msgName+".MsgHead."+des[i]));
                        HBCIUtils.log(("*** setting MsgRef."+des[i]+" to "+temp),HBCIUtils.LOG_WARN);
                        msghead_st+=(i==0?"+":":");
                        msghead_st+=temp;
                    }
                    st=new StringBuffer(st).replace(0,idx,msghead_st).toString();
                }
            } catch (Exception ex) {
                throw new HBCI_Exception("*** error while fixing missing MsgRef",ex);
            }
        }
        return st;
    }

    public String incomingClearText(String st,MsgGen gen) 
    {
        StringBuffer sb=new StringBuffer(st);

        int idx=sb.indexOf("'IIDIA:");
        if (idx!=-1) {
            int idx2=sb.indexOf("'",idx+1);
            HBCIUtils.log("*** removing invalid segment '"+sb.substring(idx+1,idx2+1)+"'",HBCIUtils.LOG_WARN);
            sb.delete(idx+1,idx2+1);
        }

        boolean quoteNext=false;
        int correctSeq=1;
        
        for (int i=0;i<sb.length();i++) {
            char ch=sb.charAt(i);
            
            if (!quoteNext && ch=='@') {
                // skip binary values
                idx=sb.indexOf("@",i+1);
                String len_st=sb.substring(i+1,idx);
                i+=Integer.parseInt(len_st)+1+len_st.length();
            } else if (!quoteNext && ch=='\'' || i==0) {
                idx=sb.indexOf(":",i+1);
                if (idx!=-1) {
                    int idx2=sb.indexOf(":",idx+1);
                    int seq=Integer.parseInt(sb.substring(idx+1,idx2));
                    if (seq!=correctSeq) {
                        HBCIUtils.log(("*** found wrong sequence number "+seq+"; replacing with "+correctSeq),HBCIUtils.LOG_WARN);
                        sb.replace(idx+1,idx2,Integer.toString(correctSeq));
                    }
                    i=idx2;
                }
                correctSeq++;
            }
            quoteNext=!quoteNext && ch=='?';
        }
        
        return sb.toString();
    }
}
