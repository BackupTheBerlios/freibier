
/*  $Id: GVFormOrder.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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
import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVFormOrder
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "FormOrder";
    }
    
    public GVFormOrder(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport,new HBCIJobResultImpl());

        addConstraint("delivery","delivery",null);
        addConstraint("formcode","code",null);

        addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
        addConstraint("count","count","");
        addConstraint("printaddr","printaddr","N");
        addConstraint("name","Address.name1","");
        addConstraint("name2","Address.name2","");
        addConstraint("street","Address.street_pf","");
        addConstraint("ort","Address.plz_ort","");
        addConstraint("tel","Address.tel","");
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
