
/*  $Id: GVTermUeb.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRTermUeb;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVTermUeb
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "TermUeb";
    }
    
    public GVTermUeb(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new GVRTermUeb());

        addConstraint("src.number","My.number",null);
        addConstraint("dst.blz","Other.KIK.blz",null);
        addConstraint("dst.number","Other.number",null);
        addConstraint("btg.value","BTG.value",null);
        addConstraint("btg.curr","BTG.curr",null);
        addConstraint("name","name",null);
        addConstraint("date","date",null);

        addConstraint("src.blz","My.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("src.country","My.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("dst.country","Other.KIK.country",passport.getCountry());
        addConstraint("name2","name2","");
        addConstraint("key","key","51");

        Properties parameters=getJobRestrictions();
        int        maxusage=Integer.parseInt(parameters.getProperty("maxusage"));

        for (int i=0;i<maxusage;i++) {
            String name=HBCIUtilsInternal.withCounter("usage",i);
            addConstraint(name,"usage."+name,"");
        }
    }

    protected void extractResults(Properties result,String header,int idx)
    {
        String orderid=result.getProperty(header+".orderid");
        ((GVRTermUeb)(jobResult)).setOrderId(orderid);
        
        if (orderid!=null && orderid.length()!=0) {
            Properties p=getParams();
            Properties p2=new Properties();
            
            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                p2.setProperty(key.substring(key.indexOf(".")+1),
                               p.getProperty(key));
            }
            
            getMainPassport().setPersistentData("termueb_"+orderid,p2);
        }
    }
    
    public void setParam(String paramName,String value)
    {
        Properties res=getJobRestrictions();

        if (paramName.equals("key")) {
            boolean atLeastOne=false;
            boolean found=false;

            for (int i=0;;i++) {
                String st=res.getProperty(HBCIUtilsInternal.withCounter("key",i));

                if (st==null)
                    break;

                atLeastOne=true;

                if (st.equals(value)) {
                    found=true;
                    break;
                }
            }

            if (atLeastOne&&!found) {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_INV_KEY",value);
                if (!HBCIUtilsInternal.ignoreError(getMainPassport(),"client.errors.ignoreWrongJobDataErrors",msg))
                    throw new InvalidUserDataException(msg);
            }
        }
        
        super.setParam(paramName,value);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("src");
        checkAccountCRC("dst");
    }
}
