
/*  $Id: SigFactory.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.security.factory;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportList;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.security.Sig;
import org.kapott.hbci.tools.ObjectFactory;

public class SigFactory 
    extends ObjectFactory 
{
    private static SigFactory instance;
    
    public static SigFactory getInstance()
    {
        if (instance==null) {
            instance=new SigFactory();
        }
        return instance;
    }
    
    private SigFactory()
    {
        maxPoolSize=Integer.parseInt(HBCIUtils.getParam("kernel.objpool.Sig","8"));
    }
    
    public Sig createSig(MSG msg, MsgGen gen, HBCIPassportList passports)
    {
        Sig ret;
        
        if (freeObjects.isEmpty()) {
            ret=new Sig(msg,gen,passports);
            addToUsedPool(ret);
        } else {
            ret=(Sig)getFreeObject();
            try {
                ret.init(msg,gen,passports);
                addToUsedPool(ret);
            } catch (RuntimeException e) {
                addToFreePool(ret);
                throw e;
            }
        }
        
        return ret;
    }
    
    public void unuseObject(Object o)
    {
        if (o!=null) {
            ((Sig)o).destroy();
            super.unuseObject(o);
        }
    }
}
