
/*  $Id: GVFestList.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRFestCondList;
import org.kapott.hbci.GV_Result.GVRFestList;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public class GVFestList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "FestList";
    }
    
    public GVFestList(String name,HBCIPassportInternal passport)
    {
        super(name,passport,new GVRFestList());
    }
    
    public GVFestList(HBCIPassportInternal passport)
    {
        this(getLowlevelName(),passport);
        
        addConstraint("my.number","KTV.number",null);
        addConstraint("my.blz","KTV.KIK.blz",null);
        addConstraint("my.country","KTV.KIK.country",null);
        addConstraint("dummy","allaccounts","N");
    }
    
    protected void extractResults(Properties result,String header,int idx)
    {
        GVRFestList.Entry entry=new GVRFestList.Entry();
        
        entry.anlagebetrag=new Value();
        entry.anlagebetrag.value=HBCIUtils.string2Value(result.getProperty(header+".Anlagebetrag.value"));
        entry.anlagebetrag.curr=result.getProperty(header+".Anlagebetrag.curr");
        
        if (result.getProperty(header+".Anlagekto.number")!=null) {
            entry.anlagekonto=new Konto();
            entry.anlagekonto.blz=result.getProperty(header+".Anlagekto.KIK.blz");
            entry.anlagekonto.country=result.getProperty(header+".Anlagekto.KIK.country");
            entry.anlagekonto.number=result.getProperty(header+".Anlagekto.number");
            getMainPassport().fillAccountInfo(entry.anlagekonto);
        }
        
        if (result.getProperty(header+".Ausbuchungskto.number")!=null) {
            entry.ausbuchungskonto=new Konto();
            entry.ausbuchungskonto.blz=result.getProperty(header+".Ausbuchungskto.KIK.blz");
            entry.ausbuchungskonto.country=result.getProperty(header+".Ausbuchungskto.KIK.country");
            entry.ausbuchungskonto.number=result.getProperty(header+".Ausbuchungskto.number");
            getMainPassport().fillAccountInfo(entry.ausbuchungskonto);
        }
        
        entry.belastungskonto=new Konto();
        entry.belastungskonto.blz=result.getProperty(header+".Belastungskto.KIK.blz");
        entry.belastungskonto.country=result.getProperty(header+".Belastungskto.KIK.country");
        entry.belastungskonto.number=result.getProperty(header+".Belastungskto.number");
        getMainPassport().fillAccountInfo(entry.belastungskonto);
        
        if (result.getProperty(header+".Zinskto.number")!=null) {
            entry.zinskonto=new Konto();
            entry.zinskonto.blz=result.getProperty(header+".Zinskto.KIK.blz");
            entry.zinskonto.country=result.getProperty(header+".Zinskto.KIK.country");
            entry.zinskonto.number=result.getProperty(header+".Zinskto.number");
            getMainPassport().fillAccountInfo(entry.zinskonto);
        }
        
        entry.id=result.getProperty(header+".kontakt");
        
        String st=result.getProperty(header+".kontoauszug");
        entry.kontoauszug=(st!=null)?Integer.parseInt(st):0;
        st=result.getProperty(header+".status");
        entry.status=(st!=null)?Integer.parseInt(st):0;

        entry.verlaengern=result.getProperty(header+".wiederanlage").equals("2");

        if (result.getProperty(header+".Zinsbetrag.value")!=null) {
            entry.zinsbetrag=new Value();
            entry.zinsbetrag.value=HBCIUtils.string2Value(result.getProperty(header+".Zinsbetrag.value"));
            entry.zinsbetrag.curr=result.getProperty(header+".Zinsbetrag.curr");
        }
        
        entry.konditionen=new GVRFestCondList.Cond();
        entry.konditionen.ablaufdatum=HBCIUtils.string2Date(result.getProperty(header+".FestCond.ablaufdate"));
        entry.konditionen.anlagedatum=HBCIUtils.string2Date(result.getProperty(header+".FestCond.anlagedate"));
        entry.konditionen.id=result.getProperty(header+".FestCond.condid");
        entry.konditionen.name=result.getProperty(header+".FestCond.condbez");

        if (result.getProperty(header+".FestCondVersion.version")!=null) {
            entry.konditionen.date=HBCIUtils.strings2Date(result.getProperty(header+".FestCondVersion.date"),
                                                          result.getProperty(header+".FestCondVersion.time"));
            entry.konditionen.version=result.getProperty(header+".FestCondVersion.version");
        }
        
        st=result.getProperty(header+".FestCond.zinsmethode");
        if (st.equals("A"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_30_360;
        else if (st.equals("B"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_2831_360;
        else if (st.equals("C"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_2831_365366;
        else if (st.equals("D"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_30_365366;
        else if (st.equals("E"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_2831_365;
        else if (st.equals("F"))
            entry.konditionen.zinsmethode=GVRFestCondList.Cond.METHOD_30_365;
        
        entry.konditionen.zinssatz=HBCIUtilsInternal.string2Double(result.getProperty(header+".FestCond.zinssatz"));
        entry.konditionen.minbetrag=new Value();
        entry.konditionen.minbetrag.value=HBCIUtils.string2Value(result.getProperty(header+".FestCond.MinBetrag.value"));
        entry.konditionen.minbetrag.curr=result.getProperty(header+".FestCond.MinBetrag.curr");
        entry.konditionen.name=result.getProperty(header+".FestCond.condbez");

        if (result.getProperty(header+".FestCond.MaxBetrag.value")!=null) {
            entry.konditionen.maxbetrag=new Value();
            entry.konditionen.maxbetrag.value=HBCIUtils.string2Value(result.getProperty(header+".FestCond.MaxBetrag.value"));
            entry.konditionen.maxbetrag.curr=result.getProperty(header+".FestCond.MaxBetrag.curr");
        }
        
        if (result.getProperty(header+".Prolong.laufzeit")!=null) {
            entry.verlaengerung=new GVRFestList.Entry.Prolong();
            entry.verlaengerung.betrag=new Value();
            entry.verlaengerung.betrag.value=HBCIUtils.string2Value(result.getProperty(header+".Prolong.BTG.value"));
            entry.verlaengerung.betrag.curr=result.getProperty(header+".Prolong.BTG.curr");
            entry.verlaengerung.laufzeit=Integer.parseInt(result.getProperty(header+".Prolong.laufzeit"));
            entry.verlaengerung.verlaengern=result.getProperty(header+".Prolong.wiederanlage").equals("2");
        }
        
        ((GVRFestList)jobResult).addEntry(entry);
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
