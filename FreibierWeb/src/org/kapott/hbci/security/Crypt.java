
/*  $Id: Crypt.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.MultipleSEGs;
import org.kapott.hbci.protocol.MultipleSyntaxElements;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class Crypt
{
    private MSG msg;
    private MsgGen gen;
    private HBCIPassportInternal passport;

    private String u_secfunc;    // 4=normal; 998=klartext
    private String u_secmethod;  // 5=ddv, 6=rdh
    private String u_blz;        // schluesseldaten
    private String u_country;
    private String u_keyuserid;
    private String u_keynum;
    private String u_keyversion;
    private String u_cid;
    private String u_sysId;
    private String u_role;
    private String u_alg;       // crypthead.cryptalg.alg
    private String u_mode;      // crypthead.cryptalg.mode
    private String u_compfunc;

    public void setParam(String name, String value)
    {
        try {
            Field field=this.getClass().getDeclaredField("u_"+name);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_CRYPT_SET",new Object[] {name,value}),HBCIUtils.LOG_DEBUG);
            field.set(this,value);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while setting parameter",ex);
        }
    }

    private void initData(MSG msg, MsgGen gen, HBCIPassportInternal passport)
    {
        this.msg = msg;
        this.gen = gen;
        this.passport = passport;
    }
    
    public Crypt(MSG msg, MsgGen gen, HBCIPassportInternal passport)
    {
        initData(msg,gen,passport);
    }

    public void init(MSG msg, MsgGen gen, HBCIPassportInternal passport)
    {
        initData(msg,gen,passport);
    }

    private byte[] getPlainString()
    {
        try {
            // remove msghead and msgtail first
            StringBuffer ret=new StringBuffer(1024);
            List childs=msg.getChildContainers();
            int len=childs.size();

            /* skip one segment at start and one segment at end of message
               (msghead and msgtail), the rest will be encrypted */
            for (int i=1;i<len-1;i++) {
                ret.append(((MultipleSyntaxElements)(childs.get(i))).toString(0));
            }

            // pad message
            int padLength=8-(ret.length()%8);
            for (int i=0;i<padLength;i++) {
                ret.append((char)(padLength));
            }

            return ret.toString().getBytes("ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while extracting plain message string",ex);
        }
    }

    public MSG cryptIt(String newName)
    {
        MSG newmsg=msg;

        if (passport.hasInstEncKey()) {
            String msgName = msg.getName();
            Node msgNode = msg.getSyntaxDef(msgName, gen.getSyntax());
            String dontcryptAttr = ((Element)msgNode).getAttribute("dontcrypt");

            if (dontcryptAttr.length() == 0) {
                try {
                    setParam("secfunc",passport.getSecMethodPlain());
                    setParam("secmethod",passport.getSecMethod56());
                    setParam("blz",passport.getBLZ());
                    setParam("country",passport.getCountry());
                    setParam("keyuserid",passport.getInstEncKeyName());
                    setParam("keynum",passport.getInstEncKeyNum());
                    setParam("keyversion",passport.getInstEncKeyVersion());
                    setParam("cid",passport.getCID());
                    setParam("sysId",passport.getSysId());
                    setParam("role","1");
                    setParam("alg",passport.getCryptAlg());
                    setParam("mode",passport.getCryptMode());
                    setParam("compfunc","0"); // *** spaeter kompression implementieren

                    byte[][] crypteds=passport.encrypt(getPlainString());

                    String msgPath=msg.getPath();
                    String dialogid=msg.getValueOfDE(msgPath+".MsgHead.dialogid");
                    String msgnum=msg.getValueOfDE(msgPath+".MsgHead.msgnum");
                    String segnum=msg.getValueOfDE(msgPath+".MsgTail.SegHead.seq");
                    
                    Date d=new Date();

                    gen.set(newName+".CryptData.data","B"+new String(crypteds[1],"ISO-8859-1"));
                    gen.set(newName+".CryptHead.CryptAlg.alg",u_alg);
                    gen.set(newName+".CryptHead.CryptAlg.mode",u_mode);
                    gen.set(newName+".CryptHead.CryptAlg.enckey","B"+new String(crypteds[0],"ISO-8859-1"));
                    gen.set(newName+".CryptHead.CryptAlg.secmethod",u_secmethod);
                    gen.set(newName+".CryptHead.SecIdnDetails.func",(newmsg.getName().endsWith("Res")?"2":"1"));
                    gen.set(newName+".CryptHead.KeyName.KIK.blz",u_blz);
                    gen.set(newName+".CryptHead.KeyName.KIK.country",u_country);
                    gen.set(newName+".CryptHead.KeyName.userid",u_keyuserid);
                    gen.set(newName+".CryptHead.KeyName.keynum",u_keynum);
                    gen.set(newName+".CryptHead.KeyName.keyversion",u_keyversion);
                    if (passport.getSysStatus().equals("0")) {
                        gen.set(newName+".CryptHead.SecIdnDetails.cid","B"+u_cid);
                    } else {
                        gen.set(newName+".CryptHead.SecIdnDetails.sysid",u_sysId);
                    }
                    gen.set(newName+".CryptHead.SecTimestamp.date",HBCIUtils.date2String(d));
                    gen.set(newName+".CryptHead.SecTimestamp.time",HBCIUtils.time2String(d));
                    gen.set(newName+".CryptHead.role",u_role);
                    gen.set(newName+".CryptHead.secfunc",u_secfunc);
                    gen.set(newName+".CryptHead.compfunc",u_compfunc);
                    gen.set(newName+".MsgHead.dialogid",dialogid);
                    gen.set(newName+".MsgHead.msgnum",msgnum);
                    gen.set(newName+".MsgTail.msgnum",msgnum);
                    
                    if (newName.endsWith("Res")) {
                        gen.set(newName+".MsgHead.MsgRef.dialogid",dialogid);
                        gen.set(newName+".MsgHead.MsgRef.msgnum",msgnum);
                    }

                    newmsg=gen.generate(newName);

                    // renumerate crypto-segments
                    for (int i=1;i<=2;i++) {
                        SEG seg=(SEG)(((MultipleSEGs)((newmsg.getChildContainers()).get(i))).getElements().get(0));
                        seg.setSeq(997+i,SyntaxElement.ALLOW_OVERWRITE);
                    }

                    newmsg.propagateValue(newmsg.getPath()+".MsgTail.SegHead.seq",segnum,
                            SyntaxElement.DONT_TRY_TO_CREATE,
                            SyntaxElement.ALLOW_OVERWRITE);
                    newmsg.autoSetMsgSize(gen);
                } catch (Exception ex) {
                    throw new HBCI_Exception("*** error while encrypting",ex);
                }
            }
            else HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_CRYPT_DONTWANT"),HBCIUtils.LOG_DEBUG);
        }
        else HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_CRYPT_NOKEY"),HBCIUtils.LOG_WARN);

        return newmsg;
    }

    private boolean isCrypted()
    {
        boolean ret = true;
        MultipleSyntaxElements seglist = (MultipleSyntaxElements)(msg.getChildContainers().get(1));

        if (seglist instanceof MultipleSEGs) {
            SEG sighead = null;

            try {
                sighead = (SEG)(seglist.getElements().get(0));
            } catch (Exception e) {
                ret = false;
            }

            if (ret) {
                String sigheadCode = "HNVSK";

                if (!sighead.getCode(gen).equals(sigheadCode))
                    ret = false;
            }
        }
        else ret = false;

        return ret;
    }

    public String decryptIt()
    {
        StringBuffer ret=new StringBuffer(msg.toString(0));

        if (passport.hasMyEncKey()) {
            if (isCrypted()) {
                try {
                    String msgName=msg.getName();

                    List childs=msg.getChildContainers();
                    SEG msghead=(SEG)(((MultipleSEGs)(childs.get(0))).getElements().get(0));
                    SEG msgtail=(SEG)(((MultipleSEGs)(childs.get(childs.size()-1))).getElements().get(0));

                    // verschluesselte daten extrahieren
                    SEG cryptdata=(SEG)(((MultipleSEGs)(childs.get(2))).getElements().get(0));
                    byte[] cryptedstring=cryptdata.getValueOfDE(msgName+".CryptData.data").getBytes("ISO-8859-1");

                    // key extrahieren
                    SEG crypthead=(SEG)(((MultipleSEGs)(childs.get(1))).getElements().get(0));
                    byte[] cryptedkey=crypthead.getValueOfDE(msgName+
                                      ".CryptHead.CryptAlg.enckey").getBytes("ISO-8859-1");

                    // neues secfunc (klartext/encrypted)
                    String secfunc=crypthead.getValueOfDE(msgName+".CryptHead.secfunc");
                    if (!secfunc.equals(passport.getSecMethodPlain())) {
                        String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTSFFAIL",new Object[] {secfunc,
                                                          passport.getSecMethodPlain()});
                        if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCryptErrors",errmsg))
                            throw new HBCI_Exception(errmsg);
                    }

                    // *** diese checks werden vorerst abgeschaltet, damit pin-tan reibungslos geht
                    /*
                                     // constraint checking
                                     String secmethod=crypthead.getValueOfDE(msgName+".CryptHead.CryptAlg.secmethod");
                         if (!secmethod.equals(passport.getSecMethod56()) && !(passport instanceof HBCIPassportPinTan))
                        throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_CRYPTMETHODFAIL",new Object[] {secmethod,passport.getSecMethod56()}));
                                     String mode=crypthead.getValueOfDE(msgName+".CryptHead.CryptAlg.mode");
                                     if (!mode.equals(passport.getCryptMode()))
                         throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_CRYPTMODEFAIL",new Object[] {secmethod,passport.getCryptMode()}));
                     */

                    if (passport.getSysStatus().equals("1")) {
                        String sysid=null;
                        try {
                            // falls noch keine system-id ausgehandelt wurde, so sendet der
                            // hbci-server auch keine... deshalb der try-catch-block
                            sysid=crypthead.getValueOfDE(msgName+".CryptHead.SecIdnDetails.sysid");
                        } catch (Exception e) {
                            sysid="0";
                        }
                        
                        // TODO: sysid checken (kann eigentlich auch entfallen, weil
                        // das jeweils auf höherer ebene geschehen sollte!)
                    } else {
                        String cid=crypthead.getValueOfDE(msgName+".CryptHead.SecIdnDetails.cid");
                        if (!cid.equals(passport.getCID())) {
                            String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTCIDFAIL");
                            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCryptErrors",errmsg))
                                throw new HBCI_Exception(errmsg);
                        }
                        
                        // TODO: cid checken
                    }

                    // *** spaeter kompression implementieren
                    String compfunc=crypthead.getValueOfDE(msgName+".CryptHead.compfunc");
                    if (!compfunc.equals("0")) {
                        String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTCOMPFUNCFAIL",compfunc);
                        if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCryptErrors",errmsg))
                            throw new HBCI_Exception(errmsg);
                    }

                    byte[] plainMsg=passport.decrypt(cryptedkey,cryptedstring);
                    int padLength=plainMsg[plainMsg.length-1];

                    // FileOutputStream fo=new FileOutputStream("decrypt.dat");
                    // fo.write(plainMsg);
                    // fo.close();

                    // neuen nachrichtenstring zusammenbauen
                    ret=new StringBuffer(1024);
                    ret.append(msghead.toString(0)).
                        append(new String(plainMsg,0,plainMsg.length-padLength,"ISO-8859-1")).
                        append(msgtail.toString(0));
                } catch (Exception ex) {
                    throw new HBCI_Exception("*** error while decrypting",ex);
                }
            }
            else HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_CRYPT_PLAIN"),HBCIUtils.LOG_DEBUG);
        }
        else HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_DECRYPT_NOKEY"),HBCIUtils.LOG_WARN);
        
        return ret.toString();
    }
    
    public void destroy()
    {
        gen=null;
        msg=null;
        passport=null;
        u_alg=null;
        u_blz=null;
        u_cid=null;
        u_compfunc=null;
        u_country=null;
        u_keynum=null;
        u_keyuserid=null;
        u_keyversion=null;
        u_mode=null;
        u_role=null;
        u_secfunc=null;
        u_secmethod=null;
        u_sysId=null;
    }
}
