
/*  $Id: HBCIPassportPinTan.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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
import java.security.Security;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.security.HBCIProvider;

/** <p>Passport-Klasse für HBCI mit PIN/TAN. Dieses Sicherheitsverfahren wird erst
    in FinTS 3.0 spezifiziert, von einigen Banken aber schon mit früheren HBCI-Versionen
    angeboten.</p><p>
    Bei diesem Verfahren werden die Nachrichten auf HBCI-Ebene nicht mit kryptografischen
    Verfahren signiert oder verschlüsselt. Als "Signatur" werden statt dessen TANs 
    zusammen mit einer PIN verwendet. Die PIN wird dabei in <em>jeder</em> HBCI-Nachricht als
    Teil der "Signatur" eingefügt, doch nicht alle Nachrichten benötigen eine TAN.
    Eine TAN wird nur bei der Übermittlung bestimmter Geschäftsvorfälle benötigt. Welche
    GV das konkret sind, ermittelt <em>HBCI4Java</em> automatisch aus den BPD. Für jeden GV, der
    eine TAN benötigt, wird diese via Callback abgefragt und in die Nachricht eingefügt.</p><p>
    Die Verschlüsselung der Nachrichten bei der Übertragung erfolgt auf einer höheren
    Transportschicht. Die Nachrichten werden nämlich nicht direkt via TCP/IP übertragen,
    sondern in das HTTP-Protokoll eingebettet. Die Verschlüsselung der übertragenen Daten
    erfolgt dabei auf HTTP-Ebene (via SSL = HTTPS).</p><p>
    Wie auch bei {@link org.kapott.hbci.passport.HBCIPassportRDH} wird eine "Schlüsseldatei"
    verwendet. In dieser werden allerdings keine kryptografischen Schlüssel abgelegt, sondern
    lediglich die Zugangsdaten für den HBCI-Server (Hostadresse, Nutzerkennung, usw.) sowie
    einige zusätzliche Daten (BPD, UPD, zuletzt benutzte HBCI-Version). Diese Datei wird
    vor dem Abspeichern verschlüsselt. Vor dem Erzeugen bzw. erstmaligen Einlesen wird via
    Callback nach einem Passwort gefragt, aus welchem der Schlüssel für die Verschlüsselung
    der Datei berechnet wird</p>*/
