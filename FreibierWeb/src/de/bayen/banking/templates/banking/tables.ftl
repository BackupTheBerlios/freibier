<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: tables.ftl,v 1.1 2005/04/05 21:34:48 tbayen Exp $ -->
<#assign title="Banking-Programm"/>
<#include "include/macros.ftl"/>
<@page>
  <h1>${title}</h1>
  <h2>Bearbeitung von Überweisungen und Lastschriften</h2>
  <ul>
    <li>
      <@link action="list" table="Ausgangskoerbe" id="-">
        Ausgangskorb auswählen</@link>
      (um Überweisungen und Lastschriften zu bearbeiten)
    </li><li>
      <@link action="list" table="Pool" id="-">
        Auftrags-Pool ansehen</@link>
      (um Aufträge zur Bank zu versenden)
    </li>
  </ul>
  <h2>Kontoauszüge und Sonderfunktionen</h2>
  <ul>
    <li>
      <@link action="auszugform">Kontoauszug anzeigen</@link>
    </li><li>
      <@link action="dtausimport" view="tables">
        DTAUS-Datei importieren</@link>
      (Auftragsdateien aus fremdem Programm in den Pool holen)
    </li><li>
      <@link theme="standard" action="tables">
        Datenbank-Administrationsmodus</@link>
    </li>
  </ul>
</@page>
<#--
* $Log: tables.ftl,v $
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->