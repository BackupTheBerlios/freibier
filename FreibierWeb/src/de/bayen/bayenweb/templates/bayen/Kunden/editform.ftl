<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: editform.ftl,v 1.1 2005/04/05 21:34:47 tbayen Exp $ -->
<#assign title="Kundenstammdaten für '${record.getFormatted(fields[0])}'"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name=uri.table/>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur aktuellen Tabelle"/>
<@page>
  <table class="page">
    <colgroup>
      <#-- Beide Spalten immer genau gleichgroß -->
      <col width="50%" span="2"/>
    </colgroup>
    <form name="form" action="<@call action="edit" view="editform"/>" method="post">
    <#-- Das Primärfeld als Index muss mit übergeben werden:
         Schreibe ich das in die URL, geht das bei method="get" schief. -->
    <input type="hidden" name="${primarykey}" value="${record.getField(primarykey).format()}"/>
    <tr>
      <td colspan="2">
        <h3>Objekt-Informationen</h3>
      </th>
    </tr><tr>
      <td class="nopadding">
        <table class="fill">
          <tr><@dateneingabefeld feld="Durstname" titel="DURST-Name" record=record/></tr>
          <tr><@dateneingabefeld feld="Betreiber"  record=record/></tr>
          <tr><@dateneingabefeld feld="Objektname"  record=record/></tr>
          <tr><@dateneingabefeld feld="Strasse"  record=record/></tr>
          <tr>
            <@feldheader feld="" titel="PLZ/Ort"/>
            <td class="nowrap maxwidth">
              <@feldeingabefeld feld="PLZ"  record=record/>
              &nbsp;
              <@feldeingabefeld feld="Ort"  record=record/>
            </td>
          </tr>
          <#if (lists[0].list?size>0)>
            <tr>
              <@feldheader feld="" titel="Telefon"/>
              <td class="nowrap">
                <span class="data">
                  ${lists[0].list[0].getFormatted("Telefon")}
                </span>
                <#if lists[0].list[0].getFormatted("Bemerkung") != "">
                  &nbsp;
                  <span class="data">
                    ${lists[0].list[0].getFormatted("Bemerkung")}
                  </span>
                </#if>
              </td>
            </tr>
          </#if>
        </table>
      </td>
      <td class="nopadding">
        <table class="fill">
          <tr><@dateneingabefeld feld="Kundennr"     record=record/></tr>
          <tr><@dateneingabefeld feld="Betreuer"     record=record/></tr>
          <tr><@dateneingabefeld feld="Lieferstatus" record=record/></tr>
        </table>
      </td>
    </tr><tr>
      <td class="empty layout" colspan=2/>   <#---------------------------------------------------->
    </tr><tr>
      <td class="nopadding" colspan="2">
        <table class="fill">
          <colgroup>
            <col/>
            <col width="25%" span=4/>
          </colgroup>
          <tr>
            <td></td>
            <th>Gaststätte</th>
            <th>Saal</th>
            <th>Biergarten</th>
            <th>Kegelbahn</th>
          </tr><tr>
            <th>Plätze/Anzahl</th>
            <td><@feldeingabefeld feld="Plaetze_Gastraum"  record=record/></td>
            <td><@feldeingabefeld feld="Plaetze_Saal"  record=record/></td>
            <td><@feldeingabefeld feld="Plaetze_Biergarten"  record=record/></td>
            <td><@feldeingabefeld feld="Plaetze_Kegelbahn"  record=record/></td>
          </tr>
        </table>
      </td>
    </tr><tr>
      <td class="empty layout" colspan=2/>   <#---------------------------------------------------->
    </tr><tr>
      <td class="nopadding">
        <table class="fill">
          <tr><@dateneingabefeld feld="Oeffnungszeit" titel="Öffnungszeiten"  record=record/></tr>
          <tr><@dateneingabefeld feld="Ruhetage"  record=record/></tr>
          <tr><@dateneingabefeld feld="Anruftag"  record=record/></tr>
          <tr><@dateneingabefeld feld="Liefertag"  record=record/></tr>
        </table>
      </td><td class="nopadding">
        <table class="fill">
          <tr><@dateneingabefeld feld="Betriebstyp"  record=record/></tr>
          <tr><@dateneingabefeld feld="Bierkuehlung" titel="Bierkühlung"  record=record/></tr>
          <tr><@dateneingabefeld feld="Sortiment"  record=record/></tr>
          <tr><@dateneingabefeld feld="Bindung"  record=record/></tr>
        </table>
      </td>
    </tr><tr>
      <td class="empty layout" colspan=2/>   <#---------------------------------------------------->
    </tr><tr>
      <td>
        <h3>Betreiber-Informationen privat</h3>
      </td><td>
        <h3>Hauseigentümer-Informationen</h3>
      </td>
    </tr><tr>
      <td class="nopadding">
        <div class="nogrow">
          <table class="fill">
            <tr><@dateneingabefeld feld="Privat_Strasse" titel="Strasse"  record=record/></tr>
            <tr>
              <@feldheader feld="" titel="PLZ/Ort"/>
              <td class="nowrap maxwidth">
                <@feldeingabefeld feld="Privat_PLZ"  record=record/>
                &nbsp;
                <@feldeingabefeld feld="Privat_Ort"  record=record/>
              </td>
            </tr>
            <tr><@dateneingabefeld feld="Privat_Geb" titel="Geburtstag"  record=record/></tr>
            <tr><@dateneingabefeld feld="Betreiber_seit" titel="Betreiber seit"  record=record/></tr>
            <tr><@dateneingabefeld feld="Steuernr"  record=record/></tr>
          </table>
        </div>
      </td><td class="nopadding">
        <div class="nogrow maxheight">
          <table class="fill maxheight">
            <tr><@dateneingabefeld feld="Hauseigentuemer" titel="Hauseigent."  record=record/></tr>
            <tr><@dateneingabefeld feld="HE_Strasse"  titel="Strasse"  record=record/></tr>
            <tr>
              <@feldheader feld="" titel="PLZ/Ort"/>
              <td class="nowrap maxwidth">
                <@feldeingabefeld feld="HE_PLZ"  record=record/>
                &nbsp;
                <@feldeingabefeld feld="HE_Ort"  record=record/>
              </td>
            </tr>
            <#-- der style-Eintrag hier ist sonst nicht mein Stil :-) aber das ist nur dazu da,
                 daß es vernünftig aussieht, wenn im Ausdruck Spalten zusammengeschoben werden -->
            <tr class="maxheight"><td class="layout" style="border-left-style:solid"/></tr>
          </table>
        </div>
      </td>
    </tr><tr>
      <td class="empty layout" colspan=2/>   <#---------------------------------------------------->
    </tr><tr>
      <td>
        <h3>Kontaktliste intern</h3>
      </td><td>
        <h3>Kontaktliste</h3>
      </td>
    </tr><tr>
      <td class="nopadding">
        <table class="fill">
          <colgroup>
            <col/>  <col width="65%"/>  <col width="35%"/>
          </colgroup>
          <@unterliste sublist=lists[0]/>
        </table>
      </td><td class="nopadding">
        <table class="fill">
          <colgroup>
            <col/>  <col width="65%"/>  <col width="35%"/>
          </colgroup>
          <@unterliste sublist=lists[1]/>
          <tr><td class="layout icon empty"></td><td></td><td></td></tr>  <#-- Freizeile zum Nachtragen -->
          <tr><td class="layout icon empty"></td><td></td><td></td></tr>  <#-- Freizeile zum Nachtragen -->
          <tr><td class="layout icon empty"></td><td></td><td></td></tr>  <#-- Freizeile zum Nachtragen -->
          <tr><td class="layout"></td><td class="layout" colspan="2"><@sublistnewbutton sublist=lists[1]/></td></tr>
        </table>
      </td>
    </tr><tr>
      <td class="empty layout" colspan=2/>   <#---------------------------------------------------->
    </tr><tr>
      <td colspan="2">
        <h3>Bemerkungen</h3>
      </td>
    </tr><tr class="maxheight">
      <td colspan="2">
        <div class="forscreen">
          <@feldeingabefeld feld="Bemerkungen"  record=record/>
        </div>
        <div class="forprint">
        <@feldausgabe feld="Bemerkungen"  record=record/>
        </div>
      </td>
    </tr>
  </table>
  </div>
  <div class="forscreen">
    <button name="submit" type="submit" value="ok">Bestätigen</button>
  </form>
