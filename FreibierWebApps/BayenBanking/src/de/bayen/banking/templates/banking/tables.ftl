<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: tables.ftl,v 1.1 2006/01/24 00:26:01 tbayen Exp $ -->
<#assign title="Banking-Programm"/>
<#include "include/macros.ftl"/>
<@page>
  <h1>${title}</h1>
  <h2>Bearbeitung von �berweisungen und Lastschriften</h2>
  <ul>
    <li>
      <@link action="list" table="Ausgangskoerbe" id="-">
        <@icon name="mail_get" title="Ausgangskorb ausw�hlen"/>
        Ausgangskorb ausw�hlen</@link>
      (um �berweisungen und Lastschriften zu bearbeiten)
    </li><li>
      <@link action="list" table="Pool" id="-">
        <@icon name="mail_generic" title="Auftrags-Pool ansehen"/>
        Auftrags-Pool ansehen</@link>
      (um Auftr�ge zur Bank zu versenden)
    </li>
  </ul>
  <h2>Kontoausz�ge und Sonderfunktionen</h2>
  <ul>
    <li>
      <@link action="auszugform">
        <@icon name="mail_generic" title="Kontoauszug anzeigen"/>
        Kontoauszug abholen und anzeigen</@link>
    </li><li>
      <#assign redir><@call action="list" view="list" table="Pool"/></#assign>
      <@link action="dtausimport"
             view="redirect-"+redir>
        <@icon name="revert" title="DTAUS-Datei importieren"/>
        DTAUS-Datei importieren</@link>
      (Auftragsdateien aus fremdem Programm in den Pool holen)
    </li><li>
      <@link theme="standard" action="tables">
        <@icon name="fileimport" title="Datenbank-Administration"/>
        Datenbank-Administrationsmodus</@link>
      (nur f�r den Systemverwalter)
    </li>
  </ul>
</@page>
<#--
* $Log: tables.ftl,v $
* Revision 1.1  2006/01/24 00:26:01  tbayen
* Erste eigenst�ndige Version (1.6beta)
* sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
*
* Revision 1.3  2005/08/07 16:56:13  tbayen
* Produktionsversion 1.5
*
* Revision 1.2  2005/04/06 21:14:10  tbayen
* Anwenderprobleme behoben,
* redirect-view implementiert
* allgemeine Verbesserungen der Oberfl�che
*
* Revision 1.1  2005/04/05 21:34:48  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.1  2005/04/05 21:14:12  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
-->