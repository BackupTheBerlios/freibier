
/*  $Id: HBCIPassportRDHNew.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $

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
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.security.RSAPrivateCrtKey2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** <p>Passport-Klasse f�r RDH-Zug�nge mit Sicherheitsmedium "Datei". Bei dieser Variante
    werden sowohl die HBCI-Zugangsdaten wie auch die kryptografischen Schl�ssel f�r
    die Signierung/Verschl�sselung der HBCI-Nachrichten in einer Datei gespeichert.
    Der Dateiname kann dabei beliebig vorgegeben werden. Da diese Datei vertrauliche
    Informationen enth�lt, wird der Inhalt verschl�sselt abgespeichert.
    Vor dem Erzeugen bzw. Einlesen wird via Callback-Mechanismus nach einem Passwort
    gefragt, aus dem der Schl�ssel zur Verschl�sselung/Entschl�sselung der Schl�sseldatei
    berechnet wird.</p><p>
    Wie auch bei {@link org.kapott.hbci.passport.HBCIPassportDDV} werden in
    der Schl�sseldatei zus�tzliche Informationen gespeichert. Dazu geh�ren u.a. die BPD
    und die UPD sowie die HBCI-Version, die zuletzt mit diesem Passport benutzt wurde.
    Im Gegensatz zu den "Hilfsdateien" bei DDV-Passports darf die Schl�sseldatei bei
    RDH-Passports aber niemals manuell gel�scht werden, da dabei auch die kryptografischen
    Schl�ssel des Kunden verlorengehen. Diese k�nnen nicht wieder hergestellt werden, so
    dass in einem solchen Fall ein manuelles Zur�cksetzes des HBCI-Zuganges bei der Bank
    erfolgen muss!</p>
    <p>Die Schl�sseldateien, die <em>HBCI4Java</em> mit dieser Klasse erzeugt und verwaltet, sind
    <b>nicht kompatibel</b> zu den Schl�sseldateien anderer HBCI-Software (z.B. VR-NetWorld
    o.�.). Es ist also nicht m�glich, durch Auswahl des Sicherheitsverfahrens "RDH" oder "RDHNew" und
    Angabe einer schon existierenden Schl�sseldatei, die mit einer anderen HBCI-Software
    erstellt wurde, diese Schl�sseldatei unter <em>HBCI4Java</em> zu benutzen! Es ist jedoch im
    Prinzip m�glich, mit der "anderen" Software die Kundenschl�ssel sperren zu lassen und
    anschlie�end mit <em>HBCI4Java</em> eine v�llig neue Schl�sseldatei zu erzeugen. Das hat aber zwei
    Nachteile: Zum einen muss nach dem Neuerzeugen der Schl�sseldatei auch ein neuer
    INI-Brief erzeugt und an die Bank gesandt werden, um die neuen Schl�ssel freischalten
    zu lassen. Au�erdem l�sst sich nat�rlich die <em>HBCI4Java</em>-Schl�sseldatei nicht mehr
    in der "anderen" HBCI-Software benutzen. Ein Parallel-Betrieb verschiedener HBCI-Softwarel�sungen,
    die alle auf dem RDH-Verfahren mit Sicherheitsmedium "Datei" (oder Diskette) basieren,
    ist meines Wissens nicht m�glich.</p>
    <p>Ein weiterer Ausweg aus diesem Problem w�re, eine technische Beschreibung des
    Formates der Schl�sseldateien der "anderen" HBCI-Software zu besorgen und diese
    dem <a href="mailto:hbci4java@kapott.org">Autor</a> zukommen zu lassen, damit eine Passport-Variante
    implementiert werden kann, die mit Schl�sseldateien dieser "anderen" Software arbeiten kann.</p>
    @see org.kapott.hbci.tools.INILetter INILetter */
