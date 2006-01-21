<#-- Erzeugt am 26.02.2005 von tbayen
     $Id: macros.ftl,v 1.2 2006/01/21 23:10:10 tbayen Exp $ 
     
     Diese Datei enthält verschiedene Makros, die die Arbeit der anderen Templates
     einfacher und übersichtlicher machen.
-->
<#include "page.ftl"/>

<#macro icon name alt="" title="" params={} action="" theme="" view="" table="" id="">
  <#if action != "">
    <a class="imagelink" href="<@call params=params action=action theme=theme view=view table=table id=id/>"
  </#if>
  <img class="inline" src="${
  uri.parser.createURI(uri,{
    "action":"image",
    "theme" :theme,
    "view"  :"binarydata",
    "table" :"-",
    "id"    :"-"
  },{"name":name+".png"})
}" alt="${alt}" title="${title}" border="0"/><#t/>
  <#if action != ""><#t/>
    </a><#t/>
  </#if>
</#macro>



<#macro link title="" params={} action="" theme="" view="" table="" id="">
  <a href="<@call params=params action=action theme=theme view=view table=table id=id/>"
  <#if title != "">
    title="${title}"
  </#if>
  ><#nested/></a>
</#macro>



<#--
  Ergibt eine URL, die die Applikation mit den übergebenen Parametern
  aufruft.
 -->
<#macro call params={} action="" theme="" view="" table="" id="">${
  uri.parser.createURI(uri,{
    "action":action,
    "theme" :theme,
    "view"  :view,
    "table" :table,
    "id"    :id
  },params)
}</#macro>

<#--
* $Log: macros.ftl,v $
* Revision 1.2  2006/01/21 23:10:10  tbayen
* Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
*
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.5  2005/03/28 03:09:45  tbayen
* Binärdaten (BLOBS) in der Datenbank und im Webinterface
*
* Revision 1.4  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.3  2005/03/19 12:55:04  tbayen
* Zwischenmodell entfernt, stattdessen
* direkter Zugriff auf Freibier-Klassen
*
* Revision 1.2  2005/02/26 16:31:02  tbayen
* versciedene Verbesserungen in den Templates
*
* Revision 1.1  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
-->