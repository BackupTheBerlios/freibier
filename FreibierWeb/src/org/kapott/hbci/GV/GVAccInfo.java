
/*  $Id: GVAccInfo.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRAccInfo;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public class GVAccInfo 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "AccInfo";
    }
    
    public GVAccInfo(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new GVRAccInfo());
 
        addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
        addConstraint("all","allaccounts","N");
    }

    public void extractResults(Properties result,String header,int idx)
    {
        GVRAccInfo.AccInfo info=new GVRAccInfo.AccInfo();
        String st;
        
        info.account=new Konto();
        info.account.blz=result.getProperty(header+".My.KIK.blz");
        info.account.country=result.getProperty(header+".My.KIK.country");
        info.account.number=result.getProperty(header+".My.number");
        info.account.curr=result.getProperty(header+".curr");
        info.account.name=result.getProperty(header+".name");
        info.account.name2=result.getProperty(header+".name2");
        info.account.type=result.getProperty(header+".accbez");
        
        info.comment=result.getProperty(header+".info");
        if ((st=result.getProperty(header+".opendate"))!=null)
            info.created=HBCIUtils.string2Date(st);
        info.habenzins=((st=result.getProperty(header+".habenzins"))!=null)?HBCIUtilsInternal.string2Double(st):-1;
        info.sollzins=((st=result.getProperty(header+".sollzins"))!=null)?HBCIUtilsInternal.string2Double(st):-1;
        info.ueberzins=((st=result.getProperty(header+".overdrivezins"))!=null)?HBCIUtilsInternal.string2Double(st):-1;
        if ((st=result.getProperty(header+".kredit.value"))!=null)
            info.kredit=new Value(st,result.getProperty(header+".kredit.curr"));
        if ((st=result.getProperty(header+".refkto.number"))!=null)
            info.refAccount=new Konto(result.getProperty(header+".refkto.KIK.country"),
                                      result.getProperty(header+".refkto.KIK.blz"),
                                      st);
        info.turnus=((st=result.getProperty(header+".turnus"))!=null)?Integer.parseInt(st):-1;
        info.versandart=((st=result.getProperty(header+".versandart"))!=null)?Integer.parseInt(st):-1;
        info.type=((st=result.getProperty(header+".acctype"))!=null)?Integer.parseInt(st):-1;
 
        ((GVRAccInfo)getJobResult()).addEntry(info);
    }
 
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
