<#-- Erzeugt am 05.04.2005 von tbayen
     $Id: -->
<#assign title="Pool von Ausgabe-Dateien"/>
<#include "include/listmacros.ftl"/>
<#-- Ich ändere hier die Anzeige-Reihenfolge der Felder: -->
<#assign fields=["Bezeichnung","Versandzeit","Ergebniscode","Dateiname","DTAUS"]/>
<@page>
  <h1>${title}</h1>
  <p>
  Diese Liste enthält alle Ausgabe-Dateien, die zur Bank geschickt werden können
  oder bereits wurden.</p>
  <table>
    <tr>
      <td class="layout"></td>   <#-- Eine Spalte vorher für das Icon -->
      <td class="layout"></td>   <#-- noch eine für Delete -->
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
      <tr>
        <td class="layout icon">
          <@icon name="bomb" alt="löschen" title="löschen" 
                 action="delete" table=tablename 
                 id=record.getFormatted(record.getRecordDefinition().getPrimaryKey())
          />
        <@recordrow_short fields=fields tablename=uri.table record=record/>
        </td>
      </tr>
    </#list>
  </table>

  <p>
  Ist eine Versandzeit eingetragen, ist das ein Zeichen für eine erfolgreich
  versendete Datei. Der Ergebniscode zeigt ggf. noch genauere Informationen. Im
  Fehlerfall kann der Ergebniscode sehr ausufernde Texte (Fehlermeldungen) 
  enthalten. Dies ist normal und kein Grund zur Panik.</p>
  <p>
  Mit dem roten Kreuz (<@icon name="bomb" title="löschen"/>) 
  kann man Dateien aus dem Pool löschen, wenn diese sicher versandt sind und 
  man keine Kopie mehr benötigt. Bei Problemen mit dem Versand per HBCI kann 
  man die DTAUS-Datei z.B. auf eine Diskette speichern, indem man auf das 
  Symbol in der Spalte "DTAUS" klickt.
  </p>

</@page>
<#--
* $Log: list.ftl,v $
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