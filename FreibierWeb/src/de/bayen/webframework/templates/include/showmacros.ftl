<#-- Erzeugt am 02.04.2005 von tbayen
     $Id: showmacros.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ 
     
     Diese Datei enthält verschiedene Makros, die die Arbeit von "show.ftl"
     einfacher und übersichtlicher machen 
-->
<#include "listmacros.ftl"/>

<#-- Header eines Eingabefeldes für Listen oder links in der Eingabetabelle 
--------------------------------------------------------------------------->
<#macro feldheader feld titel="" typedef="">
  <#if titel=="">
    <th>${feld}</th>
  <#else/>
    <th>${titel}</th>
  </#if>
</#macro>



<#-- Ausgabefeld für das übergebene Datenfeld
--------------------------------------------->
<#macro eingabefeld feld record>
  <span class="data">
    <@feldausgabe feld=feld record=record/>
  </span>
</#macro>



<#macro datenfeld feld record titel="">
  <@feldheader feld=feld titel=titel/>
  <td class="maxwidth nowrap">
    <@eingabefeld feld=feld record=record/>
  </td>
</#macro>



<#macro unterliste sublist>
  <tr><td class="layout"></td>
    <#list sublist.fields as feld>
      <@feldheader feld=feld/>
    </#list>
  </tr>
  <#list sublist.list as record>
    <tr>
      <@recordrow_short fields=sublist.fields tablename=sublist.tablename 
                        record=record/>
    </tr>
  </#list>
</#macro>

<#--
* $Log: showmacros.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*

-->