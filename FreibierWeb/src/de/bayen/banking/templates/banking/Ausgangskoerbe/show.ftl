<#-- Erzeugt am 02.04.2005 von tbayen
     $Id: show.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#assign title="Ausgangskorb '${record.getField(fields[0]).format()}'"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name="Ausgangskörbe">
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="anderen Ausgangskorb wählen">
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
    <div class="forscreen">
      <a href="<@call view="editform"/>">Diese Daten ändern</a>
    </div>
  </td></tr><tr><td class="layout">

  <#assign thislist=lists[0]> <#-- Das sind die Transaktionen -->
  </td></tr><tr><td>
  <h3>${thislist.name}</h3>
  <table class="fill">
    <#assign sublist=thislist/>  <#-- ein Grossteil hier ist kopiert von "unterliste" -->
    <tr><td class="layout"></td>
      <#list sublist.fields as feld>
        <@feldheader feld=feld/>
      </#list>
    </tr>
    <#list sublist.list as record>
      <tr>
        <@recordrow_short fields=sublist.fields tablename=sublist.tablename 
                          record=record/>
        <td class="layout icon">
          <@icon name="bomb" alt="löschen" title="löschen" 
                 action="delete" table=sublist.tablename 
                 id=record.getFormatted(record.getRecordDefinition().getPrimaryKey())
          />
        </td>
      </tr>
    </#list>
  </table>
  <@sublistnewbutton sublist=thislist/>

  <#-- Summe der Transaktionen ausrechnen und ausgeben: -->   
  <#assign summe=0/>
  <#list lists[0].list as record>
    <#assign summe=summe+record.getField('Betrag').getValue()/>
  </#list>
  Summe der Transaktionen: ${summe?string.currency}<br/>
  <div class="forscreen">
    <h2>Funktionen:</h2>
      <ul>
        <li>
          <@link action="copy2pool" view="show">
            Ausgangskorb leeren und in den Ausgangspool geben</@link>
          (vorher drucken!)
        </li><li>
          <@link action="list" view="list" table="Pool" id="-">
            Auftrags-Pool ansehen</@link>
          (um Aufträge zur Bank zu versenden)
        </li>
      </ul>
  </div>
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