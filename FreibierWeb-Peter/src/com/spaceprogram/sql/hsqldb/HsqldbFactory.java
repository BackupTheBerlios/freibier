package com.spaceprogram.sql.hsqldb;

import com.crossdb.sql.AlterTableQuery;
import com.crossdb.sql.CreateTableQuery;
import com.crossdb.sql.DeleteQuery;
import com.crossdb.sql.IWhereClause;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.crossdb.sql.UpdateQuery;
import com.crossdb.sql.WhereClause;


public class HsqldbFactory implements SQLFactory {
	
	public InsertQuery getInsertQuery(){
		HsqldbInsertQuery iq = new HsqldbInsertQuery();
		return iq;
	}
	
	public SelectQuery getSelectQuery() {
		HsqldbSelectQuery sq = new HsqldbSelectQuery();
		return sq;
	}
	public UpdateQuery getUpdateQuery(){
		HsqldbUpdateQuery uq = new HsqldbUpdateQuery();
		return uq;
	}
	public DeleteQuery getDeleteQuery(){
		HsqldbDeleteQuery dq = new HsqldbDeleteQuery();
		return dq;
	}
	public CreateTableQuery getCreateTableQuery(){
		HsqldbCreateTableQuery ctq = new HsqldbCreateTableQuery();
		return ctq;
	}

    public IWhereClause getWhereClause() {
        return new WhereClause();
    }

    public void setSequenceSuffix(String suffix) {
    }

    public AlterTableQuery getAlterTableQuery(){
		
		HsqldbAlterTableQuery atq = new HsqldbAlterTableQuery();
		return atq;
	}

	
	
	
}
