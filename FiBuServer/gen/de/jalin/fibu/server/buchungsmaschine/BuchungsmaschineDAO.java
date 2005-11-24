package de.jalin.fibu.server.buchungsmaschine;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class BuchungsmaschineDAO implements Createable {

		// int buzlid
		// string sollkontonr
		// string habenkontonr
		// int sollmwstid
		// int habenmwstid
		// int brutto
		// string belegnr
		// string buchungstext
		// int jourid
		// date valuta

	private DatabaseTable table;
	private DisplayColumns display;

	public BuchungsmaschineDAO() {
		table = new DatabaseTable("buchungsmaschine");
		display = new DisplayColumns();
		display.addColumnDefinition("buzlid", 1);
		display.addColumnDefinition("sollkontonr", 1);
		display.addColumnDefinition("habenkontonr", 1);
		display.addColumnDefinition("sollmwstid", 1);
		display.addColumnDefinition("habenmwstid", 1);
		display.addColumnDefinition("brutto", 1);
		display.addColumnDefinition("belegnr", 1);
		display.addColumnDefinition("buchungstext", 1);
		display.addColumnDefinition("jourid", 1);
		display.addColumnDefinition("valuta", 1);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "buzlid", true, true);
		createStmt.addColumn("string", "sollkontonr", true, false);
		createStmt.addColumn("string", "habenkontonr", true, false);
		createStmt.addColumn("int", "sollmwstid", true, false);
		createStmt.addColumn("int", "habenmwstid", true, false);
		createStmt.addColumn("int", "brutto", false, false);
		createStmt.addColumn("string", "belegnr", false, false);
		createStmt.addColumn("string", "buchungstext", false, false);
		createStmt.addColumn("int", "jourid", false, false);
		createStmt.addColumn("date", "valuta", false, false);
		createStmt.createDatabaseObject(connect);
	}

	public void addBuchungsmaschine
		(  Connection connect, BuchungsmaschineData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("buzlid", writeData.getBuzlid());
		insert.addSetColumn("sollkontonr", writeData.getSollkontonr());
		insert.addSetColumn("habenkontonr", writeData.getHabenkontonr());
		insert.addSetColumn("sollmwstid", writeData.getSollmwstid());
		insert.addSetColumn("habenmwstid", writeData.getHabenmwstid());
		insert.addSetColumn("brutto", writeData.getBrutto());
		insert.addSetColumn("belegnr", writeData.getBelegnr());
		insert.addSetColumn("buchungstext", writeData.getBuchungstext());
		insert.addSetColumn("jourid", writeData.getJourid());
		insert.addSetColumn("valuta", writeData.getValuta());
		insert.execute(connect);
	}

	public void updateBuchungsmaschine
		(  Connection connect, 
		   BuchungsmaschineData writeData,
		   BuchungsmaschineData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.execute(connect);
	}

	public void deleteBuchungsmaschine
	(  Connection connect
		, BuchungsmaschineData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.execute(connect);
	}

	public Vector listBuchungsmaschines
	(  Connection connect
		, BuchungsmaschineData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
	    if (display == null) display = this.display;
		SelectStatement select = new SelectStatement(table, display, orderBy);
		return select.select(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "buchungsmaschine";
	}
}
