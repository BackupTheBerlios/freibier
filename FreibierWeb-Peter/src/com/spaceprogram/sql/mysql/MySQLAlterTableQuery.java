/**
 *
 *
 * This is an initial beta of a class that will represent a query string
 *
 * @author Travis Reeder - travis@spaceprogram.com
 */


package com.spaceprogram.sql.mysql;



import com.crossdb.sql.AlterTableQuery;
import com.crossdb.sql.Column;
import com.crossdb.sql.DefaultAlterTableQuery;

public class MySQLAlterTableQuery extends DefaultAlterTableQuery implements AlterTableQuery{

	public MySQLAlterTableQuery(){
		super();
	}
	
	
	

	public String toString(){
		String ret = "ALTER TABLE " + table + " ";
		//String query2b = " ) VALUES ( ";
		//pr("col=" + cols.size() + " - " + dfs.length);
		//int m2 = 0;
		for(int i = 0; i < adds.size(); i++){
			Column col = (Column)(adds.get(i));
			ret += "ADD " + col.getName() + " " + MySQLDataTypes.getAsString(col);
            if(col.isNullable() == 0){
				ret += " NOT NULL ";
			}
			/** should this shiza be in MySQLDataTypes??
			 *
			 */
			if(col.getDefaultValue() != null){
				if(col.getType() == java.sql.Types.VARCHAR || col.getType() == java.sql.Types.CHAR){
						ret += " DEFAULT '" + col.getDefaultValue() + "' ";
				}
				else ret += " DEFAULT " + col.getDefaultValue();
			}
			ret += ",";
		}
		for(int i = 0; i < drops.size(); i++){
			//Column col = (Column)(columns.get(i));
			ret += "DROP " + drops.get(i) + ",";
		}
		ret = ret.substring(0, ret.length() -1);
		return ret;
	}

	
}
