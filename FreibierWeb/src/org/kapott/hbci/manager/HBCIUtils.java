
/*  $Id: HBCIUtils.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.security.Security;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.security.HBCIProvider;

/** <p>Hilfsklasse für diverse Tools. Diese Klasse definiert nur statische
    Methoden und Konstanten. Sie kann nicht instanziiert werden. </p>
    <p>Die wichtigsten Methoden dieser Klasse sind die Methoden zum Initialisieren des HBCI-Kernel
    ({@link #init(ClassLoader,String,org.kapott.hbci.callback.HBCICallback)}) sowie zum 
    Setzen von HBCI-Kernel-Parametern ({@link #setParam(String,String)}).</p>
    <p>Kernel-Parameter können zu jedem beliebigen Zeitpunkt der Laufzeit einer Anwendung gesetzt werden.
    Das Setzen eines Kernel-Parameters geschieht mit der Methode <code>setParam()</code>. Dieser Methode
    werden der Name eines Kernel-Parameters sowie der neue Wert für diesen Parameter übergeben. Alternativ
    bzw. in Verbindung mit dieser Variante können diese Parameter in einer Datei abgelegt werden, die
    beim Initialiseren des HBCI-Kernels eingelesen wird (via <code>Properties.load()</code>).
    Folgende Kernel-Parameter werden zur Zeit von verschiedenen Subsystemen des HBCI-Kernels ausgewertet:</p>
    <ul>
      <li><code>client.product.name</code> und <code>client.product.version</code>
          <p>Diese beiden Parameter identifizieren die HBCI-Anwendung. Diese 
          Daten werden von einigen HBCI-Servern ausgewertet, um bestimmte 
          HBCI-Anwendungen besonders zu unterstützen. Werden diese 
          Parameter nicht explizit belegt, so erhalten sie die 
          Standardwerte "HBCI4Java" und "2.4". Es wird empfohlen, diese Werte 
          nicht zu ändern.</p></li>
      <li><code>client.passport.DDV.path</code> (für DDV-Passports)
          <p>Hier wird eingestellt, wo die Datei 
          mit den Zusatzdaten ("Hilfsmedium") gespeichert werden soll. Der 
          Dateiname für das Hilfsmedium setzt sich zusammen aus dem Wert 
          dieses Parameters sowie der Seriennummer der HBCI-Chipkarte. 
          Ein Wert von "<code>/home/hbci/passports/</code>" führt also zur Speicherung 
          der Dateien im Verzeichnis <code>/home/hbci/passports</code>, wobei der 
          Dateiname nur aus der Seriennummer der Chipkarte besteht ("/" am 
          Ende beachten!). Ein Wert von "<code>/home/hbci/passports/card-</code>" führt 
          ebenfalls zur Speicherung der Dateien im Verzeichnis 
          <code>/home/hbci/passports</code>, allerdings bestehen die Dateinamen jetzt 
          aus dem Prefix card- sowie der Seriennummer der Chipkarte.</p>
          <p>In der Regel wird hier nur eine Pfadangabe verwendet werden, 
          dabei darf aber auf keinen Fall der Slash (oder Backslash unter 
          Windows) vergessen werden, da der Dateiname aus einer simplen 
          Aneinanderkettung von Parameterwert und Seriennummer besteht.</p></li>
      <li><code>client.passport.DDV.libname.ddv</code> (für DDV-Passports)
          <p>Hier wird der vollständige 
          Dateiname (also mit Pfadangabe) der shared library (dynamisch 
          ladbaren Bibliothek) angegeben, welche als Bindeglied zwischen 
          Java und der CTAPI-Bibliothek für den Chipkartenleser fungiert. 
          Diese Bibliothek wird bereits mit dem <em>HBCI4Java</em>-Paket 
          mitgeliefert.</p></li>
      <li><code>client.passport.DDV.libname.ctapi</code> (für DDV-Passports)
          <p>Mit diesem Parameter wird 
          der komplette Dateiname (mit Pfad) der CTAPI-Bibliothek 
          eingestellt, die die CTAPI-Schnittstelle für den zu 
          verwendenden Chipkartenleser implementiert. Diese Bibliothek 
          ist vom Hersteller des Chipkartenterminals zu beziehen.</p></li> 
      <li><code>client.passport.DDV.port</code> (für DDV-Passports)
          <p>Die logische Portnummer, an der der 
          Chipkartenleser angeschlossen ist (i.d.R. 0, 1 oder 2, abhängig 
          vom Anschluss (COM1, COM2, USB) und vom Treiber (manche Treiber 
          beginnen mit der Zählung bei 1, andere bei 0)) (am besten 
          ausprobieren). Achtung -- unter UN*X darauf achten, dass für 
          den ausführenden Nutzer Schreib- und Leserechte auf das 
          entsprechende Device (/dev/ttyS0, /dev/ttyUSB0 o.ä.) bestehen.</p></li>
      <li><code>client.passport.DDV.ctnumber</code> (für DDV-Passports)
          <p>Die logische Nummer des 
          Chipkartenterminals, die im weiteren Verlauf verwendet werden 
          soll. Dies ist i.d.R. 0, falls mehrere Chipkartenterminals 
          angeschlossen und in Benutzung sind, sind diese einfach 
          durchzunummerieren.</p></li>
      <li><code>client.passport.DDV.usebio</code> (für DDV-Passports)
          <p>Dieser Parameter kann entweder 0, 1 oder -1 sein und hat
          nur Bedeutung, wenn die PIN-Eingabe direkt am Chipkartenterminal
          erfolgt (also wenn <code>client.passport.DDV.softpin</code> ungleich
          1 ist und wenn ein Keypad am Chipkartenterminal vorhanden ist).</p> 
          <p>Wenn dieser Wert auf 1 gesetzt wird, so bedeutet 
          das, dass die PIN-Eingabe nicht manuell erfolgt, sondern dass 
          statt dessen biometrische Merkmale des Inhabers ausgewertet 
          werden. Zurzeit ist dieses Feature speziell auf den 
          Chipkartenleser PinPad-Bio von Reiner-SCT angepasst, bei dem 
          einem Fingerabdruck eine PIN zugeordnet werden kann, deren 
          Eingabe beim Auflegen des Fingers simuliert wird. Für 
          andere biometriefähige Chipkartenterminals wird dieser 
          Parameter wahrscheinlich nicht funktionieren, entsprechende 
          Unterstützung ist aber geplant.</p>
          <p>Durch das Setzen dieses Wertes auf 0 wird das Benutzen der
          Biometrie-Einheit definitiv abgeschaltet. Bei einem Wert von -1 wird
          automatisch geprüft, ob eine Biometrie-Einheit verfügbar ist. Wenn ja,
          so wird diese benutzt, ansonsten erfolgt die PIN-Eingabe über das
          Keypad des Chipkartenterminals</p></li>
      <li><code>client.passport.DDV.softpin</code> (für DDV-Passports)
          <p>Dieser Parameter kann entweder 0, 
          1 oder -1 enthalten. Für alle Chipkartenterminals, die über 
          keine eigene Tastatur zur Eingabe der PIN verfügen, ist er auf 
          1 zu setzen. Damit wird der HBCI-Kernel darüber informiert, 
          dass die PIN vom Anwender über die PC-Tastatur einzugeben ist. 
          Durch Setzen dieses Wertes auf 0 wird die PIN-Eingabe für das 
          Keypad des Chipkartenlesers erzwungen.</p> 
          <p>Setzt man den Parameter auf -1, so wird automatisch erkannt, 
          welches PIN-Eingabeverfahren bei dem jeweils verwendeten 
          Chipkartenterminal zu benutzen ist.</p></li>
      <li><code>client.passport.DDV.entryidx</code> (für DDV-Passports)
          <p>Prinzipiell kann auf einer DDV-Chipkarte mehr als ein Datensatz mit
          HBCI-Zugangsdaten gespeichert werden (bis zu fünf). Dieser Parameter
          legt fest, welcher der fünf Datensätze tatsächlich benutzt werden soll.
          Da in den meisten Fällen aber nur der erste Datensatzu tatsächlich belegt
          ist, wird dieser Parameter meist den Wert "1" haben (ist auch default,
          falls dieser Parameter gar nicht gesetzt ist).</p></li>
      <li><code>client.passport.RDHNew.filename</code> (für RDHNew-Passports)
          <p>Dieser Parameter legt den 
          Dateinamen der Schlüsseldatei fest. Diese Datei sollte am 
          besten auf einem mobilen Datenträger (Diskette) gespeichert 
          sein. Außerdem sollte ein Backup dieser Datei angefertigt 
          werden, da bei Verlust der Schlüsseldatei keine 
          HBCI-Kommunikation mehr möglich ist.</p></li>
      <li><code>client.passport.RDHNew.init</code> (für RDHNew-Passports)
          <p>Dieser Parameter ist immer auf "1" zu 
          setzen (wird nur intern anders verwendet).</p></li>
      <li><code>client.passport.RDH.filename</code> (für RDH-Passports; <b><em>diese Variante der
          RDH-Passports sollte nicht mehr benutzt werden, sondern <code>RDHNew</code></em></b>;
          siehe Datei <code>README.RDHNew</code>)
          <p>analog zu <code>client.passport.RDHNew.filename</code>.</p></li>
      <li><code>client.passport.RDH.init</code> (für RDH-Passports; <b><em>diese Variante der
          RDH-Passports sollte nicht mehr benutzt werden, sondern <code>RDHNew</code></em></b>;
          siehe Datei <code>README.RDHNew</code>)
          <p>analog zu <code>client.passport.RDHNew.init</code>.</p></li>
      <li><code>client.passport.PinTan.filename</code> (für PIN/TAN-Passports)
          <p>Dieser Parameter legt den 
          Dateinamen der "Schlüsseldatei" fest. Beim PIN/TAN-Verfahren handelt
          es sich nicht wirklich um eine Schlüsseldatei, da bei diesem Sicherheitsverfahren
          keine kryptografischen Schlüssel auf HBCI-Ebene eingesetzt werden. In dieser
          Datei werden also nur die HBCI-Zugangsdaten abgelegt.</p></li>
      <li><code>client.passport.PinTan.certfile</code> (für PIN/TAN-Passports)
          <p>Dieser Parameter gibt den Dateinamen einer Datei an, die
          ein Zertifikat für die Kommunikation via HTTPS (SSL-Verschlüsselung)
          enthält. Diese Datei kann mit dem Tool <code>keytool</code> erzeugt werden,
          welches zur Java-Laufzeitumgebung gehört. Das Zertifikat (ein bestätigter
          öffentlicher Schlüssel) kann i.d.R. von der Bank angefordert werden.</p>
          <p>Dieser Parameter wird nur dann benötigt, wenn das SSL-Zertifikat der Bank
          nicht mit dem defaultmäßig in die JRE eingebauten TrustStore überprüft
          werden kann (also am besten erst ohne diesen Parameter ausprobieren -
          wenn eine entsprechende Fehlermeldung erscheint, muss das jeweilige Zertifikat
          von der Bank angefordert, mit <code>keytool</code> konvertiert und hier
          angegeben werden). Wenn ein entsprechendes Root-Zertifikat für die Überprüfung
          gar nicht zur Verfügung steht, so kann mit dem Parameter
          <code>client.passport.PinTan.checkcert</code> die Zertifikatsüberprüfung
          gänzlich deaktiviert werden.</p></li>
      <li><code>client.passport.PinTan.checkcert</code> (für PIN/TAN-Passports)
          <p>Dieser Parameter steht defaultmäßig auf "<code>1</code>". Wird dieser
          Parameter allerdings auf "<code>0</code>" gesetzt, so wird die Überprüfung
          des Bank-Zertifikates, welches für die SSL-Kommunikation verwendet
          wird, deaktiviert. Diese Vorgehensweise wird nicht empfohlen, da dann
          Angriffe auf das SSL-Protokoll und damit auch auf die HBCI-Kommunikation 
          möglich sind. In einigen Fällen steht aber kein Root-Zertifikat für die 
          Überprüfung des SSL-Zertifikats der Bank zur Verfügung, so dass diese
          Überprüfung abgeschaltet werden <em>muss</em>, um überhaupt eine Kommunikation
          mit der Bank zu ermöglichen.</p></li>
      <li><code>client.passport.PinTan.init</code> (für PIN/TAN-Passports)
          <p>Dieser Parameter ist immer auf "1" zu 
          setzen (wird nur intern anders verwendet).</p></li>
      <li><code>client.passport.SIZRDHFile.filename</code> (für SIZRDHFile-Passports)
          <p>Dieser Parameter legt den 
          Dateinamen der SIZ-Schlüsseldatei fest. Dabei handelt es sich
          um die Schlüsseldatei, die von anderer HBCI-Software (z.B. <em>StarMoney</em>)
          erzeugt wurde.</p>
          <p>Siehe dazu auch <code>README.SIZRDHFile</code></p></li>
      <li><code>client.passport.SIZRDHFile.libname</code> (für SIZRDHFile-Passports)
          <p>Dieser Parameter gibt den vollständigen Dateinamen der SIZ-RDH-Laufzeitbibliothek
          an. Diese Bibliothek ist <em>nicht</em> Teil von <em>HBCI4Java</em>, sondern muss separat 
          von <a href="http://hbci4java.kapott.org#download">http://hbci4java.kapott.org</a>
          heruntergeladen und installiert werden.</p>
          <p>Siehe dazu auch <code>README.SIZRDHFile</code></p></li>
      <li><code>client.passport.SIZRDHFile.init</code> (für SIZRDHFile-Passports)
          <p>Dieser Parameter ist immer auf "1" zu 
          setzen (wird nur intern anders verwendet).</p>
          <p>Siehe dazu auch <code>README.SIZRDHFile</code></p></li>
      <li><code>client.passport.OpenHBCI.mediafile</code> (für OpenHBCI-Passports)
          <p>Dieser Parameter legt den 
          Dateinamen der Schlüsseldatei fest.
          Siehe dazu auch Datei <code>README.OpenHBCI</code>.</p></li>
      <li><code>client.passport.OpenHBCI.datafile</code> (für OpenHBCI-Passports)
          <p>Dieser Parameter legt den 
          Dateinamen der Datei fest, in welcher die Informationen zu den einzelnen
          HBCI-Zugängen gespeichert sind (i.d.R. <code>.openhbci</code> im Homedirectory
          des Nutzers). Siehe dazu auch Datei <code>README.OpenHBCI</code>.</p></li>
      <li><code>client.passport.OpenHBCI.init</code> (für OpenHBCI-Passports)
          <p>Dieser Parameter ist immer auf "1" zu 
          setzen (wird nur intern anders verwendet).</p></li>
      <li><code>client.passport.Anonymous.filename</code> (für Anonymous-Passports)
          <p>Dieser Parameter legt den 
          Dateinamen der Schlüsseldatei fest.</p></li>
      <li><code>client.passport.Anonymous.init</code> (für Anonymous-Passports)
          <p>Dieser Parameter ist immer auf "1" zu 
          setzen (wird nur intern anders verwendet).</p></li>
      <li><code>client.passport.default</code>
          <p>Wird bei der Erzeugung eines Passport-Objektes 
          ({@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance()})
          nicht explizit angegeben, für welches Sicherheitsverfahren ein Passport-Objekt 
          erzeugt werden soll, so wird der Wert dieses Parameters 
          benutzt, um die entsprechende Variante auszuwählen. Gültige 
          Werte sind "<code>DDV</code>", "<code>RDHNew</code>", "<code>RDH</code>" (nicht
          mehr benutzen!), "<code>PinTan</code>", "<code>SIZRDHFile</code>", "<code>OpenHBCI</code>"
          oder "<code>Anonymous</code>" (Groß-/Kleinschreibung beachten).</p></li>
      <li><code>client.retries.passphrase</code>
          <p>Ist das Passwort für die Entschlüsselung der Passport-Datei falsch, so kann die Eingabe
          so oft wiederholt werden, wie dieser Parameter angibt, bevor eine Exception geworfen und
          die weitere Programmausführung unterbrochen wird.</p></li>
      <li><code>client.connection.localPort</code>
          <p>Für Anwendungen, die sich hinter einer Firewall befinden, 
          welche nur ausgehende Verbindungen mit bestimmten lokalen 
          Portnummern zulässt (sowas soll's geben), kann mit diesem 
          Parameter die Portnummer festgelegt werden, die lokal benutzt 
          werden soll. Dieser Parameter hat im Moment nur bei "normalen" 
          HBCI-Verbindungen Auswirkungen. Beim PIN/TAN-Verfahren wird 
          eine HTTPS-Verbindung mit dem HBCI-Server aufgebaut, für diese 
          Verbindung wird der localPort-Parameter im Moment noch nicht ausgewertet.</p></li>
      <li><code>kernel.kernel.xmlpath</code>
          <p>(wird nicht gesetzt, zur Zeit nur intern benutzt)</p></li>
      <li><code>kernel.kernel.blzpath</code>
          <p>(wird nicht gesetzt, zur Zeit nur intern benutzt)</p></li>
      <li><code>log.loglevel.default</code>
          <p>Mit diesem Parameter kann eingestellt werden, welche vom 
          HBCI-Kernel erzeugten Log-Ausgaben tatsächlich bis zur 
          Anwendung gelangen. Dieser Parameter kann Werte von 1 (nur 
          Fehlermeldungen) bis 5 (einschließlich aller Debug-Ausgaben) annehmen.</p>
          <p>Bei Problemen mit dem Kernel diesen Level bitte auf 4 oder 5 setzen, 
          alle erzeugten Log-Ausgaben protokollieren
          und zusammen mit einer Beschreibung des Problems an den 
          <a href="mailto:hbci4java@kapott.org">Autor</a> schicken.</p></li>
      <li><code>kernel.rewriter</code>
          <p>Einige HBCI-Server-Implementationen bzw. die Backend-Systeme 
          einiger Banken halten sich nicht strikt an die in der 
          HBCI-Spezifikation vorgeschriebenen Formate. Um solche 
          Unzulänglichkeiten nicht direkt im HBCI-Kernel abfangen zu 
          müssen, existieren sogenannte Rewriter-Module. Ein solches 
          Modul ist für jeweils einen bekannten "Bug" zuständig. Kurz vor 
          dem Versand und direkt nach dem Eintreffen von HBCI-Nachrichten 
          werden diese durch alle registrierten Rewriter-Module 
          geschickt. Für ausgehende Nachrichten werden hier u.U. nicht 
          HBCI-konforme Veränderungen vorgenommen, die vom jeweiligen 
          HBCI-Server so erwartet werden. Eingehende Nachrichten, die 
          nicht HBCI-konform sind, werden so umgeformt, dass sie der 
          Spezifikation entsprechen. Auf diese Art und Weise kann der 
          HBCI-Kernel immer mit streng HBCI-konformen Nachrichten arbeiten.
          Siehe dazu auch die Paketdokumentation zum Paket
          <code>org.kapott.hbci.rewrite</code>.</p>
          <p>Der Parameter <code>kernel.rewriter</code> legt die Liste aller 
          Rewriter-Module fest, welche eingehende und ausgehende Nachrichten 
          durchlaufen sollen. Wird dieser Parameter nicht gesetzt, so
          verwendet <em>HBCI4Java</em> eine default-Liste von aktivierten
          Rewriter-Modulen (kann mit {@link #getParam(String)} ermittelt werden).
          Wird dieser Parameter gesetzt, so wird die default-Einstellung
          überschrieben. Es können mehrere zu durchlaufende Rewriter-Module
          angegeben werden, indem sie durch Komma voneinander getrennt werden.</p></li>
      <li><p>Die folgenden Parameter legen die Größe sog. Object-Pools fest, die
          intern von <em>HBCI4Java</em> verwendet werden. Object-Pools stellen eine
          Art Cache dar, um Instanzen häufig benutzter Klassen nicht jedesmal neu
          zu erzeugen. Statt dessen werden nicht mehr benötigte Objekte in einem
          Pool verwaltet, aus dem bei Bedarf wieder Objekte entnommen werden. Die
          Größe der Pools für die einzelnen Objekttypen kann hier festgelegt werden.
          Falls Speicherprobleme auftreten (<code>OutOfMemory</code>-Exception), so sollten 
          diese Werte verringert werden. Durch Setzen eines Wertes auf "<code>0</code>" wird
          das Object-Pooling für die entsprechenden Objekte komplett deaktiviert.
          Zur Zeit werden nur bei der Nachrichtenerzeugung und -analyse Object-Pools 
          verwendet. In der folgenden Auflistung steht in Klammern jeweils der 
          eingebaute default-Wert.</p>
          <ul>
            <li><p><code>kernel.objpool.MSG</code> -- Pool für Nachrichten-Objekte (3)</p></li>
            <li><p><code>kernel.objpool.SF</code>  -- Pool für SF- (Segmentfolgen-) Objekte (128)</p></li>
            <li><p><code>kernel.objpool.SEG</code> -- Pool für Segment-Objekte (256)</p></li>
            <li><p><code>kernel.objpool.DEG</code> -- Pool für DEG- (Datenelementgruppen-) Objekte (256)</p></li>
            <li><p><code>kernel.objpool.DE</code> -- Pool für Datenelement-Objekte (1024)</p></li>
            <li><p><code>kernel.objpool.Sig</code> -- Pool für Signatur-Objekte (3)</p></li>
            <li><p><code>kernel.objpool.Crypt</code> -- Pool für Crypt-Objekte (3)</p></li>
            <li><p><code>kernel.objpool.Syntax</code> -- Pool für Daten-Objekte (=Werte in Nachrichten) (128 je Datentyp)</p></li>
          </ul></li>
      <li><p>Mit den folgenden Parametern kann <em>HBCI4Java</em> veranlasst
          werden, beim Auftreten bestimmter Fehler keine Exception zu werfen, sondern
          diesen Fehler zu ignorieren bzw. den Anwender entscheiden zu lassen,
          ob der Fehler ignoriert werden soll. Bei den Fehlern handelt es sich
          hauptsächlich um Fehler, die beim Überprüfen von Eingabedaten und
          Institutsnachrichten bzgl. der Einhaltung der HBCI-Spezifikation
          auftreten.</p>
          <p>Jeder der folgenden Parameter kann einen der Werte <code>yes</code>,
          <code>no</code> oder <code>callback</code> annehmen. Ist ein Parameter auf
          <code>no</code> gesetzt, so wird beim Auftreten des jeweiligen Fehlers
          eine entsprechende Exception geworfen. Dieses Verhalten ist das Standardverhalten
          und entspricht dem der Vorgängerversionen von <em>HBCI4Java</em>. Ist
          ein Parameter auf <code>yes</code> gesetzt, so wird der Fehler komplett
          ignoriert. Es wird nur eine entsprechende Warnung mit Loglevel <code>LOG_WARN</code>
          erzeugt. Wird ein Parameter auf <code>callback</code> gesetzt, so wird ein
          Callback mit dem Callback-Reason <code>HAVE_ERROR</code> erzeugt, bei dem
          die Callback-Message (Parameter <code>msg</code>) die entsprechende Fehlermeldung
          enthält. Gibt die Callback-Methode einen leeren String im <code>retData</code>-Objekt
          zurück, so bedeutet das für <em>HBCI4Java</em>, dass der entsprechende
          Fehler ignoriert werden soll (so als wäre der Parameter auf <code>yes</code>
          gesetzt). Ist der Rückgabestring nicht leer, so wird <em>HBCI4Java</em> eine
          entsprechende Exception werfen, so als wäre der zugehörige Parameter gleich
          <code>no</code>. Nähere Informationen zu Callbacks befinden sich in der
          Beschreibung des Interfaces {@link org.kapott.hbci.callback.HBCICallback}.</p>
          <p><b>"Normalen" Benutzern von <em>HBCI4Java</em> ist dringend von der Verwendung
          dieser Parameter abzuraten, weil sie bei falscher Anwendung dazu führen können,
          dass <em>HBCI4Java</em> gar nicht mehr funktioniert.</b> Diese Parameter sind
          nur für <em>HBCI4Java</em>-Entwickler (also mich ;-)) gedacht und sind hier nur
          der Vollständigkeit halber aufgeführt.</p>
          <p>Eine genauere Beschreibung der einzelnen Parameter befindet sich in
          der Properties-Template-Datei <code>hbci.props.template</code>.</p></li>
      <li><code>client.errors.ignoreJobResultStoreErrors</code></li>
      <li><code>client.errors.ignoreWrongJobDataErrors</code></li>
      <li><code>client.errors.ignoreWrongDataLengthErrors</code></li>
      <li><code>client.errors.ignoreWrongDataSyntaxErrors</code></li>
      <li><code>client.errors.ignoreAddJobErrors</code></li>
      <li><code>client.errors.ignoreCreateJobErrors</code></li>
      <li><code>client.errors.ignoreExtractKeysErrors</code></li>
      <li><code>client.errors.ignoreDialogEndErrors</code></li>
      <li><code>client.errors.ignoreSecMechCheckErrors</code></li>
      <li><code>client.errors.ignoreVersionCheckErrors</code></li>
      <li><code>client.errors.ignoreSignErrors</code></li>
      <li><code>client.errors.ignoreMsgSizeErrors</code></li>
      <li><code>client.errors.ignoreCryptErrors</code></li>
      <li><code>client.errors.ignoreMsgCheckErrors</code></li>
      <li><code>client.errors.allowOverwrites</code></li>
      <li><code>client.errors.ignoreValidValueErrors</code></li>
      <li><code>client.errors.ignoreSegSeqErrors</code></li>
    </ul> */
