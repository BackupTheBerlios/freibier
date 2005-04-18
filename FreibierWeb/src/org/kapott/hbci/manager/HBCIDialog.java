
/*  $Id: HBCIDialog.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

package org.kapott.hbci.manager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportList;
import org.kapott.hbci.status.HBCIDialogStatus;
import org.kapott.hbci.status.HBCIInstMessage;
import org.kapott.hbci.status.HBCIMsgStatus;

/* @brief A class for managing exactly one HBCI-Dialog

    A HBCI-Dialog consists of a number of HBCI-messages. These
    messages will be sent (and the responses received) one
    after the other, without timegaps between them (to avoid
    network timeout problems).

    The messages generated by a HBCI-Dialog are at first DialogInit-Message,
    after that a message that contains one ore more "Geschaeftsvorfaelle"
    (i.e. the stuff that you really want to do via HBCI), and at last
    a DialogEnd-Message.

    In this class we have two API-levels, a mid-level API (for manually
    creating and processing dialogs) and a high-level API (for automatic
    creation of typical HBCI-dialogs). For each method the API-level is
    given in its description 
*/
public final class HBCIDialog
{
    private HBCIKernelImpl       kernel;    /* The kernel object to be used by this dialog */
    private HBCIPassportInternal mainPassport;
    private boolean              isAnon;
    private String               anonSuffix;
    private String               dialogid;  /* The dialogID for this dialog (unique for each dialog) */
    private long                 msgnum;    /* An automatically managed message counter. */
    private List                 msgs;    /* this array contains all messages to be sent (excluding
                                             dialogInit and dialogEnd); each element of the arrayList
                                             is again an ArrayList, where each element is one
                                             task (GV) to be sent with this specific message */
    private Properties listOfGVs;    // liste aller GVs in der aktuellen msg; key ist der hbciCode des jobs,
                                     // value ist die anzahl dieses jobs in der aktuellen msg

    /** @brief Creates a new instance of an HBCIDialog.

        @param kernel A HBCIKernelImpl object to be used when processing the job.
        @param mainPassport The mainPassport to be used */
    public HBCIDialog(HBCIKernelImpl kernel,HBCIPassportInternal mainPassport)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_DIALOG_NEW"),HBCIUtils.LOG_DEBUG);

