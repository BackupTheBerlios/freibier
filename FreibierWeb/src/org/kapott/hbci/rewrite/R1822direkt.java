
/*  $Id: R1822direkt.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SyntaxElement;

/** Rewriter-Modul für Kontoauszüge der 1822direkt-Bank (und vielleicht andere). Die
    Kontoauszüge können nicht geparst werden, weil das verwendete SWIFT-Format von
    dem in der HBCI-Spezifikation vorgeschriebenen Format abweicht. Dieses Modul
    korrigiert die Fehler in den Kontoauszugsdaten, so dass Kontoauszüge mit
    <em>HBCI4Java</em> wieder zu parsen sind. */
public final class R1822direkt
    extends Rewrite
{
    private String rewriteKUms(String st)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_RWR_KUMS"),HBCIUtils.LOG_DEBUG);

        StringBuffer temp=new StringBuffer(st);
        int          posi=0;
        
        boolean wrongCRLF=false;
        while ((posi=temp.indexOf("\n",posi))!=-1) {
            if (posi==0 || temp.charAt(posi-1)!='\r') {
                temp.replace(posi,posi+1,"\r\n");
                posi+=2;
                wrongCRLF=true;
            } else {
                posi++;
            }
        }

        boolean wrongDelimiter=false;
        posi=0;
        while ((posi=temp.indexOf("@@",posi))!=-1) {
            temp.replace(posi,posi+2,"\r\n");
            wrongDelimiter=true;
        }

        boolean wrongEndSequence=false;
        if (!temp.substring(temp.length()-3,temp.length()).equals("\r\n-")) {
            wrongEndSequence=true;
            
            posi=temp.length()-1;
            while (posi>=0) {
                char ch=temp.charAt(posi);
                if (ch=='\r' || ch=='\n' || ch=='-')
                    posi--;
                else
                    break;
            }
            
            if (posi>=0)
                temp.replace(posi+1,temp.length(),"\r\n-");
        }
        
        if (!temp.toString().equals(st)) {
            HBCIUtils.log("*** this institute produces buggy account statements!",HBCIUtils.LOG_WARN);
            HBCIUtils.log("*** wrongCRLF:"+wrongCRLF
                    +" wrongDelimiter:"+wrongDelimiter
                    +" wrongEnd:"+wrongEndSequence,
                    HBCIUtils.LOG_WARN);
        }
        return temp.toString();
    }

    public MSG incomingData(MSG msg,MsgGen gen)
    {
        String     header="GVRes";
        Properties data=msg.getData();
        
        for (Enumeration i=data.propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.startsWith(header) && 
                key.indexOf("KUms")!=-1 &&
                key.endsWith(".booked")) {
                    
                String st=msg.getValueOfDE(msg.getName()+"."+key);
                st=rewriteKUms(st);
                msg.propagateValue(msg.getName()+"."+key,"B"+st,
                        SyntaxElement.DONT_TRY_TO_CREATE,
                        SyntaxElement.ALLOW_OVERWRITE);
            }
        }

        return msg;
    }
}
