<#-- Erzeugt am 26.02.2005 von tbayen
     $Id: listmacros.ftl,v 1.3 2005/04/07 20:32:49 tbayen Exp $ 
     
     Diese Datei enthält verschiedene Makros, die die Arbeit von "list.ftl"
     einfacher und übersichtlicher machen 
-->

<#include "macros.ftl"/>

<#-- Erzeugt eine einzelne Zeile einer Tabelle
---------------------------------------------->
<#macro recordrow fields tablename record>
  <tr>
    <@recordrow_short fields=fields tablename=tablename record=record/>
    <td class="layout icon">
      <@icon name="bomb" alt="löschen" title="löschen" 
             action="delete" table=tablename 
             view="redirect-"+uri.original
             id=record.getFormatted(record.getRecordDefinition().getPrimaryKey())
      />
    </td>
  </tr>
</#macro>



<#-- Erzeugt eine einzelne Zeile einer Tabelle ohne Delete-Icon (unnd ohne <tr>)
-------------------------------------------------------------------------------->
<#macro recordrow_short fields tablename record>
  <td class="layout icon">
    <@icon name="edit" alt=">" title="ändern von ${record.getField(0).format()}" 
           action="show" view="editform" table=tablename
           id=record.getFormatted(record.getRecordDefinition().getPrimaryKey())
    />
    <@icon name="fileopen" alt=">" title="anzeigen von ${record.getField(0).format()}" 
           action="show" view="show" table=tablename
           id=record.getFormatted(record.getRecordDefinition().getPrimaryKey())
    />
  </td>
  <#list fields as feld>
    <#assign align=record.getRecordDefinition().getFieldDef(feld).getProperty("align")?default("left")/>
    <#if record.getField(feld).getLength() < 100>
      <td class="nowrap ${align}">
    <#else/>
      <td class="${align}">
    </#if>
    <@feldausgabe feld=feld tablename=tablename record=record/>
    </td>
    </#list>
</#macro>



<#-- Gibt ein einzelnes Datenfeld aus (erzeugt bei Foreign Keys ggf. einen Link)
-------------------------------------------------------------------------------->
<#macro feldausgabe feld record tablename="">
  <#assign objekt=record.getField(feld)>
  <#if (feld=record.getRecordDefinition().getFieldDef(0).getName() || feld="id") && tablename != "">
    <@link action="show" view="show" table=tablename id=record.getFormatted(record.getRecordDefinition().getPrimaryKey()) title="anzeigen von ${objekt.format()}">
      ${objekt.formatNice()}
    </@link>
  <#else/>
    <#if objekt.getProperty("foreignkey.table")?exists>
      <@link action="search" view="show" table=tablename table=objekt.getProperty("foreignkey.table") id="-" params={"_"+objekt.getProperty("foreignkey.indexcolumn"):objekt.format()} title="anzeigen">
        ${objekt.formatNice()}
      </@link>
    <#else/>
      <#if objekt.getTypeDefinition().getStringType() = "blob">
        <@icon name="filesave" 
               action="download" view="binarydata" table=tablename 
               id="${record.getField(record.getRecordDefinition().getPrimaryKey()).getValue()}/${feld}"
               params={"field":feld} 
               title="${objekt.formatNice()}"/>
      <#else/>
        <#if objekt.getTypeDefinition().getStringType() = "bool">
          <#if objekt.format()=="true">
            <@icon name="button_ok" alt="Ja" title="Ja"/>
          <#else/>
            <@icon name="button_cancel" alt="Nein" title="Nein"/>
          </#if>
        <#else/>
          <#if objekt.getLength() &gt; 50>
            <pre>${objekt.formatNice()}</pre>   <#-- pre für Textfelder -->
          <#else/>
            ${objekt.formatNice()}
          </#if>
        </#if>
  	  </#if>
    </#if>
  </#if>
</#macro>



<#--
* $Log: listmacros.ftl,v $
* Revision 1.3  2005/04/07 20:32:49  tbayen
* Ausrichtung von Zahlenfeldern korrigiert
*
* Revision 1.2  2005/04/06 21:14:10  tbayen
* Anwenderprobleme behoben,
* redirect-view implementiert
* allgemeine Verbesserungen der Oberfläche
*
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.7  2005/03/28 15:53:03  tbayen
* Boolean-Typen eingeführt
*
* Revision 1.6  2005/03/28 03:09:45  tbayen
* Binärdaten (BLOBS) in der Datenbank und im Webinterface
*
* Revision 1.5  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.4  2005/03/19 12:55:04  tbayen
* Zwischenmodell entfernt, stattdessen
* direkter Zugriff auf Freibier-Klassen
*
* Revision 1.3  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.2  2005/02/26 16:31:02  tbayen
* versciedene Verbesserungen in den Templates
*
* Revision 1.1  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
-->