<#-- Erzeugt am 26.02.2005 von tbayen
     $Id: editmacros.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ 
     
     Diese Datei enthält verschiedene Makros, die die Arbeit von "editform.ftl"
     einfacher und übersichtlicher machen 
-->
<#include "showmacros.ftl"/>
<#include "listmacros.ftl"/>

<#-- Eingabefeld für das übergebene Datenfeld
--------------------------------------------->
<#macro feldeingabefeld feld record>
  <#assign objekt=record.getField(feld)>
  <#if objekt.getReadonly() >
    <span class="data">
      <@feldausgabe feld=feld record=record/>
    </span>
    <input type="hidden" name="_${feld}" value="${objekt.format()}"/>
  <#else/>

    <#if objekt.getProperty("foreignkey.table")?exists >
      <#assign indexcolumn=objekt.getProperty("foreignkey.indexcolumn")/>
      <#assign resultcolumn=objekt.getProperty("foreignkey.resultcolumn")/>
      <#assign foreigntable=objekt.getProperty("foreignkey.table")/>
      <select name="_${feld}">
      <#list objekt.getTypeDefinition().getPossibleValues() as opt>
        <#assign index = "opt.${indexcolumn}.format()"?eval/>
        <#assign result = "opt.${resultcolumn}.format()"?eval/>
	    <#if index=objekt.format()>
          <#assign selected=result/>
          <option selected="selected" value="${index}">${result}</option>
	    <#else/>
          <option value="${index}">${result}</option>
        </#if>
      </#list>
      </select>
      <a class="imagelink" href="<@call action="search" view="editform" table=foreigntable id="-" params={"_${indexcolumn}":objekt.format()}/>">
        <#if selected?exists>
          <@icon name="link" alt=">" title="anzeigen von '${selected}'"/>
        </#if>
      </a>
    <#else/>

      <#if objekt.getTypeDefinition().getStringType() = "blob">
        <@feldausgabe feld=feld record=record/>&nbsp;ersetzen:<input name="_${feld}" type="file" size="15" maxlength="${objekt.getLength()}"/>
  	  </#if>
      <#if objekt.getTypeDefinition().getStringType() = "char">
  	    <input name="_${feld}" type="text" value="${objekt.format()}" size="${objekt.getLength()}" maxlength="${objekt.getLength()}"/>
  	  </#if>
      <#if objekt.getTypeDefinition().getStringType() = "int">
        <input name="_${feld}" type="text" value="${objekt.format()}" size="${objekt.getLength()}" maxlength="${objekt.getLength()}"/>
      </#if>
      <#if objekt.getTypeDefinition().getStringType() = "decimal">
        <input name="_${feld}" type="text" value="${objekt.format()}" size="${objekt.getLength()}" maxlength="${objekt.getLength()}"/>
      </#if>
      <#if objekt.getTypeDefinition().getStringType() = "text">
        <textarea name="_${feld}" cols="80" rows="10">${objekt.format()}</textarea>
      </#if>
      <#if objekt.getTypeDefinition().getStringType() = "bool">
        <select name="_${feld}">
	    <#if objekt.format()=="true">
          <option selected="selected" value="true">Ja</option>
          <option value="false">Nein</option>
	    <#else/>
          <option value="true">Ja</option>
          <option selected="selected" value="false">Nein</option>
        </#if>
      </#if>

    </#if>
  </#if>
</#macro>


<#macro dateneingabefeld feld record titel="">
  <@feldheader feld=feld titel=titel/>
  <td class="maxwidth nowrap">
    <@feldeingabefeld feld=feld record=record/>
  </td>
</#macro>



<#macro sublistnewbutton sublist>
  <div class="forscreen">
    <@link action="new" view="editform" table=sublist.tablename id="-" 
           params={"_${sublist.indexcolumn}":record.getField(primarykey).format()}>
      Neu anlegen
    </@link>
  </div>
</#macro>

<#--
* $Log: editmacros.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.11  2005/03/28 15:53:03  tbayen
* Boolean-Typen eingeführt
*
* Revision 1.10  2005/03/28 03:09:45  tbayen
* Binärdaten (BLOBS) in der Datenbank und im Webinterface
*
* Revision 1.9  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.8  2005/03/19 12:55:04  tbayen
* Zwischenmodell entfernt, stattdessen
* direkter Zugriff auf Freibier-Klassen
*
* Revision 1.7  2005/03/02 18:02:56  tbayen
* Probleme mit leeren Tabellen behoben
* Einige Änderungen wie mit Günther besprochen
*
* Revision 1.6  2005/02/28 11:18:28  tbayen
* Layout verfeinert
*
* Revision 1.5  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.4  2005/02/26 16:31:02  tbayen
* versciedene Verbesserungen in den Templates
*
* Revision 1.3  2005/02/26 13:26:53  tbayen
* editmacros.ftl aufgeräumt und formatiert
*
* Revision 1.2  2005/02/26 13:16:52  tbayen
* Datenbank-Deklaration und Properties geändert
*
* Revision 1.1  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
-->