public class HBCIPassportPinTan
    extends AbstractHBCIPassport
{
    private String    filename;
    private SecretKey passportKey;
    private String    pin;
    private String    certfile;
    private boolean   checkCert;

    private final static byte[] CIPHER_SALT={(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,
                                             (byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};
    private final static int CIPHER_ITERATIONS=987;

    public HBCIPassportPinTan(Object init,int dummy)
    {
        super(init);
        
        if (Security.getProvider("HBCIProvider") == null)
            Security.addProvider(new HBCIProvider());
    }

    public HBCIPassportPinTan(Object initObject)
    {
        this(initObject,0);

        String  header="client.passport.PinTan.";
        String  filename=HBCIUtils.getParam(header+"filename");
        boolean init=HBCIUtils.getParam(header+"init").equals("1");
        
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PASS_LOADFILE",filename),HBCIUtils.LOG_DEBUG);
        setFileName(filename);
        setPort(new Integer(443));
        setCertFile(HBCIUtils.getParam("client.passport.PinTan.certfile"));
        setCheckCert(HBCIUtils.getParam("client.passport.PinTan.checkcert","1").equals("1"));

        if (init) {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_PASS_LOADDATE",filename),HBCIUtils.LOG_DEBUG);
            
            if (!new File(filename).canRead()) {
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_PASS_NEWFILE"),HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,true,true,true);
                saveChanges();
            }

            ObjectInputStream o=null;
            try {
                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));
                
                while (true) {
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
                setBPD((Properties)(o.readObject()));
                setUPD((Properties)(o.readObject()));

                setHBCIVersion((String)o.readObject());
                setCustomerId((String)o.readObject());
                setFilterType((String)o.readObject());
            } catch (Exception e) {
                throw new HBCI_Exception("*** loading of passport file failed",e);
            }

            try {
                o.close();
            } catch (Exception e) {
                HBCIUtils.log(e);
            }
            
            if (askForMissingData(true,true,true,true,true,true,true))
                saveChanges();
        }
    }

    /** Gibt den Dateinamen der Schlüsseldatei zurück.
        @return Dateiname der Schlüsseldatei */
    public String getFileName() 
    {
        return filename;
    }

    public void setFileName(String filename) 
    { 
        this.filename=filename;
    }
    
    public Comm getCommInstance()
    {
        return Comm.getInstance("PinTan",this);
    }
    
    public boolean isSupported()
    {
        boolean ret=false;
        Properties bpd=getBPD();
        
        if (bpd!=null) {
            // loop through bpd and search for PinTanPar segment
            for (Enumeration e=bpd.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                
                if (key.startsWith("Params")) {
                    int posi=key.indexOf(".");
                    if (key.substring(posi+1).startsWith("PinTanPar")) {
                        ret=true;
                        break;
                    }
                }
            }
        } else {
            ret=true;
        }
        
        return ret;
    }

    public String getProfileMethod()
    {
        return "";
    }
    
    public String getProfileVersion()
    {
        return "";
    }

    public boolean needUserKeys()
    {
        return false;
    }
    
    public boolean needInstKeys()
    {
        return false;
    }
    
    public boolean needUserSig()
    {
        return true;
    }
    
    public String getSysStatus()
    {
        return "1";
    }

    public boolean hasInstSigKey()
    {
        return true;
    }
    
    public boolean hasInstEncKey()
    {
        return true;
    }
    
    public boolean hasMySigKey()
    {
        return true;
    }
    
    public boolean hasMyEncKey()
    {
        return true;
    }
    
    public HBCIKey getInstSigKey()
    {
        return null;
    }
    
    public HBCIKey getInstEncKey()
    {
        return null;
    }
    
    public HBCIKey getInstDigKey()
    {
        return null;
    }
    
    public String getInstSigKeyName()
    {
        return getUserId();
    }

    public String getInstSigKeyNum()
    {
        return "0";
    }

    public String getInstSigKeyVersion()
    {
        return "0";
    }

    public String getInstEncKeyName()
    {
        return getUserId();
    }

    public String getInstEncKeyNum()
    {
        return "0";
    }

    public String getInstEncKeyVersion()
    {
        return "0";
    }

    public String getMySigKeyName()
    {
        return getUserId();
    }

    public String getMySigKeyNum()
    {
        return "0";
    }

    public String getMySigKeyVersion()
    {
        return "0";
    }

    public String getMyEncKeyName()
    {
        return getUserId();
    }

    public String getMyEncKeyNum()
    {
        return "0";
    }

    public String getMyEncKeyVersion()
    {
        return "0";
    }
    
    public HBCIKey getMyPublicDigKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateDigKey()
    {
        return null;
    }

    public HBCIKey getMyPublicSigKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateSigKey()
    {
        return null;
    }

    public HBCIKey getMyPublicEncKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateEncKey()
    {
        return null;
    }

    public String getCryptMode()
    {
        return "2";
    }

    public String getCryptAlg()
    {
        return "13";
    }

    public String getSecMethod56()
    {
        return "5";
    }

    public String getSecMethod12()
    {
        return "999";
    }

    public String getSecMethodPlain()
    {
        return "998";
    }

    public String getSigAlg()
    {
        return "10";
    }

    public String getSigMode()
    {
        return "16";
    }

    public String getHashAlg()
    {
        return "999";
    }
    
    public void setInstDigKey(HBCIKey key)
    {
    }

    public void setInstSigKey(HBCIKey key)
    {
    }

    public void setInstEncKey(HBCIKey key)
    {
    }

    public void setMyPublicDigKey(HBCIKey key)
    {
    }

    public void setMyPrivateDigKey(HBCIKey key)
    {
    }

    public void setMyPublicSigKey(HBCIKey key)
    {
    }

    public void setMyPrivateSigKey(HBCIKey key)
    {
    }

    public void setMyPublicEncKey(HBCIKey key)
    {
    }

    public void setMyPrivateEncKey(HBCIKey key)
    {
    }
    
    public int getMaxGVperMsg()
    {
        return 1;
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
            o.writeObject(getBPD());
            o.writeObject(getUPD());

            o.writeObject(getHBCIVersion());
            o.writeObject(getCustomerId());
            o.writeObject(getFilterType());

            o.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception("*** saving of passport file failed",e);
        }
    }
    
    protected String getPinTanInfo(String code)
    {
        String     ret="";
        Properties bpd=getBPD();
        
        if (bpd!=null) {
            boolean isGV=false;
            StringBuffer paramCode=new StringBuffer(code).replace(1,2,"I").append("S");

            for (Enumeration e=bpd.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();

                if (key.startsWith("Params")&&
                    key.indexOf(".PinTanPar1.ParPinTan.PinTanGV")!=-1&&
                    key.endsWith(".segcode")) {

                    String code2=bpd.getProperty(key);
                    if (code.equals(code2)) {
                        key=key.substring(0,key.length()-("segcode").length())+"needtan";
                        ret=bpd.getProperty(key);
                        break;
                    }
                } else if (key.startsWith("Params")&&
                           key.endsWith(".SegHead.code")) {

                    String code2=bpd.getProperty(key);
                    if (paramCode.equals(code2)) {
                        isGV=true;
                    }
                }
            }

            // wenn das kein GV ist, dann ist es ein Admin-Segment
            if (ret.length()==0&&!isGV) {
                ret="A";
            }
        }
        
        return ret;
    }

    public byte[] sign(byte[] data)
    {
        try {
            if (pin==null) {
                StringBuffer s=new StringBuffer();

                HBCIUtilsInternal.getCallback().callback(this,
                                                 HBCICallback.NEED_PT_PIN,
                                                 HBCIUtilsInternal.getLocMsg("CALLB_NEED_PTPIN"),
                                                 HBCICallback.TYPE_SECRET,
                                                 s);
                if (s.length()==0) {
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINZERO"));
                }
                pin=s.toString();
            }

            String tan="";
            String codes=new String(data,"ISO-8859-1");
            StringTokenizer tok=new StringTokenizer(codes,"|");

            while (tok.hasMoreTokens()) {
                String code=tok.nextToken();
                String info=getPinTanInfo(code);
                
                if (info.equals("J")) {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PT_NEEDTAN",code),HBCIUtils.LOG_DEBUG);

                    if (tan.length()==0) {
                        StringBuffer s=new StringBuffer();
                        HBCIUtilsInternal.getCallback().callback(this,
                                                         HBCICallback.NEED_PT_TAN,
                                                         HBCIUtilsInternal.getLocMsg("CALLB_NEED_PTTAN"),
                                                         HBCICallback.TYPE_TEXT,
                                                         s);
                        if (s.length()==0) {
                            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_TANZERO"));
                        }
                        tan=s.toString();
                    } else {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_PT_TOOMUCHGV",code),HBCIUtils.LOG_WARN);
                    }
                } else if (info.equals("N")) {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_PT_NOTNEEDED",code),HBCIUtils.LOG_DEBUG);
                } else if (info.length()==0) {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_PT_CODENOTFOUND",code),HBCIUtils.LOG_WARN);
                }
            }

            return (pin+"|"+tan).getBytes("ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception("*** signing failed",ex);
        }
    }

    public boolean verify(byte[] data,byte[] sig)
    {
        return true;
    }

    public byte[][] encrypt(byte[] plainMsg)
    {
        try {
            int padLength=plainMsg[plainMsg.length-1];
            byte[] encrypted=new String(plainMsg,0,plainMsg.length-padLength,"ISO-8859-1").getBytes("ISO-8859-1");
            return new byte[][] {new byte[8],encrypted};
        } catch (Exception ex) {
            throw new HBCI_Exception("*** encrypting message failed",ex);
        }
    }

    public byte[] decrypt(byte[] cryptedKey,byte[] cryptedMsg)
    {
        try {
            return new String(new String(cryptedMsg,"ISO-8859-1")+'\001').getBytes("ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception("*** decrypting of message failed",ex);
        }
    }
    
    public void close()
    {
        super.close();
        passportKey=null;
    }
    
    private void setCertFile(String filename)
    {
        this.certfile=filename;
    }
    
    public String getCertFile()
    {
        return certfile;
    }
    
    protected void setCheckCert(boolean skip)
    {
        this.checkCert=skip;
    }
    
    public boolean getCheckCert()
    {
        return checkCert;
    }
}
