
/*  $Id: HBCIStatus.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/** <p>Menge zusammengeh�riger Status-Informationen. In Objekten dieser
    Klasse kann eine Menge von HBCI-Statuscodes sowie eine Menge von
    Exceptions gespeichert werden. Der Sinn dieser Klasse ist die
    Zusammenfassung von mehreren Status-Informationen, die logisch
    zusammengeh�ren (z.B. alle Status-Informationen, die ein bestimmtes
    Nachrichtensegment betreffen).
    </p><p>
    Objekte dieser Klasse werden beispielsweise in 
    {@link org.kapott.hbci.status.HBCIMsgStatus} verwendet,
    um globale und segmentbezogene Status-Informationen voneinander getrennt
    zu sammeln. </p>*/
public final class HBCIStatus
{
    /** Statuscode f�r "alle Statusinformationen besagen OK" */
    public static final int STATUS_OK=0;
    /** Statuscode f�r "Gesamtstatus kann nicht ermittelt werden". (z.B. weil
        gar keine Informationen in diesem Objekt enthalten sind) */
    public static final int STATUS_UNKNOWN=1;
    /** Statuscode f�r "es ist mindestens ein Fehlercode enthalten" */
    public static final int STATUS_ERR=2;
    
    private HBCIRetVal[] retVals;
    private Exception[]  exceptions;
    
    public HBCIStatus()
    {
        retVals=new HBCIRetVal[0];
        exceptions=new Exception[0];
    }
    
    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void addException(Exception e)
    {
        ArrayList list=new ArrayList(Arrays.asList(exceptions));
        list.add(e);
        exceptions=(Exception[])list.toArray(exceptions);
        
        HBCIUtils.log(e);
    }

    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void addRetVal(HBCIRetVal ret)
    {
        ArrayList list=new ArrayList(Arrays.asList(retVals));
        list.add(ret);
        retVals=(HBCIRetVal[])list.toArray(retVals);
    }

    /** Gibt zur�ck, ob in diesem Status-Objekt Exceptions gespeichert sind
        @return <code>true</code>, falls Exceptions gespeichert sind,
                sonst <code>false</code>*/
    public boolean hasExceptions()
    {
        return exceptions.length!=0;
    }
    
    private boolean hasX(char code)
    {
        boolean ret=false;
        
        for (int i=0;i<retVals.length;i++) {
            if (retVals[i].code.charAt(0)==code) {
                ret=true;
                break;
            }
        }
        
        return ret;
    }
    
    /** Gibt zur�ck, ob in den R�ckgabedaten in diesem Objekt Fehlermeldungen
        enthalten sind
        @return <code>true</code>, falls Fehlermeldungen vorhanden sind,
                sonst <code>false</code>*/
    public boolean hasErrors()
    {
        return hasX('9');
    }
    
    /** Gibt zur�ck, ob in den R�ckgabedaten in diesem Objekt Warnungen
        enthalten sind
        @return <code>true</code>, falls Warnungen vorhanden sind,
                sonst <code>false</code>*/
    public boolean hasWarnings()
    {
        return hasX('3');
    }
    
    /** Gibt zur�ck, ob in den R�ckgabedaten in diesem Objekt Erfolgsmeldungen
        enthalten sind
        @return <code>true</code>, falls Erfolgsmeldungen vorhanden sind,
                sonst <code>false</code>*/
    public boolean hasSuccess()
    {
        return hasX('0');
    }
    
    private HBCIRetVal[] getX(char code)
    {
        ArrayList ret_a=new ArrayList();
        
        for (int i=0;i<retVals.length;i++) {
            HBCIRetVal rv=retVals[i];
            
            if (rv.code.charAt(0)==code) {
                ret_a.add(rv);
            }
        }
        
        HBCIRetVal[] ret=new HBCIRetVal[0];
        if (ret_a.size()>0) {
            ret=(HBCIRetVal[])ret_a.toArray(ret);
        }
        
        return ret;
    }
    
    /** Gibt die in diesem Status-Objekt gespeicherten Exceptions zur�ck
        @return Array mit Exceptions, die w�hrend der HBCI-Kommunikation
        aufgetreten sind. */
    public Exception[] getExceptions()
    {
        return exceptions;
    }
    
    /** Gibt alle in diesem Status-Objekt gespeicherten R�ckgabewerte zur�ck
     @return Array mit <code>HBCIRetVal</code>s, die w�hrend der HBCI-Kommunikation
     aufgetreten sind. */
    public HBCIRetVal[] getRetVals()
    {
        return retVals;
    }
    
    /** Gibt die in diesem Objekt gespeicherten Fehlermeldungen zur�ck
        @return Array mit HBCI-Returncodes, die allesamt Fehlermeldungen beschreiben */
    public HBCIRetVal[] getErrors()
    {
        return getX('9');
    }