</@page>

<#--
* $Log: editform.ftl,v $
* Revision 1.1  2005/04/05 21:34:47  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.3  2005/04/05 21:14:11  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.2  2005/03/22 19:28:27  tbayen
* Telefonbuch als zweite Applikation
* Behebung einiger Bugs; jetzt Version 1.2
*
* Revision 1.1  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.7  2005/03/19 12:55:04  tbayen
* Zwischenmodell entfernt, stattdessen
* direkter Zugriff auf Freibier-Klassen
*
* Revision 1.6  2005/03/02 18:02:56  tbayen
* Probleme mit leeren Tabellen behoben
* Einige Änderungen wie mit Günther besprochen
*
* Revision 1.5  2005/02/28 11:18:28  tbayen
* Layout verfeinert
*
* Revision 1.4  2005/02/28 01:28:48  tbayen
* Import-Funktion eingebaut
* Diese Version erstmalig auf dem Produktionsserver installiert
*
* Revision 1.3  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.1  2005/02/26 16:31:02  tbayen
* versciedene Verbesserungen in den Templates
*
* Revision 1.10  2005/02/26 13:16:52  tbayen
* Datenbank-Deklaration und Properties geändert
*
* Revision 1.9  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
* Revision 1.8  2005/02/26 10:11:15  tbayen
* Untertabellen viel schöner angezeigt
*
* Revision 1.7  2005/02/26 10:03:46  tbayen
* Anzeige von Icons verbessert
*
* Revision 1.6  2005/02/25 21:44:07  tbayen
* Löschen von Datensätzen
* Formulare per HTTP-POST-Methode
*
* Revision 1.5  2005/02/24 13:24:59  tbayen
* Referenzen und Listen funktionieren jetzt!
*
* Revision 1.4  2005/02/24 12:1:46  tbayen
* Anzeigen von Referenzen (Editieren noch nicht)
*
* Revision 1.3  2005/02/24 11:48:33  tbayen
* automatische Aktivierung des ersten Eingabefeldes
*
* Revision 1.2  2005/02/24 00:35:28  tbayen
* Listen funktionieren, Datensätze anlegen funktioniert
*
* Revision 1.1  2005/02/23 11:40:58  tbayen
* recht taugliche Version mit Authentifizierung und
* Trennung von allgem. und applik.-spezifischen Dingen
*
-->