<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: editform.ftl,v 1.2 2005/04/06 21:14:10 tbayen Exp $ -->
<#assign title="Lastschrift/�berweisung eingeben bzw. �ndern"/>
<#include "include/editmacros.ftl"/>
<@page>
  <h1>${title}</h1>
  <table><tr><td class="layout">
    <form name="form" enctype="multipart/form-data" action="<@call action="edit" view="show"/>" method="post">
    <#-- Das Prim�rfeld als Index muss mit �bergeben werden:
         Schreibe ich das in die URL, geht das bei method="get" schief. -->
    <input type="hidden" name="${primarykey}" value="${record.getField(primarykey).format()}"/>
    <table class="fill">
      <#list fields as feld>
        <tr>
          <#if feld=="blz"> <#-- Das finde ich nicht sch�n -->
            <th>BLZ</th>
            <td class="maxwidth nowrap">
              <@feldeingabefeld feld=feld record=record/>
            </td>
          <#else/>
            <@dateneingabefeld feld=feld record=record/>
          </#if>
        </tr>
      </#list>
    </table>
  </td></tr><tr><td class="layout">
    <table class="fill maxwidth">
      <tr>
        <td class="leftbutton oneofthree">
          <button name="submit" type="submit" value="ok">Eingabe best�t.</button>
          </form>
        </td><td class="middlebutton oneofthree">
          <form name="newbutton" action="<@call action="new" view="editform" id="-"/>" method="post">
            <button name="new" type="submit" value="ok">Neuer Datensatz</button>
          </form>
        </td><td class="rightbutton oneofthree">
          <form name="deletebutton" action="<@call action="delete"/>" method="post">
            <button name="delete" type="submit" value="ok">Diesen l�schen</button>
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
* Revision 1.2  2005/04/06 21:14:10  tbayen
* Anwenderprobleme behoben,
* redirect-view implementiert
* allgemeine Verbesserungen der Oberfl�che
*
* Revision 1.1  2005/04/05 21:34:46  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:08  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->