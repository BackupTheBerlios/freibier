<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: list.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#assign title="Ausgangsk�rbe ausw�hlen"/>
<#include "include/listmacros.ftl"/>
<@page>
  <h1>${title}</h1>
  <p>
  W�hlen Sie einen der Ausgangsk�rbe (mit <@icon name="forward"/>) aus, 
  um �berweisungen oder Lastschriften zu bearbeiten:</p>
  <table>
    <tr>
      <td class="layout"></td>   <#-- Eine Spalte vorher f�r das Icon -->
      <#list fields as feld>
      <th>
        <#if order=feld>
          <#if orderdir="ASC">
          <@link params={"order":feld, "orderdir":"DESC"}>${feld}</@link>
            <@icon name="down" alt="/\\"/>
          <#else/>
          <@link params={"order":feld, "orderdir":"ASC"}>${feld}</@link>
            <@icon name="up" alt="/\\"/>
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
  </table>

  <form action="<@call action="new" view="editform"/>" method="post">
    <button name="new" type="submit" value="ok">Neu anlegen</button>
  </form>

</@page>
<#--
* $Log: list.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->