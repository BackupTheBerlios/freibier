<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: page.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#macro page>
<html>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-15">  <#-- voll egal! -->
  <head>
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="${uri.baseurl}/stylesheet.css" media="all"/>
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
      <a href="<@call action="tables" table="-" id="-"/>">Tabellen</a>
      <span class="desc">Auswahl einer Datentabelle</span>
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
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.6  2005/03/21 02:06:16  tbayen
* Komplette �berarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.5  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit G�nther besprochen
*
* Revision 1.4  2005/02/24 21:20:28  tbayen
* Umlaute k�nnen benutzt werden
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