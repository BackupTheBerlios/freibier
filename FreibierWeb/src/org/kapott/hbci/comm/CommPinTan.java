
/*  $Id: CommPinTan.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.MSG;

public final class CommPinTan
    extends Comm
{
    private URL                url;
    private HttpsURLConnection conn;
    private boolean            checkCert;
    
    private static class MyTrustManager 
        implements X509TrustManager 
    {
        public X509Certificate[] getAcceptedIssuers() 
        { 
            return null;
        }
        
        public void checkClientTrusted(X509Certificate[] chain,String authType) 
        {
        }
        
        public void checkServerTrusted(X509Certificate[] chain,String authType) 
        {
        }
    }
    
    public CommPinTan(HBCIPassportInternal passport)
    {
        super(passport);
        checkCert=((HBCIPassportPinTan)passport).getCheckCert();
        
        String trustStore=((HBCIPassportPinTan)passport).getCertFile();
        if (checkCert && trustStore!=null && trustStore.length()!=0) {
            System.setProperty("javax.net.ssl.trustStore",trustStore);
        }
        
        try {
            String fullpath=passport.getHost();
            int    slashIdx=fullpath.indexOf("/");
            if (slashIdx==-1)
                slashIdx=fullpath.length();
            String host=fullpath.substring(0,slashIdx);
            String path=fullpath.substring(slashIdx);
            
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PT_COMMNEW",
                        "https://"+host+":"+passport.getPort()+path),
                    HBCIUtils.LOG_INFO);
            url=new URL("https",host,passport.getPort().intValue(),path);

            /* *** how to implement this ?
                     int localPort=Integer.parseInt(HBCIUtils.getParam("client.connection.localPort","0"));
                     if (localPort!=0) {
                s.setReuseAddress(true);
                s.bind(new InetSocketAddress(localPort));
                     }
             */
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CONNERR"),e);
        }
    }

    protected void ping(MSG msg)
    {
        try {
            byte[] b=filter.encode(msg.toString(0));

            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PT_COMMCONN"),HBCIUtils.LOG_DEBUG);
            conn=(HttpsURLConnection)url.openConnection();
            
            if (!checkCert) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMMPT_NOCERTS"),HBCIUtils.LOG_DEBUG);
                
                SSLContext sslContext=SSLContext.getInstance("SSL");
                sslContext.init(null, 
                        new TrustManager[] {new MyTrustManager()},
                        new SecureRandom());
                SSLSocketFactory sf=sslContext.getSocketFactory();
                conn.setSSLSocketFactory(sf);
                
                conn.setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname,SSLSession session)
                    {
                        return true;
                    }
                });
            }
            
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            conn.getOutputStream().write(b);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDERR"),e);
        }
    }

    protected StringBuffer pong(MsgGen gen)
    {
        try {
            byte[] b=new byte[1024];
            StringBuffer ret=new StringBuffer();

            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_COMM_WAIT"),HBCIUtils.LOG_INFO);

            int msgsize=conn.getContentLength();
            int num;

            if (msgsize!=-1) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_FOUND",Integer.toString(msgsize)),HBCIUtils.LOG_DEBUG);
            } else {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMMPT_NOMSGSIZE"),HBCIUtils.LOG_DEBUG);
            }
            InputStream i=conn.getInputStream();

            while (msgsize!=0 && (num=i.read(b))>0) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_RECV",Integer.toString(num)),HBCIUtils.LOG_DEBUG2);
                ret.append(new String(b,0,num,"ISO-8859-1"));
                msgsize-=num;
                if (msgsize>=0) {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMM_CURRENT",Integer.toString(msgsize)),HBCIUtils.LOG_DEBUG2);
                } else {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_COMMPT_READXBYTES",Integer.toString(num)),HBCIUtils.LOG_DEBUG2);
                }
            }

            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_COMM_CLOSE"),HBCIUtils.LOG_DEBUG);
            conn.disconnect();
            return new StringBuffer(filter.decode(ret.toString()));
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_RECVERR"),e);
        }
    }

    protected void closeConnection()
    {
    }
}
