
/*  $Id: RRVB.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2004  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
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

/** Rewriter-Modul für Kontoauszüge der Raiffeisen-Volksbank Erlangen (und 
    vielleicht anderer). Die Kontoauszüge enthalten falsche Delimiter für die
    einzelnen Buchungstage. Dieses Modul korrigiert die Fehler in den 
    Kontoauszugsdaten (thx to Ludwig Balke). */
public class RRVB 
    extends Rewrite
{
    private String rewriteKUms(String st)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_RWR_KUMS"),HBCIUtils.LOG_DEBUG);

        StringBuffer temp=new StringBuffer(st);
        int          posi=0;
        
        while ((posi=temp.indexOf("\r\n:20:",posi))!=-1) {
            if (posi>0 && temp.charAt(posi-1)!='-') {
                temp.replace(posi,posi+2,"\r\n-\r\n");
                posi+=9;
            } else {
                posi++;
            }
        }

        if (!temp.toString().equals(st)) {
            HBCIUtils.log("*** this institute produces buggy account statements!",HBCIUtils.LOG_WARN);
            HBCIUtils.log("*** days are not delimited by '\\r\\n-\\r\\n'",HBCIUtils.LOG_WARN);
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
