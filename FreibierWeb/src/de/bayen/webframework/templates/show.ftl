<#-- Erzeugt am 02.04.2005 von tbayen
     $Id: show.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#assign title="Datensatz '${record.getField(fields[0]).format()}' aus Tabelle '${uri.table}'"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name=uri.table>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur aktuellen Tabelle">
<@page>
  <h1>${title}</h1>
  <table><tr><td class="layout">
    <table class="fill">
      <#list fields as feld>
        <tr>
          <@datenfeld feld=feld record=record/>
        </tr>
      </#list>
    </table>
  </td></tr><tr><td colspan="2">
    <a href="<@call view="editform"/>">Diese Daten ändern</a>
  </td></tr><tr><td class="layout">

  <#if (lists?size>0) >
    </td></tr><tr><td>
    <h2>enthaltene Listen</h2>
  </#if>
  <#list lists as thislist>
  </td></tr><tr><td>
    <h3>${thislist.name}</h3>
    <table class="fill">
      <@unterliste sublist=thislist/>
    </table>
    </#list>
  </td></tr></table>
</@page>

<#--
* $Log: show.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->