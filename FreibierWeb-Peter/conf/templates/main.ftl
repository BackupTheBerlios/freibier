<#import "/common.ftl" as common>

<@common.page title="Index">

  <#if tablenames?size = 0>
    <p>Keine Tabellen vorhanden</p>
  <#else>
    <table cellspacing="0" cellpadding="5">
      <tr>
        <#list tablenames as tabname>
          <td bgcolor="#ee00ee">
        	    <a href="?table=${tabname}">${tabname}</a>
          </td>
        </#list>
      </tr>
    </table>
  </#if>

  <#if (error?length > 0)><p><em>${error}</em></p></#if>
  <p><a href="?action=scroll&key=up">&lt;--</a>&nbsp;<a href="?action=scroll&key=down">--&gt;</a></p>

  <table cellspacing="2" cellpadding="5" width="100%">
    <tr>
      <form action="?" method="post">
        <th bgcolor="#00eeee">
        	<input type="hidden" name="action" value="setfilter" />
        	<br />
        	<input type="submit" value="Filter" />
        </th>
	    <#list columnnames as column>
          <th bgcolor="#00eeee">
            <a href="?action=order&orderby=${column}">${column}</a><br />
            <input name="ft_${column}" type="text" value=""/>
          </th>
    	    </#list>
      </form>
    </tr>
    <#list data as row>
      <tr>
        <#if row[row.table.primaryKey]?string == editkey >
    	  <form action="?" method="post">
	        <td bgcolor="#eeeeee">
	        	<input type="submit" value="Save" />
	        	<input type="hidden" name="row" value="${row[row.table.primaryKey]}" />
	        	<input type="hidden" name="action" value="save" />
	        </td>
            <#list columnnames as column>
              <td bgcolor="#eeeeee">
	            <input type="text" name="ed_${column}" value="${row[column]}" />
	          </td>
            </#list>
	      </form>
        <#else>
          <td bgcolor="#eeeeee"><a href="?action=edit&row=${row[row.table.primaryKey]}">Edit</a></td>
          <#list columnnames as column>
	        <td bgcolor="#eeeeee">${row[column]}</td>
	      </#list>
	    </#if>
      </tr>
    </#list>  
  </table>

</@common.page>