public class HBCIPassportRDHNew 
    extends HBCIPassportRDH
{
    public HBCIPassportRDHNew(Object initObject)
    {
        this(initObject,0);

        String  header="client.passport.RDHNew.";
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
            
            try {
                DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                DocumentBuilder db=dbf.newDocumentBuilder();
                Element root=null;

                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));

                while (true) {          // loop for entering the correct passphrase
                    if (passportKey==null)
                        passportKey=calculatePassportKey(FOR_LOAD);

                    PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
                    Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
                    cipher.init(Cipher.DECRYPT_MODE,passportKey,paramspec);

                    root=null;
                    CipherInputStream ci=null;
                    
                    try {
                        ci=new CipherInputStream(new FileInputStream(getFileName()),cipher);
                        root=db.parse(ci).getDocumentElement();
                    } catch (SAXException e) {
                        passportKey=null;

                        retries--;
                        if (retries<=0)
                            throw new InvalidPassphraseException(e);
                    } finally {
                        if (ci!=null)
                            ci.close();
                    }

                    if (root!=null)
                        break;
                }
                
                setCountry(getElementValue(root,"country"));
                setBLZ(getElementValue(root,"blz"));
                setHost(getElementValue(root,"host"));
                setPort(new Integer(getElementValue(root,"port")));
                setUserId(getElementValue(root,"userid"));
                setCustomerId(getElementValue(root,"customerid"));
                setSysId(getElementValue(root,"sysid"));
                setSigId(new Long(getElementValue(root,"sigid")));
                setHBCIVersion(getElementValue(root,"hbciversion"));
                
                setBPD(getElementProps(root,"bpd"));
                setUPD(getElementProps(root,"upd"));
                
                setInstSigKey(getElementKey(root,"inst","S","public"));
                setInstEncKey(getElementKey(root,"inst","V","public"));
                setMyPublicSigKey(getElementKey(root,"user","S","public"));
                setMyPrivateSigKey(getElementKey(root,"user","S","private"));
                setMyPublicEncKey(getElementKey(root,"user","V","public"));
                setMyPrivateEncKey(getElementKey(root,"user","V","private"));
                
                if (askForMissingData(true,true,true,true,false,true,true))
                    saveChanges();
            } catch (Exception e) {
                throw new HBCI_Exception("*** error while reading passport file",e);
            }
        }
    }
    
    private String getElementValue(Element root,String name)
    {
        String ret=null;
        
        NodeList list=root.getElementsByTagName(name);
        if (list!=null && list.getLength()!=0) {
            Node content=list.item(0).getFirstChild();
            if (content!=null)
                ret=content.getNodeValue();
        }
            
        return ret;
    }
    
    private Properties getElementProps(Element root,String name)
    {
        Properties ret=null;
        
        Node base=root.getElementsByTagName(name).item(0);
        if (base!=null) {
            ret=new Properties();
            NodeList entries=base.getChildNodes();
            int len=entries.getLength();

            for (int i=0;i<len;i++) {
                Node n=entries.item(i);
                if (n.getNodeType()==Node.ELEMENT_NODE) {
                    ret.setProperty(((Element)n).getAttribute("name"),
                                    ((Element)n).getAttribute("value"));
                }
            }
        }
        
        return ret;
    }
    
    private HBCIKey getElementKey(Element root,String owner,String type,String part)
        throws Exception
    {
        HBCIKey ret=null;
        
        NodeList keys=root.getElementsByTagName("key");
        int len=keys.getLength();
        
        for (int i=0;i<len;i++) {
            Node n=keys.item(i);
            if (n.getNodeType()==Node.ELEMENT_NODE) {
                Element keynode=(Element)n;
                if (keynode.getAttribute("owner").equals(owner) &&
                    keynode.getAttribute("type").equals(type) &&
                    keynode.getAttribute("part").equals(part)) {
                
                    Key key;
                    
                    if (part.equals("public")) {
                        RSAPublicKeySpec spec=new RSAPublicKeySpec(new BigInteger(getElementValue(keynode,"modulus")),
                                                                   new BigInteger(getElementValue(keynode,"exponent")));
                        key=KeyFactory.getInstance("RSA").generatePublic(spec);
                    } else {
                        String modulus=getElementValue(keynode,"modulus");
                        String privexponent=getElementValue(keynode,"exponent");
                        String pubexponent=getElementValue(keynode,"pubexponent");
                        String p=getElementValue(keynode,"p");
                        String q=getElementValue(keynode,"q");
                        String dP=getElementValue(keynode,"dP");
                        String dQ=getElementValue(keynode,"dQ");
                        String qInv=getElementValue(keynode,"qInv");
                        
                        if (privexponent==null) {
                            // only CRT
                            HBCIUtils.log("*** private "+type+" key is CRT-only",HBCIUtils.LOG_DEBUG);
                            key=new RSAPrivateCrtKey2(new BigInteger(p),
                                                      new BigInteger(q),
                                                      new BigInteger(dP),
                                                      new BigInteger(dQ),
                                                      new BigInteger(qInv));
                        } else if (p==null) {
                            // only exponent
                            HBCIUtils.log("*** private "+type+" key is exponent-only",HBCIUtils.LOG_DEBUG);
                            RSAPrivateKeySpec spec=new RSAPrivateKeySpec(new BigInteger(modulus),
                                                                         new BigInteger(privexponent));
                            key=KeyFactory.getInstance("RSA").generatePrivate(spec);
                        } else {
                            // complete data
                            HBCIUtils.log("*** private "+type+" key is fully specified",HBCIUtils.LOG_DEBUG);
                            RSAPrivateCrtKeySpec spec=new RSAPrivateCrtKeySpec(new BigInteger(modulus),
                                                                               new BigInteger(pubexponent),
                                                                               new BigInteger(privexponent),
                                                                               new BigInteger(p),
                                                                               new BigInteger(q),
                                                                               new BigInteger(dP),
                                                                               new BigInteger(dQ),
                                                                               new BigInteger(qInv));
                            key=KeyFactory.getInstance("RSA").generatePrivate(spec);
                        }
                    }
                    
                    ret=new HBCIKey(getElementValue(keynode,"country"),
                                    getElementValue(keynode,"blz"),
                                    getElementValue(keynode,"userid"),
                                    getElementValue(keynode,"keynum"),
                                    getElementValue(keynode,"keyversion"),
                                    key);
                    
                    break;
                }
            }
        }
        
        return ret;
    }

    public HBCIPassportRDHNew(Object init,int dummy)
    {
        super(init,dummy);
    }
    
    public void saveChanges()
    {
        try {
            if (passportKey==null)
                passportKey=calculatePassportKey(FOR_SAVE);

            PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
            Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE,passportKey,paramspec);

            DocumentBuilderFactory fac=DocumentBuilderFactory.newInstance();
            fac.setValidating(false);
            DocumentBuilder db=fac.newDocumentBuilder();
            
            Document doc=db.newDocument();
            Element root=doc.createElement("HBCIPassportRDHNew");
            
            createElement(doc,root,"country",getCountry());
            createElement(doc,root,"blz",getBLZ());
            createElement(doc,root,"host",getHost());
            createElement(doc,root,"port",getPort().toString());
            createElement(doc,root,"userid",getUserId());
            createElement(doc,root,"customerid",getCustomerId());
            createElement(doc,root,"sysid",getSysId());
            createElement(doc,root,"sigid",getSigId().toString());
            createElement(doc,root,"hbciversion",getHBCIVersion());
            
            createPropsElement(doc,root,"bpd",getBPD());
            createPropsElement(doc,root,"upd",getUPD());
            
            createKeyElement(doc,root,"inst","S","public",getInstSigKey());
            createKeyElement(doc,root,"inst","V","public",getInstEncKey());
            createKeyElement(doc,root,"user","S","public",getMyPublicSigKey());
            createKeyElement(doc,root,"user","S","private",getMyPrivateSigKey());
            createKeyElement(doc,root,"user","V","public",getMyPublicEncKey());
            createKeyElement(doc,root,"user","V","private",getMyPrivateEncKey());
            
            TransformerFactory tfac=TransformerFactory.newInstance();
            Transformer tform=tfac.newTransformer();
            
            tform.setOutputProperty(OutputKeys.METHOD,"xml");
            tform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            tform.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
            tform.setOutputProperty(OutputKeys.INDENT,"yes");
            
            File passportfile=new File(getFileName());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);
            
            CipherOutputStream co=new CipherOutputStream(new FileOutputStream(tempfile),cipher);
            tform.transform(new DOMSource(root),new StreamResult(co));
            
            co.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception("*** saving of passport file failed",e);
        }
    }

    private void createElement(Document doc,Element root,String elemName,String elemValue)
    {
        Node elem=doc.createElement(elemName);
        root.appendChild(elem);
        Node data=doc.createTextNode(elemValue);
        elem.appendChild(data);
    }
    
    private void createPropsElement(Document doc,Element root,String elemName,Properties p)
    {
        if (p!=null) {
            Node base=doc.createElement(elemName);
            root.appendChild(base);
            
            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                String value=p.getProperty(key);
                
                Element data=doc.createElement("entry");
                data.setAttribute("name",key);
                data.setAttribute("value",value);
                base.appendChild(data);
            }
        }
    }
    
    private void createKeyElement(Document doc,Element root,String owner,String type,String part,HBCIKey key)
    {
        if (key!=null) {
            Element base=doc.createElement("key");
            base.setAttribute("owner",owner);
            base.setAttribute("type",type);
            base.setAttribute("part",part);
            root.appendChild(base);
            
            createElement(doc,base,"country",key.country);
            createElement(doc,base,"blz",key.blz);
            createElement(doc,base,"userid",key.userid);
            createElement(doc,base,"keynum",key.num);
            createElement(doc,base,"keyversion",key.version);
            
            Element keydata=doc.createElement("keydata");
            base.appendChild(keydata);

            byte[] e=key.key.getEncoded();
            String encoded=(e!=null)?HBCIUtils.encodeBase64(e):null;
            String format=key.key.getFormat();

            if (encoded!=null) {
                Element data=doc.createElement("rawdata");
                data.setAttribute("format",format);
                data.setAttribute("encoding","base64");
                keydata.appendChild(data);
                Node content=doc.createTextNode(encoded);
                data.appendChild(content);
            }
            
            if (part.equals("public")) {
                createElement(doc,keydata,"modulus",((RSAPublicKey)key.key).getModulus().toString());
                createElement(doc,keydata,"exponent",((RSAPublicKey)key.key).getPublicExponent().toString());
            } else {
                if (key.key instanceof RSAPrivateCrtKey) {
                    HBCIUtils.log("*** saving "+type+" key as fully specified",HBCIUtils.LOG_DEBUG);
                    createElement(doc,keydata,"modulus",((RSAPrivateCrtKey)key.key).getModulus().toString());
                    createElement(doc,keydata,"exponent",((RSAPrivateCrtKey)key.key).getPrivateExponent().toString());
                    createElement(doc,keydata,"pubexponent",((RSAPrivateCrtKey)key.key).getPublicExponent().toString());
                    createElement(doc,keydata,"p",((RSAPrivateCrtKey)key.key).getPrimeP().toString());
                    createElement(doc,keydata,"q",((RSAPrivateCrtKey)key.key).getPrimeQ().toString());
                    createElement(doc,keydata,"dP",((RSAPrivateCrtKey)key.key).getPrimeExponentP().toString());
                    createElement(doc,keydata,"dQ",((RSAPrivateCrtKey)key.key).getPrimeExponentQ().toString());
                    createElement(doc,keydata,"qInv",((RSAPrivateCrtKey)key.key).getCrtCoefficient().toString());
                } else if (key.key instanceof RSAPrivateKey) {
                    HBCIUtils.log("*** saving "+type+" key as exponent-only",HBCIUtils.LOG_DEBUG);
                    createElement(doc,keydata,"modulus",((RSAPrivateKey)key.key).getModulus().toString());
                    createElement(doc,keydata,"exponent",((RSAPrivateKey)key.key).getPrivateExponent().toString());
                } else if (key.key instanceof RSAPrivateCrtKey2) {
                    HBCIUtils.log("*** saving "+type+" key as crt-only",HBCIUtils.LOG_DEBUG);
                    createElement(doc,keydata,"p",((RSAPrivateCrtKey2)key.key).getP().toString());
                    createElement(doc,keydata,"q",((RSAPrivateCrtKey2)key.key).getQ().toString());
                    createElement(doc,keydata,"dP",((RSAPrivateCrtKey2)key.key).getdP().toString());
                    createElement(doc,keydata,"dQ",((RSAPrivateCrtKey2)key.key).getdQ().toString());
                    createElement(doc,keydata,"qInv",((RSAPrivateCrtKey2)key.key).getQInv().toString());
                } else {
                    HBCIUtils.log("*** key has none of the known types - please contact the author!",HBCIUtils.LOG_WARN);
                }
            }
        }         
    }
    
    /** Gibt den Dateinamen der verwendeten Schl�sseldatei zur�ck.
        @return Dateiname der Schl�sseldatei */
    public String getFileName()
    {
        return super.getFileName();
    }
}