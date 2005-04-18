<#-- Erzeugt am 16.04..2005 von tbayen
     $Id: exception.ftl,v 1.1 2005/04/18 10:57:55 tbayen Exp $ -->
<#assign title="Programmfehler aufgetreten"/>
<#include "include/macros.ftl"/>
<@page>
  <h1>${title}</h1>
  <ol>
    <#list exceptions as exception>
      <li>${exception.class}:<br/>
        <b>${exception.message}</b>
        <!--
          ${exception.stacktrace}
        -->
      </li>
    </#list>
  </ol>
</@page>
<#--
* $Log: exception.ftl,v $
* Revision 1.1  2005/04/18 10:57:55  tbayen
* Urlaubsarbeit:
* Eigenes View, um Exceptions abzufangen
* System von verteilten Properties-Dateien
*
-->