<#-- Erzeugt am 21.02.2005 von tbayen
     $Id: editform.ftl,v 1.3 2006/01/28 14:13:37 tbayen Exp $ -->
<#assign title="Datensatz '${record.getField(fields[0]).format()}' aus Tabelle '${uri.table}'"/>
<#include "include/editmacros.ftl"/>
<#assign menu1_name=uri.table>
<#assign menu1_link><@call action="list" id="-"/></#assign>
<#assign menu1_desc="Zur aktuellen Tabelle">
<@page>
  <h1>${title}</h1>
  <table><tr><td class="layout">
    <form name="form" enctype="multipart/form-data" action="<@call action="edit" view="show"/>" method="post">
    <#-- Das Prim�rfeld als Index muss mit �bergeben werden:
         Schreibe ich das in die URL, geht das bei method="get" schief. -->
    <input type="hidden" name="${primarykey}" value="${record.getField(primarykey).format()}"/>
    <table class="fill">
      <#list fields as feld>
      <tr>
        <@dateneingabefeld feld=feld record=record/>
      </tr>
      </#list>
    </table>
  </td></tr>
  <tr><td colspan=2 class="empty layout"></td></tr>  <#-- Leerzeile vor den Buttons -->
  <tr><td class="layout">
    <table class="fill maxwidth">
      <tr>
        <td class="leftbutton oneofthree">
          <button name="submit" type="submit" value="ok">Eingabe best�t.</button>
          </form>
        </td><td class="middlebutton oneofthree">
          <form name="newbutton" action="<@call action="new" view="editform" id="-"/>" method="post">
            <button name="new" type="submit" value="ok">Neuer Datensatz</button>
          </form>
        </td><td class="rightbutton oneofthree">
          <form name="deletebutton" action="<@call action="delete"/>" method="post">
            <button name="delete" type="submit" value="ok">Diesen l�schen</button>
          </form>
        </td>
      </tr>
    </table>

  <#if (lists?size>0) >
    </td></tr><tr><td>
    <h2>enthaltene Listen</h2>
  </#if>
  <#list lists as thislist>
  </td></tr><tr><td>
    <h3>${thislist.name}</h3>
    <table class="fill">
      <@unterliste sublist=thislist/>
    </table>
    <@sublistnewbutton sublist=thislist/>
    </#list>
  </td></tr></table>
</@page>

<#--
* $Log: editform.ftl,v $
* Revision 1.3  2006/01/28 14:13:37  tbayen
* Layout verbessert
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
* Revision 1.4  2005/03/28 03:09:46  tbayen
* Bin�rdaten (BLOBS) in der Datenbank und im Webinterface
*
* Revision 1.3  2005/03/23 18:56:01  tbayen
* Buttons im editform-view h�bsch formatiert
*
* Revision 1.2  2005/03/22 19:28:27  tbayen
* Telefonbuch als zweite Applikation
* Behebung einiger Bugs; jetzt Version 1.2
*
* Revision 1.1  2005/03/21 02:06:16  tbayen
* Komplette �berarbeitung des Web-Frameworks
* insbes. Modularisierung von URIParser und Actions
*
* Revision 1.13  2005/03/19 12:55:04  tbayen
* Zwischenmodell entfernt, stattdessen
* direkter Zugriff auf Freibier-Klassen
*
* Revision 1.12  2005/02/27 18:38:22  tbayen
* Kunden-Formuar wie mit G�nther besprochen
*
* Revision 1.11  2005/02/26 16:31:02  tbayen
* versciedene Verbesserungen in den Templates
*
* Revision 1.10  2005/02/26 13:16:52  tbayen
* Datenbank-Deklaration und Properties ge�ndert
*
* Revision 1.9  2005/02/26 12:46:05  tbayen
* Layout verbessert und Templates in Makros zerlegt
*
* Revision 1.8  2005/02/26 10:11:15  tbayen
* Untertabellen viel sch�ner angezeigt
*
* Revision 1.7  2005/02/26 10:03:46  tbayen
* Anzeige von Icons verbessert
*
* Revision 1.6  2005/02/25 21:44:07  tbayen
* L�schen von Datens�tzen
* Formulare per HTTP-POST-Methode
*
* Revision 1.5  2005/02/24 13:24:59  tbayen
* Referenzen und Listen funktionieren jetzt!
*
* Revision 1.4  2005/02/24 12:1:46  tbayen
* Anzeigen von Referenzen (Editieren noch nicht)
*
* Revision 1.3  2005/02/24 11:48:33  tbayen
* automatische Aktivierung des ersten Eingabefeldes
*
* Revision 1.2  2005/02/24 00:35:28  tbayen
* Listen funktionieren, Datens�tze anlegen funktioniert
*
* Revision 1.1  2005/02/23 11:40:58  tbayen
* recht taugliche Version mit Authentifizierung und
* Trennung von allgem. und applik.-spezifischen Dingen
*
-->