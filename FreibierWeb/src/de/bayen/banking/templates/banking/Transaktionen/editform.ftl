<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: editform.ftl,v 1.1 2005/04/05 21:34:46 tbayen Exp $ -->
<#assign title="Last-/Gutschrift eingeben bzw. ändern"/>
<#include "include/editmacros.ftl"/>
<@page>
  <h1>${title}</h1>
  <table><tr><td class="layout">
    <form name="form" enctype="multipart/form-data" action="<@call action="edit" view="show"/>" method="post">
    <#-- Das Primärfeld als Index muss mit übergeben werden:
         Schreibe ich das in die URL, geht das bei method="get" schief. -->
    <input type="hidden" name="${primarykey}" value="${record.getField(primarykey).format()}"/>
    <table class="fill">
      <#list fields as feld>
      <tr>
        <@dateneingabefeld feld=feld record=record/>
      </tr>
      </#list>
    </table>
  </td></tr><tr><td class="layout">
    <table class="fill maxwidth">
      <tr>
        <td class="leftbutton oneofthree">
          <button name="submit" type="submit" value="ok">Eing. bestätigen</button>
          </form>
        </td><td class="middlebutton oneofthree">
          <form name="newbutton" action="<@call action="new" view="editform" id="-"/>" method="post">
            <button name="new" type="submit" value="ok">Neuer Datensatz</button>
          </form>
        </td><td class="rightbutton oneofthree">
          <form name="deletebutton" action="<@call action="delete"/>" method="post">
            <button name="delete" type="submit" value="ok">Diesen löschen</button>
          </form>
        </td>
      </tr>
    </table>

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
    <@sublistnewbutton sublist=thislist/>
    </#list>
  </td></tr></table>
</@page>

<#--
* $Log: editform.ftl,v $
* Revision 1.1  2005/04/05 21:34:46  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:08  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->