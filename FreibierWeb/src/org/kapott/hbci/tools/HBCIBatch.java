
/*  $Id: HBCIBatch.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2004  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.FileSystemClassLoader;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

/** Tool zum Ausführen von HBCI-Jobs, die in einer Batch-Datei definiert werden 
 *  können.
 *  <pre>
 *  args[0] - configfile für HBCIUtils.init() (Property-File mit Kernel-Parametern
 *            [siehe API-Doc zu org.kapott.hbci.manager.HBCIUtils])</li>
 *  args[1] - Dateiname der Antwortdatei für Callbacks
 *    passphrase.source=here|stdin
 *    passphrase=
 *    softpin=
 *    pin.source=here|stdin
 *    pin=
 *    tan=
 *  args[2] - Dateiname der Batch-Datei (jobnamen und parameter siehe
 *            API-Doc zu Paket org.kapott.hbci.GV)
 *    hljobname:jobid[:customerid]
 *    jobparam=paramvalue
 *    jobparam=paramvalue
 *    
 *    #lljobname:jobid[:customerid]
 *    ...
 *  args[3] - Dateiname der Ausgabedatei (mehr dazu siehe unten)
 *    jobid
 *    resultparam=value
 *    resultparam=value
 *  
 *    jobid
 *    ...
 *  [args[4]] - Dateiname der Log-Datei
 *  </pre>
 *  
 *  <p>Verwendetes Passport muss bereits wenigstens einmal benutzt worden sein!</p>
 *  
 *  <p>Alle Jobs, bei deren Ausführung ein Fehler auftritt, werden nicht in die
 *  "normale" Ausgabedatei aufgenommen. Statt dessen wird eine zweite Aus-
 *  gabedatei erzeugt, die den gleichen Namen wie die "normale" Ausgabedatei
 *  plus ein Suffix ".err" hat. In dieser Fehlerdatei wird für jeden fehler-
 *  haften Job folgende Struktur geschrieben (String in "<>" wird durch die
 *  jeweiligen werte ersetzt):</p>
 *  <pre>
 *    jobid: <jobid> 
 *    global error:
 *    <allg. fehlermeldung zur hbci-nachricht, in der der job ausgeführt werden sollte>
 *    job error:
 *    <fehlermeldung zu dem nachrichten-segment, in welchem der job definiert war>
 *  
 *    jobid: <jobid>
 *    ...
 *  </pre>
 *  <p>das ist zwar nicht besonders schön, reicht aber vielleicht erst mal (?)
 *  Alternativ dazu könnte ich anbieten, dass eine vollständige Fehlernachricht
 *  über den *kompletten* Batch-Vorgang in eine Fehlerdatei geschrieben wird,
 *  sobald *irgendein* Job nicht sauber ausgeführt wurde (das hätte den Vorteil, 
 *  dass auch Fehler, die nicht direkt mit einem bestimmten Job in Verbindung
 *  stehen [z.B. Fehler bei der Dialog-Initialisierung] ordentlich geloggt
 *  werden).</p> */
public class HBCIBatch
{
    private static class MyCallback
    extends HBCICallbackConsole
    {
        private Properties answers;

        public MyCallback(String[] args)
            throws FileNotFoundException,IOException
        {
            answers=new Properties();
            FileInputStream answerFile=new FileInputStream(args[1]);
            answers.load(answerFile);
            answerFile.close();
            
            if (answers.getProperty("passphrase.source").equals("stdin")) {
                String passphrase=new BufferedReader(new InputStreamReader(System.in)).readLine();
                answers.setProperty("passphrase",passphrase);
            }
            if (answers.getProperty("pin.source").equals("stdin")) {
                String pin=new BufferedReader(new InputStreamReader(System.in)).readLine();
                answers.setProperty("pin",pin);
            }
            
            if (args.length>=5) {
                PrintStream outStream=new PrintStream(new FileOutputStream(args[4]));
                System.setOut(outStream);
                System.setErr(outStream);
            }
        }
        
        public synchronized void callback(HBCIPassport passport,int reason,String msg,int datatype,StringBuffer retData)
        {
            switch (reason) {
                case NEED_PASSPHRASE_LOAD:
                    HBCIUtils.log("callback with reason NEED_PASSPHRASE_LOAD",HBCIUtils.LOG_INFO);
                    retData.replace(0,retData.length(),answers.getProperty("passphrase"));
                    break;
                case NEED_PT_PIN:
                    HBCIUtils.log("callback with reason NEED_PT_PIN",HBCIUtils.LOG_INFO);
                    retData.replace(0,retData.length(),answers.getProperty("pin"));
                    break;
                case NEED_PT_TAN:
                    HBCIUtils.log("callback with reason NEED_PT_TAN",HBCIUtils.LOG_INFO);
                    retData.replace(0,retData.length(),answers.getProperty("tan"));
                    break;
                case NEED_SOFTPIN:
                    HBCIUtils.log("callback with reason NEED_SOFTPIN",HBCIUtils.LOG_INFO);
                    retData.replace(0,retData.length(),answers.getProperty("softpin"));
                    break;
                case NEED_CONNECTION:
                case HAVE_INST_MSG:
                case HAVE_CRC_ERROR:
                case CLOSE_CONNECTION:
                    HBCIUtils.log("callback with reason "+reason+": "+msg,HBCIUtils.LOG_INFO);
                    break;
            }
        }
    }
    
