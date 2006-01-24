<#-- Erzeugt am 24.03.2005 von tbayen
     $Id: auszug.ftl,v 1.1 2006/01/24 00:26:01 tbayen Exp $ -->
<#assign title="aktueller Kontoauszug"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name="anderer Auszug"/>
<#assign menu1_link><@call action="auszugform" table="-" id="-"/></#assign>
<#assign menu1_desc="Zum Auswahlformular für Kontoauszüge"/>
<@page>
<#if auszug?exists>
  <#list auszug as buchungstag>
    <table class="maxwidth">
      <colgroup>
        <col width="15%" span="1"/>
        <col width="45%" span="1"/>
        <col width="20%" span="2"/>
      </colgroup>
      <tr>
        <td class="layout maxwidth" colspan="5">
          <table class="maxwidth">
            <colgroup>
              <col width="25%" span="4"/>
            </colgroup>
            <tr>
              <th>Auszug vom</th>
              <th>Kontonummer</th>
              <th>Kreditinstitut</th>
              <th>BLZ</th>
            </tr><tr>
              <td>${buchungstag.buchungsdatum}</td>
              <td>${buchungstag.kontonummer}</td>
              <td>${buchungstag.bank}</td>
              <td>${buchungstag.blz}</td>
            </tr>
          </table>
        </td>
      </tr><tr>
        <td class="empty layout" colspan=2/>   <#---------------------------------------------------->
      </tr><tr>
        <th>Datum</th>
<#--        <th>Valuta</th>-->
        <th>Text</th>
        <th>Betrag</th>
        <th>Saldo</th>
      </tr><tr>
          <td class="borderlesslist underline"></td>
<#--          <td class="borderlesslist underline"></td>-->
          <td class="borderlesslist underline">Anfangssaldo</td>
          <td class="borderlesslist underline"></td>
          <td class="borderlesslist underline numbers">${buchungstag.startsaldo}</td>
      </tr>
      <#list buchungstag.zeilen as buchung>
        <#if buchung.pappenheimer==1>
          <tr class="pappenheimer">
        <#else/>
          <tr>
        </#if>
          <td class="borderlesslist">${buchung.datum}</td>
<#--          <td class="borderlesslist">${buchung.valuta}</td>-->
          <#if buchung.betragsh=="S">
            <td class="borderlesslist soll">${buchung.vwz[0]?default("---")}</td>
            <td class="borderlesslist numbers soll">${buchung.betrag}</td>
          <#else/>
            <td class="borderlesslist haben">${buchung.vwz[0]?default("---")}</td>
            <td class="borderlesslist numbers haben">${buchung.betrag}</td>
          </#if>
          <td class="borderlesslist numbers">${buchung.saldo}</td>
        </tr>
        <#list buchung.vwz as vwz>
          <#if vwz != buchung.vwz[0]>
            <#if buchung.pappenheimer==1>
              <tr class="pappenheimer">
            <#else/>
              <tr>
            </#if>
              <td class="borderlesslist"></td>
<#--              <td class="borderlesslist"></td>-->
              <td class="borderlesslist">${vwz}</td>
              <td class="borderlesslist"></td>
              <td class="borderlesslist"></td>
            </tr>
          </#if>
        </#list>
        <#if buchung.pappenheimer==1>
          <tr class="pappenheimer">
        <#else/>
          <tr>
        </#if>
          <td class="borderlesslist underline"></td>
<#--          <td class="borderlesslist underline"></td>-->
          <td class="borderlesslist underline"></td>
          <td class="borderlesslist underline"></td>
          <td class="borderlesslist underline"></td>
        </tr>
      </#list>
      <tr>
        <td class="borderlesslist underline"></td>
<#--        <td class="borderlesslist underline"></td>-->
        <td class="borderlesslist underline">Endsaldo</td>
        <td class="borderlesslist underline"></td>
        <td class="borderlesslist underline numbers">${buchungstag.endsaldo}</td>
      </tr>
    </table>
    <br/>
  </#list>
<#else/>
  Kein Auszug vorhanden.
</#if>
<#--
  <hr/><h2>Auszugtext</h2>
  <pre>
    ${auszugtext?default("")}
  </pre>

  <hr/><h2>Protokoll</h2>
  <pre>
    ${hbcilog?default("")}
  </pre>
-->

  <hr/><h2>Status</h2>
  <pre>
${globstatus?default("")}
${jobstatus?default("")}
  </pre>

<#if globerror?exists && (globerror?length>0)>
  <hr/><h2>GlobError</h2>
  <pre>
    ${globerror}
  </pre>
</#if>
  
<#if joberror?exists && (joberror?length>0)>
  <hr/><h2>JobError</h2>
  <pre>
    ${joberror?default("")}
  </pre>
</#if>

</@page>
<#--
* $Log: auszug.ftl,v $
* Revision 1.1  2006/01/24 00:26:01  tbayen
* Erste eigenständige Version (1.6beta)
* sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
*
* Revision 1.1  2005/04/05 21:34:47  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*

-->