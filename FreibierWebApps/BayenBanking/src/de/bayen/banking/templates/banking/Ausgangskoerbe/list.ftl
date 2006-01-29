<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: list.ftl,v 1.3 2006/01/29 01:27:50 tbayen Exp $ -->
<#assign title="Ausgangskörbe auswählen"/>
<#include "include/listmacros.ftl"/>
<@page>
  <h1>${title}</h1>
  <p>
  Wählen Sie einen der Ausgangskörbe (mit <@icon name="fileopen"/>) aus, 
  um Überweisungen oder Lastschriften zu bearbeiten:</p>
  <table>
    <tr>
      <td class="layout"></td>   <#-- Eine Spalte vorher für das Icon -->
      <#list fields as feld>
      <th>
        <#if order=feld>
          <#if orderdir="ASC">
          <@link params={"order":feld, "orderdir":"DESC"}>${feld}</@link>
            <@icon name="1downarrow" alt="/\\"/>
          <#else/>
          <@link params={"order":feld, "orderdir":"ASC"}>${feld}</@link>
            <@icon name="1uparrow" alt="/\\"/>
          </#if>
        <#else/>
          <@link params={"order":feld, "orderdir":"ASC"}>${feld}</@link>
        </#if>
      </th>
      </#list>
    </tr>
    <#list list as record>
      <@recordrow fields=fields tablename=uri.table record=record/>
    </#list>
    <tr><td colspan="7" class="empty layout"></td></tr>  <#-- Leerzeile vor den Buttons -->
  </table>

  <form action="<@call action="new" view="editform"/>" method="post">
    <button name="new" type="submit" value="ok">Neuen Ausgangskorb anlegen</button>
  </form>

</@page>
<#--
* $Log: list.ftl,v $
* Revision 1.3  2006/01/29 01:27:50  tbayen
* optische Verbesserung
*
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