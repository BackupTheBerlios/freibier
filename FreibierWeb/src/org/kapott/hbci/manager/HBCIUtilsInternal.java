
/*  $Id: HBCIUtilsInternal.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.manager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Properties;
import java.util.ResourceBundle;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.passport.HBCIPassport;

public class HBCIUtilsInternal
{

    public static Properties blzs;
    public static Hashtable  callbacks;  // threadgroup->callbackObject
    public static Hashtable  locMsgs;    // threadgroup->resourceBundle

    public static String double2String(double value)
    {
        DecimalFormat format=new DecimalFormat("0.##");
        DecimalFormatSymbols symbols=format.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(symbols);
        format.setDecimalSeparatorAlwaysShown(false);
        return format.format(value);
    }

    public static String getBLZData(String blz)
    {
        return blz!=null?blzs.getProperty(blz,"|||"):"|||";
    }

    public static HBCICallback getCallback()
    {
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        return (HBCICallback)callbacks.get(group);
    }

    public static String getLocMsg(String key)
    {
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        return ((ResourceBundle)locMsgs.get(group)).getString(key);
    }

    public static String getLocMsg(String key,Object o)
    {
        return HBCIUtilsInternal.getLocMsg(key,new Object[] {o});
    }

    public static String getLocMsg(String key,Object[] o)
    {
        return MessageFormat.format(getLocMsg(key),o);
    }

    public static boolean ignoreError(HBCIPassport passport,String paramName,String msg)
    {
        boolean ret=false;
        String  paramValue=HBCIUtils.getParam(paramName,"no");
        
        if (paramValue.equals("yes")) {
            HBCIUtils.log(msg,HBCIUtils.LOG_ERR);
            HBCIUtils.log(getLocMsg("ERR_UTIL_IGNERR",new String[] {paramName,"yes"}),HBCIUtils.LOG_ERR);
            ret=true;
        } else if (paramValue.equals("callback")) {
            StringBuffer sb=new StringBuffer();
            getCallback().callback(passport,
                                   HBCICallback.HAVE_ERROR,
                                   msg,
                                   HBCICallback.TYPE_BOOLEAN,
                                   sb);
            if (sb.length()==0) {
                HBCIUtils.log(msg,HBCIUtils.LOG_ERR);
                HBCIUtils.log(getLocMsg("ERR_UTIL_IGNERR",new String[] {paramName,"callback"}),HBCIUtils.LOG_ERR);
                ret=true;
            }
        }
        
        return ret;
    }

    public static double string2Double(String st)
    {
        return Double.parseDouble(st);
    }

    public static String withCounter(String st, int idx)
    {
        return st + ((idx!=0)?"_"+Integer.toString(idx+1):"");
    }

}
