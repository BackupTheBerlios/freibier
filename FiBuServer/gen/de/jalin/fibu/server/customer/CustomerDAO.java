// Generiert mit xmlrpcgen

package de.jalin.fibu.server.customer;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class CustomerDAO implements Createable {

		// int custid
		// string firma
		// int bilanzkonto
		// int guvkonto
		// string jahr
		// string periode
		// date since
		// date lastupdate

	private DatabaseTable table;
	private DisplayColumns display;

	public CustomerDAO() throws XmlRpcTransactionException {
		table = new DatabaseTable("customer");
		display = new DisplayColumns();
		display.addColumnDefinition("custid", 1);
		display.addColumnDefinition("firma", 1);
		display.addColumnDefinition("bilanzkonto", 1);
		display.addColumnDefinition("guvkonto", 1);
		display.addColumnDefinition("jahr", 1);
		display.addColumnDefinition("periode", 1);
		display.addColumnDefinition("since", 2);
		display.addColumnDefinition("lastupdate", 2);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "custid", true, true);
		createStmt.addColumn("string", "firma", true, false);
		createStmt.addColumn("int", "bilanzkonto", true, false);
		createStmt.addColumn("int", "guvkonto", true, false);
		createStmt.addColumn("string", "jahr", true, false);
		createStmt.addColumn("string", "periode", true, false);
		createStmt.addColumn("date", "since", true, false);
		createStmt.addColumn("date", "lastupdate", true, false);
		createStmt.createDatabaseObject(connect);
	}
	
	public void dropDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement dropStmt = new CreateTableStatement(table);
		dropStmt.dropDatabaseObject(connect);
	}
	
	public void addCustomer
		(  Connection connect, CustomerData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("custid", writeData.getCustid());
		insert.addSetColumn("firma", writeData.getFirma());
		insert.addSetColumn("bilanzkonto", writeData.getBilanzkonto());
		insert.addSetColumn("guvkonto", writeData.getGuvkonto());
		insert.addSetColumn("jahr", writeData.getJahr());
		insert.addSetColumn("periode", writeData.getPeriode());
		insert.addSetColumn("since", writeData.getSince());
		insert.addSetColumn("lastupdate", writeData.getLastupdate());
		insert.execute(connect);
	}

	public void updateCustomer
		(  Connection connect, 
		   CustomerData writeData,
		   CustomerData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("custid", whereData.getCustid());
		update.addWhereColumn("firma", whereData.getFirma());
		update.addWhereColumn("bilanzkonto", whereData.getBilanzkonto());
		update.addWhereColumn("guvkonto", whereData.getGuvkonto());
		update.addWhereColumn("jahr", whereData.getJahr());
		update.addWhereColumn("periode", whereData.getPeriode());
		update.addSetColumn("firma", writeData.getFirma());
		update.addSetColumn("bilanzkonto", writeData.getBilanzkonto());
		update.addSetColumn("guvkonto", writeData.getGuvkonto());
		update.addSetColumn("jahr", writeData.getJahr());
		update.addSetColumn("periode", writeData.getPeriode());
		update.execute(connect);
	}

	public void deleteCustomer
	(  Connection connect
		, CustomerData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("custid", whereData.getCustid());
		delete.addWhereColumn("firma", whereData.getFirma());
		delete.addWhereColumn("bilanzkonto", whereData.getBilanzkonto());
		delete.addWhereColumn("guvkonto", whereData.getGuvkonto());
		delete.addWhereColumn("jahr", whereData.getJahr());
		delete.addWhereColumn("periode", whereData.getPeriode());
		delete.execute(connect);
	}

	public Vector listCustomers
	(  Connection connect
		, CustomerData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
	    if (display == null) display = this.display;
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("custid", whereData.getCustid());
		select.addWhereColumn("firma", whereData.getFirma());
		select.addWhereColumn("bilanzkonto", whereData.getBilanzkonto());
		select.addWhereColumn("guvkonto", whereData.getGuvkonto());
		select.addWhereColumn("jahr", whereData.getJahr());
		select.addWhereColumn("periode", whereData.getPeriode());
		return select.select(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "customer";
	}
	
	public DisplayColumns getDisplayColumns() {
		return display;
	}
	
}