public final class HBCIUtils
{
    public static final int LOG_NONE=0;
    /** Loglevel für Fehlerausgaben */
    public static final int LOG_ERR=1;
    /** Loglevel für Warnungen */
    public static final int LOG_WARN=2;
    /** Loglevel für Informationen */
    public static final int LOG_INFO=3;
    /** Loglevel für Debug-Ausgaben */
    public static final int LOG_DEBUG=4;
    /** Loglevel für Debug-Ausgaben für extreme-Debugging */
    public static final int LOG_DEBUG2=5;

    private static Hashtable  configs;  // threadgroup->hashtable(paramname->paramvalue)
    private static char[] base64table={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
                                       'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
                                       'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
                                       'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'};
    static {
        initDataStructures();
    }
    
    private static void initDataStructures()
    {
        configs=new Hashtable();
        HBCIUtilsInternal.callbacks=new Hashtable();
        HBCIUtilsInternal.blzs=new Properties();
        HBCIUtilsInternal.locMsgs=new Hashtable();
    }
    
    private HBCIUtils()
    {
    }

    /** <p>Initialisieren der <em>HBCI4Java</em>-Umgebung.
        Diese Methode muss <em>vor allen anderen</em> HBCI-Methoden aufgerufen
        werden. Hiermit wird die <em>HBCI4Java</em>-Laufzeitumgebung initialisiert.
        Dazu gehören das Laden verschiedener Dateien aus dem <em>HBCI4Java</em>-Classpath
        (Dateien für die Lokalisierung von Nachrichten, Verzeichnis der Banken
        usw.) sowie das Initialisieren einiger interner Datenstrukturen. 
        <p>Zusätzlich wird in dieser Methode die Methode {@link #initThread(ClassLoader,String,HBCICallback)}
        aufgerufen, um alle Datenstrukturen, die <code>ThreadGroup</code>-weise verwaltet werden,
        für die aktuelle <code>ThreadGroup</code> zu initialisieren. Siehe dazu auch die Dokumentation
        zu {@link #initThread(ClassLoader,String,HBCICallback)} sowie die Datei
        <code>README.MultiThread</code>.</p>
        @param cl der ClassLoader, der zum Laden von <code>configfile</code>
               verwendet werden soll. 
        @param configfile der Name des zu ladenden Property-Files.
        @param callback das zu verwendende Callback-Objekt. Beim Aufruf dieser Methode
               darf <code>callback</code> niemals <code>null</code> sein (im Gegensatz
               zum Aufruf von <code>initThread</code>, um weitere <code>ThreadGroups</code>
               zu initialisieren).*/
    public static synchronized void init(ClassLoader cl,String configfile,HBCICallback callback)
    {
        // System.setProperty("file.encoding","ISO-8859-1");
        
        try {
            initThread(cl,configfile,callback);
            
            try {
                throw new Exception();
            } catch (Exception e) {
                cl=Class.forName(e.getStackTrace()[0].getClassName()).getClassLoader();
            }

            refreshBLZList(cl);

            if (Security.getProvider("HBCIProvider")==null)
                Security.addProvider(new HBCIProvider());
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_INIT_MAIN"),e);
        }
    }
    
