<#-- Erzeugt am 19.01.2006 von tbayen
     $Id: page.ftl,v 1.1 2006/01/21 23:20:50 tbayen Exp $ -->
<#macro page>
<html>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-15">  <#-- voll egal! -->
  <head>
    <title>${title}</title>
    <#-- Diese Variante lädt eine externe Datei als stylesheet:
    <link rel="stylesheet" type="text/css" href="${uri.baseurl}/stylesheet.css" media="all"/>
    -->
    <style type="text/css">
      <#-- Hier ist auch parse=true denkbar für z.B. Theme-abhängige stylesheets -->
      <#include "stylesheet.css" parse=false>
    </style>
    <#if focus?exists>
    <#if redirect?exists>
      <meta http-equiv="refresh" content="0; URL=${redirect}">
    </#if>
    <script type="text/javascript">//<![CDATA[
      function focus_onload(){ document.form._${focus}.focus() }
    //]]></script>
    </#if>
  </head>
  <#if focus?exists>
  <body onload="focus_onload()">
  <#else/>
  <body>
  </#if>
  <#if nomainmenu?default(0) = 0>
    <ul class="menu">
      <li>
        <a href="<@call action="list" table="Portfolios" id="-"/>">Portfolios</a>
        <span class="desc">Liste aller Portfolios</span>
      </li>
      <li>
        <a href="<@call action="show" table="Portfolios" id="1"/>">Portfolio anzeigen</a>
        <span class="desc">Anzeige des ersten Portfolios</span>
      </li>
      <#if menu1_name?exists>
      <li>
        <a href="${menu1_link}">${menu1_name}</a>
        <span class="desc">${menu1_desc}</span>
      </li>
      </#if>
    </ul>
  </#if>
  <#if debug?exists>
    ${debug}
  </#if>
  <#nested/>
  </body>
</html>
</#macro>
<#--
* $Log: page.ftl,v $
* Revision 1.1  2006/01/21 23:20:50  tbayen
* Erste Version 1.0 des DepotManagers
* erste FreibierWeb-Applikation im eigenen Paket
*
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.6  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.5  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.4  2005/02/24 21:20:28  tbayen
* Umlaute können benutzt werden
*
* Revision 1.3  2005/02/24 13:24:59  tbayen
* Referenzen und Listen funktionieren jetzt!
*
* Revision 1.2  2005/02/24 11:48:33  tbayen
* automatische Aktivierung des ersten Eingabefeldes
*
* Revision 1.1  2005/02/23 11:40:58  tbayen
* recht taugliche Version mit Authentifizierung und
* Trennung von allgem. und applik.-spezifischen Dingen
*
-->