    /** Gibt die in diesem Objekt gespeicherten Warnungen zur�ck
        @return Array mit HBCI-Returncodes, die allesamt Warnmeldungen beschreiben */
    public HBCIRetVal[] getWarnings()
    {
        return getX('3');
    }

    /** Gibt die in diesem Objekt gespeicherten Erfolgsmeldungen zur�ck
        @return Array mit HBCI-Returncodes, die allesamt Erfolgsmeldungen beschreiben */
    public HBCIRetVal[] getSuccess()
    {
        return getX('0');
    }

    /** Gibt einen Code zur�ck, der den zusammengefassten Status aller in diesem
        Objekt gespeicherten R�ckgabewerte beschreibt. Daf�r gibt es folgende
        M�glichkeiten:
        <ul>
          <li><code>STATUS_OK</code> wird zur�ckgegeben, wenn es keine Fehlermeldungen
              oder Exceptions gegeben hat und mindestens eine Erfolgsmeldung oder
              Warnung enthalten ist</li>
         <li><code>STATUS_ERR</code> wird zur�ckgegeben, wenn wenigstens eine
             Exception aufgetreten ist oder wenigstens eine Fehlermeldung enthalten
             ist.</li>
         <li><code>STATUS_UNKNOWN</code> wird zur�ckgegeben, wenn keine der beiden
             o.g. Bedingungen zutrifft.</li>
        </ul> 
        @return einen Code, der den zusammengefassten Status aller R�ckgabewerte
                beschreibt. */
    public int getStatusCode()
    {
        int code;
        
        if (hasExceptions() || hasErrors()) {
            code=STATUS_ERR;
        } else if (hasSuccess() || hasWarnings()) {
            code=STATUS_OK;
        } else {
            code=STATUS_UNKNOWN;
        }
        
        return code;
    }
    
    /** Gibt einen String zur�ck, der alle Fehlermeldungen der hier enthaltenen
        R�ckgabewerte im Klartext enth�lt. F�r evtl. enthaltene Exception wird
        die entsprechende Beschreibung in Langform (siehe
        {@link org.kapott.hbci.manager.HBCIUtils#exception2String(Exception)})
        benutzt.
        @return String mit allen Fehlermeldungen */
    public String getErrorString()
    {
        StringBuffer ret=new StringBuffer();
        
        if (hasExceptions()) {
            for (int i=0;i<exceptions.length;i++) {
                ret.append(HBCIUtils.exception2String(exceptions[i]));
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        if (hasErrors()) {
            HBCIRetVal[] errList=getErrors();
            for (int i=0;i<errList.length;i++) {
                ret.append(errList[i].toString());
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        return ret.toString().trim();
    }
    
    /** Gibt die Status-Informationen aller enthaltenen HBCI-R�ckgabewerte
        als ein String zur�ck. Dabei werden jeweils alle Exceptions in Kurzform
        (siehe {@link org.kapott.hbci.manager.HBCIUtils#exception2StringShort(Exception)}),
        Fehlermeldungen,
        Warnungen und Erfolgsmeldungen zusammengefasst und aneinander gekettet.
        @return String mit allen gespeicherten Status-Informationen */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        if (hasExceptions()) {
            ret.append(HBCIUtilsInternal.getLocMsg("STAT_EXCEPTIONS")+":"+System.getProperty("line.separator"));
            for (int i=0;i<exceptions.length;i++) {
                ret.append(HBCIUtils.exception2StringShort(exceptions[i]));
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        if (hasErrors()) {
            ret.append(HBCIUtilsInternal.getLocMsg("STAT_ERRORS")+":"+System.getProperty("line.separator"));
            HBCIRetVal[] errList=getErrors();
            for (int i=0;i<errList.length;i++) {
                ret.append(errList[i].toString()+System.getProperty("line.separator"));
            }
        }
        
        if (hasWarnings()) {
            ret.append(HBCIUtilsInternal.getLocMsg("STAT_WARNINGS")+":"+System.getProperty("line.separator"));
            HBCIRetVal[] warnList=getWarnings();
            for (int i=0;i<warnList.length;i++) {
                ret.append(warnList[i].toString()+System.getProperty("line.separator"));
            }
        }
        
        if (hasSuccess()) {
            ret.append(HBCIUtilsInternal.getLocMsg("STAT_SUCCESS")+":"+System.getProperty("line.separator"));
            HBCIRetVal[] succList=getSuccess();
            for (int i=0;i<succList.length;i++) {
                ret.append(succList[i].toString()+System.getProperty("line.separator"));
            }
        }
        
        return ret.toString().trim();
    }
}