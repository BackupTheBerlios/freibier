
/*  $Id: HBCIHandler.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.lang.reflect.Constructor;
import java.security.KeyPair;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kapott.hbci.GV.GVTemplate;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIDialogStatus;
import org.kapott.hbci.status.HBCIExecStatus;

/** <p>Ein Handle f�r genau einen HBCI-Zugang. Diese Klasse stellt das Verbindungsglied
    zwischen der Anwendung und dem HBCI-Kernel dar. F�r jeden HBCI-Zugang, den
    die Anwendung benutzt, muss ein entsprechender HBCI-Handler angelegt werden.
    Darin sind folgende Daten zusammengefasst:</p>
    <ul>
      <li>Ein {@link org.kapott.hbci.passport.HBCIPassport}, welches die Nutzeridentifikationsdaten
          sowie die Zugangsdaten zum entsprechenden HBCI-Server enth�lt</li>
      <li>Die zu benutzende HBCI-Versionsnummer</li>
      <li>interne Daten zur Verwaltung der Dialoge bei der Kommunikation
          mit dem HBCI-Server</li>
    </ul>
    <p>Alle Anfragen der Anwendung an den HBCI-Kernel laufen �ber einen solchen
    Handler, womit gleichzeit eindeutig festgelegt ist, welche HBCI-Verbindung
    diese Anfrage betrifft.</p>
    <p>Die prinzipielle Benutzung eines Handlers sieht in etwa wiefolgt aus:
    <pre>
// ...
HBCIPassport passport=AbstractHBCIPassport.getInstance();
HBCIHandler handle=new HBCIHandler(passport.getHBCIVersion(),passport);

HBCIJob jobSaldo=handle.newJob("SaldoReq");       // n�chster Auftrag ist Saldenabfrage
jobSaldo.setParam("number","1234567890");         // Kontonummer f�r Saldenabfrage
handle.addJob(jobSaldo);

HBCIJob jobUeb=handle.newJob("Ueb");
jobUeb.setParam("src.number","1234567890");
jobUeb.setParam("dst.number","9876543210");
// ...
handle.addJob(jobUeb);

// ...

HBCIExecStatus status=handle.execute();

// Auswerten von status
// Auswerten der einzelnen job-Ergebnisse

handle.close();
</pre> */
public final class HBCIHandler
{
    public final static int REFRESH_BPD=1;
    public final static int REFRESH_UPD=2;
    
    private HBCIKernelImpl       kernel;
    private HBCIPassportInternal passport;
    private Map                  dialogs;
    
    /** Anlegen eines neuen HBCI-Handler-Objektes. Beim Anlegen wird
        �berpr�ft, ob f�r die angegebene HBCI-Version eine entsprechende
        Spezifikation verf�gbar ist. Au�erdem wird das �bergebene
        Passport �berpr�ft. Dabei werden - falls nicht vorhanden - die BPD und die UPD
        vom Kreditinstitut geholt. Bei Passports, die asymmetrische Verschl�sselungsverfahren
        benutzen (RDH), wird zus�tzlich �berpr�ft, ob alle ben�tigten Schl�ssel vorhanden
        sind. Gegebenenfalls werden diese aktualisiert.
        @param hbciversion zu benutzende HBCI-Version. g�ltige Werte sind:
               <ul>
                 <li><code>null</code> - es wird <em>die</em> HBCI-Version benutzt, die bei der
                     letzten Verwendung dieses Passports benutzt wurde</li>
                 <li>"<code>201</code>" f�r HBCI 2.01</li>
                 <li>"<code>210</code>" f�r HBCI 2.1</li>
                 <li>"<code>220</code>" f�r HBCI 2.2</li>
                 <li>"<code>plus</code>" f�r HBCI+</li>
                 <li>"<code>300</code>" f�r FinTS 3.0</li>
               </ul>
        @param passport das zu benutzende Passport. Dieses muss vorher mit
               {@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance()}
               erzeugt worden sein */
    public HBCIHandler(String hbciversion,HBCIPassport passport)
    {
        try {
            if (passport==null)
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_NULL"));
            
            if (hbciversion==null) {
                hbciversion=passport.getHBCIVersion();
            }
            if (hbciversion.length()==0)
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_NO_HBCIVERSION"));

            this.kernel=new HBCIKernelImpl(hbciversion);
            this.passport=(HBCIPassportInternal)passport;

            registerInstitute();
            registerUser();

            if (!passport.getHBCIVersion().equals(hbciversion)) {
                this.passport.setHBCIVersion(hbciversion);
                this.passport.saveChanges();
            }

            dialogs=new Hashtable();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_CREATE_HANDLE"),e);
        }
    }
    
