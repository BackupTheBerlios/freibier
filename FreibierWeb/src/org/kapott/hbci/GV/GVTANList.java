
/*  $Id: GVTANList.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.GV;

import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRTANList;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;

public class GVTANList 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "TANListList";
    }
    
    public GVTANList(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new GVRTANList());
    }
    
    public void extractResults(Properties result,String header,int idx)
    {
        GVRTANList.TANList list=new GVRTANList.TANList();
        
        list.zustand=result.getProperty(header+".zustand").charAt(0);
        list.number=result.getProperty(header+".listnumber");
        String st=result.getProperty(header+".date");
        if (st!=null)
            list.date=HBCIUtils.string2Date(st);
            
        for (int i=0;;i++) {
            String tanheader=HBCIUtilsInternal.withCounter(header+".TANInfo",i);
            
            st=result.getProperty(tanheader+".usage");
            if (st==null)
                break;
            
            GVRTANList.TANInfo info=new GVRTANList.TANInfo();
            
            info.usage=Integer.parseInt(st);
            info.tan=result.getProperty(tanheader+".tan");
            
            if (info.usage!=0)
                info.timestamp=HBCIUtils.strings2Date(result.getProperty(tanheader+".usagedate"),
                                                      result.getProperty(tanheader+".usagetime"));
                
            list.addTANInfo(info);
        }
        
        ((GVRTANList)jobResult).addTANList(list);
    }
}
