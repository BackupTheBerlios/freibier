
/*  $Id: GVUebForeign.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

public class GVUebForeign 
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "UebForeign";
    }
    
    public GVUebForeign(String name,HBCIPassportInternal passport)
    {
        super(name,passport,new HBCIJobResultImpl());
    }

    public GVUebForeign(HBCIPassportInternal passport)
    {
        this(getLowlevelName(),passport);

        addConstraint("src.number","My.number",null);
        addConstraint("src.name","myname",null);
        addConstraint("dst.name","othername",null);
        addConstraint("dst.kiname","otherkiname",null);
        addConstraint("btg.value","BTG.value",null);
        addConstraint("btg.curr","BTG.curr",null);

        addConstraint("src.blz","My.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("src.country","My.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("dst.country","Other.KIK.country","");
        addConstraint("dst.blz","Other.KIK.blz","");
        addConstraint("dst.number","Other.number","");
        addConstraint("dst.iban","otheriban","");
        addConstraint("kostentraeger","kostentraeger","1");
        addConstraint("usage","usage","");
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("src");
    }
}
