
/*  $Id: GVDauerDel.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVDauerDel
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "DauerDel";
    }
    
    public GVDauerDel(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new HBCIJobResultImpl());
        
        addConstraint("orderid","orderid",null);
        addConstraint("date","date","");
    }
    
    public void setParam(String paramName,String value)
    {
        if (paramName.equals("date")) {
            Properties res=getJobRestrictions();
            String st_cantermdel=res.getProperty("cantermdel");
            
            if (st_cantermdel!=null && st_cantermdel.equals("N")) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_SCHEDDELSTANDORDUNAVAIL");
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
            
            // *** minpretime und maxpretime auswerten
        } else if (paramName.equals("orderid")) {
            Properties p=(Properties)getMainPassport().getPersistentData("dauer_"+value);
            if (p==null) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_NOSUCHSO",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
                p=new Properties();
            }

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();

                if (!key.equals("date") &&
                    !key.startsWith("Aussetzung.")) {
                    setLowlevelParam(getName()+"."+key,
                                     p.getProperty(key));
                }
            }
        }
        
        super.setParam(paramName,value);
    }
}