    private void registerInstitute()
    {
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KRNL_REGINST"),HBCIUtils.LOG_DEBUG);
            HBCIInstitute inst=new HBCIInstitute(kernel,passport);
            inst.register();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_REG_INST"),ex);
        }
    }

    private void registerUser()
    {
        try {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_KRNL_REGUSER"),HBCIUtils.LOG_DEBUG);
            HBCIUser user=new HBCIUser(kernel,passport);
            user.register();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_REG_USER"),ex);
        }
    }

    /** <p>Schlie�en des Handlers. Diese Methode sollte immer dann aufgerufen werden,
        wenn die entsprechende HBCI-Verbindung nicht mehr ben�tigt wird. </p><p>
        Beim Schlie�en des Handlers wird das Passport ebenfalls geschlossen.
        Sowohl das Passport-Objekt als auch das Handler-Objekt k�nnen anschlie�end
        nicht mehr benutzt werden.</p> */
    public void close()
    {
        if (passport!=null) {
            try {
                passport.close();
            } catch (Exception e) {
                HBCIUtils.log(e);
            }
        }
        
        passport=null;
        kernel=null;
        dialogs=null;
    }
    
    /* gibt die zu verwendende Customer-Id zur�ck. Wenn keine angegeben wurde
     * (customerId==null), dann wird die derzeitige passport-customerid 
     * verwendet */
    private String fixUnspecifiedCustomerId(String customerId)
    {
        if (customerId==null) {
            customerId=passport.getCustomerId();
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_HANDLER_DEFCUSTID",customerId),HBCIUtils.LOG_DEBUG);
        }
        return customerId;
    }
    
    /* gibt ein Dialog-Objekt f�r eine bestimmte Kunden-ID zur�ck. Existiert f�r
     * die Kunden-ID noch kein Dialog-Objekt, so wird eines erzeugt */
    private HBCIDialog getDialogFor(String customerId)
    {
        HBCIDialog dialog=(HBCIDialog)dialogs.get(customerId);
        if (dialog==null) {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_HANDLER_NEWDIALOG",customerId),HBCIUtils.LOG_DEBUG);
            dialog=new HBCIDialog(kernel,passport);
            dialogs.put(customerId,dialog);
        }
        
        return dialog;
    }

    /** <p>Beginn einer neuen HBCI-Nachricht innerhalb eines Dialoges festlegen.
        Normalerweise muss diese Methode niemals manuell aufgerufen zu werden!</p>
        <p>Mit dieser Methode wird der HBCI-Kernel gezwungen, eine neue HBCI-Nachricht 
        anzulegen, in die alle nachfolgenden Gesch�ftsvorf�lle aufgenommen werden. 
        Die <code>customerId</code> legt fest, f�r welchen Dialog die neue Nachricht
        erzeugt werden soll. F�r eine genauere Beschreibung von Dialogen und
        <code>customerid</code>s siehe {@link #addJob(String,HBCIJob)}. </p>
        @param customerId die Kunden-ID, f�r deren Dialog eine neue Nachricht
        begonnen werden soll */
    public void newMsg(String customerId)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_HANDLER_NEWMSG",customerId),HBCIUtils.LOG_DEBUG);
        getDialogFor(fixUnspecifiedCustomerId(customerId)).newMsg();
    }
    
    /** Erzwingen einer neuen Nachricht im Dialog f�r die aktuelle Kunden-ID.
        Diese Methode arbeitet analog zu {@link #newMsg(String)}, nur dass hier
        die <code>customerid</code> mit der Kunden-ID vorbelegt ist, wie sie
        im aktuellen Passport gespeichert ist. Siehe dazu auch
        {@link #addJob(String,org.kapott.hbci.GV.HBCIJob)}.*/
    public void newMsg()
    {
        newMsg(null);
    }
    
    /** <p>Erzeugen eines neuen Highlevel-HBCI-Jobs. Diese Methode gibt ein neues Job-Objekt zur�ck. Dieses
        Objekt wird allerdings noch <em>nicht</em> zum HBCI-Dialog hinzugef�gt. Statt dessen
        m�ssen erst alle zur Beschreibung des jeweiligen Jobs ben�tigten Parameter mit
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)} gesetzt werden.
        Anschlie�end kann der Job mit {@link #addJob(String,org.kapott.hbci.GV.HBCIJob)} zum
        HBCI-Dialog hinzugef�gt werden.</p>
        <p>Eine Beschreibung aller unterst�tzten Gesch�ftsvorf�lle befindet sich
        im Package <code>org.kapott.hbci.GV</code>.</p>
        @param jobname der Name des Jobs, der erzeugt werden soll. G�ltige
               Job-Namen sowie die ben�tigten Parameter sind in der Beschreibung des Packages
               <code>org.kapott.hbci.GV</code> zu finden.
        @return ein Job-Objekt, f�r das die entsprechenden Job-Parameter gesetzt werden m�ssen und
                welches anschlie�end zum HBCI-Dialog hinzugef�gt werden kann. */
    public HBCIJob newJob(String jobname)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_HANDLER_NEWJOB",jobname),HBCIUtils.LOG_DEBUG);
        
        if (jobname==null || jobname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        HBCIJobImpl ret=null;

        try {
            Class cl=Class.forName("org.kapott.hbci.GV.GV"+jobname);
            Constructor cons=cl.getConstructor(new Class[] {HBCIPassportInternal.class});
            ret=(HBCIJobImpl)cons.newInstance(new Object[] {passport});
        } catch (Exception e) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_JOB_CREATE_ERR",jobname);
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCreateJobErrors",msg))
                throw new HBCI_Exception(msg,e);
        }
        
        if (ret!=null)
            ret.setMsgGen(kernel.getMsgGen());
        return ret;
    }
    
    /** Erzeugt ein neues Lowlevel-Job-Objekt. F�r eine Beschreibung des Unterschiedes
        zwischen High- und Lowlevel-Definition von Jobs siehe Package <code>org.kapott.hbci.GV</code>.
        @param gvname der Lowlevel-Name des zu erzeugenden Jobs
        @return ein neues Job-Objekt, f�r das erst alle ben�tigten Lowlevel-Parameter gesetzt
                werden m�ssen und das anschlie�end zum HBCI-Dialog hinzugef�gt werden kann */
    public HBCIJob newLowlevelJob(String gvname)
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_HANDLER_NEWLLJOB",gvname),HBCIUtils.LOG_DEBUG);

        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));

        HBCIJobImpl ret=new GVTemplate(gvname,passport);
        ret.setMsgGen(kernel.getMsgGen());
        return ret;
    }

    /** <p>Hinzuf�gen eines Jobs zu einem Dialog. Nachdem alle
        Jobparameter mit 
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)}
        gesetzt wurden, kann der komplett spezifizierte Job mit dieser Methode
        zur Auftragsliste eines Dialoges hinzugef�gt werden.</p>
        <p>Die <code>customerId</code> gibt an, unter welcher Kunden-ID dieser Job
        ausgef�hrt werden soll. Existiert f�r eine HBCI-Nutzerkennung (ein Passport)
        nur genau eine Kunden-ID (wie das i.d.R. der Fall ist), so kann der
        <code>customerId</code>-Parameter weggelassen werden - <em>HBCI4Java</em>
        verwendet dann automatisch die richtige Kunden-ID (als Kunden-ID wird in diesem
        Fall der Wert von {@link HBCIPassport#getCustomerId()} verwendet). Gibt es aber mehrere
        g�ltige Kunden-IDs f�r einen HBCI-Zugang, so muss die Kunden-ID,
        die f�r diesen Job verwendet werden soll, mit angegeben werden.</p>
        <p>Jeder Auftrag (=Job) ist i.d.R. an ein bestimmtes Konto des Auftraggebers
        gebunden (�berweisung: das Belastungskonto; Saldenabfrage: das abzufragende
        Konto usw.). Als Kunden-ID f�r einen Auftrag muss <em>die</em> Kunden-ID
        angegeben werden, die f�r dieses Konto verf�gungsberechtigt ist.</p>
        <p>I.d.R. liefert eine Bank Informationen �ber alle Konten, auf die
        via HBCI zugegriffen werden kann. Ist das der Fall, so kann die Menge
        dieser Konten mit {@link HBCIPassport#getAccounts()} ermittelt werden.
        In jedem zur�ckgemeldeten {@link org.kapott.hbci.structures.Konto}-Objekt 
        ist im Feld <code>customerid</code> vermerkt, welche Kunden-ID f�r 
        dieses Konto verf�gungsberechtigt ist. Diese Kunden-ID m�sste dann also 
        beim Hinzuf�gen eines Auftrages angegeben werden, welcher das jeweilige 
        Konto betrifft.</p>
        <p>Liefert eine Bank diese Informationen nicht, so hat die Anwendung selbst
        eine Kontenverwaltung zu implementieren, bei der jedem Nutzerkonto eine
        zu verwendende Kunden-ID zugeordnet ist.</p>
        <p>Ein HBCI-Dialog kann aus beliebig vielen HBCI-Nachrichten bestehen. <em>HBCI4Java</em> versucht zun�chst,
        alle Jobs in einer einzigen Nachricht unterzubringen. Kann ein Job nicht mehr zur aktuellen
        Nachricht hinzugef�gt werden (weil sonst bestimmte vorgegebene Bedingungen nicht eingehalten
        werden), so legt <em>HBCI4Java</em> automatisch eine neue Nachricht an, zu der der Job schlie�lich
        hinzugef�gt wird. Beim Ausf�hren des HBCI-Dialoges (siehe {@link #execute()}) werden dann
        nat�rlich <em>alle</em> erzeugten Nachrichten zum HBCI-Server gesandt.</p> 
        <p>Der HBCI-Kernel bestimmt also automatisch, ob ein Auftrag noch mit in die aktuelle Nachricht
        aufgenommen werden kann, oder ob eine separate Nachricht erzeugt werden muss. Der manuelle
        Aufruf von {@link #newMsg()} ist deshalb im Prinzip niemals notwendig, es sei denn,
        es soll aus anderen Gr�nden eine neue Nachricht begonnen werden.</p>
        @param customerId die Kunden-ID, zu deren Dialog der Auftrag hinzugef�gt werden soll 
        @param job ein Job-Objekt, welches mit {@link #newJob(String)} oder
               {@link #newLowlevelJob(String)} erzeugt und mit
               {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)}
               parametrisiert wurde. */
    public void addJob(String customerId,HBCIJob job)
    {
        customerId=fixUnspecifiedCustomerId(customerId);
        
        HBCIDialog dialog=null;
        try {
            dialog=getDialogFor(customerId);
            dialog.addTask((HBCIJobImpl)job);
        } finally {
            // wenn beim hinzuf�gen des jobs ein fehler auftrat, und wenn der
            // entsprechende dialog extra f�r diesen fehlerhaften job erzeugt
            // wurde, dann kann der (leere) dialog auch wieder aus der liste
            // auszuf�hrender dialoge entfernt werden
            
            if (dialog!=null) {
                if (dialog.getAllTasks().size()==0) {
                    HBCIUtils.log("*** removing empty dialog for customerid "+customerId+" from list of dialogs",HBCIUtils.LOG_DEBUG);
                    dialogs.remove(customerId);
                }
            }
        }
    }
    
    /** <p>Hinzuf�gen eines Jobs zu einem Dialog. Diese Methode arbeitet analog zu 
        {@link #addJob(String,org.kapott.hbci.GV.HBCIJob)}, nur dass hier
        die <code>customerid</code> mit der Kunden-ID vorbelegt ist, wie sie
        im aktuellen Passport gespeichert ist.</p>
        @param job der hinzuzuf�gende Auftrag */
    public void addJob(HBCIJob job)
    {
        addJob(null,job);
    }

    /** <p>Ausf�hren aller bisher erzeugten Auftr�ge. Diese Methode veranlasst den HBCI-Kernel,
        die Auftr�ge, die durch die Aufrufe der Methode 
        {@link #addJob(String,org.kapott.hbci.GV.HBCIJob)}
        zur Auftragsliste hinzugef�gt wurden, auszuf�hren. </p>
        <p>Beim Hinzuf�gen der Auftr�ge zur Auftragsqueue (mit {@link #addJob(org.kapott.hbci.GV.HBCIJob)}
        oder {@link #addJob(String,org.kapott.hbci.GV.HBCIJob)}) wird implizit oder explizit
        eine Kunden-ID mit angegeben, unter der der jeweilige Auftrag ausgef�hrt werden soll.
        In den meisten F�llen hat ein Benutzer nur eine einzige Kunden-ID, so dass die
        Angabe entfallen kann, es wird dann automatisch die richtige verwendet. Werden aber
        mehrere Auftr�ge via <code>addJob()</code> zur Auftragsqueue hinzugef�gt, und sind
        diese Auftr�ge unter teilweise unterschiedlichen Kunden-IDs auszuf�hren, dann wird
        f�r jede verwendete Kunden-ID ein separater HBCI-Dialog erzeugt und ausgef�hrt.
        Das �u�ert sich dann also darin, dass beim Aufrufen der Methode {@link #execute()}
        u.U. mehrere HBCI-Dialog mit der Bank gef�hrt werden, und zwar je einer f�r jede Kunden-ID,
        f�r die wenigstens ein Auftrag existiert. Innerhalb eines HBCI-Dialoges werden alle
        auszuf�hrenden Auftr�ge in m�glichst wenige HBCI-Nachrichten verpackt.</p>
        <p>Dazu wird eine Reihe von HBCI-Nachrichten mit dem HBCI-Server der Bank ausgetauscht. Die
        Menge der dazu verwendeten HBCI-Nachrichten kann dabei nur bedingt beeinflusst werden, da <em>HBCI4Java</em> 
        u.U. selbstst�ndig Nachrichten erzeugt, u.a. wenn ein Auftrag nicht mehr mit in eine Nachricht
        aufgenommen werden konnte, oder wenn eine Antwortnachricht nicht alle verf�gbaren Daten
        zur�ckgegeben hat, so dass <em>HBCI4Java</em> mit einer oder mehreren weiteren Nachrichten den Rest
        der Daten abholt. </p>
        <p>Nach dem Nachrichtenaustausch wird ein Status-Objekt zur�ckgegeben,
        welches zur Auswertung aller ausgef�hrten Dialoge benutzt werden kann.</p>
        @return ein Status-Objekt, anhand dessen der Erfolg oder das Fehlschlagen
                der Dialoge festgestellt werden kann. */
    public HBCIExecStatus execute()
    {
        String origCustomerId=passport.getCustomerId();
        try {
            HBCIExecStatus ret=new HBCIExecStatus();
            
            while (!dialogs.isEmpty()) {
                String customerid=(String)dialogs.keySet().iterator().next();
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("INFO_HANDLER_EXEC",customerid),HBCIUtils.LOG_INFO);
                passport.setCustomerId(customerid);
                
                try {
                    HBCIDialog dialog=getDialogFor(customerid);
                    HBCIDialogStatus dialogStatus=dialog.doIt();
                    ret.addDialogStatus(customerid,dialogStatus);
                } catch (Exception e) {
                    ret.addException(customerid,e);
                } finally {
                    dialogs.remove(customerid);
                }
            }
            return ret;
        } finally {
            reset();
            passport.setCustomerId(origCustomerId);
            passport.closeComm();
        }
    }
    
    /** <p>Sperren der Nutzerschl�ssel. Das ist nur dann sinnvoll, wenn zwei Bedinungen erf�llt sind:</p>
        <ol>
          <li>Das verwendete Passport erlaubt die Sperrung der Schl�ssel des Nutzers (nur RDH)</li>
          <li>Im verwendeten Passport sind auch tats�chlich bereits Nutzerschl�ssel hinterlegt.</li>
        </ol>
        <p>Ist mindestens eine der beiden Bedingungen nicht erf�llt, so wird diese Methode mit einer
        Exception abgebrochen.</p>
        <p>Nach dem erfolgreichen Aufruf dieser Methode muss dieses HBCIHandler-Objekt mit
        {@link #close()} geschlossen werden. Anschlie�end muss mit dem gleichen Passport-Objekt
        ein neues HBCIHandler-Objekt angelegt werden, damit das Passport neu initialisiert wird. Bei
        dieser Neu-Initialisierung werden neue Nutzerschl�ssel f�r das Passport generiert.
<pre>
// ...
hbciHandle.lockKeys();
hbciHandle.close();

hbciHandle=new HBCIHandle(hbciversion,passport);
// ...
</pre>
        Um die Nutzerschl�ssel eines Passport nur zu <em>�ndern</em>, kann die Methode
        {@link #setKeys(java.security.KeyPair,java.security.KeyPair)} 
        oder {@link #newKeys()} aufgerufen werden.</p>
        <p>Ab Version 2.4.0 von <em>HBCI4Java</em> muss der HBCIHandler nach dem
        Schl�sselsperren nicht mehr geschlossen werden. Statt dessen k�nnen direkt
        nach der Schl�sselsperrung neue Schl�ssel erzeugt oder manuell gesetzt
        werden (mit den Methoden {@link #setKeys(java.security.KeyPair,java.security.KeyPair)}
        bzw. {@link #newKeys()}. </p>
        <p>In jedem Fall muss f�r die neuen Schl�ssel, die nach einer Schl�sselsperrung
        erzeugt werden, ein neuer INI-Brief generiert und an die Bank versandt werden.</p>*/
    public void lockKeys()
    {
        try {
            new HBCIUser(kernel,passport).lockKeys();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),ex);
        }
    }
    
    /** <p>Erzeugen neuer kryptografischer Schl�ssel f�r den Nutzer.
        Mit dieser Methode wird f�r den Nutzer sowohl ein neues Signier- als auch ein
        neues Chiffrierschl�sselpaar erzeugt. Die neuen Schl�sseldaten werden anschlie�end
        automatisch an die Bank �bermittelt. Sofern diese Aktion erfolgreich verl�uft,
        werden die neuen Schl�ssel in der Passport-Datei (Schl�sseldatei) gespeichert.</p>
        <p><b>ACHTUNG!</b> Vor dieser Aktion sollte unbedingt ein Backup der aktuellen Schl�sseldatei
        durchgef�hrt werden. Bei ung�nstigen Konstellationen von Fehlermeldungen seitens
        des Kreditinstitutes kann es n�mlich passieren, dass die neuen Schl�ssel trotz
        eingegangener Fehlermeldung gespeichert werden, dann w�ren aber die alten (noch g�ltigen)
        Schl�ssel �berschrieben.</p>
        <p><b>ACHTUNG!</b> In noch ung�nstigeren F�llen kann es auch vorkommen, dass neue Schl�ssel
        generiert und erfolgreich an die Bank �bermittelt werden, die neuen Schl�ssel aber nicht
        in der Schl�sseldatei gespeichert werden. Das ist insofern der ung�nstigste Fall, da
        die Bank dann schon die neuen Schl�ssel kennt, in der Passport-Datei aber noch die
        alten Schl�ssel enthalten sind und die soeben generierten neuen Schl�ssel "aus Versehen"
        weggeworfen wurden.</p> */
    public void newKeys()
    {
        try {
            new HBCIUser(kernel,passport).generateNewKeys();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GENKEYS_ERR"),ex);
        }
    }
    
    /** <p>Setzen der Nutzerschl�ssel auf vorgegebene Daten.
        Mit dieser Methode wird f�r den Nutzer sowohl ein neues Signier- als auch ein
        neues Chiffrierschl�sselpaar gesetzt. Die neuen Schl�sseldaten werden anschlie�end
        automatisch an die Bank �bermittelt. Sofern diese Aktion erfolgreich verl�uft,
        werden die neuen Schl�ssel in der Passport-Datei (Schl�sseldatei) gespeichert.</p>
        <p><b>ACHTUNG!</b> Vor dieser Aktion sollte unbedingt ein Backup der aktuellen Schl�sseldatei
        durchgef�hrt werden. Bei ung�nstigen Konstellationen von Fehlermeldungen seitens
        des Kreditinstitutes kann es n�mlich passieren, dass die neuen Schl�ssel trotz
        eingegangener Fehlermeldung gespeichert werden, dann w�ren aber die alten (noch g�ltigen)
        Schl�ssel �berschrieben.</p> */
    public void setKeys(KeyPair sigKey,KeyPair encKey)
    {
        try {
            new HBCIUser(kernel,passport).manuallySetNewKeys(sigKey,encKey);
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SETKEYS_ERR"),ex);
        }
    }

    /** Zur�cksetzen des Handlers auf den Ausgangszustand. Diese Methode kann
     aufgerufen werden, wenn alle bisher hinzugef�gten Nachrichten und
     Auftr�ge wieder entfernt werden sollen. Nach dem Ausf�hren eines
     Dialoges mit {@link #execute()} wird diese Methode 
     automatisch aufgerufen. */
    public void reset()
    {
        dialogs.clear();
    }
    
    /** Gibt das Passport zur�ck, welches in diesem Handle benutzt wird.
        @return Passport-Objekt, mit dem dieses Handle erzeugt wurde */
    public HBCIPassport getPassport()
    {
        return passport;
    }
    
    /** Gibt das HBCI-Kernel-Objekt zur�ck, welches von diesem HBCI-Handler
     * benutzt wird. Das HBCI-Kernel-Objekt kann u.a. benutzt werden, um
     * alle f�r die aktuellen HBCI-Version (siehe {@link #getHBCIVersion()}) 
     * implementierten Gesch�ftsvorf�lle abzufragen. 
     * @return HBCI-Kernel-Objekt, mit dem der HBCI-Handler arbeitet */
    public HBCIKernel getKernel()
    {
        return kernel;
    }
    
    /** Gibt die HBCI-Versionsnummer zur�ck, f�r die der aktuelle HBCIHandler
     * konfiguriert ist.
     * @return HBCI-Versionsnummer, mit welcher dieses Handler-Objekt arbeitet */
    public String getHBCIVersion()
    {
        return kernel.getHBCIVersion();
    }
    
    /** <p>Gibt die Namen aller vom aktuellen HBCI-Zugang (d.h. Passport) 
     * unterst�tzten Lowlevel-Jobs zur�ck. Alle hier zur�ckgegebenen Job-Namen 
     * k�nnen als Argument beim Aufruf der Methode 
     * {@link #newLowlevelJob(String)} benutzt werden.</p>
     * <p>In dem zur�ckgegebenen Properties-Objekt enth�lt jeder Eintrag als
     * Key den Lowlevel-Job-Namen; als Value wird die Versionsnummer des
     * jeweiligen Gesch�ftsvorfalls angegeben, die von <em>HBCI4Java</em> mit dem
     * aktuellen Passport und der aktuell eingestellten HBCI-Version
     * benutzt werden wird.</p>
     * <p><em>(Prinzipiell unterst�tzt <em>HBCI4Java</em> f�r jeden
     * Gesch�ftsvorfall mehrere GV-Versionen. Auch eine Bank bietet i.d.R. f�r
     * jeden GV mehrere Versionen an. Wird mit <em>HBCI4Java</em> ein HBCI-Job
     * erzeugt, so verwendet <em>HBCI4Java</em> immer automatisch die h�chste
     * von der Bank unterst�tzte GV-Versionsnummer. Diese Information ist
     * f�r den Anwendungsentwickler kaum von Bedeutung und dient haupts�chlich 
     * zu Debugging-Zwecken.)</em></p>
     * <p>Zum Unterschied zwischen High- und Lowlevel-Jobs siehe die
     * Beschreibung im Package <code>org.kapott.hbci.GV</code>.</p>
     * @return Sammlung aller vom aktuellen Passport unterst�tzten HBCI-
     * Gesch�ftsvorfallnamen (Lowlevel) mit der jeweils von <em>HBCI4Java</em>
     * verwendeten GV-Versionsnummer.*/
    public Properties getSupportedLowlevelJobs()
    {
        Hashtable  allValidJobNames=kernel.getAllLowlevelJobs();
        Properties paramSegments=passport.getParamSegmentNames();
        Properties result=new Properties();
        
        for (Enumeration e=paramSegments.propertyNames();e.hasMoreElements();) {
            String segName=(String)e.nextElement();
            
            // �berpr�fen, ob parameter-segment tats�chlich zu einem GV geh�rt
            // gilt z.b. f�r "PinTan" nicht
            if (allValidJobNames.containsKey(segName))
                result.put(segName,paramSegments.getProperty(segName));
        }
        
        return result;
    }
    
    /** <p>Gibt alle Parameter zur�ck, die f�r einen Lowlevel-Job gesetzt
        werden k�nnen. Wird ein Job mit {@link #newLowlevelJob(String)}
        erzeugt, so kann der gleiche <code>gvname</code> als Argument dieser
        Methode verwendet werden, um eine Liste aller Parameter zu erhalten, die
        f�r diesen Job durch Aufrufe der Methode 
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)}
        gesetzt werden k�nnen bzw. m�ssen.</p>
        <p>Aus der zur�ckgegebenen Liste ist nicht ersichtlich, ob ein bestimmter
        Parameter optional ist oder gesetzt werden <em>muss</em>. Das kann aber
        durch Benutzen des Tools {@link org.kapott.hbci.tools.ShowLowlevelGVs}
        ermittelt werden.</p>
        <p>Jeder Eintrag der zur�ckgegebenen Liste enth�lt einen String, welcher als
        erster Parameter f�r den Aufruf von <code>HBCIJob.setParam()</code> benutzt
        werden kann - vorausgesetzt, der entsprechende Job wurde mit
        {@link #newLowlevelJob(String)} erzeugt. </p>
        <p>Diese Methode verwendet intern die Methode 
        {@link HBCIKernel#getLowlevelJobParameterNames(String, String)}. 
        Unterschied ist, dass diese Methode zum einen �berpr�ft, ob  der 
        angegebene Lowlevel-Job �berhaupt vom aktuellen Passport unterst�tzt wird.
        Au�erdem wird automatisch die richtige Versionsnummer an
        {@link HBCIKernel#getLowlevelJobParameterNames(String, String)} �bergeben
        (n�mlich die Versionsnummer, die <em>HBCI4Java</em> auch beim Anlegen
        eines Jobs via {@link #newLowlevelJob(String)} verwenden wird).</p>
        <p>Zur Beschreibung von High- und Lowlevel-Jobs siehe auch die Dokumentation
        im Package <code>org.kapott.hbci.GV</code>.</p>
        @param gvname der Lowlevel-Jobname, f�r den eine Liste der Job-Parameter
        ermittelt werden soll
        @return eine Liste aller Parameter-Bezeichnungen, die in der Methode
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)}
        benutzt werden k�nnen */
    public List getLowlevelJobParameterNames(String gvname)
    {
        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException("*** gvname must not be empty");
        
        String version=getSupportedLowlevelJobs().getProperty(gvname);
        if (version==null)
            throw new HBCI_Exception("*** lowlevel job "+gvname+" not supported");
        
        return kernel.getLowlevelJobParameterNames(gvname,version);
    }
    
    /** <p>Gibt eine Liste mit Strings zur�ck, welche Bezeichnungen f�r die einzelnen R�ckgabedaten
        eines Lowlevel-Jobs darstellen. Jedem {@link org.kapott.hbci.GV.HBCIJob} ist ein
        Result-Objekt zugeordnet, welches die R�ckgabedaten und Statusinformationen zu dem jeweiligen
        Job enth�lt (kann mit {@link org.kapott.hbci.GV.HBCIJob#getJobResult()}
        ermittelt werden). Bei den meisten Highlevel-Jobs handelt es sich dabei um bereits aufbereitete
        Daten (Kontoausz�ge werden z.B. nicht in dem urspr�nglichen SWIFT-Format zur�ckgegeben, sondern
        bereits als fertig geparste Buchungseintr�ge).</p>
        <p>Bei Lowlevel-Jobs gibt es diese Aufbereitung der Daten nicht. Statt dessen m�ssen die Daten
        manuell aus der Antwortnachricht extrahiert und interpretiert werden. Die einzelnen Datenelemente
        der Antwortnachricht werden in einem Properties-Objekt bereitgestellt 
        ({@link org.kapott.hbci.GV_Result.HBCIJobResult#getResultData()}). Jeder Eintrag
        darin enth�lt den Namen und den Wert eines Datenelementes aus der Antwortnachricht.</p>
        <p>Die Methode <code>getLowlevelJobResultNames()</code> gibt nun alle g�ltigen Namen zur�ck,
        f�r welche in dem Result-Objekt Daten gespeichert sein k�nnen. Ob f�r ein Datenelement tats�chlich
        ein Wert in dem Result-Objekt existiert, wird damit nicht bestimmt, da einzelne Datenelemente
        optional sind.</p>
        <p>Diese Methode verwendet intern die Methode
        {@link HBCIKernel#getLowlevelJobResultNames(String, String)}. 
        Unterschied ist, dass diese Methode zum einen �berpr�ft, ob  der 
        angegebene Lowlevel-Job �berhaupt vom aktuellen Passport unterst�tzt wird.
        Au�erdem wird automatisch die richtige Versionsnummer an
        {@link HBCIKernel#getLowlevelJobResultNames(String, String)} �bergeben
        (n�mlich die Versionsnummer, die <em>HBCI4Java</em> auch beim Anlegen
        eines Jobs via {@link #newLowlevelJob(String)} verwenden wird).</p>
        <p>Mit dem Tool {@link org.kapott.hbci.tools.ShowLowlevelGVRs} kann offline eine
        Liste aller Job-Result-Datenelemente erzeugt werden.</p>
        <p>Zur Beschreibung von High- und Lowlevel-Jobs siehe auch die Dokumentation
        im Package <code>org.kapott.hbci.GV</code>.</p>
        @param gvname Lowlevelname des Gesch�ftsvorfalls, f�r den die Namen der R�ckgabedaten ben�tigt werden.
        @return Liste aller m�glichen Property-Keys, f�r die im Result-Objekt eines Lowlevel-Jobs
        Werte vorhanden sein k�nnten */
    public List getLowlevelJobResultNames(String gvname)
    {
        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException("*** gvname must not be empty");
        
        String version=getSupportedLowlevelJobs().getProperty(gvname);
        if (version==null)
            throw new HBCI_Exception("*** lowlevel job "+gvname+" not supported");
        
        return kernel.getLowlevelJobResultNames(gvname,version);
    }
    
    /** <p>Gibt f�r einen Job alle bekannten Einschr�nkungen zur�ck, die bei
     der Ausf�hrung des jeweiligen Jobs zu beachten sind. Diese Daten werden aus den
     Bankparameterdaten des aktuellen Passports extrahiert. Sie k�nnen von einer HBCI-Anwendung
     benutzt werden, um gleich entsprechende Restriktionen bei der Eingabe von
     Gesch�ftsvorfalldaten zu erzwingen (z.B. die maximale Anzahl von Verwendungszweckzeilen,
     ob das �ndern von terminierten �berweisungen erlaubt ist usw.).</p>
     <p>Die einzelnen Eintr�ge des zur�ckgegebenen Properties-Objektes enthalten als Key die
     Bezeichnung einer Restriktion (z.B. "<code>maxusage</code>"), als Value wird der
     entsprechende Wert eingestellt. Die Bedeutung der einzelnen Restriktionen ist zur Zeit
     nur der HBCI-Spezifikation zu entnehmen. In sp�teren Programmversionen werden entsprechende
     Dokumentationen zur internen HBCI-Beschreibung hinzugef�gt, so dass daf�r eine Abfrageschnittstelle
     implementiert werden kann.</p>
     <p>I.d.R. werden mehrere Versionen eines Gesch�ftsvorfalles von der Bank
     angeboten. Diese Methode ermittelt automatisch die "richtige" Versionsnummer
     f�r die Ermittlung der GV-Restriktionen aus den BPD (und zwar die selbe,
     die <em>HBCI4Java</em> beim Erzeugen eines Jobs benutzt). </p>
     <p>Siehe dazu auch {@link HBCIJob#getJobRestrictions()}.</p>
     @param gvname Lowlevel-Name des Gesch�ftsvorfalles, f�r den die Restriktionen
     ermittelt werden sollen
     @return Properties-Objekt mit den einzelnen Restriktionen */
    public Properties getLowlevelJobRestrictions(String gvname)
    {
        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException("*** gvname must not be empty");
        
        String version=getSupportedLowlevelJobs().getProperty(gvname);
        if (version==null)
            throw new HBCI_Exception("*** lowlevel job "+gvname+" not supported");
        
        return passport.getJobRestrictions(gvname,version);
    }

    /** <p>�berpr�fen, ein bestimmter Highlevel-Job von der Bank angeboten
        wird. Diese Methode kann benutzt werden, um <em>vor</em> dem Erzeugen eines
        {@link org.kapott.hbci.GV.HBCIJob}-Objektes zu �berpr�fen, ob
        der gew�nschte Job �berhaupt von der Bank angeboten wird. Ist das 
        nicht der Fall, so w�rde der Aufruf von 
        {@link org.kapott.hbci.manager.HBCIHandler#newJob(String)} 
        zu einer Exception f�hren.</p>
        <p>Eine Liste aller zur Zeit verf�gbaren Highlevel-Jobnamen ist in der Paketbeschreibung
        des Packages <code>org.kapott.hbci.GV</code> zu finden. Wird hier nach einem Highlevel-Jobnamen
        gefragt, der nicht in dieser Liste enthalten ist, so wird eine Exception geworfen.</p>
        <p>Mit dieser Methode k�nnen nur Highlevel-Jobs �berpr�ft werden. Zum �berpr�fen,
        ob ein bestimmter Lowlevel-Job unterst�tzt wird, ist die Methode
        {@link HBCIHandler#getSupportedLowlevelJobs()}
        zu verwenden.</p>
        @param jobnameHL der Highlevel-Name des Jobs, dessen Unterst�tzung �berpr�ft werden soll
        @return <code>true</code>, wenn dieser Job von der Bank unterst�tzt wird und
        mit <em>HBCI4Java</em> verwendet werden kann; ansonsten <code>false</code> */ 
    public boolean isSupported(String jobnameHL)
    {
        if (jobnameHL==null || jobnameHL.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        try {
            Class cl=Class.forName("org.kapott.hbci.GV.GV"+jobnameHL);
            String lowlevelName=(String)cl.getMethod("getLowlevelName",null).invoke(null,null);
            return getSupportedLowlevelJobs().keySet().contains(lowlevelName);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_HANDLER_HLCHECKERR",jobnameHL),e);
        }
    }
    
    /** Abholen der BPD bzw. UPD erzwingen. Beim Aufruf dieser Methode wird
     * automatisch ein HBCI-Dialog ausgef�hrt, der je nach Wert von <code>selectX</code>
     * die BPD und/oder UPD erneut abholt. Alle bis zu diesem Zeitpunkt erzeugten
     * ({@link #addJob(HBCIJob)}) und noch nicht ausgef�hrten Jobs werden dabei 
     * wieder aus der Job-Schlange entfernt. 
     * @param selectX kann aus einer Kombination (Addition) der Werte
     * {@link #REFRESH_BPD} und {@link #REFRESH_UPD} bestehen
     * @return Status-Objekt, welches Informationen �ber den ausgef�hrten 
     * HBCI-Dialog enth�lt */ 
    public HBCIDialogStatus refreshXPD(int selectX) 
    {
        if ((selectX & REFRESH_BPD)!=0) {
            passport.clearBPD();
        }
        if ((selectX & REFRESH_UPD)!=0) {
            passport.clearUPD();
        }

        reset();
        getDialogFor(passport.getCustomerId());
        return (HBCIDialogStatus)execute().getDialogStatusList().get(0);
    }
}
