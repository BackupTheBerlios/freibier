
/*  $Id: RSAPrivateCrtKey2.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.security;

import java.math.BigInteger;
import java.security.PrivateKey;

public class RSAPrivateCrtKey2
    implements PrivateKey
{
    private BigInteger p;
    private BigInteger q;
    private BigInteger dP;
    private BigInteger dQ;
    private BigInteger qInv;
    
    public RSAPrivateCrtKey2(BigInteger p,BigInteger q,BigInteger dP,BigInteger dQ,BigInteger qInv)
    {
        this.p=p;
        this.q=q;
        this.dP=dP;
        this.dQ=dQ;
        this.qInv=qInv;
    }
    
    public BigInteger getP()
    {
        return p;
    }

    public BigInteger getQ()
    {
        return q;
    }

    public BigInteger getdP()
    {
        return dP;
    }

    public BigInteger getdQ()
    {
        return dQ;
    }

    public BigInteger getQInv()
    {
        return qInv;
    }

    public byte[] getEncoded()
    {
        return null;
    }
    
    public String getAlgorithm()
    {
        return "RSA";
    }
    
    public String getFormat()
    {
        return null;
    }
}
