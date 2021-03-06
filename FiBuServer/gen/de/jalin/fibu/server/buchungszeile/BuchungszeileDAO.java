// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungszeile;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class BuchungszeileDAO implements Createable {

		// int buzlid
		// int buchid
		// int kontoid
		// int betrag
		// bool soll
		// bool haben

	private DatabaseTable table;
	private DisplayColumns display;

	public BuchungszeileDAO() throws XmlRpcTransactionException {
		table = new DatabaseTable("buchungszeile");
		display = new DisplayColumns();
		display.addColumnDefinition("buzlid", 1);
		display.addColumnDefinition("buchid", 1);
		display.addColumnDefinition("kontoid", 1);
		display.addColumnDefinition("betrag", 1);
		display.addColumnDefinition("soll", 1);
		display.addColumnDefinition("haben", 1);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "buzlid", true, true);
		createStmt.addColumn("int", "buchid", true, false);
		createStmt.addColumn("int", "kontoid", true, false);
		createStmt.addColumn("int", "betrag", true, false);
		createStmt.addColumn("bool", "soll", true, false);
		createStmt.addColumn("bool", "haben", true, false);
		createStmt.createDatabaseObject(connect);
	}
	
	public void dropDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement dropStmt = new CreateTableStatement(table);
		dropStmt.dropDatabaseObject(connect);
	}
	
	public void addBuchungszeile
		(  Connection connect, BuchungszeileData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("buzlid", writeData.getBuzlid());
		insert.addSetColumn("buchid", writeData.getBuchid());
		insert.addSetColumn("kontoid", writeData.getKontoid());
		insert.addSetColumn("betrag", writeData.getBetrag());
		insert.addSetColumn("soll", writeData.getSoll());
		insert.addSetColumn("haben", writeData.getHaben());
		insert.execute(connect);
	}

	public void updateBuchungszeile
		(  Connection connect, 
		   BuchungszeileData writeData,
		   BuchungszeileData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("buzlid", whereData.getBuzlid());
		update.addWhereColumn("buchid", whereData.getBuchid());
		update.addWhereColumn("kontoid", whereData.getKontoid());
		update.addWhereColumn("betrag", whereData.getBetrag());
		update.addWhereColumn("soll", whereData.getSoll());
		update.addWhereColumn("haben", whereData.getHaben());
		update.addSetColumn("betrag", writeData.getBetrag());
		update.addSetColumn("soll", writeData.getSoll());
		update.addSetColumn("haben", writeData.getHaben());
		update.execute(connect);
	}

	public void deleteBuchungszeile
	(  Connection connect
		, BuchungszeileData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("buzlid", whereData.getBuzlid());
		delete.addWhereColumn("buchid", whereData.getBuchid());
		delete.addWhereColumn("kontoid", whereData.getKontoid());
		delete.addWhereColumn("betrag", whereData.getBetrag());
		delete.addWhereColumn("soll", whereData.getSoll());
		delete.addWhereColumn("haben", whereData.getHaben());
		delete.execute(connect);
	}

	public QueryResult listBuchungszeiles
	(  Connection connect
		, BuchungszeileData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
	    if (display == null) display = this.display;
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("buzlid", whereData.getBuzlid());
		select.addWhereColumn("buchid", whereData.getBuchid());
		select.addWhereColumn("kontoid", whereData.getKontoid());
		select.addWhereColumn("betrag", whereData.getBetrag());
		select.addWhereColumn("soll", whereData.getSoll());
		select.addWhereColumn("haben", whereData.getHaben());
		return select.executeQuery(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "buchungszeile";
	}
	
	public DisplayColumns getDisplayColumns() {
		return display;
	}
	
}
