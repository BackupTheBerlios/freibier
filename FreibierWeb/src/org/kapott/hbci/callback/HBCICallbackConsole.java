
/*  $Id: HBCICallbackConsole.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $

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

package org.kapott.hbci.callback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.INILetter;
import org.kapott.hbci.status.HBCIMsgStatus;

/** Default-Implementation einer Callback-Klasse für textbasierte Anwendungen. Diese Klasse
    ist eine vollständig funktionsfähige Callback-Klasse und kann direkt in eigenen
    Anwendungen benutzt werden. Dabei werden alle Aus- und Eingaben auf der Konsole
    vorgenommen, d.h. es werden keine GUI-Elemente erzeugt. */
public class HBCICallbackConsole
    extends AbstractHBCICallback
{
    /** Schreiben von Logging-Ausgaben auf STDOUT. Diese Methode implementiert die Logging-Schnittstelle
        des {@link org.kapott.hbci.callback.HBCICallback}-Interfaces</a>. Die Log-Informationen,
        die dieser Methode übergeben werden, werden formatiert auf STDOUT ausgegeben. In dem
        ausgegebenen String sind in enthalten das Log-Level der Message, ein Zeitstempel im
        Format "<code>yyyy.MM.dd HH:mm:ss.SSS</code>", die Namen der ThreadGroup und des Threads, aus dem 
        heraus die Log-Message erzeugt wurde, der Klassenname der Klasse, welche die Log-Ausgabe
        erzeugt hat sowie die eigentliche Log-Message */
    public synchronized void log(String msg,int level,Date date,StackTraceElement trace)
    {
        String[] levels={"NON","ERR","WRN","INF","DBG","DB2"};
        StringBuffer ret=new StringBuffer(128);
        ret.append("<").append(levels[level]).append("> ");
        
        SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
        ret.append("[").append(df.format(date)).append("] ");
        
        Thread thread=Thread.currentThread();
        ret.append("[").append(thread.getThreadGroup().getName()+
                               "/"+
                               thread.getName()).append("] ");
        
        String classname=trace.getClassName();
        String hbciname="org.kapott.hbci.";
        if (classname!=null && classname.startsWith(hbciname))
            ret.append(classname.substring((hbciname).length())).append(": ");
        
        if (msg==null)
            msg="";
        StringBuffer escapedString=new StringBuffer();
        int len=msg.length();
        
        for (int i=0;i<len;i++) {
            char ch=msg.charAt(i);
            int  x=ch;
            
            if ((x<26 && x!=9 && x!=10 && x!=13) || ch=='\\') {
                String temp=Integer.toString(x,16);
                if (temp.length()!=2)
                    temp="0"+temp;
                escapedString.append("\\"+temp);
            }
            else escapedString.append(ch);
        }
        ret.append(escapedString);
        System.out.println(ret.toString());
    }
    
    /** Diese Methode reagiert auf alle möglichen Callback-Ursachen. Bei Callbacks, die nur
        Informationen an den Anwender übergeben sollen, werden diese auf STDOUT ausgegeben.
        Bei Callbacks, die Aktionen vom Anwender erwarten (Einlegen der Chipkarte), wird eine
        entsprechende Aufforderung auf STDOUT ausgegeben. Bei Callbacks, die eine Eingabe vom
        Nutzer erwarten, wird die entsprechende Eingabeaufforderung auf STDOUT ausgegeben und die
        Eingabe von STDIN gelesen.*/
    public void callback(HBCIPassport passport,int reason,String msg,int datatype,StringBuffer retData)
    {
        System.out.println(HBCIUtilsInternal.getLocMsg("CALLB_PASS_IDENT",passport.getClientData("init")));
        
        try {
            INILetter iniletter;
            Date      date;
            String    st;
            
            switch (reason) {
                case NEED_PASSPHRASE_LOAD:
                case NEED_PASSPHRASE_SAVE:
                    System.out.print(msg+": ");
                    System.out.flush();
                    
                    st=(new BufferedReader(new InputStreamReader(System.in))).readLine();
                    if (reason==NEED_PASSPHRASE_SAVE) {
                        System.out.print(msg+" (again): ");
                        System.out.flush();
                        
                        String st2=(new BufferedReader(new InputStreamReader(System.in))).readLine();
                        if (!st.equals(st2))
                            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PWDONTMATCH"));
                    }
                    retData.replace(0,retData.length(),st);
                    break;

                case NEED_CHIPCARD:
                    System.out.println(msg);
                    break;

                case NEED_HARDPIN:
                    System.out.println(msg);
                    break;

                case NEED_SOFTPIN:
                case NEED_PT_PIN:
                case NEED_PT_TAN:
                    System.out.print(msg+": ");
                    System.out.flush();
                    retData.replace(0,retData.length(),
                                    (new BufferedReader(new InputStreamReader(System.in))).readLine());
                    break;

                case HAVE_HARDPIN:
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_CLB_ENDHARDPIN"),HBCIUtils.LOG_DEBUG);
                    break;

                case HAVE_CHIPCARD:
                    HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_CLB_ENDCHIPCARD"),HBCIUtils.LOG_DEBUG);
                    break;

                case NEED_COUNTRY:
                case NEED_BLZ:
                case NEED_HOST:
                case NEED_PORT:
                case NEED_FILTER:
                case NEED_USERID:
                case NEED_CUSTOMERID:
                    System.out.print(msg+" ["+retData.toString()+"]: ");
                    System.out.flush();
                    st=(new BufferedReader(new InputStreamReader(System.in))).readLine();
                    if (st.length()==0)
                        st=retData.toString();
                    retData.replace(0,retData.length(),st);
                    break;

                case NEED_NEW_INST_KEYS_ACK:
                    System.out.println(msg);
                    iniletter=new INILetter(passport,INILetter.TYPE_INST);
                    System.out.println(HBCIUtilsInternal.getLocMsg("EXPONENT")+": "+HBCIUtils.data2hex(iniletter.getKeyExponent()));
                    System.out.println(HBCIUtilsInternal.getLocMsg("MODULUS")+": "+HBCIUtils.data2hex(iniletter.getKeyModulus()));
                    System.out.println(HBCIUtilsInternal.getLocMsg("HASH")+": "+HBCIUtils.data2hex(iniletter.getKeyHash()));
                    System.out.print("<ENTER>=OK, \"ERR\"=ERROR: ");
                    System.out.flush();
                    retData.replace(0,retData.length(),
                                    (new BufferedReader(new InputStreamReader(System.in))).readLine());
                    break;

                case HAVE_NEW_MY_KEYS:
                    iniletter=new INILetter(passport,INILetter.TYPE_USER);
                    date=new Date();
                    System.out.println(HBCIUtilsInternal.getLocMsg("DATE")+": "+HBCIUtils.date2String(date));
                    System.out.println(HBCIUtilsInternal.getLocMsg("TIME")+": "+HBCIUtils.time2String(date));
                    System.out.println(HBCIUtilsInternal.getLocMsg("BLZ")+": "+passport.getBLZ());
                    System.out.println(HBCIUtilsInternal.getLocMsg("USERID")+": "+passport.getUserId());
                    System.out.println(HBCIUtilsInternal.getLocMsg("KEYNUM")+": "+passport.getMyPublicSigKey().num);
                    System.out.println(HBCIUtilsInternal.getLocMsg("KEYVERSION")+": "+passport.getMyPublicSigKey().version);
                    System.out.println(HBCIUtilsInternal.getLocMsg("EXPONENT")+": "+HBCIUtils.data2hex(iniletter.getKeyExponent()));
                    System.out.println(HBCIUtilsInternal.getLocMsg("MODULUS")+": "+HBCIUtils.data2hex(iniletter.getKeyModulus()));
                    System.out.println(HBCIUtilsInternal.getLocMsg("HASH")+": "+HBCIUtils.data2hex(iniletter.getKeyHash()));
                    System.out.println(msg);
                    break;

                case HAVE_INST_MSG:
                    System.out.println(msg);
                    System.out.println(HBCIUtilsInternal.getLocMsg("CONTINUE"));
                    new BufferedReader(new InputStreamReader(System.in)).readLine();
                    break;

                case NEED_REMOVE_CHIPCARD:
                    System.out.println(msg);
                    break;

                case HAVE_CRC_ERROR:
                    System.out.println(msg);

                    int idx=retData.indexOf("|");
                    String blz=retData.substring(0,idx);
                    String number=retData.substring(idx+1);

                    System.out.print(HBCIUtilsInternal.getLocMsg("BLZ")+" ["+blz+"]: ");
                    System.out.flush();
                    String s=(new BufferedReader(new InputStreamReader(System.in))).readLine();
                    if (s.length()==0)
                        s=blz;
                    blz=s;

                    System.out.print(HBCIUtilsInternal.getLocMsg("ACCNUMBER")+" ["+number+"]: ");
                    System.out.flush();
                    s=(new BufferedReader(new InputStreamReader(System.in))).readLine();
                    if (s.length()==0)
                        s=number;
                    number=s;

                    retData.replace(0,retData.length(),blz+"|"+number);
                    break;

                case HAVE_ERROR:
                    System.out.println(msg);
                    System.out.print("<ENTER>=OK, \"ERR\"=ERROR: ");
                    System.out.flush();
                    retData.replace(0,retData.length(),
                                    (new BufferedReader(new InputStreamReader(System.in))).readLine());
                    break;
                    
                case NEED_SIZENTRY_SELECT:
                    StringTokenizer tok=new StringTokenizer(retData.toString(),"|");
                    while (tok.hasMoreTokens()) {
                        String entry=tok.nextToken();
                        StringTokenizer tok2=new StringTokenizer(entry,";");
                        
                        String tempblz;
                        System.out.println(tok2.nextToken()+": "+
                                           HBCIUtilsInternal.getLocMsg("BLZ")+"="+(tempblz=tok2.nextToken())+
                                           " ("+HBCIUtils.getNameForBLZ(tempblz)+") "+
                                           HBCIUtilsInternal.getLocMsg("USERID")+"="+tok2.nextToken());
                    }
                    System.out.print(HBCIUtilsInternal.getLocMsg("CALLB_SELECT_ENTRY")+": ");
                    System.out.flush();
                    retData.replace(0,retData.length(),
                                    (new BufferedReader(new InputStreamReader(System.in))).readLine());
                    break;

                case NEED_CONNECTION:
                case CLOSE_CONNECTION:
                    System.out.println(msg);
                    System.out.println(HBCIUtilsInternal.getLocMsg("CONTINUE"));
                    new BufferedReader(new InputStreamReader(System.in)).readLine();
                    break;

                default:
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_UNKNOWN",Integer.toString(reason)));
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
        }
    }

    /** Wird diese Methode von <em>HBCI4Java</em> aufgerufen, so wird der aktuelle
        Bearbeitungsschritt (mit evtl. vorhandenen zusätzlichen Informationen)
        auf STDOUT ausgegeben. */
    public synchronized void status(HBCIPassport passport,int statusTag,Object[] o)
    {
        switch (statusTag) {
            case STATUS_INST_BPD_INIT:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_DATA"));
                break;
            case STATUS_INST_BPD_INIT_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_DATA_DONE",passport.getBPDVersion()));
                break;
            case STATUS_INST_GET_KEYS:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_KEYS"));
                break;
            case STATUS_INST_GET_KEYS_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_KEYS_DONE"));
                break;
            case STATUS_SEND_KEYS:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_SEND_MY_KEYS"));
                break;
            case STATUS_SEND_KEYS_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_SEND_MY_KEYS_DONE"));
                System.out.println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_INIT_SYSID:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SYSID"));
                break;
            case STATUS_INIT_SYSID_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SYSID_DONE",o[1].toString()));
                System.out.println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_INIT_SIGID:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SIGID"));
                break;
            case STATUS_INIT_SIGID_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SIGID_DONE",o[1].toString()));
                System.out.println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_INIT_UPD:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_USER_DATA"));
                break;
            case STATUS_INIT_UPD_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_REC_USER_DATA_DONE",passport.getUPDVersion()));
                break;
            case STATUS_LOCK_KEYS:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK"));
                break;
            case STATUS_LOCK_KEYS_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK_DONE"));
                System.out.println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_DIALOG_INIT:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_INIT"));
                break;
            case STATUS_DIALOG_INIT_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_INIT_DONE",o[1]));
                System.out.println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_SEND_TASK:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_NEW_JOB",((HBCIJob)o[0]).getName()));
                break;
            case STATUS_SEND_TASK_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_JOB_DONE",((HBCIJob)o[0]).getName()));
                break;
            case STATUS_DIALOG_END:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_END"));
                break;
            case STATUS_DIALOG_END_DONE:
                System.out.println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_END_DONE"));
                System.out.println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_MSG_CREATE:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_CREATE",o[0].toString()));
                break;
            case STATUS_MSG_SIGN:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_SIGN"));
                break;
            case STATUS_MSG_CRYPT:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_CRYPT"));
                break;
            case STATUS_MSG_SEND:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_SEND"));
                break;
            case STATUS_MSG_RECV:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_RECV"));
                break;
            case STATUS_MSG_PARSE:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_PARSE",o[0].toString()+")"));
                break;
            case STATUS_MSG_DECRYPT:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_DECRYPT"));
                break;
            case STATUS_MSG_VERIFY:
                System.out.println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_VERIFY"));
                break;
            default:
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("STATUS_INVALID",Integer.toString(statusTag)));
        }
    }
}
