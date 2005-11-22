package de.jalin.fibu.server.mwst;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class MwstDAO implements Createable {

		// int mwstid
		// int mwstsatz
		// string mwsttext
		// int mwstkontosoll
		// int mwstkontohaben
		// bool mwstsatzaktiv

	private DatabaseTable table;

	public MwstDAO() {
		table = new DatabaseTable("mwst");
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "mwstid", true, false);
		createStmt.addColumn("int", "mwstsatz", true, false);
		createStmt.addColumn("string", "mwsttext", true, false);
		createStmt.addColumn("int", "mwstkontosoll", true, false);
		createStmt.addColumn("int", "mwstkontohaben", true, false);
		createStmt.addColumn("bool", "mwstsatzaktiv", true, false);
		createStmt.createDatabaseObject(connect);
	}

	public void addMwst
		(  Connection connect, MwstData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("mwstid", writeData.getMwstid());
		insert.addSetColumn("mwstsatz", writeData.getMwstsatz());
		insert.addSetColumn("mwsttext", writeData.getMwsttext());
		insert.addSetColumn("mwstkontosoll", writeData.getMwstkontosoll());
		insert.addSetColumn("mwstkontohaben", writeData.getMwstkontohaben());
		insert.addSetColumn("mwstsatzaktiv", writeData.getMwstsatzaktiv());
		insert.execute(connect);
	}

	public void updateMwst
		(  Connection connect, 
		   MwstData writeData,
		   MwstData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("mwstid", whereData.getMwstid());
		update.addWhereColumn("mwstsatz", whereData.getMwstsatz());
		update.addWhereColumn("mwsttext", whereData.getMwsttext());
		update.addWhereColumn("mwstkontosoll", whereData.getMwstkontosoll());
		update.addWhereColumn("mwstkontohaben", whereData.getMwstkontohaben());
		update.addWhereColumn("mwstsatzaktiv", whereData.getMwstsatzaktiv());
		update.addSetColumn("mwsttext", writeData.getMwsttext());
		update.addSetColumn("mwstsatzaktiv", writeData.getMwstsatzaktiv());
		update.execute(connect);
	}

	public void deleteMwst
	(  Connection connect
		, MwstData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("mwstid", whereData.getMwstid());
		delete.addWhereColumn("mwstsatz", whereData.getMwstsatz());
		delete.addWhereColumn("mwsttext", whereData.getMwsttext());
		delete.addWhereColumn("mwstkontosoll", whereData.getMwstkontosoll());
		delete.addWhereColumn("mwstkontohaben", whereData.getMwstkontohaben());
		delete.addWhereColumn("mwstsatzaktiv", whereData.getMwstsatzaktiv());
		delete.execute(connect);
	}

	public Vector listMwsts
	(  Connection connect
		, MwstData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("mwstid", whereData.getMwstid());
		select.addWhereColumn("mwstsatz", whereData.getMwstsatz());
		select.addWhereColumn("mwsttext", whereData.getMwsttext());
		select.addWhereColumn("mwstkontosoll", whereData.getMwstkontosoll());
		select.addWhereColumn("mwstkontohaben", whereData.getMwstkontohaben());
		select.addWhereColumn("mwstsatzaktiv", whereData.getMwstsatzaktiv());
		return select.select(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "mwst";
	}
}
