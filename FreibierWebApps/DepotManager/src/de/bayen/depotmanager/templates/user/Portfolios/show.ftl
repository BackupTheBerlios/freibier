<#-- Erzeugt am 17.01.2006 von tbayen
     $Id: show.ftl,v 1.1 2006/01/21 23:20:50 tbayen Exp $ -->
<#assign title="${record.getField(fields[0])}"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name=uri.table>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur aktuellen Tabelle">
<#assign nomainmenu=1>
<@page>
  <h1>${title}</h1>
  <table><tr><td class="layout">
    <table><tr><td class="layout">
      <table class="fill">
        <#list fields as feld>
          <tr>
            <@datenfeld feld=feld record=record/>
          </tr>
        </#list>
      </table>
    </td></tr>
    <tr class="forscreen"><td colspan="2">
        <a href="<@call view="editform"/>">Diese Daten ändern</a>
    </td></tr>
    <tr><td class="layout">

    <#if (lists?size>0) >
      </td></tr><tr><td>
      <h2>enthaltene Listen</h2>
    </#if>
    <#list lists as thislist>
    </td></tr><tr><td>
      <h3>${thislist.name}</h3>
      <table class="fill">
        <@unterliste sublist=thislist/>
      </table>
      </#list>
    </td></tr></table>
  </td>
  <td class="layout maxwidth"></td>
  <td class="nowrap forscreen">
    <ul>
      <li><@link action="aktualisieren" view="redirect-${uri.original}">Kursdaten aktualisieren</@link></li>
      <li><@link action="list" table="Wertpapiere">Wertpapiere bearbeiten</@link></li>
      <li><@link action="list" table="Bewegungen">Transaktionen bearbeiten</@link></li>
      <li><@link theme="standard" action="tables">Administrationsmodus</@link></li>
    </ul>
  </td></tr></table>



  <h2>Aufstellung aller Transaktionen</h2>

  <table class="maxwidth">
  <#list bewegungen?values?sort_by("ISIN") as papier>
    <tr><th colspan="4">
      <h3>${papier.ISIN} - ${papier.Name}
        <span class="forscreen">
          (<a href="http://de.finance.yahoo.com/q?s=${papier.ISIN}">Yahoo</a>)
        </span>
      </h3>
    </th></tr><tr>
      <th>Datum</th>
      <th class="right">Anzahl</th>
      <th class="right">Kurs</th>
      <th class="right">Betrag</th>
    </tr><tr>
    <#list papier.einzelbewegungen as bew>
      <td>${bew.Datum}</td>
      <td class="numbers">${bew.Anzahl}</td>
      <td class="numbers">${bew.Kurs} &euro;</td>
      <td class="numbers">${bew.Betrag} &euro;</td>
    </#list>
    </tr><tr>
      <td class="layout"></td><td class="topline numbers">${papier.AnzahlNice}</td>
      <td class="layout"></td><td class="topline numbers">${papier.SummeNice} &euro;</td>
    </tr><tr>
      <td>Kurs vom ${papier.KursDatum}</td><td></td>
      <td class="numbers">${papier.KursAktuellNice} &euro;</td><td class="numbers">${papier.SummeAktuellNice} &euro;</td>
    </tr><tr>
      <td>
        <#if papier.GV="G">
          Gewinn
        <#else/>
          Verlust
        </#if>
      </td><td></td>
      <td></td><td
      <#if papier.GV="G">
        class="numbers gruen gross topline doubleunderline"
      <#else/>
        class="numbers rot gross topline doubleunderline"
      </#if>
      >${papier.GewinnNice} &euro;</td>
    </tr><tr>
      <td class="empty layout" colspan="4"></td>
    </tr>
  </#list>
    <tr><td>Gesamtbestand
    </td><td></td><td></td>
    <td class="numbers gross">${SummeNice} &euro;</td>
    </td></tr>
    <tr><td>
      <#if GV="G">
        Gesamtgewinn
      <#else/>
        Gesamtverlust
      </#if>
    </td><td></td><td></td><td
    <#if GV="G">
      class="numbers gruen gross topline doubleunderline"
    <#else/>
      class="numbers rot   gross topline doubleunderline"
    </#if>
      >${GewinnNice} &euro;
    </td></tr>
  </table>
</@page>

<#--
* $Log: show.ftl,v $
* Revision 1.1  2006/01/21 23:20:50  tbayen
* Erste Version 1.0 des DepotManagers
* erste FreibierWeb-Applikation im eigenen Paket
*
-->