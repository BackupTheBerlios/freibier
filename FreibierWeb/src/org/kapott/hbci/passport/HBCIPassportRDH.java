
/*  $Id: HBCIPassportRDH.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.security.HBCIProvider;
import org.kapott.hbci.security.RSAPrivateCrtKey2;
import org.kapott.hbci.security.SignatureParamSpec;

/** <p><em><b>Veraltete</b></em> Passport-Klasse für RDH-Zugänge mit Sicherheitsmedium "Datei". 
    Diese Klasse sollte nicht mehr benutzt werden, sondern statt dessen die Klasse
    {@link org.kapott.hbci.passport.HBCIPassportRDHNew}.
    RDH-Passport-Datei können mit dem Tool 
    {@link org.kapott.hbci.tools.ConvertRDHPassport} oder
    mit Hilfe des separat verfügbaren <em>HBCI4Java Passport Editors</em> 
    in RDHNew-Passport-Dateien umgewandelt werden. Siehe dazu auch die Daten 
    <code>README.RDHNew</code></p>
    <p>Das API dieser Klasse ist identisch zu dem der Klasse
    {@link org.kapott.hbci.passport.HBCIPassportRDHNew}. Siehe
    Beschreibung dort.</p>.*/
public class HBCIPassportRDH
    extends AbstractHBCIPassport
{
    protected String      filename;
    protected SecretKey   passportKey;
    protected HBCIKey[][] keys;

    protected final static byte[] CIPHER_SALT={(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,
                                               (byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};
    protected final static int CIPHER_ITERATIONS=987;

    public HBCIPassportRDH(Object init,int dummy)
    {
        super(init);
        
        if (Security.getProvider("HBCIProvider") == null)
            Security.addProvider(new HBCIProvider());

        keys=new HBCIKey[3][];
        for (int i=0;i<3;i++) {
            keys[i]=new HBCIKey[2];
            for (int j=0;j<2;j++) {
                keys[i][j]=null;
            }
        }
    }

    public HBCIPassportRDH(Object initObject)
    {
        this(initObject,0);

        String  header="client.passport.RDH.";
        String  filename=HBCIUtils.getParam(header+"filename");
        boolean init=HBCIUtils.getParam(header+"init").equals("1");

        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PASS_LOADFILE",filename),HBCIUtils.LOG_DEBUG);
        setFileName(filename);

        if (init) {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_PASS_LOADDATE",filename),HBCIUtils.LOG_DEBUG);
            
            setFilterType("None");
            setPort(new Integer(3000));
            
            if (!new File(filename).canRead()) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_PASS_NEWFILE"),HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,false,true,true);
                saveChanges();
            }

            ObjectInputStream o=null;
            try {
                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));
                
                while (true) {          // loop for entering the correct passphrase
                    if (passportKey==null)
                        passportKey=calculatePassportKey(FOR_LOAD);

                    PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
                    Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
                    cipher.init(Cipher.DECRYPT_MODE,passportKey,paramspec);
                 
                    o=null;
                    try {
                        o=new ObjectInputStream(new CipherInputStream(new FileInputStream(filename),cipher));
                    } catch (StreamCorruptedException e) {
                        passportKey=null;
                        
                        retries--;
                        if (retries<=0)
                            throw new InvalidPassphraseException(e);
                    }
                    
                    if (o!=null)
                        break;
                }

                setCountry((String)(o.readObject()));
                setBLZ((String)(o.readObject()));
                setHost((String)(o.readObject()));
                setPort((Integer)(o.readObject()));
                setUserId((String)(o.readObject()));
                setSysId((String)(o.readObject()));
                setSigId((Long)(o.readObject()));
                setBPD((Properties)(o.readObject()));
                setUPD((Properties)(o.readObject()));

                for (int i=0;i<3;i++) {
                    for (int j=0;j<2;j++) {
                        setKey(i,j,(HBCIKey)(o.readObject()));
                    }
                }

                setCID((String)(o.readObject()));
                setHBCIVersion((String)o.readObject());
                setCustomerId((String)o.readObject());
                
                HBCIKey k=getMyPrivateSigKey();
                if (k!=null && k.key!=null && !(k.key instanceof RSAPrivateCrtKey)) {
                    HBCIUtils.log("*** private sig key is no CRT key, please contact the author!",HBCIUtils.LOG_WARN);
                }
                
                k=getMyPrivateEncKey();
                if (k!=null && k.key!=null && !(k.key instanceof RSAPrivateCrtKey)) {
                    HBCIUtils.log("*** private enc key is no CRT key, please contact the author!",HBCIUtils.LOG_WARN);
                }
            } catch (Exception e) {
                throw new HBCI_Exception("*** loading of passport file failed",e);
            }

            try {
                o.close();
            } catch (Exception e) {
                HBCIUtils.log(e);
            }
            
            if (askForMissingData(true,true,true,true,false,true,true))
                saveChanges();
        }
    }
    
    public String getFileName() 
    {
        return filename;
    }

    public final void setFileName(String filename) 
    { 
        this.filename=filename;
    }

    public boolean isSupported()
    {
        boolean ret=false;
        
        if (getBPD()!=null) {
            String[][] methods=getSuppSecMethods();
            
            for (int i=0;i<methods.length;i++) {
                if (methods[i][0].equals("RDH")) {
                    ret=true;
                    break;
                }
            }
        } else {
            ret=true;
        }
        
        return ret;
    }

    public Comm getCommInstance()
    {
        return Comm.getInstance("Standard",this);
    }
    
    public String getProfileMethod()
    {
        return "RDH";
    }
    
    public String getProfileVersion()
    {
        return "1";
    }

    public String getSysStatus()
    {
        return "1";
    }

    public boolean needInstKeys()
    {
        return true;
    }
    
    public boolean needUserKeys()
    {
        return true;
    }
    
    public boolean needUserSig()
    {
        return false;
    }
    
    public boolean hasInstSigKey()
    {
        return getInstSigKey()!=null;
    }
    
    public boolean hasInstEncKey()
    {
        return getInstEncKey()!=null;
    }
    
    public boolean hasMySigKey()
    {
        return getMyPublicSigKey()!=null;
    }
    
    public boolean hasMyEncKey()
    {
        return getMyPublicEncKey()!=null;
    }
    
    public HBCIKey getKey(int i,int j)
    {
        return keys[i][j];
    }
    
    public void setInstSigKey(HBCIKey key)
    {
        setKey(0,0,key);
    }

    public void setInstEncKey(HBCIKey key)
    {
        setKey(0,1,key);
    }

    public void setInstDigKey(HBCIKey key)
    {
        // *** not yet implemented
    }

    public void setMySigKey(HBCIKey key)
    {
        setKey(1,0,key);
        setKey(1,1,key);
    }

    public void setMyEncKey(HBCIKey key)
    {
        setKey(2,0,key);
        setKey(2,1,key);
    }

    public void setMyDigKey(HBCIKey key)
    {
        // ***
    }
    
    public void setMyPublicSigKey(HBCIKey key)
    {
        setKey(1,0,key);
    }

    public void setMyPrivateSigKey(HBCIKey key)
    {
        setKey(1,1,key);
    }

    public void setMyPublicEncKey(HBCIKey key)
    {
        setKey(2,0,key);
    }

    public void setMyPrivateEncKey(HBCIKey key)
    {
        setKey(2,1,key);
    }

    public void setMyPublicDigKey(HBCIKey key)
    {
        // ***
    }

    public void setMyPrivateDigKey(HBCIKey key)
    {
        // ***
    }

    public HBCIKey getMyPublicSigKey()
    {
        return getKey(1,0);
    }

    public HBCIKey getMyPrivateSigKey()
    {
        return getKey(1,1);
    }

    public HBCIKey getMyPublicEncKey()
    {
        return getKey(2,0);
    }

    public HBCIKey getMyPrivateEncKey()
    {
        return getKey(2,1);
    }

    public HBCIKey getMyPublicDigKey()
    {
        // ***
        return null;
    }
    
    public HBCIKey getMyPrivateDigKey()
    {
        // ***
        return null;
    }
    
    public HBCIKey getInstSigKey()
    {
        return getKey(0,0);
    }

    public String getInstSigKeyName()
    {
        return getInstSigKey()!=null?getInstSigKey().userid:null;
    }

    public String getInstSigKeyNum()
    {
        return getInstSigKey()!=null?getInstSigKey().num:null;
    }

    public String getInstSigKeyVersion()
    {
        return getInstSigKey()!=null?getInstSigKey().version:null;
    }
    
    public HBCIKey getInstEncKey()
    {
        return getKey(0,1);
    }

    public String getInstEncKeyName()
    {
        return getInstEncKey()!=null?getInstEncKey().userid:null;
    }

    public String getInstEncKeyNum()
    {
        return getInstEncKey()!=null?getInstEncKey().num:null;
    }

    public String getInstEncKeyVersion()
    {
        return getInstEncKey()!=null?getInstEncKey().version:null;
    }

    public String getMySigKeyName()
    {
        return getMyPublicSigKey()!=null?getMyPublicSigKey().userid:null;
    }

    public String getMySigKeyNum()
    {
        return getMyPublicSigKey()!=null?getMyPublicSigKey().num:null;
    }

    public String getMySigKeyVersion()
    {
        return getMyPublicSigKey()!=null?getMyPublicSigKey().version:null;
    }

    public String getMyEncKeyName()
    {
        return getMyPublicEncKey()!=null?getMyPublicEncKey().userid:null;
    }

    public String getMyEncKeyNum()
    {
        return getMyPublicEncKey()!=null?getMyPublicEncKey().num:null;
    }

    public String getMyEncKeyVersion()
    {
        return getMyPublicEncKey()!=null?getMyPublicEncKey().version:null;
    }

    public String getSecMethod56()
    {
        return "6";
    }

    public String getSecMethod12()
    {
        return "1";
    }

    public String getSigAlg()
    {
        return "10";
    }

    public String getSigMode()
    {
        return "16";
    }

    public String getSecMethodPlain()
    {
        return "4";
    }

    public String getCryptAlg()
    {
        return "13";
    }

    public String getCryptMode()
    {
        return "2";
    }

    public String getHashAlg()
    {
        return "999";
    }

    public final void setKey(int i,int j,HBCIKey key)
    {
        // System.out.println("passportRDH: setting key "+i+","+j+" to "+(key==null?"null":key.country+":"+key.blz+":"+key.cid+":"+key.num+":"+key.version));
        keys[i][j]=key;
    }

    public void resetPassphrase()
    {
        passportKey=null;
    }

    public void saveChanges()
    {
        try {
            if (passportKey==null)
                passportKey=calculatePassportKey(FOR_SAVE);
            
            PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
            Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE,passportKey,paramspec);

            File passportfile=new File(getFileName());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);

            ObjectOutputStream o=new ObjectOutputStream(new CipherOutputStream(new FileOutputStream(tempfile),cipher));

            o.writeObject(getCountry());
            o.writeObject(getBLZ());
            o.writeObject(getHost());
            o.writeObject(getPort());
            o.writeObject(getUserId());
            o.writeObject(getSysId());
            o.writeObject(getSigId());
            o.writeObject(getBPD());
            o.writeObject(getUPD());

            for (int i=0;i<3;i++) {
                for (int j=0;j<2;j++) {
                    HBCIKey key=getKey(i,j);

                    if (key!=null) {
                        o.writeObject(new HBCIKey(key.country,key.blz,key.userid,key.num,key.version,key.key));
                    } 
                    else o.writeObject(null);
                }
            }

            o.writeObject(getCID());
            o.writeObject(getHBCIVersion());
            o.writeObject(getCustomerId());

            o.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);

            HBCIKey k=getMyPrivateSigKey();
            if (k!=null && k.key!=null && !(k.key instanceof RSAPrivateCrtKey)) {
                HBCIUtils.log("*** private sig key is no CRT key, please contact the author!",HBCIUtils.LOG_WARN);
            }

            k=getMyPrivateEncKey();
            if (k!=null && k.key!=null && !(k.key instanceof RSAPrivateCrtKey)) {
                HBCIUtils.log("*** private enc key is no CRT key, please contact the author!",HBCIUtils.LOG_WARN);
            }
        } catch (Exception e) {
            throw new HBCI_Exception("*** saving of passport file failed",e);
        }
    }

    public byte[] sign(byte[] data)
    {
        try {
            Signature sig=Signature.getInstance("ISO9796","HBCIProvider");
            sig.setParameter(new SignatureParamSpec("RIPEMD160","HBCIProvider"));
            sig.initSign((PrivateKey)(getMyPrivateSigKey().key));
            sig.update(data);
            byte[] result=sig.sign();
            result=checkFor96Bytes(result);
            return result;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** signing of message failed",ex);
        }
    }

    public boolean verify(byte[] data,byte[] sig)
    {
        try {
            Signature sign=Signature.getInstance("ISO9796","HBCIProvider");
            sign.setParameter(new SignatureParamSpec("RIPEMD160","HBCIProvider"));
            sign.initVerify((PublicKey)(getInstSigKey().key));
            sign.update(data);
            return sign.verify(sig);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** verification of message signature failed",ex); 
        }
    }

    private SecretKey createMsgKey()
    {
        try {
            KeyGenerator generator=KeyGenerator.getInstance("DESede");
            SecretKey key=generator.generateKey();
            SecretKeyFactory factory=SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec spec=(DESedeKeySpec)(factory.getKeySpec(key,DESedeKeySpec.class));
            byte[] bytes=spec.getKey();

            System.arraycopy(bytes,0,bytes,16,8);

            spec=new DESedeKeySpec(bytes);
            key=factory.generateSecret(spec);

            return key;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not create message key",ex);
        }
    }

    private byte[] encryptMessage(byte[] plainMsg,SecretKey msgkey)
    {
        try {
            Cipher cipher=Cipher.getInstance("DESede/CBC/NoPadding");
            byte[] iv=new byte[8];
            Arrays.fill(iv,(byte)(0));
            IvParameterSpec spec=new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE,msgkey,spec);

            return cipher.doFinal(plainMsg);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not encrypt message",ex);
        }
    }
    
    private byte[] checkFor96Bytes(byte[] buffer)
    {
        byte[] result=buffer;
        
        if (buffer.length!=96) {
            HBCIUtils.log("*** checking for encrypted msg_key_length==96; current length is "+buffer.length,HBCIUtils.LOG_WARN);
            if (buffer.length>96) {
                int diff=buffer.length-96;
                boolean ok=true;

                for (int i=0;i<diff;i++) {
                    if (buffer[i]!=0x00) {
                        HBCIUtils.log("*** byte "+i+" if enc_msg_key is not zero, but it should be zero - please contact the author",HBCIUtils.LOG_WARN);
                        ok=false;
                    }
                }

                if (ok) {
                    HBCIUtils.log("*** removing "+diff+" unnecessary null-bytes from enc_msg_key",HBCIUtils.LOG_WARN);
                    result=new byte[96];
                    System.arraycopy(buffer,diff,result,0,96);
                }
            } else if (buffer.length<96) {
                int diff=96-buffer.length;
                HBCIUtils.log("*** prepending "+diff+" null bytes to enc_msg_key",HBCIUtils.LOG_WARN);
                result=new byte[96];
                Arrays.fill(result,(byte)0);
                System.arraycopy(buffer,0,result,diff,buffer.length);
            }
        }
        
        return result;
    }

    private byte[] encryptKey(SecretKey msgkey)
    {
        try {
            // schluessel als byte-array abspeichern

            SecretKeyFactory factory=SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec spec=(DESedeKeySpec)(factory.getKeySpec(msgkey,DESedeKeySpec.class));
            byte[] plainKey=spec.getKey();

            byte[] plainText=new byte[96];
            Arrays.fill(plainText,(byte)(0));
            System.arraycopy(plainKey,0,plainText,plainText.length-16,16);
            BigInteger m=new BigInteger(+1,plainText);

            Key k=getInstEncKey().key;
            BigInteger c=m.modPow(((RSAPublicKey)(k)).getPublicExponent(),
                                  ((RSAPublicKey)(k)).getModulus());
 
            byte[] result=c.toByteArray();
            result=checkFor96Bytes(result);
            return result;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not encrypt message key",ex);
        }
    }

    public byte[][] encrypt(byte[] plainMsg)
    {
        try {
            SecretKey msgkey=createMsgKey();
            byte[] cryptMsg=encryptMessage(plainMsg,msgkey);
            byte[] cryptKey=encryptKey(msgkey);

            byte[][] ret=new byte[2][];
            ret[0]=cryptKey;
            ret[1]=cryptMsg;

            return ret;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while encrypting",ex);
        }
    }

    public byte[] decrypt(byte[] cryptedKey,byte[] cryptedMsg)
    {
        try {
            // key entschluesseln
            Key k=getMyPrivateEncKey().key;
            
            byte[] plainKey;
            if (k instanceof RSAPrivateKey) {
                HBCIUtils.log("*** decrypting message key with (n,d)-algorithm",HBCIUtils.LOG_DEBUG);
                BigInteger exponent=((RSAPrivateKey)(k)).getPrivateExponent();
                BigInteger modulus=((RSAPrivateKey)(k)).getModulus();

                BigInteger c=new BigInteger(+1,cryptedKey);
                plainKey=c.modPow(exponent,modulus).toByteArray();
            } else {
                HBCIUtils.log("*** decrypting message key with (p,q,dP,dQ,qInv)-algorithm",HBCIUtils.LOG_DEBUG);
                BigInteger p=((RSAPrivateCrtKey2)k).getP();
                BigInteger q=((RSAPrivateCrtKey2)k).getQ();
                BigInteger dP=((RSAPrivateCrtKey2)k).getdP();
                BigInteger dQ=((RSAPrivateCrtKey2)k).getdQ();
                BigInteger qInv=((RSAPrivateCrtKey2)k).getQInv();
        
                BigInteger c=new BigInteger(+1,cryptedKey);
                BigInteger m1=c.modPow(dP,p);
                BigInteger m2=c.modPow(dQ,q);
                BigInteger h=m1.subtract(m2).multiply(qInv).mod(p); 
                plainKey=m2.add(q.multiply(h)).toByteArray();
            }

            byte[] realPlainKey=new byte[24];
            System.arraycopy(plainKey,plainKey.length-16,realPlainKey,0,16);
            System.arraycopy(plainKey,plainKey.length-16,realPlainKey,16,8);

            DESedeKeySpec spec=new DESedeKeySpec(realPlainKey);
            SecretKeyFactory fac=SecretKeyFactory.getInstance("DESede");
            SecretKey key=fac.generateSecret(spec);

            // nachricht entschluesseln
            Cipher cipher=Cipher.getInstance("DESede/CBC/NoPadding");
            byte[] ivarray=new byte[8];
            Arrays.fill(ivarray,(byte)(0));
            IvParameterSpec iv=new IvParameterSpec(ivarray);
            cipher.init(Cipher.DECRYPT_MODE,key,iv);
            return cipher.doFinal(cryptedMsg);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while decrypting message",ex);
        }
    }
    
    public void close()
    {
        super.close();
        passportKey=null;
    }
}
