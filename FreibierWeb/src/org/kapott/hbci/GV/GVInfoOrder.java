
/*  $Id: GVInfoOrder.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRInfoOrder;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVInfoOrder
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "InfoDetails";
    }
    
    public GVInfoOrder(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new GVRInfoOrder());

        addConstraint("code","InfoCodes.code",null);
        
        addConstraint("name","Address.name1","");
        addConstraint("name2","Address.name2","");
        addConstraint("street","Address.street_pf","");
        addConstraint("ort","Address.ort","");
        addConstraint("plz","Address.plz_ort","");
        addConstraint("plz","Address.plz","");
        addConstraint("country","Address.country","");
        addConstraint("tel","Address.tel","");
        addConstraint("fax","Address.fax","");
        addConstraint("email","Address.email","");

        for (int i=1;i<10;i++) {
            addConstraint(HBCIUtilsInternal.withCounter("code",i),
                          HBCIUtilsInternal.withCounter("InfoCodes.code",i),
                          "");
        }
    }

    protected void extractResults(Properties result,String header,int idx)
    {
        for (int i=0;;i++) {
            String header2=HBCIUtilsInternal.withCounter(header+".Info",i);
            
            if (result.getProperty(header2+".code")==null)
                break;
            
            GVRInfoOrder.Info entry=new GVRInfoOrder.Info();
            
            entry.code=result.getProperty(header2+".code");
            entry.msg=result.getProperty(header2+".msg");

            ((GVRInfoOrder)(jobResult)).addEntry(entry);
        }
    }
}
