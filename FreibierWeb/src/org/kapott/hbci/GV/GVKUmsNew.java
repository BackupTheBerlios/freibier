
/*  $Id: GVKUmsNew.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import org.kapott.hbci.passport.HBCIPassportInternal;

public final class GVKUmsNew
    extends GVKUmsAll
{
    public static String getLowlevelName()
    {
        return "KUmsNew";
    }
    
    public GVKUmsNew(HBCIPassportInternal passport)
    {
        super(getLowlevelName(),passport);

        addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
        addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("maxentries","maxentries","");
        addConstraint("dummyall","allaccounts","N");
    }
}
