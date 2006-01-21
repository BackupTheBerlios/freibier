<#-- Erzeugt am 20.01.2006 von tbayen
     $Id: index.ftl,v 1.1 2006/01/21 23:20:50 tbayen Exp $ -->
<#assign title="Depotmanager"/>
<#include "include/macros.ftl"/>
<#assign redirect><@call theme="user" action="show" table="Portfolios" id="1"/></#assign>
<#include "include/macros.ftl"/>
<@page>
  <h1>Depotmanager</h1>
  Sollten Sie nicht automatisch zur Startseite umgeleitet werden, so
  <a href="${redirect}">klicken Sie hier</a>.
</@page>
<#--
* $Log: index.ftl,v $
* Revision 1.1  2006/01/21 23:20:50  tbayen
* Erste Version 1.0 des DepotManagers
* erste FreibierWeb-Applikation im eigenen Paket
*
-->