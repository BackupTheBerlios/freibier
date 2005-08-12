
/*  $Id: HBCIPassportOpenHBCI.java,v 1.2 2005/08/12 22:57:11 tbayen Exp $

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

package org.kapott.hbci.passport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.datatypes.SyntaxCtr;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.security.RSAPrivateCrtKey2;

/** <p>Passport-Variante für die Arbeit mit 
    <a href="http://openhbci.sf.net">OpenHBCI</a>-Schlüsseldateien. OpenHBCI ist
    eine freie HBCI-Client-Implementierung in C++. Diese Klasse ermöglicht es,
    die mit OpenHBCI erzeugten Schlüsseldateien für RDH-Zugänge auch mit
    <em>HBCI4Java</em> zu verwenden. Die OpenHBCI-Schlüsseldateien können gelesen,
    geschrieben und (im Prinzip) auch völlig neu erzeugt werden. Es gibt allerdings
    einige Einschränkungen, die in der Datei <code>README.OpenHBCI</code>
    nachgelesen werden können.</p>
    <p>Es ist also möglich, eine existierende OpenHBCI-Schlüsseldatei direkt mit
    <em>HBCI4Java</em> zu benutzen. Dazu muss als Passport-Variante 
    <code>OpenHBCI</code> ausgewählt werden - siehe 
    {@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance()}.</p>
    <p>Außerdem ist es möglich, einen "frischen" RDH-Zugang erstmals mit <em>HBCI4Java</em>
    zu initialisieren und dabei gleich eine OpenHBCI-Schlüsseldatei anzulegen. Diese
    OpenHBCI-Schlüsseldatei kann dann sowohl mit <em>HBCI4Java</em> also auch
    mit OpenHBCI verwendet werden (--> <code>README.OpenHBCI</code>!).</p> 
    <p>Mit dem Tool {@link org.kapott.hbci.tools.ConvertOpenHBCIPassport}
    kann eine existierende OpenHBCI-Schlüsseldatei in das <em>HBCI4Java</em>-interne
    RDHNew-Format konvertiert werden. Das ist ebenfalls mit dem <em>HBCI4Java Passport Editor</em>
    möglich, ebenso wie die Konvertierung in die andere Richtung (RDHNew --> OpenHBCI)</p>*/
