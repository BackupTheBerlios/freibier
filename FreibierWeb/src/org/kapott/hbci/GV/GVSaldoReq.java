
/*  $Id: GVSaldoReq.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;

public class GVSaldoReq
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "Saldo";
    }
    
    public GVSaldoReq(String name,HBCIPassportInternal passport)
    {
        super(name,passport,new GVRSaldoReq());
    }

    public GVSaldoReq(HBCIPassportInternal passport)
    {
        this(getLowlevelName(),passport);

        addConstraint("maxentries","maxentries","");
        addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
        addConstraint("dummyall","allaccounts", "N");
    }
    
    protected void extractResults(Properties result,String header,int idx)
    {
        GVRSaldoReq.Info info=new GVRSaldoReq.Info();

        info.konto=new Konto();
        info.konto.country=result.getProperty(header+".KTV.KIK.country");
        info.konto.blz=result.getProperty(header+".KTV.KIK.blz");
        info.konto.number=result.getProperty(header+".KTV.number");
        info.konto.type=result.getProperty(header+".kontobez");
        info.konto.curr=result.getProperty(header+".curr");
        getMainPassport().fillAccountInfo(info.konto);
        
        info.ready=new Saldo();
        String st=result.getProperty(header+".booked.value");
        info.ready.cd=result.getProperty(header+".booked.CreditDebit");
        info.ready.value.value=(st!=null)?HBCIUtils.string2Value(st):0;
        info.ready.value.curr=result.getProperty(header+".booked.curr");
        info.ready.timestamp=HBCIUtils.strings2Date(result.getProperty(header+".booked.date"),
                                                    result.getProperty(header+".booked.time"));
        
        st=result.getProperty(header+".pending.value");
        if (st!=null) {
            info.unready=new Saldo();
            info.unready.cd=result.getProperty(header+".pending.CreditDebit");
            info.unready.value.value=HBCIUtils.string2Value(st);
            info.unready.value.curr=result.getProperty(header+".pending.curr");
            info.unready.timestamp=HBCIUtils.strings2Date(result.getProperty(header+".pending.date"),
                                                          result.getProperty(header+".pending.time"));
        }
        
        st=result.getProperty(header+".kredit.value");
        if (st!=null) {
            info.kredit=new Value();
            info.kredit.value=HBCIUtils.string2Value(st);
            info.kredit.curr=result.getProperty(header+".kredit.curr");
        }
        
        st=result.getProperty(header+".available.value");
        if (st!=null) {
            info.available=new Value();
            info.available.value=HBCIUtils.string2Value(st);
            info.available.curr=result.getProperty(header+".available.curr");
        }
        
        st=result.getProperty(header+".used.value");
        if (st!=null) {
            info.used=new Value();
            info.used.value=HBCIUtils.string2Value(st);
            info.used.curr=result.getProperty(header+".used.curr");
        }
        
        ((GVRSaldoReq)(jobResult)).store(info);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
