<#-- Erzeugt am 25.02.2005 von tbayen
     $Id: delete.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#assign title="Datensatz aus Tabelle '${uri.table}' gelöscht"/>
<#include "include/macros.ftl"/>
<#assign menu1_name=uri.table>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur aktuellen Tabelle">
<@page>
  <h1>${title}</h1>
</@page>
<#--
* $Log: delete.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.3  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.2  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.1  2005/02/25 21:44:07  tbayen
* Löschen von Datensätzen
* Formulare per HTTP-POST-Methode
*
-->