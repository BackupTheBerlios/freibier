
/*  $Id: GVCardList.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRCardList;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.structures.Value;

public class GVCardList 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "CardList";
    }
    
    public GVCardList(HBCIPassportInternal passport)
   {
       super(getLowlevelName(),passport,new GVRCardList());
       
       addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
       addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
       addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
   }
   
   public void extractResults(Properties result,String header,int idx)
   {
       GVRCardList.CardInfo info=new GVRCardList.CardInfo();
       String st;
       
       info.cardnumber=result.getProperty(header+".cardnumber");
       info.cardordernumber=result.getProperty(header+".nextcardnumber");
       info.cardtype=Integer.parseInt(result.getProperty(header+".cardtype"));
       info.comment=result.getProperty(header+".comment");
       if ((st=result.getProperty(header+".cardlimit.value"))!=null) {
           info.limit=new Value(st,result.getProperty(header+".cardlimit.curr"));
       }
       info.owner=result.getProperty(header+".name");
       if ((st=result.getProperty(header+".validfrom"))!=null) {
           info.validFrom=HBCIUtils.string2Date(st);
       }
       if ((st=result.getProperty(header+".validuntil"))!=null) {
           info.validUntil=HBCIUtils.string2Date(st);
       }
       
       ((GVRCardList)getJobResult()).addEntry(info);
   }
    
   public void verifyConstraints()
   {
       super.verifyConstraints();
       checkAccountCRC("my");
   }
}