        this.kernel=kernel;
        this.mainPassport=mainPassport;
        this.isAnon=mainPassport.isAnonymous();
        this.anonSuffix=isAnon?"Anon":"";
        this.msgs=new ArrayList();
        this.msgs.add(new ArrayList());
        this.listOfGVs=new Properties();
    }

    /** @brief Processing the DialogInit stage and updating institute and user data from the server
               (mid-level API).

        This method processes the dialog initialization stage of an HBCIDialog. It creates
        a new rawMsg in the kernel and processes it. The return values will be
        passed to appropriate methods in the @c institute and @c user objects to
        update their internal state with the data received from the institute. */
    private HBCIMsgStatus doDialogInit()
    {
        HBCIMsgStatus ret=new HBCIMsgStatus();
        
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_DIALOG_INIT"),HBCIUtils.LOG_INFO);
            HBCIUtilsInternal.getCallback().status(mainPassport,HBCICallback.STATUS_DIALOG_INIT,null);
            String country=mainPassport.getCountry();
            String blz=mainPassport.getBLZ();
    
            kernel.rawNewMsg("DialogInit"+anonSuffix);
            kernel.rawSet("Idn.KIK.blz", blz);
            kernel.rawSet("Idn.KIK.country", country);
            if (!isAnon) {
                kernel.rawSet("Idn.customerid", mainPassport.getCustomerId());
                kernel.rawSet("Idn.sysid", mainPassport.getSysId());
                String sysstatus=mainPassport.getSysStatus();
                kernel.rawSet("Idn.sysStatus",sysstatus);
                if (mainPassport.needInstKeys()) {
                    kernel.rawSet("KeyReq.SecProfile.method",mainPassport.getProfileMethod());
                    kernel.rawSet("KeyReq.SecProfile.version",mainPassport.getProfileVersion());
                    kernel.rawSet("KeyReq.KeyName.keytype", "V");
                    kernel.rawSet("KeyReq.KeyName.KIK.country", country);
                    kernel.rawSet("KeyReq.KeyName.KIK.blz", blz);
                    kernel.rawSet("KeyReq.KeyName.userid", mainPassport.getInstEncKeyName());
                    kernel.rawSet("KeyReq.KeyName.keynum", mainPassport.getInstEncKeyNum());
                    kernel.rawSet("KeyReq.KeyName.keyversion", mainPassport.getInstEncKeyVersion());

                    if (mainPassport.hasInstSigKey()) {
                        kernel.rawSet("KeyReq_2.SecProfile.method",mainPassport.getProfileMethod());
                        kernel.rawSet("KeyReq_2.SecProfile.version",mainPassport.getProfileVersion());
                        kernel.rawSet("KeyReq_2.KeyName.keytype", "S"); // *** keytype "D"
                        kernel.rawSet("KeyReq_2.KeyName.KIK.country", country);
                        kernel.rawSet("KeyReq_2.KeyName.KIK.blz", blz);
                        kernel.rawSet("KeyReq_2.KeyName.userid", mainPassport.getInstSigKeyName());
                        kernel.rawSet("KeyReq_2.KeyName.keynum", mainPassport.getInstSigKeyNum());
                        kernel.rawSet("KeyReq_2.KeyName.keyversion", mainPassport.getInstSigKeyVersion());
                    }
                }
            }
            kernel.rawSet("ProcPrep.BPD", mainPassport.getBPDVersion());
            kernel.rawSet("ProcPrep.UPD", mainPassport.getUPDVersion());
            kernel.rawSet("ProcPrep.lang",mainPassport.getDefaultLang());
            kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
            kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.4"));
            ret=kernel.rawDoIt(mainPassport,
                               !isAnon && HBCIKernelImpl.SIGNIT,
                               !isAnon && HBCIKernelImpl.CRYPTIT,
                               !isAnon && HBCIKernelImpl.NEED_SIG,
                               !isAnon && HBCIKernelImpl.NEED_CRYPT);
            
            if (ret.isOK()) {
                Properties result=ret.getData();
                
                HBCIInstitute inst=new HBCIInstitute(kernel,mainPassport);
                inst.updateBPD(result);
                inst.extractKeys(result);
    
                HBCIUser user=new HBCIUser(kernel,mainPassport);
                user.updateUPD(result);
               
                mainPassport.saveChanges();
    
                msgnum=2;
                dialogid=result.getProperty("MsgHead.dialogid");
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_DIALOG_ID_SET")+" "+dialogid,HBCIUtils.LOG_DEBUG);

                HBCIInstMessage msg=null;
                for (int i=0;true;i++) {
                    try {
                        String header=HBCIUtilsInternal.withCounter("KIMsg",i);
                        msg=new HBCIInstMessage(result,header);
                    } catch (Exception e) {
                        break;
                    }
                    HBCIUtilsInternal.getCallback().callback(mainPassport,
                                                     HBCICallback.HAVE_INST_MSG,
                                                     msg.toString(),
                                                     HBCICallback.TYPE_NONE,
                                                     new StringBuffer());
                }
            }

            HBCIUtilsInternal.getCallback().status(mainPassport,HBCICallback.STATUS_DIALOG_INIT_DONE,new Object[] {ret,dialogid});
        } catch (Exception e) {
            ret.addException(e);
        }

        return ret;
    }

    private HBCIMsgStatus[] doJobs()
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_DIALOG_JOBS"),HBCIUtils.LOG_INFO);
        ArrayList msgstatus_a=new ArrayList();

        // durch die liste aller auszuf�hrenden nachrichten durchloopen
        int msgnum=msgs.size();
        for (int j=0;j<msgnum;j++) {
            // tasks ist liste aller jobs, die in dieser nachricht ausgef�hrt werden sollen
            ArrayList     tasks=(ArrayList)(msgs.get(j));
            
            // loop wird benutzt, um zu z�hlen, wie oft bereits "nachgehakt" wurde,
            // falls ein bestimmter job nicht mit einem einzigen nachrichtenaustausch
            // abgearbeitet werden konnte (z.b. abholen kontoausz�ge)
            int           loop=0;
            HBCIMsgStatus msgstatus=new HBCIMsgStatus();
            
            // diese schleife loopt solange, bis alle jobs der aktuellen nachricht
            // tats�chlich abgearbeitet wurden (also inclusive "nachhaken")
            while (true) {
                boolean addMsgStatus=true;
                
                try {
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_DIALOG_CUSTOM_MSG_NUM_LOOP",
                        new Object[] {Integer.toString(j+1),Integer.toString(loop+1)}),
                        HBCIUtils.LOG_DEBUG);
                    
                    int              taskNum=0;
                    HBCIPassportList msgPassports=new HBCIPassportList();
                    
                    kernel.rawNewMsg("CustomMsg");
                    
                    // durch alle jobs loopen, die eigentlich in der aktuellen
                    // nachricht abgearbeitet werden m�ssten
                    for (Iterator i=tasks.iterator();i.hasNext();) {
                        HBCIJobImpl task=(HBCIJobImpl)(i.next());
                        
                        // wenn der Task entweder noch gar nicht ausgef�hrt wurde
                        // oder in der letzten Antwortnachricht ein entsprechendes
                        // Offset angegeben wurde
                        if (task.needsContinue(loop)) {
                            task.setContinueOffset(loop);
                            
                            Properties p=task.getParams();
                            String header=HBCIUtilsInternal.withCounter("GV",taskNum);
                            
                            String taskName=task.getName();
                            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_DIALOG_ADD_TASK",taskName),HBCIUtils.LOG_DEBUG);
                            HBCIUtilsInternal.getCallback().status(mainPassport,HBCICallback.STATUS_SEND_TASK,task);
                            task.setIdx(taskNum);
                            
                            // Daten f�r den Task festlegen
                            for (Enumeration e=p.keys();e.hasMoreElements();) {
                                String key=(String)(e.nextElement());
                                kernel.rawSet(header+"."+key,p.getProperty(key));
                            }
                            
                            // additional passports f�r diesen task ermitteln
                            // und zu den passports f�r die aktuelle nachricht
                            // hinzuf�gen
                            msgPassports.addAll(task.getSignaturePassports());
                            
                            taskNum++;
                        }
                    }
                    
                    // wenn keine jobs f�r die aktuelle message existieren
                    if (taskNum==0) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_DIALOG_LOOPEND",Integer.toString(loop+1)),HBCIUtils.LOG_DEBUG);
                        addMsgStatus=false;
                        break;
                    }
                    
                    kernel.rawSet("MsgHead.dialogid", dialogid);
                    kernel.rawSet("MsgHead.msgnum", getMsgNum());
                    kernel.rawSet("MsgTail.msgnum", getMsgNum());
                    nextMsgNum();
                    
                    // nachrichtenaustausch durchf�hren
                    msgstatus=kernel.rawDoIt(msgPassports,HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);
                    Properties result=msgstatus.getData();
                    
                    // searching for first segment number that belongs to the custom_msg
                    // we look for entries like {"1","CustomMsg.MsgHead"} and so
                    // on (this data is inserted from the HBCIKernelImpl.rawDoIt() method),
                    // until we find the first segment containing a task
                    int offset=0;   // this specifies, how many segments precede the first task segment
                    for (offset=1;true;offset++) {
                        String path=result.getProperty(Integer.toString(offset));
                        if (path==null || path.startsWith("CustomMsg.GV")) {
                            if (path==null) { // wenn kein entsprechendes Segment gefunden, dann offset auf 0 setzen
                                offset=0;
                            }
                            break;
                        }
                    }
                    
                    if (offset!=0) {           
                        // f�r jeden Task die entsprechenden R�ckgabedaten-Klassen f�llen
                        // in fillOutStore wird auch "executed" fuer den jeweiligen Task auf true gesetzt.
                        for (Iterator i=tasks.iterator();i.hasNext();) {
                            HBCIJobImpl task=(HBCIJobImpl)(i.next());
                            if (task.needsContinue(loop)) {
                                // nur wenn der auftrag auch tatsaechlich gesendet werden musste
                                try {
                                    task.fillJobResult(msgstatus,offset);
                                    HBCIUtilsInternal.getCallback().status(mainPassport,HBCICallback.STATUS_SEND_TASK_DONE,task);
                                } catch (Exception e) {
                                    msgstatus.addException(e);
                                }
                            }
                        }
                    }
                    
                    if (msgstatus.hasExceptions()) {
                        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_DIALOG_LOOPABORT"),HBCIUtils.LOG_ERR);
                        break;
                    }
                    
                    loop++;
                } catch (Exception e) {
                    msgstatus.addException(e);
                } finally {
                    if (addMsgStatus) {
                        msgstatus_a.add(msgstatus);
                    }
                }
            }
        }

        HBCIMsgStatus[] ret=new HBCIMsgStatus[0];
        if (msgstatus_a.size()!=0)
            ret=(HBCIMsgStatus[])(msgstatus_a.toArray(ret));

        return ret;
    }

    /** @brief Processes the DialogEnd stage of an HBCIDialog (mid-level API).

        Works similarily to doDialogInit(). */
    private HBCIMsgStatus doDialogEnd()
    {
        HBCIMsgStatus ret=new HBCIMsgStatus();
        
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_DIALOG_END"),HBCIUtils.LOG_INFO);
            HBCIUtilsInternal.getCallback().status(mainPassport,HBCICallback.STATUS_DIALOG_END,null);
    
            kernel.rawNewMsg("DialogEnd"+anonSuffix);
            kernel.rawSet("DialogEndS.dialogid", dialogid);
            kernel.rawSet("MsgHead.dialogid", dialogid);
            kernel.rawSet("MsgHead.msgnum", getMsgNum());
            kernel.rawSet("MsgTail.msgnum", getMsgNum());
            nextMsgNum();
            ret=kernel.rawDoIt(mainPassport,
                               !isAnon && HBCIKernelImpl.SIGNIT,
                               !isAnon && HBCIKernelImpl.CRYPTIT,
                               !isAnon && HBCIKernelImpl.NEED_SIG,
                               !isAnon && HBCIKernelImpl.NEED_CRYPT);

            HBCIUtilsInternal.getCallback().status(mainPassport,HBCICallback.STATUS_DIALOG_END_DONE,ret);
        } catch (Exception e) {
            ret.addException(e);
        }

        return ret;
    }

    /** f�hrt einen kompletten dialog mit allen zu diesem
        dialog gehoerenden nachrichten/tasks aus.

        bricht diese methode mit einer exception ab, so muessen alle
        nachrichten bzw. tasks, die noch nicht ausgef�hrt wurden, 
        von der aufrufenden methode neu erzeugt werden */
    public HBCIDialogStatus doIt()
    {
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_DIALOG_DIALOG"),HBCIUtils.LOG_DEBUG);
            HBCIDialogStatus ret=new HBCIDialogStatus();
            
            HBCIMsgStatus initStatus=doDialogInit();
            ret.setInitStatus(initStatus);
                
            if (initStatus.isOK()) {
                ret.setMsgStatus(doJobs());
                ret.setEndStatus(doDialogEnd());
            }
            
            return ret;
        } finally {
            reset();
        }
    }

    private void reset()
    {
        try {
            dialogid=null;
            msgnum=1;
            msgs=new ArrayList();
            msgs.add(new ArrayList());
            listOfGVs.clear();
        } catch (Exception e) {
            HBCIUtils.log(e);
        }
    }
    
    public String getDialogID()
    {
        return dialogid;
    }

    public String getMsgNum()
    {
        return Long.toString(msgnum);
    }

    public void nextMsgNum()
    {
        msgnum++;
    }

    public void addTask(HBCIJobImpl job)
    {
        // TODO: hier evtl. auch �berpr�fen, dass nur jobs mit den gleichen
        // signatur-anforderungen (anzahl) in einer msg stehen
        
        try {
            HBCIUtils.log("*** adding job "+job.getName()+" to dialog",HBCIUtils.LOG_INFO);
            job.verifyConstraints();
            
            // check bpd.numgva here
            String hbciCode=job.getHBCICode();
            String counter_st=listOfGVs.getProperty(hbciCode);
            int counter=(counter_st!=null)?Integer.parseInt(counter_st):0;

            counter++;

            int max=mainPassport.getMaxGVperMsg();
            if (max>0 && listOfGVs.size()>max ||
                counter>job.getMaxNumberPerMsg()) {

                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_DIALOG_NEWMSG"),HBCIUtils.LOG_INFO);
                newMsg();
                counter=1;
            }

            listOfGVs.setProperty(hbciCode,Integer.toString(counter));

            ((ArrayList)(msgs.get(msgs.size()-1))).add(job);
        } catch (Exception e) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_CANTADDJOB",job.getName());
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreAddJobErrors",
                                       msg+": "+HBCIUtils.exception2String(e))) {
                throw new HBCI_Exception(msg,e);
            }
            
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("WRN_JOBSKIPPED",job.getName()),HBCIUtils.LOG_ERR);
        }
    }
    
    public List getAllTasks()
    {
        List tasks=new ArrayList();
        
        for (Iterator i=msgs.iterator();i.hasNext();) {
            tasks.addAll((List)i.next());
        }
        
        return tasks;
    }

    public void newMsg()
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_DIALOG_NEWMSG"),HBCIUtils.LOG_DEBUG);
        msgs.add(new ArrayList());
        listOfGVs.clear();
    }
}