    private final static int STATE_NEED_JOBNAME=1;
    private final static int STATE_NEED_JOBPARAMS=2;
    
    public static void main(String[] args)
        throws Exception
    {
        HBCIUtils.init(new FileSystemClassLoader(),args[0],new MyCallback(args));
        HBCIPassport passport=AbstractHBCIPassport.getInstance();
        
        try {
            HBCIHandler handler=new HBCIHandler(passport.getHBCIVersion(),passport);
            
            try {
                BufferedReader reader=new BufferedReader(new FileReader(args[2]));
                String line;
                
                try {
                    int       state=STATE_NEED_JOBNAME;
                    HBCIJob   job=null;
                    String    jobid=null;
                    String    customerId=null;
                    Hashtable jobs=new Hashtable();
                        
                    while ((line=reader.readLine())!=null) {
                        line=line.trim();
                        if (state==STATE_NEED_JOBNAME && line.length()!=0) {
                            StringTokenizer tok=new StringTokenizer(line,":");
                            
                            String jobname=tok.nextToken().trim();
                            if (jobname.startsWith("#")) {
                                job=handler.newLowlevelJob(jobname.substring(1));
                            } else {
                                job=handler.newJob(jobname);
                            }
                            
                            jobid=tok.nextToken().trim();
                            customerId=(tok.hasMoreTokens()?tok.nextToken().trim():null);
                            jobs.put(jobid,job);
                            state=STATE_NEED_JOBPARAMS;
                        } else if (state==STATE_NEED_JOBPARAMS) {
                            if (line.length()!=0) {
                                StringTokenizer tok=new StringTokenizer(line,"=");
                                job.setParam(tok.nextToken().trim(),tok.nextToken().trim());
                            } else {
                                handler.addJob(customerId,job);
                                state=STATE_NEED_JOBNAME;
                            }
                        }
                    }
                    
                    if (state==STATE_NEED_JOBPARAMS) {
                        handler.addJob(customerId,job);
                    }
                    
                    handler.execute();
                    
                    PrintWriter writer=new PrintWriter(new FileWriter(args[3]));
                    PrintWriter errWriter=new PrintWriter(new FileWriter(args[3]+".err"));
                    
                    try {
                        for (Enumeration keys=jobs.keys();keys.hasMoreElements();) {
                            jobid=(String)keys.nextElement();
                            job=(HBCIJob)jobs.get(jobid);
                            
                            if (job.getJobResult().isOK()) {
                                writer.println(jobid);
                                
                                Properties result=job.getJobResult().getResultData();
                                if (result!=null) {
                                    for (Enumeration names=result.propertyNames();names.hasMoreElements();) {
                                        String name=(String)names.nextElement();
                                        String value=result.getProperty(name);
                                        writer.println(name+"="+value);
                                    }
                                }
                                
                                writer.println();
                            } else {
                                errWriter.println("jobid: "+jobid);
                                errWriter.println("global error:");
                                errWriter.println(job.getJobResult().getGlobStatus().getErrorString());
                                errWriter.println("job error:");
                                errWriter.println(job.getJobResult().getGlobStatus().getErrorString());
                                errWriter.println();
                            }
                        }
                    } finally {
                        writer.close();
                        errWriter.close();
                    }
                } finally {
                    reader.close();
                }
            } finally {
                handler.close();
                passport=null;
            }
        } finally {
            if (passport!=null) {
                passport.close();
            }
        }
    }
}
