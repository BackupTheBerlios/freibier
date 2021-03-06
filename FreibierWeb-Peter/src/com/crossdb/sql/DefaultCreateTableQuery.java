/**
 * @author Travis Reeder - travis@spaceprogram.com
 * Date: Jun 27, 2002
 * Time: 8:47:24 PM
 *
 */
package com.crossdb.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCreateTableQuery implements CreateTableQuery {

    protected String name;
    protected List columns;
    protected boolean auto_defaults = true;

    public DefaultCreateTableQuery() {
        columns = new ArrayList();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public void setAutoDefaults(boolean b) {
        auto_defaults = b;
    }

    public abstract String toString();


    public void execute(java.sql.Statement stmt) throws SQLException {
        //q = new 	Query(conn);
        //rs = stmt.executeQuery(querystring);
        stmt.executeUpdate(toString());
    }

    public void execute(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        execute(stmt);
        stmt.close();
    }

}
