<#-- Erzeugt am 02.04.2005 von tbayen
     $Id: show.ftl,v 1.2 2006/01/28 14:19:17 tbayen Exp $ -->
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
                 view="redirect-"+uri.original
                 id=record.getFormatted(record.getRecordDefinition().getPrimaryKey())
          />
        </td>
      </tr>
    </#list>
  </table>
  <#-- Ich rufe hier nicht einfach @sublistnewbutton auf, sondern setze
       ausser dem Ausgangskorb auch noch die Zahlungsart auf den Startwert -->
  <div class="forscreen">
    <@link action="new" view="editform" table=sublist.tablename id="-" 
           params={
             "_${sublist.indexcolumn}":record.getField(primarykey).format(),
             "_Zahlungsart":record.getField("Zahlungsart").format()
           }>
      Neu anlegen
    </@link>
  </div>

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
            Ausgangskorb leeren und in den Auftrags-Pool geben</@link>
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
* Revision 1.2  2006/01/28 14:19:17  tbayen
* Zahlungsart in Transaktionen ermöglicht, Abbuch. und Lastschr. zu mischen
*
* Revision 1.1  2006/01/24 00:26:01  tbayen
* Erste eigenständige Version (1.6beta)
* sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
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
-->