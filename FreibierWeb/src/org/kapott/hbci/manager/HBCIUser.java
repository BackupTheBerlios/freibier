
/*  $Id: HBCIUser.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.manager;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.NeedKeyAckException;
import org.kapott.hbci.exceptions.ProcessException;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

/* @brief Instances of this class represent a certain user in combination with
    a certain institute. */
public final class HBCIUser
{
    private HBCIPassportInternal passport;
    private HBCIKernelImpl       kernel;
    private boolean              isAnon;
    private String               anonSuffix;

    /** @brief This constructor initializes a new user instance with the given values */
    public HBCIUser(HBCIKernelImpl kernel,HBCIPassportInternal passport)
    {
        this.kernel=kernel;
        this.passport=passport;
        this.isAnon=passport.isAnonymous();
        this.anonSuffix=isAnon?"Anon":"";
    }

    private void doDialogEnd(String dialogid,String msgnum,boolean signIt,boolean cryptIt,boolean needCrypt)
    {
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_END,null);

        kernel.rawNewMsg("DialogEnd"+anonSuffix);
        kernel.rawSet("MsgHead.dialogid",dialogid);
        kernel.rawSet("MsgHead.msgnum",msgnum);
        kernel.rawSet("DialogEndS.dialogid",dialogid);
        kernel.rawSet("MsgTail.msgnum",msgnum);
        HBCIMsgStatus status=kernel.rawDoIt(passport,
                                            !isAnon && signIt,
                                            !isAnon && cryptIt,
                                            !isAnon && HBCIKernelImpl.NEED_SIG,
                                            !isAnon && needCrypt);
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_END_DONE,status);

        if (!status.isOK()) {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("ERR_INST_ENDFAILED")+": "+status.getErrorString(),HBCIUtils.LOG_ERR);
            
            String msg=HBCIUtilsInternal.getLocMsg("ERR_INST_ENDFAILED");
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreDialogEndErrors",msg+": "+status.getErrorString()))
                throw new ProcessException(msg,status);
        }
    }

    private void sendAndActivateNewUserKeys(HBCIKey[] sigKey,HBCIKey[] encKey)
    {
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_SEND_KEYS"),HBCIUtils.LOG_INFO);
            
            String country=passport.getCountry();
            String blz=passport.getBLZ();
    
            String[] exponent = new String[2];
            String[] modulus = new String[2];
    
            for (int i=0;i<2;i++) {
                KeyFactory fac = KeyFactory.getInstance("RSA");
                
                RSAPublicKeySpec spec=null;
                if (i==0) {
                    spec=(RSAPublicKeySpec)(fac.getKeySpec(sigKey[0].key,RSAPublicKeySpec.class));
                } else if (i==1) {
                    spec=(RSAPublicKeySpec)(fac.getKeySpec(encKey[0].key,RSAPublicKeySpec.class));
                } else { // *** not yet supported
                    // ***
                }
                
                byte[] ba=spec.getPublicExponent().toByteArray();
                int    len=ba.length;
                int    startpos=0;
                while (startpos<len && ba[startpos]==0) {
                    startpos++;
                }
                exponent[i] = new String(ba,startpos,len-startpos,"ISO-8859-1");

                ba=spec.getModulus().toByteArray();
                len=ba.length;
                startpos=0;
                while (startpos<len && ba[startpos]==0) {
                    startpos++;
                }
                modulus[i] = new String(ba,startpos,len-startpos,"ISO-8859-1");
            }
    
            if (!passport.hasMySigKey()) {
                // es gibt noch gar keine Nutzerschluessel
                
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS,null);

                // sigid updated
                passport.setSigId(new Long(1));
                
                // schluessel senden
                kernel.rawNewMsg("SendKeys");
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid",passport.getSysId());
        
                kernel.rawSet("KeyChange.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange.KeyName.keynum", sigKey[0].num);
                kernel.rawSet("KeyChange.KeyName.keytype", "S"); // *** keytype "D"
                kernel.rawSet("KeyChange.KeyName.keyversion", sigKey[0].version);
                kernel.rawSet("KeyChange.PubKey.mode", "16"); // *** later real mode
                kernel.rawSet("KeyChange.PubKey.exponent", "B" + exponent[0]);
                kernel.rawSet("KeyChange.PubKey.modulus", "B" + modulus[0]);
                kernel.rawSet("KeyChange.PubKey.usage", "6");
        
                kernel.rawSet("KeyChange_2.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange_2.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange_2.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange_2.KeyName.keynum", encKey[0].num);
                kernel.rawSet("KeyChange_2.KeyName.keytype", "V");
                kernel.rawSet("KeyChange_2.KeyName.keyversion", encKey[0].version);
                kernel.rawSet("KeyChange_2.PubKey.mode", "16"); // *** later real mode
                kernel.rawSet("KeyChange_2.PubKey.exponent", "B" + exponent[1]);
                kernel.rawSet("KeyChange_2.PubKey.modulus", "B" + modulus[1]);
                kernel.rawSet("KeyChange_2.PubKey.usage", "5");
                
                passport.setMyPublicSigKey(sigKey[0]);
                passport.setMyPrivateSigKey(sigKey[1]);
                passport.setMyPublicEncKey(encKey[0]);
                passport.setMyPrivateEncKey(encKey[1]);
                // *** dig key
                passport.saveChanges();
        
                HBCIMsgStatus ret=kernel.rawDoIt(passport,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                          HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.DONT_NEED_CRYPT);
                if (!ret.isOK()) {
                    if (!ret.hasExceptions()) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_DELKEYS"),HBCIUtils.LOG_WARN);
                        passport.clearMySigKey();
                        passport.clearMyEncKey();
                        passport.clearMyDigKey();
                        passport.saveChanges();
                    } else {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_KEYS_KEPT"),HBCIUtils.LOG_WARN);
                    }
        
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDKEYERR"),ret);
                }
        
                Properties result=ret.getData();
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS_DONE,ret);
                
                try {
                    doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.DONT_SIGNIT,HBCIKernelImpl.CRYPTIT,
                                HBCIKernelImpl.DONT_NEED_CRYPT);
                } catch (Exception e) {
                    HBCIUtils.log(e);
                }
                triggerNewKeysEvent();
            } else {
                // aendern der aktuellen Nutzerschluessel
                
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT,null);

                // als erstes Dialog-Initialisierung
                kernel.rawNewMsg("DialogInit");
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid", passport.getSysId());
                String sysstatus=passport.getSysStatus();
                kernel.rawSet("Idn.sysStatus",sysstatus);
                kernel.rawSet("ProcPrep.BPD",passport.getBPDVersion());
                kernel.rawSet("ProcPrep.UPD",passport.getUPDVersion());
                kernel.rawSet("ProcPrep.lang",passport.getLang());
                kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
                kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.4"));
                HBCIMsgStatus ret=kernel.rawDoIt(passport,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                          HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);
                if (!ret.isOK())
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),ret);
    
                // evtl. Passport-Daten aktualisieren 
                Properties result=ret.getData();
                HBCIInstitute inst=new HBCIInstitute(kernel,passport);
                inst.updateBPD(result);
                updateUPD(result);
                passport.saveChanges();
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT_DONE,new Object[] {ret,result.getProperty("MsgHead.dialogid")});

                // neue Schlüssel senden
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS,null);
                kernel.rawNewMsg("ChangeKeys");
                kernel.rawSet("MsgHead.dialogid",result.getProperty("MsgHead.dialogid"));
                kernel.rawSet("MsgHead.msgnum","2");
                kernel.rawSet("MsgTail.msgnum","2");
                
                kernel.rawSet("KeyChange.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange.KeyName.keynum", sigKey[0].num);
                kernel.rawSet("KeyChange.KeyName.keytype", "S"); // *** keytype "D"
                kernel.rawSet("KeyChange.KeyName.keyversion", sigKey[0].version);
                kernel.rawSet("KeyChange.PubKey.mode", "16"); // *** later real mode
                kernel.rawSet("KeyChange.PubKey.exponent", "B" + exponent[0]);
                kernel.rawSet("KeyChange.PubKey.modulus", "B" + modulus[0]);
                kernel.rawSet("KeyChange.PubKey.usage", "6");
        
                kernel.rawSet("KeyChange_2.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange_2.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange_2.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange_2.KeyName.keynum", encKey[0].num);
                kernel.rawSet("KeyChange_2.KeyName.keytype", "V");
                kernel.rawSet("KeyChange_2.KeyName.keyversion", encKey[0].version);
                kernel.rawSet("KeyChange_2.PubKey.mode", "16"); // *** later real mode
                kernel.rawSet("KeyChange_2.PubKey.exponent", "B" + exponent[1]);
                kernel.rawSet("KeyChange_2.PubKey.modulus", "B" + modulus[1]);
                kernel.rawSet("KeyChange_2.PubKey.usage", "5");
                
                HBCIKey[] oldEncKeys=new HBCIKey[2];
                oldEncKeys[0]=passport.getMyPublicEncKey();
                oldEncKeys[1]=passport.getMyPrivateEncKey();
                
                passport.setMyPublicEncKey(encKey[0]);
                passport.setMyPrivateEncKey(encKey[1]);
                passport.saveChanges();
                
                ret=kernel.rawDoIt(passport,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                            HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);
                if (!ret.isOK()) {
                    if (!ret.hasExceptions()) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_DELKEYS"),HBCIUtils.LOG_WARN);
                        passport.setMyPublicEncKey(oldEncKeys[0]);
                        passport.setMyPrivateEncKey(oldEncKeys[1]);
                        passport.saveChanges();
                    } else {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_KEYS_KEPT"),HBCIUtils.LOG_WARN);
                    }
        
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDKEYERR"),ret);
                }
        
                passport.setSigId(new Long(1));
                passport.setMyPublicSigKey(sigKey[0]);
                passport.setMyPrivateSigKey(sigKey[1]);
                passport.saveChanges();
        
                result=ret.getData();
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS_DONE,ret);
                doDialogEnd(result.getProperty("MsgHead.dialogid"),"3",HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                                       HBCIKernelImpl.NEED_CRYPT);
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDABORT"),e); 
        } finally {
            passport.closeComm();
        }
    }
    
    private void triggerNewKeysEvent()
    {
        HBCIUtilsInternal.getCallback().callback(passport,
                                         HBCICallback.HAVE_NEW_MY_KEYS,
                                         HBCIUtilsInternal.getLocMsg("CALLB_NEW_USER_KEYS"),
                                         HBCICallback.TYPE_NONE,
                                         new StringBuffer());
        throw new NeedKeyAckException();
    }

    public void generateNewKeys()
    {
        if (passport.needUserKeys()) {
            HBCIKey[] newSigKey=null;HBCIKey[] newEncKey=null;
            try {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_GENKEYS"),HBCIUtils.LOG_INFO);

                String blz=passport.getBLZ();
                String country=passport.getCountry();
                String userid=passport.getUserId();

                newSigKey=new HBCIKey[2];
                newEncKey=new HBCIKey[2];
                String num=passport.hasMySigKey()?passport.getMyPublicSigKey().num:"0";
                num=Integer.toString(Integer.parseInt(num)+1);

                for (int i=0;i<2;i++) {
                    KeyPairGenerator keygen=KeyPairGenerator.getInstance("RSA");
                    keygen.initialize(768);
                    KeyPair pair=keygen.generateKeyPair();

                    // *** eigentlich soll ja die version und nicht die num erhöht werden... 
                    if (i==0) {
                        newSigKey[0]=new HBCIKey(country,blz,userid,num,"1",pair.getPublic());
                        newSigKey[1]=new HBCIKey(country,blz,userid,num,"1",pair.getPrivate());
                    } else {
                        newEncKey[0]=new HBCIKey(country,blz,userid,num,"1",pair.getPublic());
                        newEncKey[1]=new HBCIKey(country,blz,userid,num,"1",pair.getPrivate());
                    }
                }
            } catch (Exception ex) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GENKEYS_ERR"),ex);
            }

            sendAndActivateNewUserKeys(newSigKey,newEncKey);
        } else {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USRKEYS_UNSUPP"));
        }
    }
    
    public void manuallySetNewKeys(KeyPair sigKey,KeyPair encKey)
    {
        if (passport.needUserKeys()) {
            HBCIKey[] newSigKey=null;
            HBCIKey[] newEncKey=null;

            try {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_SETKEYS"),HBCIUtils.LOG_INFO);

                String blz=passport.getBLZ();
                String country=passport.getCountry();
                String userid=passport.getUserId();

                newSigKey=new HBCIKey[2];
                newEncKey=new HBCIKey[2];

                for (int i=0;i<2;i++) {
                    if (i==0) {
                        String num=passport.hasMySigKey()?passport.getMyPublicSigKey().num:"0";
                        num=Integer.toString(Integer.parseInt(num)+1);
                        newSigKey[0]=new HBCIKey(country,blz,userid,num,"1",sigKey.getPublic());
                        newSigKey[1]=new HBCIKey(country,blz,userid,num,"1",sigKey.getPrivate());
                    } else {
                        String num=passport.hasMyEncKey()?passport.getMyPublicEncKey().num:"0";
                        num=Integer.toString(Integer.parseInt(num)+1);
                        newEncKey[0]=new HBCIKey(country,blz,userid,num,"1",encKey.getPublic());
                        newEncKey[1]=new HBCIKey(country,blz,userid,num,"1",encKey.getPrivate());
                    }
                }
            } catch (Exception ex) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GENKEYS_ERR"),ex);
            }

            sendAndActivateNewUserKeys(newSigKey,newEncKey);
        } else {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USRKEYS_UNSUPP"));
        }
    }

    public void fetchSysId()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SYSID,null);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_GETSYSID"),HBCIUtils.LOG_INFO);
            
            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            passport.setSigId(new Long(1));
            passport.setSysId("0");
    
            kernel.rawNewMsg("Synch");
            kernel.rawSet("Idn.KIK.blz", blz);
            kernel.rawSet("Idn.KIK.country", country);
            kernel.rawSet("Idn.customerid", passport.getCustomerId());
            kernel.rawSet("Idn.sysid", "0");
            kernel.rawSet("Idn.sysStatus", "1");
            kernel.rawSet("MsgHead.dialogid", "0");
            kernel.rawSet("MsgHead.msgnum", "1");
            kernel.rawSet("MsgTail.msgnum", "1");
            kernel.rawSet("ProcPrep.BPD", passport.getBPDVersion());
            kernel.rawSet("ProcPrep.UPD", passport.getUPDVersion());
            kernel.rawSet("ProcPrep.lang", "0");
            kernel.rawSet("ProcPrep.prodName", HBCIUtils.getParam("client.product.name","HBCI4Java"));
            kernel.rawSet("ProcPrep.prodVersion", HBCIUtils.getParam("client.product.version","2.4"));
            kernel.rawSet("Sync.mode", "0");
            HBCIMsgStatus ret=kernel.rawDoIt(passport,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                      HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);
            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSYSIDFAIL"),ret);
    
            Properties result=ret.getData();
            HBCIInstitute inst=new HBCIInstitute(kernel,passport);
            
            inst.updateBPD(result);
            updateUPD(result);
            passport.setSysId(result.getProperty("SyncRes.sysid"));
            passport.saveChanges();
    
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SYSID_DONE,new Object[] {ret,passport.getSysId()});
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_NEWSYSID",passport.getSysId()),HBCIUtils.LOG_DEBUG);
            doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                                   HBCIKernelImpl.NEED_CRYPT);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSYSIDFAIL"),e);
        } finally {
            passport.closeComm();
        }
    }

    public void fetchSigId()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SIGID,null);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_GETSIGID"),HBCIUtils.LOG_INFO);
            
            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            passport.setSigId(new Long("9999999999999999"));
    
            kernel.rawNewMsg("Synch");
            kernel.rawSet("Idn.KIK.blz", blz);
            kernel.rawSet("Idn.KIK.country", country);
            kernel.rawSet("Idn.customerid", passport.getCustomerId());
            kernel.rawSet("Idn.sysid", passport.getSysId());
            kernel.rawSet("Idn.sysStatus", passport.getSysStatus());
            kernel.rawSet("MsgHead.dialogid", "0");
            kernel.rawSet("MsgHead.msgnum", "1");
            kernel.rawSet("MsgTail.msgnum", "1");
            kernel.rawSet("ProcPrep.BPD", passport.getBPDVersion());
            kernel.rawSet("ProcPrep.UPD", passport.getUPDVersion());
            kernel.rawSet("ProcPrep.lang", "0");
            kernel.rawSet("ProcPrep.prodName", HBCIUtils.getParam("client.product.name","HBCI4Java"));
            kernel.rawSet("ProcPrep.prodVersion", HBCIUtils.getParam("client.product.version","2.4"));
            kernel.rawSet("Sync.mode", "2");
            HBCIMsgStatus ret=kernel.rawDoIt(passport,passport.hasMySigKey(),HBCIKernelImpl.CRYPTIT,
                                                      HBCIKernelImpl.NEED_SIG,passport.hasMyEncKey());
            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSIGIDFAIL"),ret);
    
            Properties result=ret.getData();
            HBCIInstitute inst=new HBCIInstitute(kernel,passport);
            
            inst.updateBPD(result);
            updateUPD(result);
            passport.setSigId(new Long(result.getProperty("SyncRes.sigid")));
            passport.incSigId();
            passport.saveChanges();
    
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SIGID_DONE,new Object[] {ret,passport.getSigId()});
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_NEWSIGID",passport.getSigId()),HBCIUtils.LOG_DEBUG);
            doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",passport.hasMySigKey(),HBCIKernelImpl.CRYPTIT,
                                                                   passport.hasMyEncKey());
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSIGIDFAIL"),e);
        } finally {
            passport.closeComm();
        }
    }

    public void updateUPD(Properties result)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_USER_EXBPD"),HBCIUtils.LOG_DEBUG);

        Properties p = null;
        for (Enumeration e = result.keys(); e.hasMoreElements(); ) {
            String key = (String)(e.nextElement());
            if (key.startsWith("UPD.")) {
                if (p == null)
                    p = new Properties();
                p.setProperty(key.substring(("UPD.").length()), result.getProperty(key));
            }
        }

        if (p!=null) {
            p.setProperty("_hbciversion",kernel.getHBCIVersion());
            passport.setUPD(p);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_NEW_BPD",passport.getUPDVersion()),HBCIUtils.LOG_INFO);
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_UPD_DONE,passport.getUPD());
        }
    }

    public void fetchUPD()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_UPD,null);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_USER_RECINSTDATA"),HBCIUtils.LOG_INFO);
            
            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            kernel.rawNewMsg("DialogInit"+anonSuffix);
            kernel.rawSet("Idn.KIK.blz", blz);
            kernel.rawSet("Idn.KIK.country", country);
            if (!isAnon) {
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid", passport.getSysId());
                String sysstatus=passport.getSysStatus();
                kernel.rawSet("Idn.sysStatus",sysstatus);
            }
            kernel.rawSet("ProcPrep.BPD",passport.getBPDVersion());
            kernel.rawSet("ProcPrep.UPD","0");
            kernel.rawSet("ProcPrep.lang",passport.getLang());
            kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
            kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.4"));
            HBCIMsgStatus ret=kernel.rawDoIt(passport,
                                             !isAnon && HBCIKernelImpl.SIGNIT,
                                             !isAnon && HBCIKernelImpl.CRYPTIT,
                                             !isAnon && HBCIKernelImpl.NEED_SIG,
                                             !isAnon && HBCIKernelImpl.NEED_CRYPT);
            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),ret);
    
            Properties result=ret.getData();
            HBCIInstitute inst=new HBCIInstitute(kernel,passport);
            
            inst.updateBPD(result);
            updateUPD(result);
            passport.saveChanges();
    
            doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                                   HBCIKernelImpl.NEED_CRYPT);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),e);
        } finally {
            passport.closeComm();
        }
    }

    private void updateUserData()
    {
        if (passport.getSysStatus().equals("1")) {
            if (passport.getSysId().equals("0"))
                fetchSysId();
            if (passport.getSigId().longValue()==-1)
                fetchSigId();
        }
        
        Properties upd=passport.getUPD();
        String     hbciVersionOfUPD=(upd!=null)?upd.getProperty("_hbciversion"):null;
        
        if (passport.getUPDVersion().equals("0") ||
            hbciVersionOfUPD==null ||
            !hbciVersionOfUPD.equals(kernel.getHBCIVersion())) {
            fetchUPD();
        }
    }

    public void register()
    {
        if (passport.needUserKeys() && !passport.hasMySigKey()) {
            generateNewKeys();
        }
        updateUserData();
    }
    
    public void lockKeys()
    {
        if (!passport.needUserKeys() ||
            !passport.hasMySigKey()) {
                
            if (!passport.needUserKeys()) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_DONTHAVEUSRKEYS"));
            }
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_NOUSRKEYSAVAIL"));
        }
        
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT,null);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK"),HBCIUtils.LOG_INFO);
            
            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            kernel.rawNewMsg("DialogInit");
            kernel.rawSet("Idn.KIK.blz", blz);
            kernel.rawSet("Idn.KIK.country", country);
            kernel.rawSet("Idn.customerid", passport.getCustomerId());
            kernel.rawSet("Idn.sysid", passport.getSysId());
            kernel.rawSet("Idn.sysStatus",passport.getSysStatus());
            kernel.rawSet("ProcPrep.BPD",passport.getBPDVersion());
            kernel.rawSet("ProcPrep.UPD",passport.getUPDVersion());
            kernel.rawSet("ProcPrep.lang",passport.getLang());
            kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
            kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.4"));
            
            HBCIMsgStatus status=kernel.rawDoIt(passport,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                         HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);
            if (!status.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),status);
            
            Properties result=status.getData();
            String dialogid=result.getProperty("MsgHead.dialogid");
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT_DONE,new Object[] {status,dialogid});

            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_LOCK_KEYS,null);
            kernel.rawNewMsg("LockKeys");
            kernel.rawSet("MsgHead.dialogid",dialogid);
            kernel.rawSet("MsgHead.msgnum","2");
            kernel.rawSet("MsgTail.msgnum","2");
            kernel.rawSet("KeyLock.KeyName.KIK.country",country);
            kernel.rawSet("KeyLock.KeyName.KIK.blz",blz);
            kernel.rawSet("KeyLock.KeyName.userid",passport.getMySigKeyName());
            kernel.rawSet("KeyLock.KeyName.keynum",passport.getMySigKeyNum());
            kernel.rawSet("KeyLock.KeyName.keyversion",passport.getMySigKeyVersion());
            kernel.rawSet("KeyLock.locktype","999");

            status=kernel.rawDoIt(passport,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                           HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.DONT_NEED_CRYPT);
            if (!status.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),status);

            passport.clearMyDigKey();
            passport.clearMySigKey();
            passport.clearMyEncKey();
            
            passport.setSigId(new Long(1));
            passport.saveChanges();
                
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_LOCK_KEYS_DONE,status);
            doDialogEnd(dialogid,"3",HBCIKernelImpl.DONT_SIGNIT,HBCIKernelImpl.CRYPTIT,HBCIKernelImpl.DONT_NEED_CRYPT);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),e);
        } finally {
            passport.closeComm();
        }
    }
}
