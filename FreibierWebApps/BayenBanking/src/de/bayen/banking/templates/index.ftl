<#-- Erzeugt am 24.01.2006 von tbayen
     $Id: index.ftl,v 1.1 2006/01/24 22:41:56 tbayen Exp $ -->
<#assign title="BayenBanking"/>
<#include "include/macros.ftl"/>
<#assign redirect><@call theme="banking" action="tables"/></#assign>
<#include "include/macros.ftl"/>
<@page>
  <h1>${title}</h1>
  Sollten Sie nicht automatisch zur Startseite umgeleitet werden, so
  <a href="${redirect}">klicken Sie hier</a>.
</@page>
<#--
* $Log: index.ftl,v $
* Revision 1.1  2006/01/24 22:41:56  tbayen
* Suchfunktion repariert
*
-->