
/*  $Id: GVKUmsAll.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.GV;

import java.text.SimpleDateFormat;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci.swift.Swift;

public class GVKUmsAll
    extends HBCIJobImpl
{
    private StringBuffer buffer;

    public static String getLowlevelName()
    {
        return "KUmsZeit";
    }
    
    public GVKUmsAll(String name,HBCIPassportInternal passport)
    {
        super(name,passport,new GVRKUms());
        this.buffer=new StringBuffer();
    }

    public GVKUmsAll(HBCIPassportInternal passport)
    {
        this(getLowlevelName(),passport);

        addConstraint("my.number","KTV.number",passport.getUPD().getProperty("KInfo.KTV.number"));
        addConstraint("my.country","KTV.KIK.country",passport.getUPD().getProperty("KInfo.KTV.KIK.country"));
        addConstraint("my.blz","KTV.KIK.blz",passport.getUPD().getProperty("KInfo.KTV.KIK.blz"));
        addConstraint("startdate","startdate","");
        addConstraint("enddate","enddate","");
        addConstraint("maxentries","maxentries","");
        
        addConstraint("dummy","allaccounts","N");
    }

    protected void extractResults(Properties result,String header,int idx)
    {
        try {
            buffer.append(Swift.decodeUmlauts(result.getProperty(header+".booked")));
            String booked=buffer.toString();
            
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd");
            
            // split into "buchungstage"
            while (booked.length()!=0) {
                String st_tag=Swift.getOneBlock(booked);
                GVRKUms.BTag btag=new GVRKUms.BTag();
                
                // extract konto data
                String konto_info=Swift.getTagValue(st_tag,"25",0);
                int pos=konto_info.indexOf("/");
                String blz;
                String number;
                String curr;
                
                if (pos!=-1) {
                    blz=konto_info.substring(0,pos);
                    number=konto_info.substring(pos+1);
                    curr=null;
                    
                    for (pos=number.length();pos>0;pos--) {
                        char ch=number.charAt(pos-1);
                        
                        if (ch>='0' && ch<='9')
                            break;
                    }
                    
                    if (pos<number.length()) {
                        curr=number.substring(pos);
                        number=number.substring(0,pos);
                    }
                } else {
                    blz="";
                    number=konto_info;
                    curr="";
                }
                    
                btag.my=new Konto();
                btag.my.blz=blz;
                btag.my.number=number;
                btag.my.curr=curr;
                getMainPassport().fillAccountInfo(btag.my);
                
                // extract "auszugsnummer"
                btag.counter=Swift.getTagValue(st_tag,"28C",0);
                
                // extract "anfangssaldo"
                btag.start=new Saldo();
                
                String st_start=Swift.getTagValue(st_tag,"60F",0);
                btag.starttype='F';
                if (st_start==null) {
                    st_start=Swift.getTagValue(st_tag,"60M",0);
                    btag.starttype='M';
                }
                btag.start.cd=st_start.substring(0,1);
                
                int next=0;
                if (st_start.charAt(2)>'9') {
                    btag.start.timestamp=null;
                    next=2;
                } else {
                    btag.start.timestamp=dateFormat.parse(st_start.substring(1,7));
                    next=7;
                }
                
                btag.start.value.curr=st_start.substring(next,next+3);
                btag.start.value.value=HBCIUtils.string2Value(st_start.substring(next+3).replace(',','.'));
                
                // looping to get all "umsaetze"
                long saldo=Math.round(btag.start.value.value*100);
                if (btag.start.cd.equals("D")) {
                    saldo=-saldo;
                }
                int ums_counter=0;
                
                while (true) {
                    String st_ums=Swift.getTagValue(st_tag,"61",ums_counter);
                    if (st_ums==null)
                        break;
                    
                    GVRKUms.UmsLine line=new GVRKUms.UmsLine();
                    
                    // extract valuta
                    line.valuta=dateFormat.parse(st_ums.substring(0,6));
                    
                    // extract bdate
                    next=0;
                    if (st_ums.charAt(6)>'9') {
                        line.bdate=btag.start.timestamp;
                        next=6;
                    } else {
                        line.bdate=dateFormat.parse(st_ums.substring(0,2)+
                                st_ums.substring(6,10));
                        next=10;
                    }
                    
                    // extract credit/debit
                    if (st_ums.charAt(next)=='C' || st_ums.charAt(next)=='D') {
                        line.cd=st_ums.substring(next,next+1);
                        next++;
                    } else {
                        line.cd=st_ums.substring(next,next+2);
                        next+=2;
                    }
                    
                    // skip part of currency
                    char currpart=st_ums.charAt(next);
                    if (currpart>'9')
                        next++;
                    
                    line.value=new Value();
                    line.value.curr=btag.start.value.curr;
                    
                    // extract value and skip code
                    int npos=st_ums.indexOf("N",next);
                    line.value.value=HBCIUtils.string2Value(st_ums.substring(next,npos).replace(',','.'));
                    next=npos+4;
                    
                    // update saldo
                    if (line.cd.charAt(line.cd.length()-1)=='C') {
                        saldo+=Math.round(line.value.value*100);
                    } else {
                        saldo-=Math.round(line.value.value*100);
                    }
                    line.saldo=new Saldo();
                    line.saldo.cd=(saldo>=0)?"C":"D";
                    line.saldo.timestamp=line.bdate;
                    line.saldo.value=new Value(Math.abs(saldo/100.0),btag.start.value.curr);
                    
                    // extract customerref
                    npos=st_ums.indexOf("//",next);
                    if (npos==-1)
                        npos=st_ums.indexOf("\r\n",next);
                    if (npos==-1)
                        npos=st_ums.length();
                    line.customerref=st_ums.substring(next,npos);
                    next=npos;
                    
                    // check for instref
                    if (next<st_ums.length() && st_ums.substring(next,next+2).equals("//")) {
                        // extract instref
                        next+=2;
                        npos=st_ums.indexOf("\r\n",next);
                        if (npos==-1)
                            npos=st_ums.length();
                        line.instref=st_ums.substring(next,npos);
                        next=npos+2;
                    }
                    if (line.instref==null)
                        line.instref="";
                    
                    // check for additional information
                    if (next<st_ums.length() && st_ums.charAt(next)=='\r') {
                        next+=2;
                        
                        // extract orig Value
                        pos=st_ums.indexOf("/OCMT/",next);
                        if (pos!=-1) {
                            line.orig_value=new Value();
                            line.orig_value.curr=st_ums.substring(pos+6,pos+9);
                            
                            int slashpos=st_ums.indexOf("/",pos+9);
                            if (slashpos==-1)
                                slashpos=st_ums.length();
                            
                            line.orig_value.value=HBCIUtils.string2Value(st_ums.substring(pos+9,slashpos).replace(',','.'));
                        }
                        
                        // extract charge Value
                        pos=st_ums.indexOf("/CHGS/",next);
                        if (pos!=-1) {
                            line.charge_value=new Value();
                            line.charge_value.curr=st_ums.substring(pos+6,pos+9);
                            
                            int slashpos=st_ums.indexOf("/",pos+9);
                            if (slashpos==-1)
                                slashpos=st_ums.length();
                            
                            line.charge_value.value=HBCIUtils.string2Value(st_ums.substring(pos+9,slashpos).replace(',','.'));
                        }
                    }
                    
                    String st_multi=Swift.getTagValue(st_tag,"86",ums_counter);
                    if (st_multi!=null) {
                        line.gvcode=st_multi.substring(0,3);
                        st_multi=Swift.packMulti(st_multi.substring(3));
                        
                        if (!line.gvcode.equals("999")) {
                            line.text=Swift.getMultiTagValue(st_multi,"00");
                            line.primanota=Swift.getMultiTagValue(st_multi,"10");
                            for (int i=0;i<10;i++) {
                                line.addUsage(Swift.getMultiTagValue(st_multi,Integer.toString(20+i)));
                            }
                            
                            Konto acc=new Konto();
                            acc.blz=Swift.getMultiTagValue(st_multi,"30");
                            acc.number=Swift.getMultiTagValue(st_multi,"31");
                            acc.name=Swift.getMultiTagValue(st_multi,"32");
                            acc.name2=Swift.getMultiTagValue(st_multi,"33");
                            if (acc.blz!=null ||
                                    acc.number!=null ||
                                    acc.name!=null ||
                                    acc.name2!=null) {
                                
                                if (acc.blz==null)
                                    acc.blz="";
                                if (acc.number==null)
                                    acc.number="";
                                if (acc.name==null)
                                    acc.name="";
                                line.other=acc;
                            }
                            
                            line.addkey=Swift.getMultiTagValue(st_multi,"34");
                            for (int i=0;i<4;i++) {
                                line.addUsage(Swift.getMultiTagValue(st_multi,Integer.toString(60+i)));
                            }
                        } 
                        else line.additional=st_multi;
                    }
                    
                    btag.addLine(line);
                    ums_counter++;
                }
                
                // extract "schlusssaldo"
                btag.end=new Saldo();
                
                String st_end=Swift.getTagValue(st_tag,"62F",0);
                btag.endtype='F';
                if (st_end==null) {
                    st_end=Swift.getTagValue(st_tag,"62M",0);
                    btag.endtype='M';
                }
                btag.end.cd=st_end.substring(0,1);
                
                next=0;
                if (st_end.charAt(2)>'9') {
                    btag.end.timestamp=null;
                    next=2;
                } else {
                    btag.end.timestamp=dateFormat.parse(st_end.substring(1,7));
                    next=7;
                }
                
                // set default values for optional non-given bdates
                if (btag.start.timestamp==null) {
                    btag.start.timestamp=btag.end.timestamp;
                }
                for (int j=0;j<btag.lines.length;j++) {
                    if (btag.lines[j].bdate==null) {
                        btag.lines[j].bdate=btag.end.timestamp;
                    }
                }
                
                btag.end.value.curr=st_end.substring(next,next+3);
                btag.end.value.value=HBCIUtils.string2Value(st_end.substring(next+3).replace(',','.'));
                
                ((GVRKUms)(jobResult)).addTag(btag);
                buffer.delete(0,st_tag.length());
                booked=buffer.toString();
            }
            
            ((GVRKUms)jobResult).rest=buffer.toString();
            jobResult.storeResult("notbooked",result.getProperty(header+".notbooked"));
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
    
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
