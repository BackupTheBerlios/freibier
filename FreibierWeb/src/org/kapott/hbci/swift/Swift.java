
/*  $Id: Swift.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

package org.kapott.hbci.swift;

import org.kapott.hbci.exceptions.HBCI_Exception;

public class Swift
{
    public static String getOneBlock(String stream)
    {
        String ret=null;
        
        int endpos=stream.indexOf("\r\n-");
        while (endpos!=-1 && endpos+3<stream.length() && stream.charAt(endpos+3)!='\r') {
            endpos=stream.indexOf("\r\n-",endpos+1);
        }
        
        if (endpos!=-1) {
            endpos+=3;
            ret=stream.substring(0,endpos);
        }
        
        return ret; 
    }

    public static String getTagValue(String st,String tag,int counter)
    {
        String ret=null;

        int endpos=0;
        while (true) {
            ret=null;

            int startpos=st.indexOf(":"+tag+":",endpos);
            if (startpos!=-1) {
                endpos=st.indexOf("\r\n:",startpos);
                if (endpos==-1) {
                    endpos=st.indexOf("\r\n-",startpos);
                    while (endpos!=-1 && endpos+3<st.length()) {
                        endpos=st.indexOf("\r\n-",endpos+1);
                    }
                }
                if (endpos==-1) {
                    throw new HBCI_Exception("invalid swift stream - no end of tag found: tag="+tag);
                }
                ret=st.substring(startpos+(tag.length()+2),endpos);
            }

            if ((counter--)==0)
                break;
        }

        return ret;
    }

    public static String getTagValue(String st,String tag,String[] suffixes,int counter)
    {
        String ret=null;

        int endpos=0;
        while (true) {
            ret=null;

            int startpos=-1;
            String mytag=null;
            
            for (int i=0;i<suffixes.length;i++) {
                int p=st.indexOf(":"+tag+suffixes[i]+":",endpos);
                
                if (p!=-1) {
                    if (startpos==-1) {
                        startpos=p;
                        mytag=tag+suffixes[i];
                    } else {
                        if (p<startpos) {
                            startpos=p;
                            mytag=tag+suffixes[i];
                        }
                    }
                }
            }
            
            if (startpos!=-1) {
                endpos=st.indexOf("\r\n:",startpos);
                if (endpos==-1) {
                    endpos=st.indexOf("\r\n-",startpos);
                    while (endpos!=-1 && endpos+3<st.length()) {
                        endpos=st.indexOf("\r\n-",endpos+1);
                    }
                }
                if (endpos==-1) {
                    throw new HBCI_Exception("invalid swift stream - no end of tag found: tag="+tag);
                }
                ret=st.substring(startpos+(mytag.length()+2),endpos);
            }

            if ((counter--)==0)
                break;
        }

        return ret;
    }

    public static String packMulti(String st)
    {
        String ret="";
        int    start=0;

        while (true) {
            int pos=st.indexOf("\r\n",start);
            if (pos==-1) {
                ret+=st.substring(start);
                break;
            }
            
            ret+=st.substring(start,pos);
            start=pos+2;
        }

        return ret;
    }

    public static String getMultiTagValue(String st,String tag)
    {
        String ret=null;
        int    pos=st.indexOf("?"+tag);

        if (pos!=-1) {
            int endpos=st.indexOf("?",pos+3);
            if (endpos==-1)
                endpos=st.length();

            ret=st.substring(pos+3,endpos);
        }

        return ret;
    }
    
    public static String getLineFieldValue(String stream,String linenum,int fieldnum)
    {
        String ret=null;
        
        int linepos=0;
        while (true) {
            if (linepos>=stream.length())
                break;
            
            if (stream.charAt(linepos)==linenum.charAt(0)) {
                int end=stream.indexOf("\r\n",linepos);
                if (end==-1)
                    end=stream.length();
                String line=stream.substring(linepos+1,end);
                
                int fieldpos=0;
                for (;fieldnum>0;fieldnum--) {
                    int p=line.indexOf("+",fieldpos);
                    if (p==-1)
                        break;
                    fieldpos=p+1;
                }
                
                if (fieldnum==0) {
                    int p=line.indexOf("+",fieldpos);
                    if (p==-1)
                        p=line.length();
                    ret=line.substring(fieldpos,p);
                    if (ret.length()==0)
                        ret=null;
                }
                
                break;
            }
            
            linepos=stream.indexOf("\r\n",linepos);
            if (linepos==-1)
                break;
            linepos+=2;
        }
        
        return ret;
    }
    
    public static String decodeUmlauts(String st)
    {
        String ret=st.replace('\133','Ä');
               ret=ret.replace('\134','Ö');
               ret=ret.replace('\135','Ü');
               ret=ret.replace('\176','ß');
        return ret;
    }
}
