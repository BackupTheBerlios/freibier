package com.spaceprogram.sql.sapdb;

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

public class SapdbFactory implements SQLFactory {
    private String sequenceSuffix;

    public InsertQuery getInsertQuery(){
		SapdbInsertQuery iq = new SapdbInsertQuery();
        if(sequenceSuffix != null){
            iq.setSequenceSuffix(sequenceSuffix);
        }
		return iq;
	}
	
	public SelectQuery getSelectQuery() {
		SapdbSelectQuery sq = new SapdbSelectQuery();
		return sq;
	}
	public UpdateQuery getUpdateQuery(){
		SapdbUpdateQuery uq = new SapdbUpdateQuery();
		return uq;
	}
	public DeleteQuery getDeleteQuery(){
		SapdbDeleteQuery dq = new SapdbDeleteQuery();
		return dq;
	}
	public CreateTableQuery getCreateTableQuery(){
		SapdbCreateTableQuery ctq = new SapdbCreateTableQuery();
		return ctq;
	}

    public IWhereClause getWhereClause() {
        return new WhereClause();
    }

    public void setSequenceSuffix(String suffix) {
        this.sequenceSuffix = suffix;
    }

    public AlterTableQuery getAlterTableQuery(){
		
		SapdbAlterTableQuery atq = new SapdbAlterTableQuery();
		return atq;
	}

	
	
	
}
