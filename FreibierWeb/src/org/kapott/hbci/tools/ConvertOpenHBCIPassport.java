
/*  $Id: ConvertOpenHBCIPassport.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportRDH;
import org.kapott.hbci.passport.HBCIPassportRDHNew;

/** <p>Tool zum Konvertieren von OpenHBCI-RDH-Medien in
    RDHNew-Passport-Dateien. Das Konvertieren von OpenHBCI-Schlüsseldateien in
    das <em>HBCI4Java</em>-interne Dateiformat ist nicht zwingend notwendig, weil 
    <em>HCBI4Java</em> auch direkt mit OpenHBCI-Schlüsseldateien arbeiten kann 
    (Passport-Variante {@link org.kapott.hbci.passport.HBCIPassportOpenHBCI})</p>
    <p>Die Konvertierung von OpenHBCI-RDH-Medien in RDHNew-Passports kann auch
    mit dem separat verfügbaren <em>HBCI4Java Passport Editor</em> durchgeführt werden.
    Mit diesem Editor können darüber hinaus RDHNew-Passports auch wieder
    in OpenHBCI-Medien konvertiert werden.</p> 
    <p>Aufgerufen wird der Konverter mit
    <pre>java -cp ... org.kapott.hbci.tools.ConvertOpenHBCIPassport</pre>
    Es handelt sich um ein interaktives Programm. Nach dem Start wird nach dem
    Dateinamen einer existierenden OpenHBCI-Schlüsseldatei sowie nach dem Passwort
    für deren Entschlüsselung gefragt. Anschließend wird nach einem
    <em>neuen(!)</em> Dateinamen für die zu erstellende RDHNew-Passport-Datei
    sowie nach einem Passwort für deren Verschlüsselung gefragt. Nach Beendigung
    des Programmes existiert die RDHNew-Passport-Datei, welche ab sofort benutzt
    werden kann.</p>*/
public class ConvertOpenHBCIPassport
{
    public static void main(String[] args) 
        throws IOException
    {
        HBCIUtils.init(null,null,new HBCICallbackConsole());

        String nameOld=readParam(args,0,"Filename of existing OpenHBCI media file");
        String dataName=readParam(args,1,"Filename of OpenHBCI data file (usually .openhbci in homedir)");

        HBCIUtils.setParam("client.passport.OpenHBCI.mediafile",nameOld);
        HBCIUtils.setParam("client.passport.OpenHBCI.datafile",dataName);
        HBCIUtils.setParam("client.passport.OpenHBCI.init","1");
        HBCIPassportInternal passportOld=(HBCIPassportInternal)AbstractHBCIPassport.getInstance("OpenHBCI");

        String nameNew=readParam(args,2,"Filename of new RDHNew passport file");

        HBCIUtils.setParam("client.passport.RDHNew.filename",nameNew);
        HBCIUtils.setParam("client.passport.RDHNew.init","0");
        HBCIPassportInternal passportNew=(HBCIPassportInternal)AbstractHBCIPassport.getInstance("RDHNew");

        passportNew.setCountry(passportOld.getCountry());
        passportNew.setBLZ(passportOld.getBLZ());
        passportNew.setHost(passportOld.getHost());
        passportNew.setPort(passportOld.getPort());
        passportNew.setUserId(passportOld.getUserId());
        passportNew.setCustomerId(passportOld.getCustomerId());
        passportNew.setSysId(passportOld.getSysId());
        passportNew.setSigId(passportOld.getSigId());
        passportNew.setHBCIVersion(passportOld.getHBCIVersion());
        passportNew.setBPD(passportOld.getBPD());
        passportNew.setUPD(passportOld.getUPD());

        ((HBCIPassportRDHNew)passportNew).setInstSigKey(((HBCIPassportRDH)passportOld).getInstSigKey());
        ((HBCIPassportRDHNew)passportNew).setInstEncKey(((HBCIPassportRDH)passportOld).getInstEncKey());
        ((HBCIPassportRDHNew)passportNew).setMyPublicSigKey(((HBCIPassportRDH)passportOld).getMyPublicSigKey());
        ((HBCIPassportRDHNew)passportNew).setMyPrivateSigKey(((HBCIPassportRDH)passportOld).getMyPrivateSigKey());
        ((HBCIPassportRDHNew)passportNew).setMyPublicEncKey(((HBCIPassportRDH)passportOld).getMyPublicEncKey());
        ((HBCIPassportRDHNew)passportNew).setMyPrivateEncKey(((HBCIPassportRDH)passportOld).getMyPrivateEncKey());

        passportNew.saveChanges();

        passportOld.close();
        passportNew.close();            
    }

    private static String readParam(String[] args,int idx,String st)
        throws IOException
    {
        String ret;
        
        System.out.print(st+": ");
        System.out.flush();
        
        if (args.length<=idx) {
            ret=new BufferedReader(new InputStreamReader(System.in)).readLine();
        } else {
            System.out.println(args[idx]);
            ret=args[idx];
        }
        
        return ret;
    }
}
