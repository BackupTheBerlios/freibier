<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: list.ftl,v 1.2 2005/04/06 21:14:10 tbayen Exp $ -->
<#assign title="Tabelle '${uri.table}'"/>
<#assign fields=["Kundennr","Durstname","Objektname","Betreiber","Strasse","Ort"]/>
<#include "include/listmacros.ftl"/>
<@page>
  <h1>${title}</h1>
  <table>
    <tr>
      <td class="layout"></td>   <#-- Eine Spalte vorher für das Icon -->
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
      <@recordrow fields=fields tablename=uri.table record=record/>
    </#list>
  </table>

  <form action="<@call action="new" view="editform"/>" method="post">
    <button name="new" type="submit" value="ok">Neu anlegen</button>
  </form>

</@page>
<#--
* $Log: list.ftl,v $
* Revision 1.2  2005/04/06 21:14:10  tbayen
* Anwenderprobleme behoben,
* redirect-view implementiert
* allgemeine Verbesserungen der Oberfläche
*
* Revision 1.1  2005/04/05 21:34:47  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*
* Revision 1.2  2005/04/05 21:14:11  tbayen
* HBCI-Banking-Applikation fertiggestellt
*
* Revision 1.1  2005/03/21 02:06:16  tbayen
* Komplette Überarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.2  2005/03/19 12:55:04  tbayen
* Zwischenmodell entfernt, stattdessen
* direkter Zugriff auf Freibier-Klassen
*
* Revision 1.1  2005/03/02 18:02:56  tbayen
* Probleme mit leeren Tabellen behoben
* Einige Änderungen wie mit Günther besprochen
*
* Revision 1.9  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit Günther besprochen
*
* Revision 1.8  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
* Revision 1.7  2005/02/25 21:44:07  tbayen
* Löschen von Datensätzen
* Formulare per HTTP-POST-Methode
*
* Revision 1.6  2005/02/24 21:20:28  tbayen
* Umlaute können benutzt werden
*
* Revision 1.5  2005/02/24 15:47:43  tbayen
* Probleme mit der Neuanlage bei ForeignKeys behoben
*
* Revision 1.4  2005/02/24 12:15:46  tbayen
* Anzeigen von Referenzen (Editieren noch nicht)
*
* Revision 1.3  2005/02/24 00:35:28  tbayen
* Listen funktionieren, Datensätze anlegen funktioniert
*
* Revision 1.2  2005/02/23 12:26:04  tbayen
* sortieren von Listen geht jetzt
*
* Revision 1.1  2005/02/23 11:40:58  tbayen
* recht taugliche Version mit Authentifizierung und
* Trennung von allgem. und applik.-spezifischen Dingen
*
-->