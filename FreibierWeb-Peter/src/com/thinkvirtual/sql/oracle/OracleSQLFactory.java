package com.thinkvirtual.sql.oracle;

import com.crossdb.sql.AlterTableQuery;
import com.crossdb.sql.CreateTableQuery;
import com.crossdb.sql.DeleteQuery;
import com.crossdb.sql.IWhereClause;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.SQLFactory;
import com.crossdb.sql.SelectQuery;
import com.crossdb.sql.UpdateQuery;

/** This is an initial beta of a class that will represent a query string */

//import java.util.List;
//import java.sql.*;

public class OracleSQLFactory implements SQLFactory {
    private String sequenceSuffix;

    public InsertQuery getInsertQuery(){
		OracleInsertQuery iq = new OracleInsertQuery();
         if(sequenceSuffix != null){
            iq.setSequenceSuffix(sequenceSuffix);
        }
		return iq;
	}

	public SelectQuery getSelectQuery() {
		OracleSelectQuery sq = new OracleSelectQuery();
		return sq;
	}
	public UpdateQuery getUpdateQuery(){
		OracleUpdateQuery uq = new OracleUpdateQuery();
		return uq;
	}
	public CreateTableQuery getCreateTableQuery(){
		OracleCreateTableQuery ctq = new OracleCreateTableQuery();
		return ctq;
	}

    public IWhereClause getWhereClause() {
        return new OracleWhereClause();
    }

    public void setSequenceSuffix(String suffix) {
        this.sequenceSuffix = (suffix);
    }

    public DeleteQuery getDeleteQuery(){
		OracleDeleteQuery ctq = new OracleDeleteQuery();
		return ctq;
	}
	public AlterTableQuery getAlterTableQuery(){

		OracleAlterTableQuery atq = new OracleAlterTableQuery();
		return atq;
	}



}
