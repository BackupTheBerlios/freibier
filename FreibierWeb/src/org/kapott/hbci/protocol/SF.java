
/*  $Id: SF.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

package org.kapott.hbci.protocol;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.protocol.factory.MultipleSEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleSFsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class SF
     extends SyntaxElement
{
    protected MultipleSyntaxElements createNewChildContainer(Node ref, Document syntax)
    {
        MultipleSyntaxElements ret=null;

        if ((ref.getNodeName()).equals("SEG"))
            addChildContainer((ret=MultipleSEGsFactory.getInstance().createMultipleSEGs(ref, getPath(), syntax)));
        else if ((ref.getNodeName()).equals("SF"))
            addChildContainer((ret=MultipleSFsFactory.getInstance().createMultipleSFs(ref, getPath(), syntax)));

        return ret;
    }

    protected String getElementTypeName()
    {
        return "SF";
    }

    public SF(String type, String name, String path, int idx, Document syntax)
    {
        super(type, name, path, idx, syntax);
    }

    public void init(String type, String name, String path, int idx, Document syntax)
    {
        super.init(type,name,path,idx,syntax);
    }

    public String toString(int zero)
    {
        StringBuffer ret = new StringBuffer(256);

        if (isValid())
            for (Iterator i = getChildContainers().listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements list = (MultipleSyntaxElements)(i.next());

                if (list != null)
                    ret.append(list.toString(0));
            }

        return ret.toString();
    }

    // -------------------------------------------------------------------------------------------

    public SF(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable predefs,Hashtable valids)
    {
        super(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    public void init(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable predefs,Hashtable valids)
    {
        super.init(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    protected char getInDelim()
    {
        return '\'';
    }
    
    private String extractSegCode(StringBuffer sb)
    {
        String ret="";
        
        if (sb.length()>1) {
            char ch=sb.charAt(0);
            int startpos=0;
            if (ch=='+' || ch==':' || ch=='\'')
                startpos++;
            int endpos=sb.indexOf(":",startpos+1);
            if (endpos==-1)
                endpos=sb.length();
            ret=sb.substring(startpos,endpos);
        }
        
        return ret;
    }
    
    private String getRefSegCode(Node segref,Document syntax)
    {
        String segname=((Element)segref).getAttribute("type");
        String ret=HBCIUtils.getParam("segcodes_"+segname,"");
        
        if (ret.equals("")) {
            Element segdef=syntax.getElementById(segname);
            NodeList valueElems=segdef.getElementsByTagName("value");
            int len=valueElems.getLength();
            for (int i=0;i<len;i++) {
                Node valueNode=valueElems.item(i);
                if (valueNode.getNodeType()==Node.ELEMENT_NODE) {
                    String pathAttr=((Element)valueNode).getAttribute("path");
                    if (pathAttr.equals("SegHead.code")) {
                        ret=valueNode.getFirstChild().getNodeValue();
                        HBCIUtils.setParam("segcodes_"+segname,ret);
                        break;
                    }
                }
            }
        }
        
        return ret;
    }

    protected MultipleSyntaxElements parseNewChildContainer(Node segref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable predefs,Hashtable valids)
    {
        MultipleSyntaxElements ret=null;

        if ((segref.getNodeName()).equals("SEG")) {
            // *** this is a hack to speed up parsing of segments
            // (params, customres); das funktioniert so, dass zunächst aus dem zu parsenden
            // string der nächste seghead.code extrahiert wird (string-operationen); außerdem
            // wird ermittelt, wie der seghead.code *des* segmentes ist (segref), als welches das
            // nächste response-token *eigentlich* geparst werden soll. stimmen die beiden codes
            // nicht überein, so kann das nächste response-token mit sicherheit nicht als
            // segref-segment geparst werden, und es wird erst gar nicht versucht.
            // die zuordnung "segref"-->"seghead.code" wird nicht jedesmal neu durch nachsehen
            // in der syntax-spez aufgelöst, sondern es ist ein entsprechender cache
            // implementiert (hashtable:segname-->seghead.code).
            
            String nextSegCode=extractSegCode(res);
            String segRefCode=getRefSegCode(segref,syntax);
            
            if (segRefCode.equals(nextSegCode) || segRefCode.equals("")) {
                addChildContainer((ret=MultipleSEGsFactory.getInstance().createMultipleSEGs(segref, getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids)));
            }
        } else if ((segref.getNodeName()).equals("SF")) {
            addChildContainer((ret=MultipleSFsFactory.getInstance().createMultipleSFs(segref, getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids)));
        }

        return ret;
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        if (isValid()) {
            for (Iterator i=getChildContainers().iterator();i.hasNext();) {
                MultipleSyntaxElements l=(MultipleSyntaxElements)(i.next());
                if (l!=null) {
                    l.getElementPaths(p,segref,null,null);
                }
            }
        }
    }
    
    public void destroy()
    {
        List childContainers=getChildContainers();
        for (Iterator i=childContainers.iterator();i.hasNext();) {
            Object child=i.next();
            if (child instanceof MultipleSFs) {
                MultipleSFsFactory.getInstance().unuseObject(child);
            } else {
                MultipleSEGsFactory.getInstance().unuseObject(child);
            }
        }
        
        super.destroy();
    }
}
