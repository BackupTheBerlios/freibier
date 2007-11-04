<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: page.ftl,v 1.2 2007/11/04 15:54:06 tbayen Exp $ -->
<#macro page>
<html>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <head>
    <title>${title}</title>
    <#-- Diese Variante lädt eine externe Datei als stylesheet:
    <link rel="stylesheet" type="text/css" href="${uri.baseurl}/stylesheet.css" media="all"/>
    -->
    <style type="text/css">
      <#-- Hier ist auch parse=true denkbar für z.B. Theme-abhängige stylesheets -->
      <#include "stylesheet.css" parse=false>
    </style>
    <#if redirect?exists>
      <meta http-equiv="refresh" content="0; URL=${redirect}">
    </#if>
    <#if focus?exists>
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
  <ul class="menu">
    <li>
      <a href="<@call action="tables" table="-" id="-"/>">Banking-Hauptseite</a>
      <span class="desc">Startseite des Banking-Programms</span>
    </li>
    <#if menu1_name?exists>
    <li>
      <a href="${menu1_link}">${menu1_name}</a>
      <span class="desc">${menu1_desc}</span>
    </li>
    </#if>
  </ul>
  <#if debug?exists>
    ${debug}
  </#if>
  <#nested/>
  </body>
</html>
</#macro>
<#--
* $Log: page.ftl,v $
* Revision 1.2  2007/11/04 15:54:06  tbayen
* Anpassung an moderneres System (MySQL 5.1, Tomcat 5.5, System mit UTF-8)
*
* Revision 1.1  2006/01/24 00:26:01  tbayen
* Erste eigenständige Version (1.6beta)
* sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
*
* Revision 1.1  2005/04/05 21:34:47  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:11  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->