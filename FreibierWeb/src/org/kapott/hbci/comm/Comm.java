
/*  $Id: Comm.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

package org.kapott.hbci.comm;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.CanNotParseMessageException;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.ParseErrorException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci.rewrite.Rewrite;

public abstract class Comm
{
    protected Filter               filter;
    private   HBCIPassportInternal passport;
    
    protected abstract void ping(MSG msg);
    protected abstract StringBuffer pong(MsgGen gen);
    protected abstract void closeConnection();
        
    protected Comm(HBCIPassportInternal passport)
    {
        this.passport=passport;
        this.filter=passport.getCommFilter();
        
        HBCIUtilsInternal.getCallback().callback(passport,HBCICallback.NEED_CONNECTION,
                HBCIUtilsInternal.getLocMsg("CALLB_NEED_CONN"),HBCICallback.TYPE_NONE,new StringBuffer());
    }

    public MSG pingpong(MsgGen gen, String msgName, MSG msg)
    {
        // ausgehende nachricht versenden
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_MSG_SEND,null);
        ping(msg);

        // nachricht empfangen
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_MSG_RECV,null);
        String st = pong(gen).toString();

        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_RECVMSG")+": "+st,HBCIUtils.LOG_DEBUG2);
        MSG retmsg=null;

        try {
            // erzeugen der liste aller rewriter
            String rewriters_st=HBCIUtils.getParam("kernel.rewriter");
            ArrayList al=new ArrayList();
            StringTokenizer tok=new StringTokenizer(rewriters_st,",");
            while (tok.hasMoreTokens()) {
                String rewriterName=tok.nextToken().trim();
                if (rewriterName.length()!=0) {
                    Class cl=this.getClass().getClassLoader().loadClass("org.kapott.hbci.rewrite.R"+
                                                                        rewriterName);
                    Constructor con=cl.getConstructor(null);
                    Rewrite rewriter=(Rewrite)(con.newInstance(null));
                    al.add(rewriter);
                }
            }
            Rewrite[] rewriters=(Rewrite[])al.toArray(new Rewrite[0]);
    
            // alle rewriter für verschlüsselte nachricht durchlaufen
            for (int i=0;i<rewriters.length;i++) {
                st=rewriters[i].incomingCrypted(st,gen);
            }
            
            // versuche, nachricht als verschlüsselte nachricht zu parsen
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_MSG_PARSE,"CryptedRes");
            try {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_TRYCRYPT"),HBCIUtils.LOG_DEBUG);
                retmsg = MSGFactory.getInstance().createMSG("CryptedRes",st,st.length(),gen,MSG.DONT_CHECK_SEQ);
            } catch (ParseErrorException e) {
                // wenn das schiefgeht...
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_TRYRES",msgName+"Res"),HBCIUtils.LOG_DEBUG);

                // alle rewriter durchlaufen, um nachricht evtl. als unverschlüsselte msg zu parsen
                gen.set("_origSignedMsg",st);
                for (int i=0;i<rewriters.length;i++) {
                    st=rewriters[i].incomingClearText(st,gen);
                }
                
                // versuch, nachricht als unverschlüsselte msg zu parsen
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_MSG_PARSE,msgName+"Res");
                retmsg = MSGFactory.getInstance().createMSG(msgName+"Res",st,st.length(),gen);
            }
        } catch (Exception ex) {
            throw new CanNotParseMessageException(HBCIUtilsInternal.getLocMsg("EXCMSG_CANTPARSE"),st,ex);
        }

        return retmsg;
    }
    
    public static Comm getInstance(String name,HBCIPassportInternal passport)
    {
        try {
            Class cl=Class.forName("org.kapott.hbci.comm.Comm"+name);
            Constructor cons=cl.getConstructor(new Class[] {HBCIPassportInternal.class});
            return (Comm)cons.newInstance(new Object[] {passport});
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANTCREATECOMM",name),e);
        }
    }
    
    protected HBCIPassportInternal getPassport()
    {
        return passport;
    }
    
    public void close()
    {
        closeConnection();
        HBCIUtilsInternal.getCallback().callback(passport,HBCICallback.CLOSE_CONNECTION,
                HBCIUtilsInternal.getLocMsg("CALLB_CLOSE_CONN"),HBCICallback.TYPE_NONE,new StringBuffer());
    }
}
