
/*  $Id: GVStatus.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRStatus;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIRetVal;

public final class GVStatus
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "Status";
    }
    
    public GVStatus(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new GVRStatus());

        addConstraint("startdate","startdate","");
        addConstraint("enddate","enddate","");
        addConstraint("maxentries","maxentries","");
        
        addConstraint("jobid",null,"");
    }
    
    protected void extractResults(Properties result, String header, int idx)
    {
        GVRStatus.Entry entry=new GVRStatus.Entry();
        
        entry.dialogid=result.getProperty(header+".MsgRef.dialogid");
        entry.msgnum=result.getProperty(header+".MsgRef.msgnum");
        entry.retval=new HBCIRetVal(result,
                                    header+".RetVal",
                                    result.getProperty(header+".segref"));
        entry.retval.element=null;
                                    
        String date=result.getProperty(header+".date");
        String time=result.getProperty(header+".time");
        entry.timestamp=HBCIUtils.strings2Date(date,time);
        
        ((GVRStatus)jobResult).addEntry(entry);
    }
    
    public void setParam(String paramName,String value)
    {
        if (paramName.equals("jobid")) {
            try {
                Date dateOfJob=new SimpleDateFormat("yyyyMMdd").parse(value.substring(0,value.indexOf("/")));
                setParam("startdate",dateOfJob);
                setParam("enddate",dateOfJob);
            } catch (Exception e) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_CANTEXTRACTDATE",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),
                                           "client.errors.ignoreWrongJobDataErrors",
                                           msg+": "+HBCIUtils.exception2String(e))) {
                    throw new InvalidUserDataException(msg,e);
                }
            }
        } else {
            super.setParam(paramName,value);
        }
    }
}
