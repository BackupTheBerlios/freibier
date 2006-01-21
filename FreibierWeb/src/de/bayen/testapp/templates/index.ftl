<#-- Erzeugt am 20.01.2006 von tbayen
     $Id: index.ftl,v 1.1 2006/01/21 23:10:10 tbayen Exp $ -->
<#assign title="Web-Applikation 'testapp'"/>
<#include "include/macros.ftl"/>
<#assign redirect><@call action="tables" table="-" id="-"/></#assign>
<#include "include/macros.ftl"/>
<@page>
  <h1>Depotmanager</h1>
  Sollten Sie nicht automatisch zur Startseite umgeleitet werden, so
  <a href="${redirect}">klicken Sie hier</a>.
</@page>
<#--
* $Log: index.ftl,v $
* Revision 1.1  2006/01/21 23:10:10  tbayen
* Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
*
-->