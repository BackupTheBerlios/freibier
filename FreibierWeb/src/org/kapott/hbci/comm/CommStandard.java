
/*  $Id: CommStandard.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Hashtable;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.ParseErrorException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.factory.SEGFactory;

public final class CommStandard
    extends Comm
{
    Socket s;                /**< @internal @brief The socket for communicating with the server. */
    OutputStream o;          /**< @internal @brief The outputstream to write HBCI-messages to. */
    InputStream i;           /**< @internal @brief The inputstream to read HBCI-messages from. */

    public CommStandard(HBCIPassportInternal passport)
    {
        super(passport);
        
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_CONN",passport.getHost()+":"+passport.getPort().toString()),
                      HBCIUtils.LOG_DEBUG);
        
        try {
            s=new Socket();

            int localPort=Integer.parseInt(HBCIUtils.getParam("client.connection.localPort","0"));
            if (localPort!=0) {
                s.setReuseAddress(true);
                s.bind(new InetSocketAddress(localPort));
            }

            s.connect(new InetSocketAddress(passport.getHost(),
                                            passport.getPort().intValue()));
            i=s.getInputStream();
            o=s.getOutputStream();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CONNERR"),e);
        }
    }

    protected void ping(MSG msg)
    {
        try {
            byte[] b=filter.encode(msg.toString(0));

            o.write(b);
            o.flush();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDERR"),ex);
        }
    }

    protected StringBuffer pong(MsgGen gen)
    {
        int          num;
        byte[]       b = new byte[1024];
        StringBuffer ret = new StringBuffer();
        boolean      sizeknown = false;
        int          msgsize = -1;

        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_COMM_WAIT"),HBCIUtils.LOG_INFO);

        try {
            while ((!sizeknown||msgsize>0)&&(num=i.read(b))!=-1) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_RECV",Integer.toString(num)),HBCIUtils.LOG_DEBUG2);

                String st=new String(b,0,num,"ISO-8859-1");

                ret.append(st);

                if (!sizeknown) {
                    String msgheadName="MsgHeadInst";
                    SEG msgHead=null;
                    boolean parseOk=true;

                    try {
                        StringBuffer res=new StringBuffer(filter.decode(ret.toString()));
                        msgHead=SEGFactory.getInstance().createSEG(
                                msgheadName,"MsgHead",null,(char)0,0,
                                res,res.length(),
                                gen.getSyntax(),new Hashtable(),new Hashtable());
                    } catch (ParseErrorException e) {
                        HBCIUtils.log(e);
                        parseOk=false;
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_CANTEXTRACT"),HBCIUtils.LOG_DEBUG2);
                    }

                    if (parseOk) {
                        try {
                            String msgsizeName="MsgHead.msgsize";
                            String msgsizeStr=msgHead.getValueOfDE(msgsizeName);
                            
                            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_FOUND",msgsizeStr),HBCIUtils.LOG_DEBUG);
                            
                            msgsize=(new Integer(msgsizeStr)).intValue();
                            msgsize-=ret.length();
                            sizeknown=true;
                        } finally {
                            SEGFactory.getInstance().unuseObject(msgHead);
                        }
                    }
                } else {
                    msgsize-=num;

                }
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_CURRENT",Integer.toString(msgsize)),HBCIUtils.LOG_DEBUG2);
            }

            // FileOutputStream fo=new FileOutputStream("pong.dat");
            // fo.write(ret.toString().getBytes("ISO-8859-1"));
            // fo.close();

            return new StringBuffer(filter.decode(ret.toString()));
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_RECVERR"),ex);
        }
    }

    protected void closeConnection()
    {
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_COMM_CLOSE"),HBCIUtils.LOG_DEBUG);
            s.close();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CLOSEERR"),ex);
        }
    }
}
