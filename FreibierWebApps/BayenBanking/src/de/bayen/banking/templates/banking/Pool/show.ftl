<#-- Erzeugt am 03.04.2005 von tbayen
     $Id: show.ftl,v 1.1 2006/01/24 00:26:01 tbayen Exp $ -->
<#assign title="Auftrag '${record.getField(fields[0]).format()}' aus dem Pool"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name=uri.table>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur Pool-Übersicht">
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
  </td></tr><tr><td colspan="2">
  </td></tr></table>

  <h2>Informationen aus der DTAUS-Datei</h2>
  <#if dtauslg=="L">
    <#assign dtaustyp="Lastschriften"/>
  <#else/>
    <#assign dtaustyp="Überweisungen"/>
  </#if>
  <ul>
    <li>BLZ: ${dtausblz}</li> 
    <li>Kontonummer: ${dtauskontonummer}</li>
    <li>Summe: ${dtaussumme} &euro;</li>
    <li>Lastschrift oder Überweisung: ${dtaustyp}</li>
  </ul>

  <h2>Versenden der ${dtaustyp}</h2>
  <form name="form" action="<@call action="senddtaus" view="show"/>" method="post">
  	TAN: <input name="tan" type="text" value="" size="6" maxlength="6"/>
    <button name="submit" type="submit" value="ok">${dtaustyp} zur Bank senden</button>
  </form>

  <h2>DTAUS-Datei wieder in Ausgangskorb zurückholen</h2>
  Diese Funktion dient z.B. zur Korrektur von bereits in den Pool gestellten
  Aufträgen. Bei Lastschriften wird die Information, ob es sich um einen 
  Einzug oder um eine Abbuchung handelt, nicht mit eingelesen. Sind die 
  Last-/Gutschriften wieder im Ausgangskorb, wird die Pool-Datei nicht gelöscht. 
  Dies muss ggf. von Hand gemacht werden.
  <form name="form" action="<@call action="dtausparse" view="show"/>" method="post">
    <select name="_konto">
      <#list korbliste as option>
    	<option value="${option.value}">${option.name}</option>
      </#list>  
    </select>
    <button name="submit" type="submit" value="ok">${dtaustyp} in angegebenen Ausgangskorb kopieren</button>
  </form>
</@page>

<#--
* $Log: show.ftl,v $
* Revision 1.1  2006/01/24 00:26:01  tbayen
* Erste eigenständige Version (1.6beta)
* sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
*
* Revision 1.3  2005/04/19 17:17:04  tbayen
* DTAUS-Dateien wieder einlesen in die Datenbank
*
* Revision 1.2  2005/04/06 21:14:10  tbayen
* Anwenderprobleme behoben,
* redirect-view implementiert
* allgemeine Verbesserungen der Oberfläche
*
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:11  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->