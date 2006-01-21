<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: tables.ftl,v 1.1 2006/01/21 23:20:50 tbayen Exp $ -->
<#assign title="Tabellenauswahl der Datenbank '${databasename}'"/>
<#include "include/macros.ftl"/>
<@page>
  <h1>${title}</h1>
  <ul>
    <#list list as tabelle>
    <li>
      <a href="<@call action="list" table=tabelle id="-"/>">${tabelle}</a>
    </li>
    </#list>
  </ul>
  <h1>Sonderfunktionen</h1>
  <ul>
    <li><a href="user-show/Portfolios/1">Depot-Manager: erstes Depot anzeigen</a>.
    <li><a href="<@call action="aktualisieren"/>">Daten aktualisieren</a></li>
    <li><a href="<@call theme="standard" action="tables"/>">Administrationsmodus</a></li>
  </ul>
</@page>
<#--
* $Log: tables.ftl,v $
* Revision 1.1  2006/01/21 23:20:50  tbayen
* Erste Version 1.0 des DepotManagers
* erste FreibierWeb-Applikation im eigenen Paket
*
-->