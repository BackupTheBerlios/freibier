<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: editform.ftl,v 1.3 2005/08/07 16:56:13 tbayen Exp $ -->
<#assign title="Lastschrift/Überweisung eingeben bzw. ändern"/>
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
          <#if feld=="BLZ">
            <th>BLZ</th>
            <td class="maxwidth nowrap">
              <@feldeingabefeld feld=feld record=record/>
              (${record_bankname})
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
        <td class="leftbutton oneoffour">
          <button name="submit" type="submit" value="ok">Eingabe bestät.</button>
        </td><td class="middlebutton oneoffour">
          <button type="submit" name="action" value="searchvorlage" 
                  title="Nach Vorlage suchen">
            Vorlage <@icon name="find" alt="?"/>
          </button>
          </form>
        </td><td class="middlebutton oneoffour">
          <form name="newbutton" action="<@call action="new" view="editform" id="-"/>" method="post">
            <button name="new" type="submit" value="ok">Neuer Datensatz</button>
          </form>
        </td><td class="rightbutton oneoffour">
          <form name="deletebutton" action="<@call action="delete"/>" method="post">
            <button name="delete" type="submit" value="ok">Diesen löschen</button>
          </form>
        </td>
      </tr>
    </table>
</@page>

<#--
* $Log: editform.ftl,v $
* Revision 1.3  2005/08/07 16:56:13  tbayen
* Produktionsversion 1.5
*
* Revision 1.2  2005/04/06 21:14:10  tbayen
* Anwenderprobleme behoben,
* redirect-view implementiert
* allgemeine Verbesserungen der Oberfläche
*
* Revision 1.1  2005/04/05 21:34:46  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:08  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->