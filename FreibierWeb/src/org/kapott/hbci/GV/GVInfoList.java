
/*  $Id: GVInfoList.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.GV_Result.GVRInfoList;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVInfoList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "InfoList";
    }
    
    public GVInfoList(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new GVRInfoList());

        addConstraint("maxentries","maxentries","");
    }

    protected void extractResults(Properties result,String header,int idx)
    {
        for (int i=0;;i++) {
            GVRInfoList.Info entry=new GVRInfoList.Info();
            String           header2=HBCIUtilsInternal.withCounter(header+".InfoInfo",i);

            if (result.getProperty(header2+".code")==null)
                break;
            
            entry.code=result.getProperty(header2+".code");
            entry.date=HBCIUtils.string2Date(result.getProperty(header2+".version"));
            entry.description=result.getProperty(header2+".descr");
            entry.format=result.getProperty(header2+".format");
            entry.type=result.getProperty(header2+".type");

            for (int j=0;;j++) {
                String hint=result.getProperty(header2+HBCIUtilsInternal.withCounter(".comment",j));
                if (hint==null)
                    break;
                entry.addComment(hint);
            }

            ((GVRInfoList)(jobResult)).addEntry(entry);
        }
    }
}