<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: tables.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
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
</@page>
<#--
* $Log: tables.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.5  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.3  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.2  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
* Revision 1.1  2005/02/23 11:40:58  tbayen
* recht taugliche Version mit Authentifizierung und
* Trennung von allgem. und applik.-spezifischen Dingen
*
-->