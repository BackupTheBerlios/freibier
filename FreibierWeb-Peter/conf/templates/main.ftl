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

  <#if (error?length > 0)><p><em>$error</em></p></#if>
  <p><a href="?action=scroll&key=up">&lt;--</a>&nbsp;<a href="?action=scroll&key=down">--&gt;</a></p>

  <table cellspacing="2" cellpadding="5" width="100%">
    <tr>
      <form action="?" method="post">
        <th bgcolor="#00eeee">
        	<input type="hidden" name="action" value="setfilter" />
        	<br />
        	<input type="submit" value="Filter" />
        </th>
	    <#list typedefinitions as typedef>
        <th bgcolor="#00eeee">
          <a href="?action=order&orderby=${typedef.name}">${typedef.name}</a><br />
   <#--       <input name="ft_${typedef.name}" type="text" value="$pattern"/> -->
        </th>
    	</#list>
      </form>
    </tr>
    <#list data as row>
      <tr>
      <#--
    #set( $key = $row.printText($row.getTable().getPrimaryKey()) )
    #if( $editkey == $key )
    	<form action="?" method="post">
	        <td bgcolor="#eeeeee">
	        	<input type="submit" value="Save" />
	        	<input type="hidden" name="save" value="#primarykey($row)" />
	        </td>
	    	#foreach ($type in $types)
	        <td bgcolor="#eeeeee">
	        	<input type="text" name="ed_$type.name" value="#format($row $type.name)" />
	        </td>
	        #end
	    </form>
    #else  -->
        <td bgcolor="#eeeeee"><a href="?action=edit&row=${row.table.primaryKey}">Edit</a></td>
    	<#list typedefinitions as typedef>
    	  <#assign fieldname="${typedef.name}">
          <td bgcolor="#eeeeee">${row[fieldname]}</td>
        </#list>
      </tr>
    </#list>  
  </table>

</@common.page>