public class HBCIPassportOpenHBCI 
    extends HBCIPassportRDH
{
    private static final int VERSION_MAJOR=1;
    private static final int VERSION_MINOR=1;
        
    private static final int TAG_VERSION_MAJOR=0x02;
    private static final int TAG_VERSION_MINOR=0x03;
    private static final int TAG_CRYPT=0xc1;
    private static final int TAG_SEQ=0x04;
    private static final int TAG_USERID=0x09;
    private static final int TAG_COUNTRY=0x0c;
    private static final int TAG_BLZ=0x0d;
    private static final int TAG_SYSID=0x0e;
    private static final int TAG_INST_SIG_KEY=0xca;
    private static final int TAG_INST_CRYPT_KEY=0xcb;
    private static final int TAG_MY_PUB_SIG_KEY=0xc5;
    private static final int TAG_MY_PRIV_SIG_KEY=0xc6;
    private static final int TAG_MY_PUB_ENC_KEY=0xc7;
    private static final int TAG_MY_PRIV_ENC_KEY=0xc8;
    private static final int TAG_KEY_ISPUBLIC=0x01;
    private static final int TAG_KEY_ISCRYPT=0x02;
    private static final int TAG_KEY_USERID=0x03;
    private static final int TAG_KEY_NUM=0x05;
    private static final int TAG_KEY_VERSION=0x04;
    private static final int TAG_KEY_PUBEXPONENT=0x07;
    private static final int TAG_KEY_MODULUS=0x06;
    private static final int TAG_KEY_PRIVEXPONENT=0x0e;
    private static final int TAG_KEY_N=0x08;
    private static final int TAG_KEY_P=0x09;
    private static final int TAG_KEY_Q=0x0a;
    private static final int TAG_KEY_DP=0x0b;
    private static final int TAG_KEY_DQ=0x0c;
    private static final int TAG_KEY_QINV=0x0d;
    
    private String datafilename;
    
    public HBCIPassportOpenHBCI(Object initObject)
    {
        super(initObject,0);

        String  header="client.passport.OpenHBCI.";
        String  filename=HBCIUtils.getParam(header+"mediafile");
        String  datfilename=HBCIUtils.getParam(header+"datafile");
        boolean init=HBCIUtils.getParam(header+"init").equals("1");

        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PASS_LOADFILE",filename),HBCIUtils.LOG_DEBUG);
        setFileName(filename);
        setDataFileName(datfilename);

        if (init) {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_PASS_LOADDATE",filename),HBCIUtils.LOG_DEBUG);

            setPort(new Integer(3000));
            setFilterType("None");
            
            if (!new File(filename).canRead()) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_PASS_NEWFILE"),HBCIUtils.LOG_WARN);
                askForMissingData(true,true,false,false,false,true,false);
                saveChanges();
            }
            
            try {
                FileInputStream fin=new FileInputStream(getFileName());
                int size=fin.available();
                byte[] buffer=new byte[size];
                
                int offset=0;
                while (size>0) {
                    int current=fin.read(buffer,offset,size);
                    size-=current;
                    offset+=current;
                }
                fin.close();
                
                int tag=byte2int(buffer[0]);
                if (tag!=TAG_CRYPT)
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_TAGERR",
                            new String[] {String.valueOf(TAG_CRYPT),String.valueOf(tag)}));
                
                int dataSize=byte2int(buffer[1]) | (byte2int(buffer[2])<<8);
                
                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));
                byte[] plainText=null;
                
                while (true) {          // loop for entering the correct passphrase
                    if (passportKey==null)
                        passportKey=calculatePassportKey(FOR_LOAD);

                    Cipher cipher=Cipher.getInstance("DESede/CBC/NoPadding");
                    IvParameterSpec ivspec=new IvParameterSpec(new byte[8]);
                    cipher.init(Cipher.DECRYPT_MODE,passportKey,ivspec);
                    plainText=cipher.doFinal(buffer,3,dataSize);
                    
                    byte[] versioninfo=new byte[4];
                    System.arraycopy(plainText,0,versioninfo,0,4);
                    byte[] expectedVersionInfo=new byte[] {(byte)TAG_VERSION_MAJOR,
                                                           (byte)1,
                                                           (byte)0,
                                                           Integer.toString(VERSION_MAJOR).getBytes()[0]};
                    if (Arrays.equals(versioninfo,expectedVersionInfo))
                        break;

                    passportKey=null;
                    retries--;
                    if (retries<=0)
                        throw new InvalidPassphraseException(null);
                }
                
                int padSize=plainText[plainText.length-1];
                byte[] tlvdata=new byte[plainText.length-padSize];
                System.arraycopy(plainText,0,tlvdata,0,plainText.length-padSize);
                plainText=null;

                byte[] content=getTLVValue(tlvdata,TAG_SEQ);
                if (content!=null){
                	// String str=bytearray2string(content);
                	// Long lo=new Long(Long.parseLong(str));
                    setSigId(new Long(Long.parseLong(bytearray2string(content))+1));
                }
                
                content=getTLVValue(tlvdata,TAG_USERID);
                if (content!=null)
                    setUserId(bytearray2string(content));
                
                content=getTLVValue(tlvdata,TAG_COUNTRY);
                if (content!=null)
                    setCountry(SyntaxCtr.getName(bytearray2string(content)));
                
                content=getTLVValue(tlvdata,TAG_BLZ);
                if (content!=null)
                    setBLZ(bytearray2string(content));

                content=getTLVValue(tlvdata,TAG_SYSID);
                if (content!=null)
                    setSysId(bytearray2string(content));
                
                byte[] keycontent=getTLVValue(tlvdata,TAG_INST_SIG_KEY);
                if (keycontent!=null)
                    setInstSigKey(createHBCIKey(keycontent));

                keycontent=getTLVValue(tlvdata,TAG_INST_CRYPT_KEY);
                if (keycontent!=null)
                    setInstEncKey(createHBCIKey(keycontent));

                keycontent=getTLVValue(tlvdata,TAG_MY_PUB_SIG_KEY);
                if (keycontent!=null)
                    setMyPublicSigKey(createHBCIKey(keycontent));

                keycontent=getTLVValue(tlvdata,TAG_MY_PRIV_SIG_KEY);
                if (keycontent!=null)
                    setMyPrivateSigKey(createHBCIKey(keycontent));

                keycontent=getTLVValue(tlvdata,TAG_MY_PUB_ENC_KEY);
                if (keycontent!=null)
                    setMyPublicEncKey(createHBCIKey(keycontent));

                keycontent=getTLVValue(tlvdata,TAG_MY_PRIV_ENC_KEY);
                if (keycontent!=null)
                    setMyPrivateEncKey(createHBCIKey(keycontent));
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_READERR"),e);
            }
            
            readDataFile();
            
            if (askForMissingData(true,true,true,false,false,true,true))
                saveChanges();
        }
    }

    private String bytearray2string(byte[] ba){
    	String str="";
    	for(int i=0; i<ba.length; i++){
    		str+=(char)(ba[i]);
    	}
    	return str;
    }
    
    private int byte2int(byte b)
    {
        int ret=b;
        if (ret<0)
            ret+=256;
        return ret;
    }
    
    private HBCIKey createHBCIKey(byte[] keycontent)
    {
        try {
            String key_userid=new String(bytearray2string(getTLVValue(keycontent,TAG_KEY_USERID)));
            //String key_userid=new String(String.valueOf(getTLVValue(keycontent,TAG_KEY_USERID)));
            System.out.println(key_userid);
            String key_num=new String(bytearray2string(getTLVValue(keycontent,TAG_KEY_NUM)));
            System.out.println(key_num);
            String key_version=new String(bytearray2string(getTLVValue(keycontent,TAG_KEY_VERSION)));
            System.out.println(key_version);
            System.out.println(bytearray2string(getTLVValue(keycontent,TAG_KEY_PUBEXPONENT)));
            BigInteger pubExponent=new BigInteger(bytearray2string(getTLVValue(keycontent,TAG_KEY_PUBEXPONENT)));

            BigInteger modulus=null;
            byte[] content=getTLVValue(keycontent,TAG_KEY_MODULUS);
            if (content!=null)
                modulus=new BigInteger(+1,content);
            
            BigInteger n=null;
            content=getTLVValue(keycontent,TAG_KEY_N);
            if (content!=null)
                n=new BigInteger(+1,content);

            BigInteger privExponent=null;
            content=getTLVValue(keycontent,TAG_KEY_PRIVEXPONENT);
            if (content!=null) 
                privExponent=new BigInteger(+1,content);

            BigInteger p=null;
            content=getTLVValue(keycontent,TAG_KEY_P);
            if (content!=null) 
                p=new BigInteger(+1,content);

            BigInteger q=null;
            content=getTLVValue(keycontent,TAG_KEY_Q);
            if (content!=null)
                q=new BigInteger(+1,content);

            BigInteger dP=null;
            content=getTLVValue(keycontent,TAG_KEY_DP);
            if (content!=null)
                dP=new BigInteger(+1,content);

            BigInteger dQ=null;
            content=getTLVValue(keycontent,TAG_KEY_DQ);
            if (content!=null)
                dQ=new BigInteger(+1,content);

            BigInteger qInv=null;
            content=getTLVValue(keycontent,TAG_KEY_QINV);
            if (content!=null)
                qInv=new BigInteger(+1,content);

            Key key_key;
            if (bytearray2string(getTLVValue(keycontent,TAG_KEY_ISPUBLIC)).equals("YES")) {
                RSAPublicKeySpec keyspec=new RSAPublicKeySpec(modulus,pubExponent);
                KeyFactory fac=KeyFactory.getInstance("RSA");
                key_key=fac.generatePublic(keyspec);
            } else {
                if (privExponent==null) {
                    // only CRT
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KEY_CRTONLY"),HBCIUtils.LOG_DEBUG);
                    key_key=new RSAPrivateCrtKey2(p,q,dP,dQ,qInv);
                } else if (p==null) {
                    // only exponent
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KEY_EXPONLY"),HBCIUtils.LOG_DEBUG);
                    RSAPrivateKeySpec spec=new RSAPrivateKeySpec(modulus!=null?modulus:n,privExponent);
                    key_key=KeyFactory.getInstance("RSA").generatePrivate(spec);
                } else {
                    // complete data
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KEY_FULL"),HBCIUtils.LOG_DEBUG);
                    RSAPrivateCrtKeySpec spec=new RSAPrivateCrtKeySpec(modulus!=null?modulus:n,new BigInteger("65537"),privExponent,
                                                                       p,q,dP,dQ,qInv);
                    key_key=KeyFactory.getInstance("RSA").generatePrivate(spec);
                }
            }

            HBCIKey hbcikey=new HBCIKey(getCountry(),getBLZ(),key_userid,
                                        key_num,key_version,
                                        key_key);

            return hbcikey;
        } catch (Exception ex) {
            throw new HBCI_Exception(ex);
        }
    }

    protected SecretKey calculatePassportKey(boolean forSaving)
    {
        try {
            StringBuffer passphrase=new StringBuffer();
            HBCIUtilsInternal.getCallback().callback(this,
                                             forSaving?HBCICallback.NEED_PASSPHRASE_SAVE
                                                      :HBCICallback.NEED_PASSPHRASE_LOAD,
                                             forSaving?HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS_NEW")
                                                      :HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS"),
                                             HBCICallback.TYPE_SECRET,
                                             passphrase);
            if (passphrase.length()==0) {
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSZERO"));
            }

            SecretKeyFactory fac=SecretKeyFactory.getInstance("DESede");
            byte[] keydata=calculateKeyDataFromPassphrase(passphrase.toString());
            DESedeKeySpec keyspec=new DESedeKeySpec(keydata);
            SecretKey passportKey=fac.generateSecret(keyspec);
            passphrase=null;

            return passportKey;
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_KEYCALCERR"),ex);
        }
    }
    
    private byte[] calculateKeyDataFromPassphrase(String pin)
    {
        try {
            byte[] ret=new byte[24];
            byte[] left=new byte[8];
            byte[] right=new byte[8];

            byte[] input=pin.getBytes("ISO-8859-1");
            int len=pin.length();

            for (int i=0;i<len;i++) {
                int j=byte2int(input[i]);

                if ((i%32)<16) {
                    if ((i%16)<8) {
                        left[i%8]^=(j<<1)&0xFF;
                    } else {
                        right[i%8]^=(j<<1)&0xFF;
                    }
                } else {
                    j=((j<<4)&0xf0)|((j>>4)&0x0f);
                    j=((j<<2)&0xcc)|((j>>2)&0x33);
                    j=((j<<1)&0xaa)|((j>>1)&0x55);
                    if ((i%16)<8) {
                        left[7-(i%8)]^=j;
                    } else {
                        right[7-(i%8)]^=j;
                    }
                }
            }

            if (len<=8)
                System.arraycopy(left,0,right,0,8);
                
            SecretKeyFactory fac=SecretKeyFactory.getInstance("DES");
            left=((DESKeySpec)fac.getKeySpec(fac.generateSecret(new DESKeySpec(left)),
                                             DESKeySpec.class)).getKey();
            right=((DESKeySpec)fac.getKeySpec(fac.generateSecret(new DESKeySpec(right)),
                                              DESKeySpec.class)).getKey();
                
            checksum(input,left);
            checksum(input,right);

            System.arraycopy(left,0,ret,0,8);
            System.arraycopy(right,0,ret,8,8);
            System.arraycopy(left,0,ret,16,8);
            return ret;
        } catch (Exception ex) {
            throw new HBCI_Exception(ex);
        }
    }
    
    private void checksum(byte[] data,byte[] adddata)
    {
        try {
            Cipher cipher=Cipher.getInstance("DES");
            SecretKeyFactory fac=SecretKeyFactory.getInstance("DES");
            DESKeySpec keyspec=new DESKeySpec(adddata);
            SecretKey key=fac.generateSecret(keyspec);
            
            byte[] tout=new byte[8];
            System.arraycopy(adddata,0,tout,0,8);
            
            byte[] tin=new byte[8];
            int offset=0;
            int len=data.length;
            
            while (len>0) {
                int current=Math.min(8,len);
                Arrays.fill(tin,(byte)0);
                System.arraycopy(data,offset,tin,0,current);
                
                offset+=current;
                len-=current;
                
                for (int i=0;i<8;i++) {
                    tin[i]^=tout[i];
                }
                
                cipher.init(Cipher.ENCRYPT_MODE,key);
                tin=cipher.doFinal(tin);
                
                System.arraycopy(tin,0,tout,0,8);
            }
            
            System.arraycopy(tout,0,adddata,0,8);
        } catch (Exception ex) {
            throw new HBCI_Exception(ex);
        }
    }
    
    private byte[] getTLVValue(byte[] data,int tag)
    {
        return getTLVValue(data,tag,0);
    }
    
    private byte[] getTLVValue(byte[] data,int tag,int idx)
    {
        byte[] ret=null;
        int len=data.length;
        int offset=0;
        
        while (offset<len) {
            int size=byte2int(data[offset+1]) | (byte2int(data[offset+2])<<8);
            
            if (byte2int(data[offset])==tag) {
                if (idx--==0) {
                    ret=new byte[size];
                    System.arraycopy(data,offset+3,ret,0,size);
                    break;
                }
            }
            
            offset+=3+size;
        }
        
        return ret;
    }
    
    public void saveChanges()
    {
        try {
            StringBuffer tlvdata=new StringBuffer();
            
            tlvdata.append(createTLV(TAG_VERSION_MAJOR,Integer.toString(VERSION_MAJOR)));
            tlvdata.append(createTLV(TAG_VERSION_MINOR,Integer.toString(VERSION_MINOR)));
            tlvdata.append(createTLV(TAG_SEQ,Long.toString(getSigId().longValue()-1)));
            tlvdata.append(createTLV(TAG_MY_PUB_SIG_KEY,
                                     createTLVKeyEntry(getMyPublicSigKey(),true,false)));
            tlvdata.append(createTLV(TAG_MY_PRIV_SIG_KEY,
                                     createTLVKeyEntry(getMyPrivateSigKey(),false,false)));
            tlvdata.append(createTLV(TAG_MY_PUB_ENC_KEY,
                                     createTLVKeyEntry(getMyPublicEncKey(),true,true)));
            tlvdata.append(createTLV(TAG_MY_PRIV_ENC_KEY,
                                     createTLVKeyEntry(getMyPrivateEncKey(),false,true)));
            tlvdata.append(createTLV(TAG_USERID,getUserId()));
            tlvdata.append(createTLV(TAG_INST_SIG_KEY,
                                     createTLVKeyEntry(getInstSigKey(),true,false)));
            tlvdata.append(createTLV(TAG_INST_CRYPT_KEY,
                                     createTLVKeyEntry(getInstEncKey(),true,true)));
            tlvdata.append(createTLV(TAG_COUNTRY,SyntaxCtr.getCode(getCountry())));
            tlvdata.append(createTLV(TAG_BLZ,getBLZ()));
            tlvdata.append(createTLV(TAG_SYSID,getSysId()));
            
            int tlvlen=tlvdata.length();
            int padSize=8-(tlvlen%8);
            byte[] plainData=new byte[tlvlen+padSize];
            System.arraycopy(tlvdata.toString().getBytes("ISO-8859-1"),0,
                             plainData,0,
                             tlvlen);
            for (int i=0;i<padSize;i++)
                plainData[tlvlen+i]=(byte)padSize;
            
            if (passportKey==null)
                passportKey=calculatePassportKey(FOR_SAVE);
            Cipher cipher=Cipher.getInstance("DESede/CBC/NoPadding");
            IvParameterSpec ivspec=new IvParameterSpec(new byte[8]);
            cipher.init(Cipher.ENCRYPT_MODE,passportKey,ivspec);
            byte[] cipherText=cipher.doFinal(plainData);
            
            String outputData=createTLV(TAG_CRYPT,new String(cipherText,"ISO-8859-1"));
            
            File passportfile=new File(getFileName());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);

            FileOutputStream fo=new FileOutputStream(tempfile);
            fo.write(outputData.getBytes("ISO-8859-1"));
            fo.close();
            
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_WRITEERR"),e);
        }
    }
    
    private String createTLV(int tag,String data)
    {
        try {
            byte[] ret=new byte[0];
            
            if (data!=null && data.length()!=0) {
                int len=data.length();
                ret=new byte[3+len];

                ret[0]=(byte)(tag&0xFF);
                ret[1]=(byte)(len&0xFF);
                ret[2]=(byte)((len>>8)&0xFF);
                System.arraycopy(data.getBytes("ISO-8859-1"),0,ret,3,len);
            }

            return new String(ret,"ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception(ex);
        }
    }
    
    private String createTLVKeyEntry(HBCIKey key,boolean isPublic,boolean isCrypt)
    {
        try {
            StringBuffer ret=new StringBuffer();
            
            if (key!=null) {
                ret.append(createTLV(TAG_KEY_ISPUBLIC,isPublic?"YES":"NO"));
                ret.append(createTLV(TAG_KEY_ISCRYPT,isCrypt?"YES":"NO"));

                ret.append(createTLV(TAG_KEY_USERID,key.userid));
                ret.append(createTLV(TAG_KEY_NUM,key.num));
                ret.append(createTLV(TAG_KEY_VERSION,key.version));

                KeyFactory fac=KeyFactory.getInstance("RSA");

                if (isPublic) {
                    RSAPublicKeySpec keyspec=(RSAPublicKeySpec)fac.getKeySpec(key.key,RSAPublicKeySpec.class);
                    ret.append(createTLV(TAG_KEY_PUBEXPONENT,keyspec.getPublicExponent().toString()));
                    ret.append(createTLV(TAG_KEY_MODULUS,new String(keyspec.getModulus().toByteArray(),"ISO-8859-1")));
                } else {
                    if (key.key instanceof RSAPrivateCrtKey) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KEY_FULL"),HBCIUtils.LOG_DEBUG);
                        RSAPrivateCrtKey rsakey=(RSAPrivateCrtKey)key.key;

                        ret.append(createTLV(TAG_KEY_PUBEXPONENT,rsakey.getPublicExponent().toString()));
                        ret.append(createTLV(TAG_KEY_PRIVEXPONENT,
                                             new String(rsakey.getPrivateExponent().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_MODULUS,new String(rsakey.getModulus().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_N,new String(rsakey.getModulus().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_P,new String(rsakey.getPrimeP().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_Q,new String(rsakey.getPrimeQ().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_DP,
                                             new String(rsakey.getPrimeExponentP().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_DQ,
                                             new String(rsakey.getPrimeExponentQ().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_QINV,
                                             new String(rsakey.getCrtCoefficient().toByteArray(),"ISO-8859-1")));
                    } else if (key.key instanceof RSAPrivateKey) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KEY_EXPONLY"),HBCIUtils.LOG_DEBUG);
                        RSAPrivateKey rsakey=(RSAPrivateKey)key.key;

                        ret.append(createTLV(TAG_KEY_PRIVEXPONENT,
                                             new String(rsakey.getPrivateExponent().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_MODULUS,new String(rsakey.getModulus().toByteArray(),"ISO-8859-1")));
                    } else if (key.key instanceof RSAPrivateCrtKey2) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KEY_CRTONLY"),HBCIUtils.LOG_DEBUG);
                        RSAPrivateCrtKey2 rsakey=(RSAPrivateCrtKey2)key.key;

                        ret.append(createTLV(TAG_KEY_P,new String(rsakey.getP().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_Q,new String(rsakey.getQ().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_DP,new String(rsakey.getdP().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_DQ,new String(rsakey.getdQ().toByteArray(),"ISO-8859-1")));
                        ret.append(createTLV(TAG_KEY_QINV,new String(rsakey.getQInv().toByteArray(),"ISO-8859-1")));
                    }
                }
            }

            return ret.toString();
        } catch (Exception ex) {
            throw new HBCI_Exception(ex);
        }
    }
    
    private void setDataFileName(String f)
    {
        datafilename=f;
    }
    
    public String getDataFileName()
    {
        return datafilename;
    }
    
    private void readDataFile()
    {
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_PASS_LOADDATE",getDataFileName()),HBCIUtils.LOG_DEBUG);
            BufferedReader is=new BufferedReader(new InputStreamReader(new FileInputStream(getDataFileName())));
            
            String st=is.readLine();
            while (st!=null) {
                if (st.startsWith("[bank/")) {
                    st=readNewBank(st,is);
                } else {
                    st=is.readLine();
                }
            }
            
            is.close();
        } catch (Exception ex) {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_READERR"),
                          HBCIUtils.LOG_WARN);
        }
    }
    
    private String readNewBank(String bankheader,BufferedReader is)
        throws Exception
    {
        Properties currentData=new Properties();
        String     bankIdx=bankheader.substring(6,bankheader.indexOf("]"));
        HBCIUtils.log("*** reading bank:"+bankIdx,HBCIUtils.LOG_DEBUG);
        
        String st;
        while ((st=is.readLine())!=null) {
            if (st.startsWith("[bank/"))
                break;
            
            if (st.startsWith("hbciversion=")) {
                String version=st.substring(st.indexOf('"')+1,
                                            st.lastIndexOf('"'));
                currentData.setProperty("hbciversion",version);
                HBCIUtils.log("*** found hbciversion="+version,HBCIUtils.LOG_DEBUG);
            }
        }
        
        while (st!=null) {
            if (st.equals("[bank/"+bankIdx+"/params]")) {
                st=readBankParams(st,currentData,is);
            } else if (st.startsWith("[bank/"+bankIdx+"/user/")) {
                st=readUser(st,currentData,is);
            }
            else if (st.startsWith("[bank/"+bankIdx+"/account/"))
                st=readAccount(st,currentData,is);
            else
                st=is.readLine();
        }
        
        return st;
    }
    
    private String readBankParams(String paramsheader,Properties currentData,BufferedReader is)
        throws Exception
    {
        HBCIUtils.log("*** reading bank params",HBCIUtils.LOG_DEBUG);
        
        String st=null;
        while ((st=is.readLine())!=null) {
            if (st.startsWith("[bank/"))
                break;
            
            if (st.startsWith("country=")) {
                String value=SyntaxCtr.getName(st.substring(st.indexOf('"')+1,
                                                            st.lastIndexOf('"')));
                currentData.setProperty("country",value);
                HBCIUtils.log("*** found country="+value,HBCIUtils.LOG_DEBUG);
            } else if (st.startsWith("code=")) {
                String value=st.substring(st.indexOf('"')+1,
                                          st.lastIndexOf('"'));
                currentData.setProperty("blz",value);
                HBCIUtils.log("*** found blz="+value,HBCIUtils.LOG_DEBUG);
            } else if (st.startsWith("addr=")) {
                String value=st.substring(st.indexOf('"')+1,
                                          st.lastIndexOf('"'));
                currentData.setProperty("host",value);
                HBCIUtils.log("*** found host="+value,HBCIUtils.LOG_DEBUG);
            }
        }
        
        return st;
    }
    
    private String readUser(String userheader,Properties currentData,BufferedReader is)
        throws Exception
    {
        String     userIdx=userheader.substring(userheader.lastIndexOf("/")+1,
                                                userheader.lastIndexOf("]"));
        HBCIUtils.log("*** reading user:"+userIdx,HBCIUtils.LOG_DEBUG);

        String st=null;
        while ((st=is.readLine())!=null) {
            if (st.startsWith("[bank/"))
                break;
            
            if (st.startsWith("id=")) {
                String value=st.substring(st.indexOf('"')+1,
                                          st.lastIndexOf('"'));
                currentData.setProperty("userid",value);
                HBCIUtils.log("*** found userid="+value,HBCIUtils.LOG_DEBUG);
                
                if (currentData.getProperty("userid","").equals(getUserId()) &&
                    currentData.getProperty("blz","").equals(getBLZ()) &&
                    currentData.getProperty("country","").equals(getCountry())) {
                    String host=currentData.getProperty("host");
                    HBCIUtils.log("setting passport host to "+host,HBCIUtils.LOG_DEBUG);
                    setHost(host);
                    String version=currentData.getProperty("hbciversion");
                    HBCIUtils.log("setting hbciversion to "+version,HBCIUtils.LOG_DEBUG);
                    setHBCIVersion(version);
                }
            }
        }
        
        String realUserHeader=userheader.substring(0,userheader.length()-1);
        while (st!=null) {
            if (st.startsWith("[bank/") &&
                !st.startsWith(realUserHeader))
                break;
            
            if (st.startsWith(realUserHeader+"/customer"))
                st=readCustomer(st,currentData,is);
            else
                st=is.readLine();
        }
        
        return st;
    }
    
    private String readCustomer(String custheader,Properties currentData,BufferedReader is)
        throws Exception
    {
        String     custIdx=custheader.substring(custheader.lastIndexOf("/")+1,
                                                custheader.lastIndexOf("]"));
        HBCIUtils.log("*** reading customer:"+custIdx,HBCIUtils.LOG_DEBUG);

        String st=null;
        while ((st=is.readLine())!=null) {
            if (st.startsWith("[bank/"))
                break;
            
            if (st.startsWith("id=")) {
                String value=st.substring(st.indexOf('"')+1,
                                          st.lastIndexOf('"'));
                HBCIUtils.log("*** found customerid="+value,HBCIUtils.LOG_DEBUG);

                if (currentData.getProperty("userid").equals(getUserId()) &&
                    currentData.getProperty("blz").equals(getBLZ()) &&
                    currentData.getProperty("country").equals(getCountry()) &&
                    getStoredCustomerId()==null) {
                    HBCIUtils.log("setting passport customerid to "+value,HBCIUtils.LOG_DEBUG);
                    setCustomerId(value);
                }
            }
        }
        
        return st;
    }
    
    private String readAccount(String accheader,Properties currentData,BufferedReader is)
        throws Exception
    {
        String st;
        while ((st=is.readLine())!=null) {
            if (st.startsWith("[bank/"))
                break;
        }
        
        return st;
    }
}
