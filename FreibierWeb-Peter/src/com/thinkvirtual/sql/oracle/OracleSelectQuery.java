/**
 *
 * @author Travis Reeder - travis@spaceprogram.com
 * @version 0.1
 */

package com.thinkvirtual.sql.oracle;

import java.util.List;
import java.sql.*;
import com.crossdb.sql.*;


public class OracleSelectQuery extends DefaultSelectQuery implements SelectQuery {



	public void addWhereCondition(String pre, int comparison, java.util.Date pred){
		//Where where = new Where(pre, comparison, pred);
		SQLDateTimeFormat sqldf = new SQLDateTimeFormat();
		wclause.addCondition(new WhereCondition(pre, comparison, " to_date('" + sqldf.format(pred) + "','YYYY-MM-DD HH24:MI:SS') "));
	}

	public void addWhereCondition(String and_or, String pre, int comparison, java.util.Date pred){
		SQLDateTimeFormat sqldf = new SQLDateTimeFormat();
		wclause.addCondition(and_or, new WhereCondition(pre, comparison, " to_date('" + sqldf.format(pred) + "','YYYY-MM-DD HH24:MI:SS') "));
	}


	public String toString(){
		String ret = "SELECT ";
        if(isDistinct()){
           ret += "DISTINCT ";
        }
		int i;
		if(columns == null || columns.size() == 0){
			ret += "* ";
		}
		else{
			// rifle through columns and spit out string'
			for(i = 0; i < columns.size(); i++){
				String column = (String)(columns.get(i));
				ret += column + ",";
			}
			if(i > 0){
				ret = ret.substring(0,ret.length() -1);
			}
		}
		ret += " FROM ";
		String join_conditions = "";
		if(tables == null || tables.size() == 0){
			return null;
		}
		else{
			// rifle through tables and return string
			for(i = 0; i < tables.size(); i++){
				Object tj = tables.get(i);
				if(tj instanceof String){
					String tablestr = (String)(tj);
					if(i == 0){
						ret += tablestr;
					}
					else{
						ret += "," + tablestr;
					}
				}
				else if(tj instanceof Join){
					// extract info from join
					Join join = (Join)(tj);



					ret += "," + join.getTableName(); // inner join default

					WhereClause jconds2 = join.getConditions();
                    List jconds = jconds2.getConditions();

					for(int m = 0; m < jconds.size(); m++){
						WhereCondition c = (WhereCondition)(jconds.get(m));
						if(m > 0 || join_conditions.length() > 0){
							join_conditions += " AND ";
						}
						if(join.getType() == Join.LEFT_OUTER_JOIN){
							/* This needs to use the (+) notation
							 http://larr.unm.edu/~owen/SQLBOL70/html/2_005_28.htm
							 http://otn.oracle.com/docs/products/oracle8i/doc_library/817_doc/server.817/a85397/state21b.htm#2065648
							 LEFT OUTER JOIN Syntax is incorrect
							 */
							join_conditions += c.getPre() + WhereClause.getOperatorString(c.getOperator()) + c.getPost() + " (+) ";
						}
						//else if(join.getType() == Join.RIGHT_OUTER_JOIN
						else{
							join_conditions += c.getPre() + WhereClause.getOperatorString(c.getOperator()) + c.getPost();
						}
					}

				}

			}
			/*	if(i > 0){
			 ret = ret.substring(0,ret.length() -1);
			 }*/
		}

		if(wclause.hasConditions()){
			ret += " WHERE ";
			// rifle through tables and return string
			ret += OracleWhereFormat.format(wclause);

		}
		else if(join_conditions.length() > 0){
			ret += " WHERE "+join_conditions; // in case it's only join conditions
		}
		if(join_conditions.length() > 0){
			ret += " AND " +join_conditions;
		}
		if(group_by == null || group_by.size() == 0){
			//return null;
		}
		else{
			ret += " GROUP BY ";
			// rifle through tables and return string
			for(i = 0; i < group_by.size(); i++){
				String group = (String)(group_by.get(i));
				ret += group + ",";
			}
			if(i > 0){
				ret = ret.substring(0,ret.length() -1);
			}
		}
		if(order_by == null || order_by.size() == 0){
			//return null;
		}
		else{
			ret += " ORDER BY ";
			// rifle through tables and return string
			for(i = 0; i < order_by.size(); i++){
				String order = (String)(order_by.get(i));
				ret += order + ",";
			}
			if(i > 0){
				ret = ret.substring(0,ret.length() -1);
			}
		}
		/*NEED ANOTHER WAY TO DO THIS, CAUSE ORACLE DOESN'T SUPPORT THIS
		 if(limit != null){
		 if(limit.length == 1){
		 ret += " LIMIT " + limit[0];
		 }
		 else{ // length = 2
		 ret += " LIMIT " + limit[0] + "," + limit[1];
		 }
		 }*/
		return ret;
	}
	public CrossdbResultSet execute(Statement stmt) throws SQLException{
		//q = new 	Query(conn);
		//rs = stmt.executeQuery(querystring);
		return new OracleResultSet(stmt.executeQuery(toString()));
	}





}
