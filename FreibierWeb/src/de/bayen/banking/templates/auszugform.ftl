<#-- Erzeugt am 24.03.2005 von tbayen
     $Id: auszugform.ftl,v 1.1 2005/04/05 21:34:47 tbayen Exp $ -->
<#assign title="Auszug abholen"/>
<#include "include/editmacros.ftl"/>
<@page>
  <h1>${title}</h1>
  <form name="form" action="<@call action="auszug"/>" method="post">
    <table>
      <tr>
        <th>Konto</th>
        <td>
    	  <select name="_konto">
    	    <#list list as option>
    	      <option value="${option.value}">${option.name}</option>
    	    </#list>  
    	  </select>
        </td>
      </tr><tr>
        <th>Anfangsdatum</th>
        <td>
    	    <input name="_startdate" type="text" value="${_startdate}" 
    	           size="8" maxlength="10"/>
        </td>
      </tr><tr>
        <th>Enddatum</th>
        <td>
  	      <input name="_enddate" type="text" value="${_enddate}" 
  	             size="8" maxlength="10"/>
        </td>
      </tr>
<#--
      <tr>
        <th>Passwort</th>
        <td>
  	      <input name="_passportpassword" type="password" value="${_passportpassword}" 
  	             size="10" maxlength="20"/>
        </td>
      </tr>
-->
      <tr>
        <td colspan="2">
  	      <input name="_passportpassword" type="hidden" value="${_passportpassword}" 
  	             size="10" maxlength="20"/>
          <button name="submit" type="submit" value="ok">Auszug holen</button>
        </td>
    </table>
  </form>
</@page>

<#--
* $Log: auszugform.ftl,v $
* Revision 1.1  2005/04/05 21:34:47  tbayen
* WebDatabase 1.4 - freigegeben auf Berlios
*

-->