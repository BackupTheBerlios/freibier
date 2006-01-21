<#-- Erzeugt am 15.01.2006 von tbayen
     $Id: aktualisieren.ftl,v 1.1 2006/01/21 23:20:50 tbayen Exp $ -->
<#assign title="Aktualisieren von Wertpapier-Daten"/>
<#include "include/macros.ftl"/>
<@page>
  <h1>${title}</h1>
  Es wurden ${gelesen} Kursdaten gelesen 
  und ${geschrieben} davon als neu in der Datenbank gespeichert.
</@page>
<#--
* $Log: aktualisieren.ftl,v $
* Revision 1.1  2006/01/21 23:20:50  tbayen
* Erste Version 1.0 des DepotManagers
* erste FreibierWeb-Applikation im eigenen Paket
*
-->