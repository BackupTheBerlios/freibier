
/*  $Id: GVCustomMsg.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVCustomMsg
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "CustomMsg";
    }
    
    public GVCustomMsg(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new HBCIJobResultImpl());
        
        addConstraint("msg","msg",null);

        addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
        addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("curr","curr","");
        addConstraint("betreff","betreff","");
        addConstraint("recpt","recpt","");
    }
    
    public void setParam(String paramName,String value)
    {
        if (paramName.equals("msg")) {
            String st_maxlen=getJobRestrictions().getProperty("maxlen");
            
            if (st_maxlen!=null) {
                int maxlen=Integer.parseInt(st_maxlen);
                
                if (value.length()>maxlen) {
                    String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_TOOLONG",new String[] {paramName,value,Integer.toString(maxlen)});
                    if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                        throw new InvalidUserDataException(msg);
                }
            }
        }
        super.setParam(paramName,value);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
