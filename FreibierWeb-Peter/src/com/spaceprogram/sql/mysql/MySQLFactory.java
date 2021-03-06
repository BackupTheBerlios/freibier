package com.spaceprogram.sql.mysql;

import com.crossdb.sql.AlterTableQuery;
import com.crossdb.sql.CreateTableQuery;
import com.crossdb.sql.DeleteQuery;
import com.crossdb.sql.IWhereClause;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.crossdb.sql.UpdateQuery;
import com.crossdb.sql.WhereClause;

/** This is an initial beta of a class that will represent a query string */

//import java.util.List;
//import java.sql.*;

public class MySQLFactory implements SQLFactory {
	
	public InsertQuery getInsertQuery(){
		MySQLInsertQuery iq = new MySQLInsertQuery();
		return iq;
	}
	
	public SelectQuery getSelectQuery() {
		MySQLSelectQuery sq = new MySQLSelectQuery();
		return sq;
	}
	public UpdateQuery getUpdateQuery(){
		MySQLUpdateQuery uq = new MySQLUpdateQuery();
		return uq;
	}
	public DeleteQuery getDeleteQuery(){
		MySQLDeleteQuery dq = new MySQLDeleteQuery();
		return dq;
	}
	public CreateTableQuery getCreateTableQuery(){
		MySQLCreateTableQuery ctq = new MySQLCreateTableQuery();
		return ctq;
	}

    public IWhereClause getWhereClause() {
        return new WhereClause();
    }

    public void setSequenceSuffix(String suffix) {
    }

    public AlterTableQuery getAlterTableQuery(){
		
		MySQLAlterTableQuery atq = new MySQLAlterTableQuery();
		return atq;
	}

	
	
	
}