    /** Entspricht {@link #initThread(ClassLoader,String,HBCICallback)} */
    public static synchronized void initThread(ClassLoader cl,String configfile)
    {
        initThread(cl,configfile,null);
    }
    
    /** <p>Initialisieren der <em>HBCI4Java</em>-Umgebung für eine neue 
        <code>ThreadGroup</code>. Soll <em>HBCI4Java</em> in
        einer multi-threaded Anwendung verwendet werden, bei der mehrere Threads
        gleichzeitig <em>HBCI4Java</em> benutzen, so muss für jeden solchen
        Thread eine separate <code>ThreadGroup</code> angelegt
        werden. Jede dieser <code>ThreadGroup</code>s muss mit dieser Methode
        für die Benutzung von <em>HBCI4Java</em> initialisiert werden. Alle
        HBCI-Kernel-Parameter sowie die HBCI-Callbacks werden für jede 
        <code>ThreadGroup</code> separat verwaltet, so dass jede 
        <code>ThreadGroup</code> also einen eigenen Satz dieser Daten benutzt.</p>
        <p>Der Thread, in dem die Methode <code>HBCIUtils.init()</code> aufgerufen wird,
        muss <em>nicht</em> zusätzlich mit <code>initThread()</code> initialisiert
        werden, das wird automatisch von der Methode <code>init()</code> übernommen.</p>
        <p>Siehe dazu auch die Datei <code>README.MultiThreading</code> in 
        den <em>HBCI4Java</em>-Archiven.</p>
        <p>Ist der Parameter <code>configfile</code> ungleich <code>null</code>, so
        wird versucht, ein Property-File mit default-Einstellungen für die
        HBCI-Kernel-Parameter für die aktuelle <code>ThreadGroup</code> zu laden. Der Name des
        Property-Files wird durch den Parameter <code>configfile</code> bestimmt.
        Wie dieser Name interpretiert wird, um das Property-File tatsächlich zu
        finden, hängt von dem zum Laden benutzten ClassLoader ab. Im Parameter
        <code>cl</code> kann dazu eine ClassLoader-Instanz übergeben werden,
        deren <code>getRessource</code>-Methode benutzt wird, um das Property-File
        zu lokalisieren und zu laden. Wird kein ClassLoader angegeben (<code>cl==null</code>),
        so wird zum Laden des Property-Files der ClassLoader benutzt, der auch zum
        Laden der aufrufenden Klasse benutzt wurde.</p>
        <p><b>Achtung</b>: Dieser Default-ClassLoader ist in den
        meisten Fällen ein ClassLoader, der in einem JAR-File bzw. im aktuellen CLASSPATH
        nach Ressourcen sucht. Soll ein Property-File von einer bestimmten Stelle im Filesystem
        geladen werden, so sollte hier statt dessen der ClassLoader 
        {@link org.kapott.hbci.manager.FileSystemClassLoader} 
        benutzt werden. In diesem Fall wird der angegebene Dateiname als relativer
        Pfad von der <em>Wurzel</em> des Dateisystems aus interpretiert. 
        Eine Demonstration befindet sich im Tool
        {@link org.kapott.hbci.tools.AnalyzeReportOfTransactions}.</p>
        <p>Außerdem wird mit dieser Methode ein Callback-Objekt registriert, welches
        von <em>HBCI4Java</em> für die Kommunikation mit der Anwendung verwendet wird.</p>
        @param cl der ClassLoader, der verwendet werden soll, um das Property-File
               <code>configfile</code> zu laden (mit der Methode 
               <code>ClassLoader.getRessource()</code>). Ist dieser Parameter
               <code>null</code>, so wird der ClassLoader verwendet, der auch zum
               Laden <em>der</em> Klasse benutzt wurde, die die aufrufende Methode enthält.
        @param configfile der Name des zu ladenden Property-Files. Ist dieser Parameter
               <code>null</code>, kein Property-File geladen.
        @param callback ein Objekt einer <code>HBCICallback</code>-Klasse, das benutzt
               wird, um Anfragen des Kernels (benötigte Daten, benötige
               Chipkarte, wichtige Informationen während der Dialog-Ausführung etc.)
               an die Anwendung weiterzuleiten. 
               Siehe dazu {@link org.kapott.hbci.callback.HBCICallback}.
               Jede <code>ThreadGroup</code> kann ein eigenes Callback-Objekt registrieren,
               welches dann für alle HBCI-Prozesse innerhalb dieser <code>ThreadGroup</code>
               verwendet wird. Wird beim Initialisieren einer <code>ThreadGroup</code>
               kein <code>callback</code>-Objekt angegeben (<code>callback==null</code>),
               dann wird für diese <code>ThreadGroup</code> das Callback-Objekt der
               "Eltern-<code>ThreadGroup</code>" verwendet. Die "initiale" <code>ThreadGroup</code>,
               die mit {@link #init(ClassLoader,String,HBCICallback)}
               initialisiert wird, muss ein <cod>callback!=null</code> spezifizieren.*/
    public static synchronized void initThread(ClassLoader cl,String configfile,HBCICallback callback)
    {
        ThreadGroup threadgroup=Thread.currentThread().getThreadGroup();
        
        if (HBCIUtilsInternal.callbacks.get(threadgroup)!=null) {
            HBCIUtils.log("*** will not initialize this threadgroup because it is already initialized",HBCIUtils.LOG_WARN);
        } else {
            try {
                synchronized (HBCIUtilsInternal.locMsgs) {
                    HBCIUtilsInternal.locMsgs.put(threadgroup,ResourceBundle.getBundle("messages",Locale.getDefault()));
                }
                
                Properties config=new Properties();
                if (configfile!=null) {
                    /* determine classloader to be used */
                    if (cl==null) {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            StackTraceElement[] stackTrace=e.getStackTrace();
                            
                            if (stackTrace.length>1) {
                                String classname=stackTrace[1].getClassName();
                                cl=Class.forName(classname).getClassLoader();
                            } else {
                                cl=ClassLoader.getSystemClassLoader();
                            }
                        }
                    }
                    
                    /* get an input stream */
                    InputStream f=null;
                    f=cl.getResourceAsStream(configfile);
                    if (f==null)
                        throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_UTIL_CANTLOADCFG",configfile));
                    
                    config.load(f);
                    f.close();
                }
                
                synchronized (configs) {
                    configs.put(threadgroup,config);
                }
                if (getParam("kernel.rewriter")==null) {
                    setParam("kernel.rewriter","SecTypeTAN,Olly,HBCIVersion,1822direkt,RVB");
                }
                
                if (callback==null) {
                    ThreadGroup parent=Thread.currentThread().getThreadGroup().getParent();
                    callback=(HBCICallback)HBCIUtilsInternal.callbacks.get(parent);
                    if (callback==null) {
                        throw new NullPointerException(HBCIUtilsInternal.getLocMsg("EXC_MISSING_CALLBACK"));
                    }
                }
                HBCIUtilsInternal.callbacks.put(threadgroup,callback);
                
                // *** diese msg wird nur sichtbar, wenn ein configfile mit log.loglevel.default=4 benutzt wird
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_THREAD_INIT",threadgroup.getName()),
                        HBCIUtils.LOG_DEBUG);
            } catch (Exception ex) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_THREAD_INIT",threadgroup.getName()),ex);
            }
        }
    }
    
    /** Aufräumen der Datenstrukturen für aktuelle <code>ThreadGroup</code>. Alle
     * <code>ThreadGroups</code>, die via {@link #initThread(ClassLoader,String,HBCICallback)}
     * initialisiert wurden, sollten kurz vor deren Ende mit dieser Methode wieder
     * "aufgeräumt" werden, damit <em>HBCI4Java</em> die entsprechenden Datenstrukturen
     * für diese <code>ThreadGroup</code> wieder freigeben kann. */
    public static synchronized void doneThread()
    {
        HBCIUtils.log("*** removing all data for current thread",HBCIUtils.LOG_DEBUG);
        
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        HBCIUtilsInternal.callbacks.remove(group);
        configs.remove(group);
        HBCIUtilsInternal.locMsgs.remove(group);
    }
    
    /** Bereinigen aller <em>HBCI4Java</em>-Datenstrukturen. Nach Aufruf dieser
     * Methode kann keine andere <em>HBCI4Java</em>-Funktion mehr benutzt
     * werden. Durch erneuten Aufruf von {@link #init(ClassLoader,String,HBCICallback)}
     * kann <em>HBCI4Java</em> wieder re-initialisiert werden. */
    public static synchronized void done()
    {
        HBCIUtils.log("*** destroying all HBCI4Java resources",HBCIUtils.LOG_DEBUG);
        initDataStructures();
    }

    /** Gibt den aktuellen Wert eines bestimmten HBCI-Parameters zurück. 
        Für jede {@link java.lang.ThreadGroup} wird ein separater
        Satz von HBCI-Parametern verwaltet.
        @param st Name des HBCI-Parameters
        @param def default-Wert, falls dieser Parameter nicht definiert ist
        @return den Wert des angegebenen HBCI-Parameters */
    public static String getParam(String st,String def)
    {
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        Properties config;
        synchronized (configs) {
            config=(Properties)configs.get(group);
        }
        if (config==null)
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_THREAD_NOTINIT",group.getName())); 
        return config.getProperty(st,def);
    }

    /** Gibt den aktuellen Wert eines bestimmten HBCI-Parameters zurück.
        Für jede {@link java.lang.ThreadGroup} wird ein separater
        Satz von HBCI-Parametern verwaltet.
        @param st Name des HBCI-Parameters
        @return den Wert des angegebenen HBCI-Parameters */
    public static String getParam(String st)
    {
        return getParam(st,null);
    }
    
    /** Ermittelt zu einer gegebenen Bankleitzahl den Namen des Institutes.
        @param blz die Bankleitzahl
        @return den Namen des dazugehörigen Kreditinstitutes. Falls die Bankleitzahl unbekannt ist,
                so wird ein leerer String zurückgegeben */
    public static String getNameForBLZ(String blz)
    {
        String result=HBCIUtilsInternal.getBLZData(blz);
        return result.substring(0,result.indexOf("|"));
    }
    
    /** Gibt zu einer gegebenen Bankleitzahl den BIC-Code zurück.
        @param blz Bankleitzahl der Bank
        @return BIC-Code dieser Bank. Falls kein BIC-Code bekannt ist, wird ein
        leerer String zurückgegeben. */
    public static String getBICForBLZ(String blz)
    {
        String data=HBCIUtilsInternal.getBLZData(blz);
        String ret=data.substring(data.indexOf('|',data.indexOf('|')+1)+1,
                                  data.lastIndexOf('|'));
        
        return ret;
    }

    /** Setzt den Wert eines HBCI-Parameters. Eine Beschreibung aller vom Kernel ausgewerteten Parameter
        befindet sich in der {@link org.kapott.hbci.manager.HBCIUtils Klassenbeschreibung} zur dieser Klasse.
        Für jede {@link java.lang.ThreadGroup} wird ein separater
        Satz von HBCI-Parametern verwaltet.
        @param key Name des HBCI-Parameters.
        @param value neuer Wert des zu setzenden HBCI-Parameters */
    public static void setParam(String key,String value)
    {
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        Properties config;
        synchronized (configs) {
            config=(Properties)configs.get(group);
        }
        if (config==null)
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_THREAD_NOTINIT",group.getName())); 
        
        synchronized (config) {
            if (value!=null) {
                config.setProperty(key,value);
            } else {
                config.remove(key);
            }
        }
    }

    /** Ausgabe eines Log-Strings über den Log-Mechanismus des HBCI-Kernels.
        @param st der auszugebende String
        @param level die "Wichtigkeit" dieser Meldung. mögliche Werte:
               <ul>
                 <li><code>LOG_ERR</code></li>
                 <li><code>LOG_WARN</code></li>
                 <li><code>LOG_INFO</code></li>
                 <li><code>LOG_DEBUG</code></li>
                 <li><code>LOG_CHIPCARD</code> (wird nur intern benutzt)</li>
               </ul> */
    public static synchronized void log(String st,int level)
    {
        if (level<=Integer.parseInt(getParam("log.loglevel.default","3"))) {
            StackTraceElement trace=null;
            try {
                throw new Exception("");
            } catch (Exception e) {
                trace=e.getStackTrace()[1];
            }
            
            HBCIUtilsInternal.getCallback().log(st,level,new Date(),trace);
        }
    }
    
    /** Ausgabe der Meldungen einer Exception-Kette mit dem Level <code>LOG_ERR</code>.
        @param e die Exception, deren <code>getMessage()</code>-Meldungen geloggt
               werden sollen */
    public static synchronized void log(Exception e)
    {
        log(e,LOG_ERR);
    }
    
    /** Erzeugen eines Strings, der die Ursache einer Exception beschreibt. Der String
        wird erzeugt, indem alle <code>getCause()</code>-Strings der Exception <code>e</code>
        und deren <code>Cause</code> rekursiv aneinandergekettet werden. Zusätzlich wird am
        Ende ein kompletter StackTrace angehängt. Der Ergebnisstring hat also die folgende Form:
        <pre>
e.getMessage() -> e.getCause().getMessage() -> e.getCause().getCause().getMessage() ...
e.printStackTrace();
        </pre> 
     *  @param e Exception, deren Messages zusammengefasst werden sollen
     *  @return String mit Meldungen der Exception-Chain */ 
    public static String exception2String(Exception e)
    {
        StringBuffer st=new StringBuffer(exception2StringShort(e));
        st.append(System.getProperty("line.separator"));

        StringWriter sw=new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        st.append("HBCI4Java stacktrace BEGIN ---");
        st.append(sw.toString());
        st.append(System.getProperty("line.separator"));
        st.append("HBCI4Java stacktrace END ---");
        
        return st.toString();
    }
    
    /** Erzeugen eines Strings, der die Messages einer Exception und deren Ursache-Exceptions
        enthält. Das Format des zurückgegebenen Strings ist analog dem von
        {@link #exception2String(Exception)}, allerdings wird bei dieser Methode
        kein StackTrace (<code>e.printStackTrace()</code>) angehängt.
        @param e Exception, deren Messages zusammengefasst werden sollen
        @return String mit Meldungen der Exception-Chain */
    public static String exception2StringShort(Exception e) 
    {
        StringBuffer st=new StringBuffer();
        boolean      first=true;
        Throwable    e2=e;

        st.append("HBCI4Java Exception BEGIN ---");

        while (e2!=null) {
            String msg=e2.getMessage();

            if (msg!=null) {
                if (first) {
                    first=false;
                } else {
                    st.append(System.getProperty("line.separator")+"  -> ");
                }
                st.append(msg);
            }
            e2=e2.getCause();
        }
        
        if (!first) {
            st.append(System.getProperty("line.separator"));
        }
        st.append("HBCI4Java Exception END ---");

        return st.toString();
    }
    
    /** Ausgabe der Meldungen einer Exception-Kette über den Log-Mechanismus
        des HBCI-Kernels. Es werden auch alle <code>getCause()</code>-Exceptions
        verfolgt und deren Meldung ausgegeben. Enthält keine der Exceptions dieser
        Kette eine Message, so wird statt dessen ein Stacktrace ausgegeben.
        @param e die Exception, deren <code>getMessage()</code>-Meldungen ausgegeben
               werden sollen. 
        @param level der Log-Level, mit dem die Meldungen geloggt werden sollen.
               Siehe dazu auch {@link #log(String,int)} */
    public static synchronized void log(Exception e,int level)
    {
        log(exception2String(e),level);
    }

    /** Wandelt ein Byte-Array in eine entsprechende hex-Darstellung um.
        @param data das Byte-Array, für das eine Hex-Darstellung erzeugt werden soll
        @return einen String, der für jedes Byte aus <code>data</code>
                zwei Zeichen (0-9,A-F) enthält. */ 
    public static String data2hex(byte[] data)
    {
        StringBuffer ret=new StringBuffer();
        
        for (int i=0;i<data.length;i++) {
            String st=Integer.toHexString(data[i]);
            if (st.length()==1) {
                st='0'+st;
            }
            st=st.substring(st.length()-2);
            ret.append(st+" ");
        }
        
        return ret.toString();
    }

    /** Wandelt ein gegebenes Datumsobjekt in einen String um
        @param date ein Datum
        @return die lokalisierte Darstellung dieses Datums als String */
    public static String date2String(Date date)
    {
        String ret;
        
        try {
            ret=DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        } catch (Exception e) {
            throw new InvalidArgumentException(date.toString());
        }
        
        return ret;
    }
    
    /** Wandelt einen String, der ein Datum in der lokalen Form enthält,
        in ein Datumsobjekt um
        @param date ein Datum in der lokalen Stringdarstellung
        @return ein entsprechendes Datumsobjekt */
    public static Date string2Date(String date)
    {
        Date ret;
        
        try {
            ret=DateFormat.getDateInstance(DateFormat.SHORT).parse(date);
        } catch (Exception e) {
            throw new InvalidArgumentException(date);
        }
        
        return ret;
    }

    /** Wandelt ein gegebenes Datums in einen String um, der die Uhrzeit enthält
        @param date ein Datumsobjekt 
        @return die lokalisierte Darstellung der Uhrzeit als String */
    public static String time2String(Date date)
    {
        String ret;
        
        try {
            ret=DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        } catch (Exception e) {
            throw new InvalidArgumentException(date.toString());
        }
        
        return ret;
    }
    
    /** Wandelt einen String, der eine Uhrzeit in der lokalen Form enthält,
        in ein Datumsobjekt um
        @param date eine Uhrzeit in der lokalen Stringdarstellung
        @return ein entsprechendes Datumsobjekt */
    public static Date string2Time(String date)
    {
        Date ret;
        
        try {
            ret=DateFormat.getTimeInstance(DateFormat.SHORT).parse(date);
        } catch (Exception e) {
            throw new InvalidArgumentException(date);
        }
        
        return ret;
    }

    /** Wandelt übergebene Datums- und Zeitangaben in ein Date-Objekt um
        @param date das Datum in der lokalen Darstellung (je nach defaultLocale())
        @param time die Uhrzeit in der lokalen Darstellung (darf <code>null</code>sein)
        @return ein Date-Objekt, bei dem Datum und Zeit auf die angegebenen Werte gesetzt sind */
    public static Date strings2Date(String date,String time)
    {
        Date ret;
        
        try {
            if (time!=null)
                ret=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).parse(date+" "+time);
            else
                ret=DateFormat.getDateInstance(DateFormat.SHORT).parse(date);
        } catch (Exception e) {
            throw new InvalidArgumentException(date+"/"+time);
        }
        
        return ret;
    }
    
    /** Gibt Daten Base64-encoded zurück. Die zu kodierenden Daten müssen als Byte-Array übergeben
        werden, als Resultat erhält man einen String mit der entsprechenden Base64-Kodierung.
        @param x zu kodierende Daten
        @return String mit Base64-kodierten Daten */
    public static String encodeBase64(byte[] x)
    {
        try {
            int origSize=x.length;

            if ((origSize%3)!=0) {
                byte[] temp=new byte[((origSize/3)+1)*3];
                System.arraycopy(x,0,temp,0,origSize);
                x=temp;
            }

            StringBuffer ret=new StringBuffer();
            int readPos=0;

            while (readPos<(x.length<<3)) {
                int modulus=readPos&7;
                int value;

                if ((readPos>>3)<origSize) {
                    if (modulus<=2) {
                        // six bits in one byte
                        value=(x[readPos>>3]>>(2-modulus))&0x3F;
                    } else {
                        // six bits in two bytes
                        value=((x[readPos>>3]<<(modulus-2))&0x3F)|
                              ((x[(readPos>>3)+1]>>(10-modulus))&((1<<(modulus-2))-1));
                    }

                    ret.append(base64table[value]);
                } else {
                    ret.append('=');

                }
                readPos+=6;
            }

            return ret.toString();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_UTIL_ENCB64"),ex);
        }
    }

    /** Dekodieren eines Base64-Datenstroms. Es wird zu einem gegebenen Base64-Datenstrom der dazugehörige
        "Klartext" zurückgegeben.
        @param st Base64-kodierten Daten
        @return dekodierter Datenstrom als Byte-Array */
    public static byte[] decodeBase64(String st)
    {
        try {
            byte[] source=st.getBytes("ISO-8859-1");
            StringBuffer ret=new StringBuffer();

            int needFromFirst=6;
            int needFromSecond=2;
            boolean abort=false;
            int byteCounter=0;

            // TB
            byte einbyte[]=new byte[1];

            for (int readPos=0;readPos<source.length;readPos++) {
                int[] values=new int[] {0,0};

                for (int step=0;step<2;step++) {
                    int value=0;

                    while ((readPos+step)<source.length) {
                        value=source[readPos+step];

                        if (value>='0'&&value<='9'||
                            value>='A'&&value<='Z'||
                            value>='a'&&value<='z'||
                            value=='+'||value=='/'||value=='=') {
                            break;
                        }

                        readPos++;
                    }

                    if (!(value>='0'&&value<='9'||
                          value>='A'&&value<='Z'||
                          value>='a'&&value<='z'||
                          value=='+'||value=='/')) {

                        abort=true;
                        break;
                    }

                    if ((char)value=='/') {
                        value=63;
                    } else if ((char)value=='+') {
                        value=62;
                    } else if ((char)value<='9') {
                        value=52+value-(byte)'0';
                    } else if ((char)value<='Z') {
                        value=value-(byte)'A';
                    } else {
                        value=26+value-(byte)'a';

                    }
                    if (step==0) {
                        values[0]=(value<<(8-needFromFirst))&0xFF;
                    } else {
                        values[1]=(value>>(6-needFromSecond))&0xFF;
                    }
                }

                if (abort) {
                    break;
                }

                //ret.append((char)((byte)(values[0]|values[1])));
                //TB
                // TODO patch melden!
                einbyte[0]=(byte)(values[0]|values[1]);
                ret.append(new String(einbyte,"ISO-8859-1"));

                if ((byteCounter&3)==2) {
                    readPos++;
                    byteCounter++;
                    needFromFirst=6;
                    needFromSecond=2;
                } else {
                    needFromFirst=6-needFromSecond;
                    needFromSecond=8-needFromFirst;
                }

                byteCounter++;
            }

            return ret.toString().getBytes("ISO-8859-1");
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_UTIL_DECB64"),ex);
        }
    }
    
    /** <p>Überprüft, ob gegebene BLZ und Kontonummer zueinander passen.
        Bei diesem Test wird wird die in die Kontonummer "eingebaute"
        Prüziffer verifiziert. Anhand der BLZ wird ermittelt, welches 
        Prüfzifferverfahren zur Überprüfung eingesetzt werden muss.</p>
        <p>Ein positives Ergebnis dieser Routine bedeutet <em>nicht</em>, dass das 
        entsprechende Konto bei der Bank <em>existiert</em>, sondern nur, dass 
        die Kontonummer bei der entsprechenden Bank prinzipiell gültig ist.</p>
        @param blz die Bankleitzahl der Bank, bei der das Konto geführt wird
        @param number die zu überprüfende Kontonummer
        @return <code>true</code> wenn die Kontonummer nicht verifiziert werden kann (z.B.
        weil das jeweilige Prüfzifferverfahren noch nicht in <em>HBCI4Java</em>
        implementiert ist) oder wenn die Prüfung erfolgreich verläuft; <code>false</code>
        wird immer nur dann zurückgegeben, wenn tatsächlich ein Prüfzifferverfahren
        zum Überprüfen verwendet wurde und die Prüfung einen Fehler ergab */ 
    public static boolean checkAccountCRC(String blz,String number)
    {
        boolean ret=true;
        
        String info=HBCIUtilsInternal.getBLZData(blz);
        String alg=info.substring(info.lastIndexOf("|")+1);
        
        if (alg.length()==2) {
            HBCIUtils.log("*** crc-checking "+blz+"/"+number,HBCIUtils.LOG_DEBUG);
            ret=checkAccountCRCByAlg(alg,number);
        } else {
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_CRC_NOTFOUND",blz),
                          HBCIUtils.LOG_WARN);
        }
        
        return ret;
    }
    
    /** Überprüfen einer Kontonummer mit einem gegebenen CRC-Algorithmus.
     * Diese Methode wird intern von {@link HBCIUtils#checkAccountCRC(String,String)}
     * aufgerufen und kann für Debugging-Zwecke auch direkt benutzt werden.
     * @param alg Nummer des zu verwendenden Prüfziffer-Algorithmus (siehe
     * Datei <code>blz.properties</code>).
     * @param number zu überprüfende Kontonummer
     * @return <code>false</code>, wenn der Prüfzifferalgorithmus für die
     * angegebene Kontonummer einen Fehler meldet, sonst <code>true</code>
     * (siehe dazu auch {@link #checkAccountCRC(String, String)}) */
    public static boolean checkAccountCRCByAlg(String alg,String number)
    {
        Class cl=null;
        Method method=null;
        
        try {
            cl=AccountCRCAlgs.class;
            method=cl.getMethod("alg_"+alg,new Class[] {int[].class});
        } catch (Exception e) {
            log(HBCIUtilsInternal.getLocMsg("WRN_CRC_NOTIMPL",alg),
                LOG_WARN);
        }
        
        boolean ret=true;

        if (method!=null) {
            try {
                int[] digits=new int[10];
                int len=number.length();
                char ch;

                for (int i=0;i<len;i++) {
                    ch=number.charAt(i);
                    digits[10-len+i]=ch-'0';
                }

                ret=((Boolean)method.invoke(null,new Object[] {digits})).booleanValue();
                HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_CRC_RES",new Object[] {alg,number,Boolean.valueOf(ret)}),
                              HBCIUtils.LOG_DEBUG);
            } catch (Exception e) {
                throw new HBCI_Exception(e);
            }
        }
        
        return ret;
    }
    
    private static void refreshBLZList(ClassLoader cl)
        throws IOException
    {
        String blzpath=HBCIUtils.getParam("kernel.kernel.blzpath");
        if (blzpath==null) {
            blzpath="";
        }
        blzpath+="blz.properties";
        InputStream f=cl.getResourceAsStream(blzpath);
        
        if (f==null)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_BLZLOAD",blzpath));
        
        refreshBLZList(f);
        f.close();
    }
    
    /** Aktivieren einer neuen Bankenliste. Diese Methode kann aufgerufen
     * werden, um während der Laufzeit einer <em>HBCI4Java</em>-Anwendung 
     * eine neue Bankenliste zu aktivieren. Die Bankenliste wird
     * aus dem übergebenen InputStream gelesen, welcher Daten im Format eines
     * Java-Properties-Files liefern muss. Das konkrete Format der Property-Einträge
     * der Bankenliste ist am Beispiel der bereits mitgelieferten Datei
     * <code>blz.properties</code> ersichtlich.
     * @param in Eingabe-Stream, der für das Laden der Bankleitzahlen-Daten verwendet
     *           werden soll */
    public static void refreshBLZList(InputStream in)
        throws IOException
    {
        HBCIUtils.log(HBCIUtilsInternal.getLocMsg("DBG_UTIL_LOADBLZ"),HBCIUtils.LOG_DEBUG);
        synchronized (HBCIUtilsInternal.blzs) {
            HBCIUtilsInternal.blzs.clear();
            HBCIUtilsInternal.blzs.load(in);
        }
    }

    /** Konvertiert einen String in einen double-Wert (entspricht 
     * <code>Double.parseDouble(st)</code>).
     * @param st String, der konvertiert werden soll (Format "<code>1234.56</code>");
     * @return double-Wert */
    public static double string2Value(String st)
    {
        return Double.parseDouble(st);
    }

    /** Wandelt einen Double-Wert in einen String im Format "<code>1234.56</code>"
     * um (also ohne Tausender-Trennzeichen und mit "." als Dezimaltrennzeichen).
     * @param value zu konvertierender Double-Wert
     * @return String-Darstellung dieses Wertes */
    public static String value2String(double value)
    {
        DecimalFormat format=new DecimalFormat("0.00");
        DecimalFormatSymbols symbols=format.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(symbols);
        format.setDecimalSeparatorAlwaysShown(true);
        return format.format(value);
    }
}
