
/*  $Id: SyntaxDTAUS.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.datatypes;

import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

public class SyntaxDTAUS
     extends SyntaxAN
{
    public static String check(String st)
    {
        st=st.toUpperCase();
        st=st.replace('ä','\133').replace('Ä','\133');
        st=st.replace('ö','\134').replace('Ö','\134');
        st=st.replace('ü','\135').replace('Ü','\135');
        st=st.replace('ß','\176');

        int len=st.length();
        for (int i=0;i<len;i++) {
            char ch=st.charAt(i);

            if (!((ch>='A' && ch<='Z') ||
                  (ch>='0' && ch<='9') ||
                  (ch==' ') || (ch=='.') || (ch==',') ||
                  (ch=='&') || (ch=='-') || (ch=='+') ||
                  (ch=='*') || (ch=='%') || (ch=='/') ||
                  (ch=='$') || 
                  (ch==0x5B) || (ch==0x5C) || (ch==0x5D) || (ch==0x7E))) {              // Ä Ö Ü ß
                
                String msg=HBCIUtilsInternal.getLocMsg("EXC_DTAUS_INV_CHAR",Character.toString(ch));
                if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreWrongDataSyntaxErrors",msg)) {
                    throw new InvalidArgumentException(msg);
                }
                st=st.replace(ch,' ');
            }
        }

        return st;
    }

    public SyntaxDTAUS(String x, int minlen, int maxlen)
    {
        super(quote(check(x.trim())),minlen,maxlen);
    }

    public void init(String x, int minlen, int maxlen)
    {
        super.init(quote(check(x.trim())),minlen,maxlen);
    }

    protected SyntaxDTAUS()
    {
        super();
    }
    
    protected void init()
    {
        super.init();
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        setContent(check(unquote(st)),minsize,maxsize);
        res.delete(0,endidx);
    }
    
    public SyntaxDTAUS(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public String toString()
    {
        String ret=super.toString();
        ret=ret.replace('\133','Ä');
        ret=ret.replace('\134','Ö');
        ret=ret.replace('\135','Ü');
        ret=ret.replace('\176','ß');
        return ret;
    }
}
