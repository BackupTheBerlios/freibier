<#-- Erzeugt am 03.04.2005 von tbayen
     $Id: show.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#assign title="Auftrag '${record.getField(fields[0]).format()}' aus dem Pool"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name=uri.table>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur Pool-�bersicht">
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
    <a href="<@call view="editform"/>">Diese Daten editieren</a>
  </td></tr><tr><td colspan="2">
  </td></tr></table>
  <h2>Informationen aus der DTAUS-Datei</h2>
  <#if dtauslg=="L">
    <#assign dtaustyp="Lastschriften"/>
  <#else/>
    <#assign dtaustyp="Gutschriften"/>
  </#if>
  <ul>
    <li>BLZ: ${dtausblz}</li> 
    <li>Kontonummer: ${dtauskontonummer}</li>
    <li>Summe: ${dtaussumme} &euro;</li>
    <li>Last- oder Gutschrift: ${dtaustyp}</li>
  </ul>
  <h2>Versenden der ${dtaustyp}</h2>
  <form name="form" action="<@call action="senddtaus" view="show"/>" method="post">
  	TAN: <input name="tan" type="text" value="" size="6" maxlength="6"/>
    <button name="submit" type="submit" value="ok">${dtaustyp} zur Bank senden</button>
  </form>
</@page>

<#--
* $Log: show.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:11  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->