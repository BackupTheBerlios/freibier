<#-- Erzeugt am 20.01.2006 von tbayen
     $Id: index.ftl,v 1.1 2006/01/21 23:10:10 tbayen Exp $ -->
<#assign title="Einstiegsseite der Webapplikation '${uri.context}/${uri.servlet}'"/>
<#include "include/macros.ftl"/>
<@page>
  <h1>Einstiegsseite der Webapplikation '${uri.context}/${uri.servlet}'</h1>
  Um Daten zu bearbeiten, w�hlen Sie im obigen Men� die �bersicht der Tabellen.
</@page>
<#--
* $Log: index.ftl,v $
* Revision 1.1  2006/01/21 23:10:10  tbayen
* Komplette �berarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
*

-->