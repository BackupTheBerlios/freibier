<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: editform.ftl,v 1.2 2006/01/28 14:19:17 tbayen Exp $ -->
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
            <#-- Bei der BLZ gebe ich noch die Bank dahinter aus -->
            <th>BLZ</th>
            <td class="maxwidth nowrap">
              <@feldeingabefeld feld=feld record=record/>
              (${record_bankname})
            </td>
          <#elseif feld=="Zahlungsart"/>
            <#-- Zahlungsart darf man nur innerhalb Last- und Gutschriften ändern -->
            <@feldheader feld=feld titel=titel/>
		    <td class="maxwidth nowrap">
              <select name="_${feld}">
                <#assign objekt=record.getField(feld)>
                <#assign indexcolumn=objekt.getProperty("foreignkey.indexcolumn")/>
                <#assign resultcolumn=objekt.getProperty("foreignkey.resultcolumn")/>
                <#assign foreigntable=objekt.getProperty("foreignkey.table")/>
                <#list objekt.getTypeDefinition().getPossibleValues() as opt>
                  <#assign index = "opt.${indexcolumn}.format()"?eval/>
                  <#assign result = "opt.${resultcolumn}.format()"?eval/>
	              <#if index=objekt.format()>
                    <#assign selected=result/>
                    <option selected="selected" value="${index}">${result}</option>
	              <#elseif objekt.format()?number&lt;2 && index?number&lt;2/>
                      <option value="${index}">${result}</option>
	              <#elseif objekt.format()?number&gt;1 && index?number&gt;1/>
                      <option value="${index}">${result}</option> 
                  </#if>
                </#list>
              </select>
              <a class="imagelink" href="<@call action="search" view="show" table=foreigntable id="-" params={"_${indexcolumn}":objekt.format()}/>">
                <#if selected?exists>
                  <@icon name="fileopen" alt=">" title="anzeigen von '${selected}'"/>
                </#if>
              </a>
            </td>
          <#else/>
            <@dateneingabefeld feld=feld record=record/>
          </#if>
        </tr>
      </#list>
    </table>
  </td></tr>
  <tr><td class="layout">
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
* Revision 1.2  2006/01/28 14:19:17  tbayen
* Zahlungsart in Transaktionen ermöglicht, Abbuch. und Lastschr. zu mischen
*
* Revision 1.1  2006/01/24 00:26:01  tbayen
* Erste eigenständige Version (1.6beta)
* sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
*
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