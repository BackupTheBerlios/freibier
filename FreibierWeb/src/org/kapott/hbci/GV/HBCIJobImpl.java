
/*  $Id: HBCIJobImpl.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.MsgGen;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.exceptions.JobNotSupportedException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportList;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.protocol.factory.SEGFactory;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public abstract class HBCIJobImpl 
    implements HBCIJob
{
    private String name;              /* @brief name of the corresponding GV-segment */
    private Properties params;       /* Eingabeparameter für diesen GV (Saldo.KTV.number) */
    private MsgGen gen;         /* msg-generator, um zu überprüfen, ob jobsegment erzeugt werden kann */
    private HBCIPassportList passports;
    protected HBCIJobResultImpl jobResult;         /* Objekt mit Rückgabedaten für diesen GV */
    private int idx;                  /* idx gibt an, der wievielte task innerhalb der aktuellen message
                                         dieser GV ist */
    private boolean executed;
    private int contentCounter;       /* Zähler, wie viele Rückgabedaten bereits in outStore eingetragen wurden 
                                           (entspricht der anzahl der antwort-segmente!)*/
    private Hashtable constraints;    /* Festlegungen, welche Parameter eine Anwendung setzen muss, wie diese im
                                         HBCI-Kernel umgesetzt werden und welche default-Werte vorgesehen sind; 
                                         die Hashtable hat als Schlüssel einen String, der angibt, wie ein Wert aus einer
                                         Anwendung heraus zu setzen ist. Der dazugehörige Value ist ein Array. Jedes Element
                                         dieses Arrays ist ein String[2], wobei das erste Element angibt, wie der Pfadname heisst,
                                         unter dem der anwendungs-definierte Wert abzulegen ist, das zweite Element gibt den
                                         default-Wert an, falls für diesen Namen *kein* Wert angebeben wurde. Ist der default-
                                         Wert="", so kann das Syntaxelement weggelassen werden. Ist der default-Wert=null,
                                         so *muss* die Anwendung einen Wert spezifizieren */
    
    protected HBCIJobImpl(String jobnameLL,HBCIPassportInternal mainPassport,HBCIJobResultImpl jobResult)
    {
        this.name=findSpecNameForGV(jobnameLL,mainPassport);
        this.params=new Properties();
        
        this.passports=new HBCIPassportList();
        this.passports.addPassport(mainPassport,HBCIPassport.ROLE_ISS);
        
        this.jobResult=jobResult;
        this.contentCounter=0;
        this.constraints=new Hashtable();
        this.executed=false;

        /* offensichtlich soll ein GV mit dem Namen name in die nachricht
           aufgenommen werden. da GV durch segmente definiert sind, und einige
           dieser segmente ein request-tag benoetigen (siehe klasse
           SyntaxElement), wird hier auf jeden fall das request-tag gesetzt.
           wenn es *nicht* benoetigt wird, schadet es auch nichts. und es ist
           auf keinen fall "zu viel" gesetzt, da dieser code nur ausgefuehrt wird,
           wenn das jeweilige segment tatsaechlich erzeugt werden soll */
        params.setProperty(this.name,"requested");
    }
    
    /* gibt den segmentcode für diesen job zurück */
    public String getHBCICode()
    {
        StringBuffer ret=null;
        
        // entfernen der versionsnummer vom aktuellen jobnamen
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".SegHead.code");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        
        // durchsuchen aller param-segmente nach einem job mit dem jobnamen des
        // aktuellen jobs
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                StringBuffer tempkey=new StringBuffer(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=new StringBuffer(passport.getBPD().getProperty(key));
                    ret.replace(1,2,"K");
                    ret.deleteCharAt(ret.length()-1);
                    break;
                }
            }
        }
        
        return ret.toString();
    }
    
    /* gibt zu einem gegebenen jobnamen des namen dieses jobs in der syntax-spez.
     * zurück (also mit angehängter versionsnummer)
     */
    private static String findSpecNameForGV(String jobnameLL,HBCIPassportInternal passport)
    {
        int maxVersion=0;
        
        // alle param-segmente durchlaufen
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            StringBuffer key=new StringBuffer((String)i.nextElement());
            
            if (key.indexOf("Params")==0) {
                key.delete(0,key.indexOf(".")+1);
                // wenn segment mit namen des aktuellen jobs gefunden wurde
                if (key.indexOf(jobnameLL)==0 &&
                    key.toString().endsWith(".SegHead.code")) {
                        
                    key.delete(0,jobnameLL.length()+("Par").length());
                    
                    // extrahieren der versionsnummer aus dem spez-namen
                    String st=key.substring(0,key.indexOf("."));
                    int    version=0;
                    
                    try {
                        version=Integer.parseInt(st);
                    } catch (Exception e) {
                    }
                    
                    // merken der größten jemals aufgetretenen versionsnummer
                    if (version!=0) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_GV_FOUNDGV",new Object[] {jobnameLL,st}),HBCIUtils.LOG_DEBUG2);
                        if (version>maxVersion) {
                            maxVersion=version;
                        }
                    }
                }
            }
        }
        
        if (maxVersion==0) {
            throw new JobNotSupportedException(jobnameLL);
        }
        
        // namen+versionsnummer zurückgeben
        return jobnameLL+Integer.toString(maxVersion);
    }
    
    public int getMaxNumberPerMsg()
    {
        int ret=1;
        
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".maxnum");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                StringBuffer tempkey=new StringBuffer(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=Integer.parseInt(passport.getBPD().getProperty(key));
                    break;
                }
            }
        }
        
        return ret;
    }

    public int getMinSigs()
    {
        int ret=0;
        
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".minsigs");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                StringBuffer tempkey=new StringBuffer(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=Integer.parseInt(passport.getBPD().getProperty(key));
                    break;
                }
            }
        }
        
        return ret;
    }

    protected void addConstraint(String frontendName,String destinationName,String defValue)
    {
        // value ist array:(lowlevelparamname, defaultvalue)
        String[] value=new String[2];
        value[0]=getName()+"."+destinationName;
        value[1]=defValue;

        // alle schon gespeicherten "ziel-lowlevelparameternamen" für den gewünschten
        // frontend-namen suchen
        String[][] values=(String[][])(constraints.get(frontendName));

        if (values==null) {
            // wenn es noch keine gibt, ein neues frontend-ding anlegen
            values=new String[1][];
            values[0]=value;
        } else {
            ArrayList a=new ArrayList(Arrays.asList(values));
            a.add(value);
            values=(String[][])(a.toArray(values));
        }

        constraints.put(frontendName,values);
    }
    
    public void verifyConstraints()
    {
        HBCIPassportInternal passport=getMainPassport();
        
        // durch alle gespeicherten constraints durchlaufen
        for (Iterator i=constraints.keySet().iterator();i.hasNext();) {
            // den frontendnamen für das constraint ermitteln
            String     frontendName=(String)(i.next());
            
            // dazu alle ziel-lowlevelparameter mit default-wert extrahieren
            String[][] values=(String[][])(constraints.get(frontendName));

            // durch alle ziel-lowlevel-parameternamen durchlaufen, die gesetzt werden müssen
            for (int j=0;j<values.length;j++) {
                String[] value=values[j];
                // lowlevel-name des parameters
                String   destination=value[0];
                // default-wert des parameters, wenn keiner angegeben wurde
                String   defValue=value[1];

                String   givenContent=params.getProperty(destination);
                String   content=null;

                content=defValue;
                if (givenContent!=null && givenContent.length()!=0)
                    content=givenContent;

                if (content==null) {
                    String msg=HBCIUtilsInternal.getLocMsg("EXC_MISSING_HL_PROPERTY",frontendName);
                    if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                        throw new InvalidUserDataException(msg);
                    content="";
                }

                // evtl. default-wert als aktuellen wert setzen
                if (content.length()!=0 && givenContent==null)
                    setParam(frontendName,content);
            }
        }
        
        // verify if segment can be created
        SEG seg=null;
        try {
            seg=SEGFactory.getInstance().createSEG(getName(),getName(),null,0,gen.getSyntax());
            for (Enumeration e=getParams().propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                String value=getParams().getProperty(key);
                seg.propagateValue(key,value,
                                   SyntaxElement.TRY_TO_CREATE,
                                   SyntaxElement.DONT_ALLOW_OVERWRITE);
            }
            seg.enumerateSegs(0,false);
            seg.validate();
        } catch (Exception ex) {
            throw new HBCI_Exception("*** the job segment for this task can not be created",ex);
        } finally {
            SEGFactory.getInstance().unuseObject(seg);
        }
    }
    
    public List getJobParameterNames()
    {
        return gen.getGVParameterNames(name);
    }
    
    public List getJobResultNames()
    {
        return gen.getGVResultNames(name);
    }
    
    public Properties getJobRestrictions()
    {
        return passports.getMainPassport().getJobRestrictions(name);
    }
    
    /** Setzen eines komplexen Job-Parameters (Kontodaten). Einige Jobs benötigten Kontodaten
        als Parameter. Diese müssten auf "normalem" Wege durch drei Aufrufe von 
        {@link #setParam(String,String)} erzeugt werden (je einer für
        die Länderkennung, die Bankleitzahl und die Kontonummer). Durch Verwendung dieser
        Methode wird dieser Weg abgekürzt. Es wird ein Kontoobjekt übergeben, für welches
        die entsprechenden drei <code>setParam(String,String)</code>-Aufrufe automatisch
        erzeugt werden.
        @param paramname die Basis der Parameter für die Kontodaten (für "<code>my.country</code>",
        "<code>my.blz</code>", "<code>my.number</code>" wäre das also "<code>my</code>")
        @param acc ein Konto-Objekt, aus welchem die zu setzenden Parameterdaten entnommen werden */
    public void setParam(String paramname,Konto acc)
    {
        if (acc.country!=null && acc.country.length()!=0)
            setParam(paramname+".country",acc.country);
        if (acc.blz!=null && acc.blz.length()!=0)
            setParam(paramname+".blz",acc.blz);
        if (acc.number!=null && acc.number.length()!=0)
            setParam(paramname+".number",acc.number);
    }

    /** Setzen eines komplexen Job-Parameters (Geldbetrag). Einige Jobs benötigten Geldbeträge
        als Parameter. Diese müssten auf "normalem" Wege durch zwei Aufrufe von 
        {@link #setParam(String,String)} erzeugt werden (je einer für
        den Wert und die Währung). Durch Verwendung dieser
        Methode wird dieser Weg abgekürzt. Es wird ein Value-Objekt übergeben, für welches
        die entsprechenden zwei <code>setParam(String,String)</code>-Aufrufe automatisch
        erzeugt werden.
        @param paramname die Basis der Parameter für die Geldbetragsdaten (für "<code>btg.value</code>" und
        "<code>btg.curr</code>" wäre das also "<code>btg</code>")
        @param v ein Value-Objekt, aus welchem die zu setzenden Parameterdaten entnommen werden */
    public void setParam(String paramname,Value v)
    {
        setParam(paramname+".value",HBCIUtils.value2String(v.value));
        if (v.curr!=null && v.curr.length()!=0)
            setParam(paramname+".curr",v.curr);
    }
    
    /** Setzen eines Job-Parameters, bei dem ein Datums als Wert erwartet wird. Diese Methode
        dient als Wrapper für {@link #setParam(String,String)}, um das Datum in einen korrekt
        formatierten String umzuwandeln. Das "richtige" Datumsformat ist dabei abhängig vom
        aktuellen Locale.
        @param paramName Name des zu setzenden Job-Parameters
        @param date Datum, welches als Wert für den Job-Parameter benutzt werden soll */     
    public void setParam(String paramName,Date date)
    {
        setParam(paramName,HBCIUtils.date2String(date));
    }

    /** Setzen eines Job-Parameters, bei dem ein Integer-Wert Da als Wert erwartet wird. Diese Methode
        dient nur als Wrapper für {@link #setParam(String,String)}.
        @param paramName Name des zu setzenden Job-Parameters
        @param i Integer-Wert, der als Wert gesetzt werden soll */     
    public void setParam(String paramName,int i)
    {
        setParam(paramName,Integer.toString(i));
    }

    /** <p>Setzen eines Job-Parameters. Für alle Highlevel-Jobs ist in der Package-Beschreibung zum
        Package {@link org.kapott.hbci.GV} eine Auflistung aller Jobs und deren Parameter zu finden.
        Für alle Lowlevel-Jobs kann eine Liste aller Parameter entweder mit dem Tool
        {@link org.kapott.hbci.tools.ShowLowlevelGVs} oder zur Laufzeit durch Aufruf
        der Methode {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobParameterNames(String)} 
        ermittelt werden.</p>
        <p>Bei Verwendung dieser oder einer der anderen <code>setParam()</code>-Methoden werden zusätzlich
        einige der Job-Restriktionen (siehe {@link #getJobRestrictions()}) analysiert. Beim Verletzen einer
        der überprüften Einschränkungen wird eine Exception mit einer entsprechenden Meldung erzeugt.
        Diese Überprüfung findet allerdings nur bei Highlevel-Jobs statt.</p>
        @param paramName der Name des zu setzenden Parameters.
        @param value Wert, auf den der Parameter gesetzt werden soll */
    public void setParam(String paramName,String value)
    {
        String[][]           destinations=(String[][])constraints.get(paramName);
        HBCIPassportInternal passport=getMainPassport();
        
        if (destinations==null) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_PARAM_NOTNEEDED",new String[] {paramName,getName()});
            if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                throw new InvalidUserDataException(msg);
            destinations=new String[0][];
        }
        
        if (value==null || value.length()==0) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_PARAM_EMPTY",new String[] {paramName,getName()});
            if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                throw new InvalidUserDataException(msg);
            value="";
        }
        
        for (int i=0;i<destinations.length;i++) {
            String[] valuePair=destinations[i];
            String   lowlevelname=valuePair[0];
            setLowlevelParam(lowlevelname,value);
        }
    }
    
    public void setContinueOffset(int loop)
    {
        String offset=getContinueOffset(loop);
        setLowlevelParam(getName()+".offset",(offset!=null)?offset:"");
    }
    
    protected void setLowlevelParam(String key,String value)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_SET_LLPARAM",new String[] {key,value}),HBCIUtils.LOG_DEBUG);
        params.setProperty(key,value);
    }

    public Properties getParams()
    {
        return params;
    }

    public void setIdx(int idx)
    {
        this.idx=idx;
    }

    public String getName()
    {
        return name;
    }

    /* stellt fest, ob für diesen Task ein neues Auftragssegment generiert werden muss.
       Das ist in zwei Fällen der Fall: der Task wurde noch nie ausgeführt; oder der Task
       wurde bereits ausgeführt, hat aber eine "offset"-Meldung zurückgegeben */
    public boolean needsContinue(int loop)
    {
        boolean needs=false;

        if (executed) {
            HBCIRetVal retval=null;
            int        num=jobResult.getRetNumber();

            for (int i=0;i<num;i++) {
                retval=jobResult.getRetVal(i);
                
                if (retval.code.equals("3040") && retval.params.length!=0 && (--loop)==0) {
                    needs=true;
                    break;
                }
            }
        }
        else needs=true;
            
        return needs;
    }

    /* gibt (sofern vorhanden) den offset-Wert des letzten HBCI-Rückgabecodes
       zurück */
    private String getContinueOffset(int loop)
    {
        String ret=null;
        int    num=jobResult.getRetNumber();
        
        for (int i=0;i<num;i++) {
            HBCIRetVal retval=jobResult.getRetVal(i);

            if (retval.code.equals("3040") && retval.params.length!=0 && (--loop)==0) {
                ret=retval.params[0];
                break;
            }
        }

        return ret;
    }

    /* füllt das Objekt mit den Rückgabedaten. Dazu wird zuerst eine Liste aller
       Segmente erstellt, die Rückgabedaten für diesen Task enthalten. Anschließend
       werden die HBCI-Rückgabewerte (RetSegs) im outStore gespeichert. Danach werden
       die GV-spezifischen Daten im outStore abgelegt */
    public void fillJobResult(HBCIMsgStatus status,int offset)
    {
        try {
            executed=true;
            Properties result=status.getData();

            // nachsehen, welche antwortsegmente ueberhaupt
            // zu diesem task gehoeren
            
            // res-num --> segmentheader (wird für sortierung der 
            // antwort-segmente benötigt)
            Hashtable keyHeaders=new Hashtable();
            for (Enumeration i=result.keys();i.hasMoreElements();) {
                String key=(String)(i.nextElement());
                if (key.startsWith("GVRes")&&
                    key.endsWith(".SegHead.ref")) {

                    String segref=result.getProperty(key);
                    if ((Integer.parseInt(segref))-offset==idx) {
                        // nummer des antwortsegments ermitteln
                        int resnum=0;
                        if (key.startsWith("GVRes_")) {
                            resnum=Integer.parseInt(key.substring(key.indexOf('_')+1,key.indexOf('.')));
                        }
                        
                        keyHeaders.put(
                            new Integer(resnum),
                            key.substring(0,key.length()-(".SegHead.ref").length()));
                    }
                }
            }
            
            saveBasicValues(result,idx+offset);
            saveReturnValues(status,idx+offset);
            
            // segment-header-namen der antwortsegmente in der reihenfolge des
            // eintreffens sortieren
            Object[] resnums=keyHeaders.keySet().toArray(new Object[0]);
            Arrays.sort(resnums);

            // alle antwortsegmente durchlaufen
            for (int i=0;i<resnums.length;i++) {
                // dabei reihenfolge des eintreffens beachten
                String header=(String)keyHeaders.get(resnums[i]);
                
                extractPlaintextResults(result,header,contentCounter);
                extractResults(result,header,contentCounter++);
                // der contentCounter wird fuer jedes antwortsegment um 1 erhoeht
            }
        } catch (Exception e) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_CANTSTORERES",getName());
            if (!HBCIUtilsInternal.ignoreError(getMainPassport(),
                                       "client.errors.ignoreJobResultStoreErrors",
                                       msg+": "+HBCIUtils.exception2String(e))) {
                throw new HBCI_Exception(msg,e);
            }
        }
    }
    
    /* wenn wenigstens ein HBCI-Rückgabewert für den aktuellen GV gefunden wurde,
       so werden im outStore zusätzlich die entsprechenden Dialog-Parameter
       gespeichert (Property @c basic.*) */
    private void saveBasicValues(Properties result,int ref)
    {
        // wenn noch keine basic-daten gespeichert sind
        if (jobResult.getDialogId()==null) {
            // Pfad des originalen MsgHead-Segmentes holen und um "orig_" ergaenzen,
            // um den Key fuer die entsprechenden Daten in das result-Property zu erhalten
            String msgheadName="orig_"+result.getProperty("1");
            
            jobResult.storeResult("basic.dialogid",result.getProperty(msgheadName+".dialogid"));
            jobResult.storeResult("basic.msgnum",result.getProperty(msgheadName+".msgnum"));
            jobResult.storeResult("basic.segnum",Integer.toString(ref));

            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_BASIC_FOR_GV_SET_TO",
                                              new Object[] {getName(),
                                                            jobResult.getDialogId()+"/"+
                                                            jobResult.getMsgNum()+"/"+
                                                            jobResult.getSegNum()}),
                          HBCIUtils.LOG_DEBUG);
        }
    }

    /* speichert die HBCI-Rückgabewerte für diesen GV im outStore ab. Dazu werden
       alle RetSegs durchgesehen; diejenigen, die den aktuellen GV betreffen, werden
       im @c data Property unter dem namen @c ret_i.* gespeichert. @i entspricht
       dabei dem @c retValCounter. */
    private void saveReturnValues(HBCIMsgStatus status,int sref)
    {
        HBCIRetVal[] retVals=status.segStatus.getRetVals();
        String       segref=Integer.toString(sref);
        
        for (int i=0;i<retVals.length;i++) {
            HBCIRetVal rv=retVals[i];
            
            if (rv.segref!=null && rv.segref.equals(segref)) {
                jobResult.jobStatus.addRetVal(rv);
            }
        }
        
        /* TODO: das geht nicht für jobs, die mehrere nachrichten benötigt haben */
        jobResult.globStatus=status.globStatus;
    }

    /* diese Methode wird i.d.R. durch abgeleitete GV-Klassen überschrieben, um die
       Rückgabedaten in einem passenden Format abzuspeichern. Diese default-Implementation
       tut nichts */
    protected void extractResults(Properties result,String header,int idx)
    {
    }

    private void extractPlaintextResults(Properties result,String header,int idx)
    {
        for (Enumeration e=result.keys();e.hasMoreElements();) {
            String key=(String)(e.nextElement());
            if (key.startsWith(header+".")) {
                jobResult.storeResult(HBCIUtilsInternal.withCounter("content",idx)+
                                      "."+
                                      key.substring(header.length()+1),result.getProperty(key));
            }
        }
    }

    public HBCIJobResult getJobResult()
    {
        return jobResult;
    }
    
    protected HBCIPassportInternal getMainPassport()
    {
        return passports.getMainPassport();
    }
    
    protected void checkAccountCRC(String frontendname)
    {
        String[][] data=(String[][])constraints.get(frontendname+".blz");
        String     lowlevelHeader=null;
        
        for (int i=0;i<data.length;i++) {
            String[] values=data[i];
            String   paramname=values[0];
            
            lowlevelHeader=paramname.substring(0,paramname.lastIndexOf(".KIK.blz"));

            String country=params.getProperty(lowlevelHeader+".KIK.country");
            String blz=params.getProperty(lowlevelHeader+".KIK.blz");
            String number=params.getProperty(lowlevelHeader+".number");
            
            String orig_blz=blz;
            String orig_number=number;

            while (true) {
                boolean crcok=new Konto(blz,number).checkCRC();

                String old_blz=blz;
                String old_number=number;

                if (!crcok) {
                    StringBuffer sb=new StringBuffer(blz+"|"+number);
                    HBCIUtilsInternal.getCallback().callback(getMainPassport(),
                                                     HBCICallback.HAVE_CRC_ERROR,
                                                     HBCIUtilsInternal.getLocMsg("CALLB_HAVE_CRC_ERROR"),
                                                     HBCICallback.TYPE_TEXT,
                                                     sb);

                    int idx=sb.indexOf("|");
                    blz=sb.substring(0,idx);
                    number=sb.substring(idx+1);
                }
                
                if (blz.equals(old_blz) && number.equals(old_number))
                    break;
            }
                
            if (!blz.equals(orig_blz) || !number.equals(orig_number))
                setParam(frontendname,new Konto(country,blz,number));
        }
    }
    
    public void setMsgGen(MsgGen gen)
    {
        this.gen=gen;
    }
    
    public void addSignaturePassport(HBCIPassport passport,String role)
    {
        HBCIUtils.log("*** adding additional passport to job "+getName(),
                HBCIUtils.LOG_DEBUG);
        passports.addPassport((HBCIPassportInternal)passport,role);
    }
    
    public HBCIPassportList getSignaturePassports()
    {
        return passports;
    }
}
