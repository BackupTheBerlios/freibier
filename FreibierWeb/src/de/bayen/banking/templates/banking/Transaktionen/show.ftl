<#-- Erzeugt am 02.04.2005 von tbayen
     $Id: show.ftl,v 1.1 2005/04/05 21:34:46 tbayen Exp $ -->
<#assign title="Last-/Gutschrift"/>
<#include "include/editmacros.ftl"/>
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

  <form name="newbutton" action="<@call action="new" view="editform" id="-"/>" method="post">
    <button name="new" type="submit" value="ok">Neuer Datensatz</button>
  </form>

</@page>

<#--
* $Log: show.ftl,v $
* Revision 1.1  2005/04/05 21:34:46  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:08  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->