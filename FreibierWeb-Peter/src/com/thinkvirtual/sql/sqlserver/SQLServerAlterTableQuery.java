package com.thinkvirtual.sql.sqlserver;

/** This is an initial beta of a class that will represent a query string */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.crossdb.sql.AlterTableQuery;
import com.crossdb.sql.Column;
import com.crossdb.sql.WhereClause;

public class SQLServerAlterTableQuery implements AlterTableQuery {
	//Query q;
	String query1;
	
	String table;
	
	
	List drops;
	List adds;
	

	
	WhereClause wclause = new WhereClause();
	
	public SQLServerAlterTableQuery(){
		query1 = "";
		drops = new ArrayList();
		adds = new ArrayList();
	}
	
	
	
	
	public void setTable(String table){
		this.table = table;
	}

	public void addColumn(Column col){
		adds.add(col);
		
	}
	public void dropColumn(String colname){
		drops.add(colname);
	}
	
	public String toString(){
		String ret = "ALTER TABLE " + table + " ";
		//String query2b = " ) VALUES ( ";
		//pr("col=" + cols.size() + " - " + dfs.length);
		//int m2 = 0;
		for(int i = 0; i < adds.size(); i++){
			Column col = (Column)(adds.get(i));
			ret += "ADD " + col.getName() + " " + SQLServerDataTypes.getAsString(col);
			if(col.isNullable() == 0){
				ret += " NOT NULL ";
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
	/*public int execute(Connection conn) throws SQLException {
		q = new 	Query(conn);
		return q.executeStatement(toString());
	 }*/
	
	
	public int execute(Statement stmt) throws SQLException{
		//q = new 	Query(conn);
		//rs = stmt.executeQuery(querystring);
		return stmt.executeUpdate(toString());
	}
	public int execute(Connection conn) throws SQLException{
		//q = new 	Query(conn);
		Statement stmt = conn.createStatement();
		return execute(stmt);
		
		
	}
	
}
