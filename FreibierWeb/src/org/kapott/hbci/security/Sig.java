
/*  $Id: Sig.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportList;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.MultipleSEGs;
import org.kapott.hbci.protocol.MultipleSyntaxElements;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.protocol.factory.SEGFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class Sig
{
    private MSG msg;
    private MsgGen gen;
    private HBCIPassportList passports;

    private String u_secmethod;
    private String u_cid;
    private String u_role;
    private String u_range;
    private String u_keyblz;
    private String u_keycountry;
    private String u_keyuserid;
    private String u_keynum;
    private String u_keyversion;
    private String u_sysid;
    private String u_sigid;
    private String u_sigalg;
    private String u_sigmode;
    private String u_hashalg;
    private String sigstring;
    
    private void initData(MSG msg, MsgGen gen, HBCIPassportList passports)
    {
        this.msg = msg;
        this.gen = gen;
        this.passports = passports;
    }

    public Sig(MSG msg, MsgGen gen, HBCIPassportList passports) 
    {
        initData(msg,gen,passports);
    }

    public void init(MSG msg, MsgGen gen, HBCIPassportList passports)
    {
        initData(msg,gen,passports);
    }

    // sighead-segment mit werten aus den lokalen variablen füllen
    private void fillSigHead(SEG sighead)
    {
        String sigheadName = sighead.getPath();
        String seccheckref = Integer.toString(Math.abs(new Random().nextInt()));

        Date d=new Date();

        sighead.propagateValue(sigheadName + ".secfunc",u_secmethod,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".seccheckref", seccheckref,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        /*** enable this later (when other range types are supported)
             sighead.propagateValue(sigheadName+".range",range,false);
        ***/
        sighead.propagateValue(sigheadName + ".role", u_role,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName+".SecIdnDetails.func",(msg.getName().endsWith("Res")?"2":"1"),
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        if (u_cid.length()!=0) {
            // DDV
            sighead.propagateValue(sigheadName + ".SecIdnDetails.cid", "B"+u_cid,
                    SyntaxElement.DONT_TRY_TO_CREATE,
                    SyntaxElement.DONT_ALLOW_OVERWRITE);
        } else {
            // RDH und PinTan
            sighead.propagateValue(sigheadName + ".SecIdnDetails.sysid", u_sysid,
                    SyntaxElement.DONT_TRY_TO_CREATE,
                    SyntaxElement.DONT_ALLOW_OVERWRITE);
        }
        sighead.propagateValue(sigheadName + ".SecTimestamp.date", HBCIUtils.date2String(d),
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".SecTimestamp.time", HBCIUtils.time2String(d),
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".secref", u_sigid,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".HashAlg.alg",u_hashalg,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".SigAlg.alg", u_sigalg,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".SigAlg.mode", u_sigmode,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".KeyName.KIK.country", u_keycountry,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".KeyName.KIK.blz", u_keyblz,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".KeyName.userid", u_keyuserid,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".KeyName.keynum", u_keynum,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
        sighead.propagateValue(sigheadName + ".KeyName.keyversion", u_keyversion,
                SyntaxElement.DONT_TRY_TO_CREATE,
                SyntaxElement.DONT_ALLOW_OVERWRITE);
    }

    // sigtail-segment mit werten aus den lokalen variablen füllen
    private void fillSigTail(SEG sighead, SEG sigtail)
    {
        String sigtailName = sigtail.getPath();

        sigtail.propagateValue(sigtailName + ".seccheckref",
                               sighead.getValueOfDE(sighead.getPath() + ".seccheckref"),
                               SyntaxElement.DONT_TRY_TO_CREATE,
                               SyntaxElement.DONT_ALLOW_OVERWRITE);
    }

    /* daten zusammensammeln, die signiert werden müssen; idx gibt dabei an,
     * die wievielte signatur erzeugt werden soll - wird benötigt, um festzustellen,
     * welche sighead- und sigtail-segmente in die signatur eingehen */
    private String collectHashData(int idx)
    {
        int          numOfPassports=passports.size();
        StringBuffer ret=new StringBuffer(1024);

        List msgelementslist = msg.getChildContainers();
        List sigheads = ((MultipleSEGs)(msgelementslist.get(1))).getElements();
        List sigtails = ((MultipleSEGs)(msgelementslist.get(msgelementslist.size() - 2))).getElements();

        // alle benötigten sighead-segmente zusammensuchen
        for (int i=numOfPassports-1-idx; i<(u_range.equals("1")?(numOfPassports-idx):numOfPassports);i++) {
            ret.append(((SEG)(sigheads.get(i))).toString(0));
        }

        // alle nutzdaten hinzufügen
        for (int i=2; i<msgelementslist.size()-2;i++) {
            ret.append(((MultipleSyntaxElements)(msgelementslist.get(i))).toString(0));
        }

        // bei schalen-modell-signaturen alle "inneren" sigtails mit hinzufügen
        for (int i=0;i<(u_range.equals("1")?0:idx);i++) {
            ret.append(((SEG)(sigtails.get(i))).toString(0));
        }

        return ret.toString();
    }
    
    private String collectHashData(int idx,int dummy)
    {
        // wird beim verifizieren benutzt, da msg.toString(0) u.u.
        // nicht den selben string erzeugt wie die eingehende nachricht
        String msgstring=gen.get("_origSignedMsg");
        return msgstring.substring(msgstring.indexOf("HNSHK:2:"),msgstring.lastIndexOf("HNSHA:"));
    }
    
    private void collectSegCodesFromSyntaxElement(SyntaxElement elem,StringBuffer result)
    {
        if (elem!=null && elem.isValid()) {
            if (elem instanceof SEG) {
                if (result.length()!=0)
                    result.append("|");
                
                result.append(((SEG)elem).getValueOfDE(elem.getPath()+".SegHead.code"));
            } else {
                List childContainers=elem.getChildContainers();
                
                for (Iterator i=childContainers.iterator();i.hasNext();) {
                    MultipleSyntaxElements container=(MultipleSyntaxElements)i.next();
                    List                   childs=container.getElements();
                    
                    for (Iterator j=childs.iterator();j.hasNext();) {
                        SyntaxElement child=(SyntaxElement)j.next();
                        collectSegCodesFromSyntaxElement(child,result);
                    }
                }
            }
        }
    }
    
    private String collectSegCodes()
    {
        StringBuffer ret=new StringBuffer();
        collectSegCodesFromSyntaxElement(msg,ret);
        return ret.toString();
    }

    public boolean signIt()
    {
        boolean              ret=false;
        HBCIPassportInternal mainPassport=passports.getMainPassport();

        if (mainPassport.hasMySigKey()) {
            String msgName = msg.getName();
            Node msgNode = msg.getSyntaxDef(msgName, gen.getSyntax());
            String dontsignAttr = ((Element)msgNode).getAttribute("dontsign");

            if (dontsignAttr.length()==0) {
                try {
                    int numOfPassports=passports.size();
                    
                    // create an empty sighead and sigtail segment for each required signature
                    for (int idx=0;idx<numOfPassports;idx++) {
                        SEG sighead=SEGFactory.getInstance().createSEG("SigHeadUser","SigHead",msgName,numOfPassports-1-idx,gen.getSyntax());
                        SEG sigtail=SEGFactory.getInstance().createSEG("SigTailUser","SigTail",msgName,idx,gen.getSyntax());
                        
                        List msgelements=msg.getChildContainers();
                        List sigheads=((MultipleSEGs)(msgelements.get(1))).getElements();
                        List sigtails=((MultipleSEGs)(msgelements.get(msgelements.size()-2))).getElements();

                        // insert sighead segment in msg
                        if ((numOfPassports-1-idx)<sigheads.size()) {
                            SEGFactory.getInstance().unuseObject(sigheads.get(numOfPassports-1-idx));
                        } else {
                            for (int i=sigheads.size()-1;i<numOfPassports-1-idx;i++) {
                                sigheads.add(null);
                            }
                        }
                        sigheads.set(numOfPassports-1-idx,sighead);
                        
                        // insert sigtail segment in message
                        if (idx<sigtails.size()) {
                            SEGFactory.getInstance().unuseObject(sigtails.get(idx));
                        } else {
                            for (int i=sigtails.size()-1;i<idx;i++) {
                                sigtails.add(null);
                            }
                        }
                        sigtails.set(idx,sigtail);
                    }
                    
                    // fill all sighead and sigtail segments
                    for (int idx=0;idx<numOfPassports;idx++) {
                        HBCIPassportInternal passport=passports.getPassport(idx);
                        String               role=passports.getRole(idx);
                        
                        setParam("secmethod",passport.getSecMethod12());
                        setParam("cid",passport.getCID());
                        setParam("role",role);
                        setParam("range","1");
                        setParam("keyblz",passport.getBLZ());
                        setParam("keycountry",passport.getCountry());
                        setParam("keyuserid",passport.getMySigKeyName());
                        setParam("keynum",passport.getMySigKeyNum());
                        setParam("keyversion",passport.getMySigKeyVersion());
                        setParam("sysid",passport.getSysId());
                        setParam("sigid",passport.getSigId().toString());
                        setParam("sigalg",passport.getSigAlg());
                        setParam("sigmode",passport.getSigMode());
                        setParam("hashalg",passport.getHashAlg());
                        passport.incSigId();
                        passport.saveChanges();
                        
                        List msgelements=msg.getChildContainers();
                        List sigheads=((MultipleSEGs)(msgelements.get(1))).getElements();
                        List sigtails=((MultipleSEGs)(msgelements.get(msgelements.size()-2))).getElements();

                        SEG sighead=(SEG)sigheads.get(numOfPassports-1-idx);
                        SEG sigtail=(SEG)sigtails.get(idx);
                        
                        fillSigHead(sighead);
                        fillSigTail(sighead,sigtail);
                    }
                     
                    msg.enumerateSegs(0,SyntaxElement.ALLOW_OVERWRITE);
                    msg.validate();
                    msg.enumerateSegs(1,SyntaxElement.ALLOW_OVERWRITE);

                    // calculate signatures for each segment
                    for (int idx=0;idx<numOfPassports;idx++) {
                        HBCIPassportInternal passport=passports.getPassport(idx);
                        List                 msgelements=msg.getChildContainers();
                        List                 sigtails=((MultipleSEGs)(msgelements.get(msgelements.size()-2))).getElements();
                        SEG                  sigtail=(SEG)sigtails.get(idx);

                        if (passport.needUserSig()) {
                            // *** bei anderen user-signaturen hier allgemeineren code schreiben
                            byte[] signature=passport.sign(collectSegCodes().getBytes("ISO-8859-1"));
                            
                            String pintan=new String(signature,"ISO-8859-1");
                            int pos=pintan.indexOf("|");
                            
                            if (pos!=-1) {
                                // wenn überhaupt eine signatur existiert
                                // (wird für server benötigt)
                                String pin=pintan.substring(0,pos);
                                msg.propagateValue(sigtail.getPath()+".UserSig.pin",pin,
                                        SyntaxElement.DONT_TRY_TO_CREATE,
                                        SyntaxElement.DONT_ALLOW_OVERWRITE);
                                
                                if (pos<pintan.length()-1) {
                                    String tan=pintan.substring(pos+1);
                                    msg.propagateValue(sigtail.getPath()+".UserSig.tan",tan,
                                            SyntaxElement.DONT_TRY_TO_CREATE,
                                            SyntaxElement.DONT_ALLOW_OVERWRITE);
                                }
                            }
                        } else { // normale signatur
                            String hashdata=collectHashData(idx);
                            byte[] signature=passport.sign(hashdata.getBytes("ISO-8859-1"));
                            
                            msg.propagateValue(sigtail.getPath()+".sig","B"+new String(signature,"ISO-8859-1"),
                                    SyntaxElement.DONT_TRY_TO_CREATE,
                                    SyntaxElement.DONT_ALLOW_OVERWRITE);
                        }
                        
                        msg.validate();
                        msg.enumerateSegs(1,SyntaxElement.ALLOW_OVERWRITE);
                        msg.autoSetMsgSize(gen);
                    }
                } catch (Exception ex) {
                    throw new HBCI_Exception("*** error while signing",ex);
                }
            }
            else HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_SIG_DONTWANT"),HBCIUtils.LOG_DEBUG);

            ret=true;
        } 
        else HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_SIG_NOKEY"),HBCIUtils.LOG_WARN);

        return ret;
    }

    private void readSigHead()
    {
        HBCIPassportInternal mainPassport=passports.getMainPassport();
        String               sigheadName=msg.getName()+".SigHead";
        
        u_secmethod=msg.getValueOfDE(sigheadName+".secfunc");

        if (u_secmethod.equals("2")) {
            // DDV
            u_cid=msg.getValueOfDE(sigheadName+".SecIdnDetails.cid");
            if (!u_cid.equals(mainPassport.getCID())) {
                String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTCIDFAIL");
                if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreSignErrors",errmsg))
                    throw new HBCI_Exception(errmsg);
            }
        } else {
            // RDH und PinTan (= 2 und 999)
            try {
                // falls noch keine system-id ausgehandelt wurde, so sendet der
                // hbci-server auch keine... deshalb der try-catch-block
                u_sysid=msg.getValueOfDE(sigheadName+".SecIdnDetails.sysid");
            } catch (Exception e) {
                u_sysid="0";
            }
        }

        u_role = msg.getValueOfDE(sigheadName + ".role");
        u_range = msg.getValueOfDE(sigheadName + ".range");
        u_keyblz = msg.getValueOfDE(sigheadName + ".KeyName.KIK.blz");
        u_keycountry = msg.getValueOfDE(sigheadName + ".KeyName.KIK.country");
        u_keyuserid = msg.getValueOfDE(sigheadName + ".KeyName.userid");
        u_keynum = msg.getValueOfDE(sigheadName + ".KeyName.keynum");
        u_keyversion = msg.getValueOfDE(sigheadName + ".KeyName.keyversion");
        u_sigid = msg.getValueOfDE(sigheadName + ".secref");
        u_sigalg = msg.getValueOfDE(sigheadName + ".SigAlg.alg");
        u_sigmode = msg.getValueOfDE(sigheadName + ".SigAlg.mode");
        u_hashalg = msg.getValueOfDE(sigheadName + ".HashAlg.alg");
        
        if (mainPassport.needUserSig()) {
            // *** bei anderen user-signaturen hier allgemeineren code schreiben
            Hashtable values=new Hashtable();
            msg.extractValues(values);
            
            String pin=(String)values.get(msg.getName()+".SigTail.UserSig.pin");
            String tan=(String)values.get(msg.getName()+".SigTail.UserSig.tan");
            
            sigstring=((pin!=null)?pin:"")+"|"+((tan!=null)?tan:"");
        } else {
            sigstring = msg.getValueOfDE(msg.getName() + ".SigTail.sig");
        }

        String checkref=msg.getValueOfDE(msg.getName()+".SigHead.seccheckref");
        String checkref2=msg.getValueOfDE(msg.getName()+".SigTail.seccheckref");

        if (checkref==null || !checkref.equals(checkref2)) {
            String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_SIGREFFAIL");
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreSignErrors",errmsg))
                throw new HBCI_Exception(errmsg);
        }
        if (!u_secmethod.equals(mainPassport.getSecMethod12())) {
            String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_SIGTYPEFAIL");
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreSignErrors",errmsg))
                throw new HBCI_Exception(errmsg);
        }
        
        // *** diese checks werden vorerst abgeschaltet, damit die pin-tan sigs
        // ohne probleme funktionieren
        /*
        if (!u_sigalg.equals(passport.getSigAlg()))
            throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_SIGALGFAIL"));
        if (!u_sigmode.equals(passport.getSigMode()))
            throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_SIGMODEFAIL"));
        if (!u_hashalg.equals(passport.getHashAlg()))
            throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_SIGHASHFAIL"));
        */
    }

    private boolean hasSig()
    {
        boolean ret = true;
        MultipleSyntaxElements seglist = (MultipleSyntaxElements)(msg.getChildContainers().get(1));

        if (seglist instanceof MultipleSEGs) {
            SEG sighead = null;
            try {
                /*** multiple signatures not supported until now ***/
                sighead = (SEG)(seglist.getElements().get(0));
            } catch (IndexOutOfBoundsException e) {
                ret = false;
            }

            if (ret) {
                String sigheadCode = "HNSHK";

                if (!sighead.getCode(gen).equals(sigheadCode))
                    ret = false;
            }
        }
        else ret = false;

        return ret;
    }

    public boolean verify()
    {
        HBCIPassportInternal mainPassport=passports.getMainPassport();
        boolean              ret=false;

        if (mainPassport.hasInstSigKey()) {
            String msgName = msg.getName();
            Node msgNode = msg.getSyntaxDef(msgName, gen.getSyntax());
            String dontsignAttr = ((Element)msgNode).getAttribute("dontsign");

            if (dontsignAttr.length()==0) {
                if (hasSig()) {
                    readSigHead();
                    try {
                        if (mainPassport.needUserSig()) {
                            // TODO hier evtl. allgemeineren code für andere
                            // user-signatures schreiben
                            ret=mainPassport.verify(collectSegCodes().getBytes("ISO-8859-1"),
                                    sigstring.getBytes("ISO-8859-1"));
                        } else {
                            ret=mainPassport.verify(collectHashData(0,0).getBytes("ISO-8859-1"),
                                    sigstring.getBytes("ISO-8859-1"));
                        }
                    } catch (Exception e) {
                        ret=false;
                    }
                } else {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_SIG_NOSIG"),HBCIUtils.LOG_WARN);
                    /* das ist nur für den fall, dass das institut prinzipiell nicht signiert
                       (also für den client-code);
                       die verify()-funktion für den server-code überprüft selbstständig, ob
                       tatsächlich eine benötigte signatur vorhanden ist (verlässt sich also nicht
                       auf dieses TRUE, was beim fehlen einer signatur zurückgegeben wird */
                    ret=true;
                }
            } else {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_VER_DONTNEED"),HBCIUtils.LOG_DEBUG);
                ret=true;
            }
        } else {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_VER_NOKEY"),HBCIUtils.LOG_WARN);
            ret=true;
        }

        return ret;
    }

    public void setParam(String key, String value)
    {
        try {
            Field f=this.getClass().getDeclaredField("u_"+key);
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_CRYPT_SET",new Object[] {key,value}),HBCIUtils.LOG_DEBUG);
            f.set(this,value);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while setting sig parameter",ex);
        }
    }
    
    public void destroy()
    {
        gen=null;
        msg=null;
        passports=null;
        sigstring=null;
        u_cid=null;
        u_hashalg=null;
        u_keyblz=null;
        u_keycountry=null;
        u_keynum=null;
        u_keyuserid=null;
        u_keyversion=null;
        u_range=null;
        u_role=null;
        u_secmethod=null;
        u_sigalg=null;
        u_sigid=null;
        u_sigmode=null;
        u_sysid=null;
    }
}
