package de.jalin.fibu.server.journal;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class JournalDAO implements Createable {

		// int jourid
		// string journr
		// string jahr
		// string periode
		// date since
		// date lastupdate
		// bool absummiert

	private DatabaseTable table;

	public JournalDAO() {
		table = new DatabaseTable("journal");
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "jourid", true, true);
		createStmt.addColumn("string", "journr", true, false);
		createStmt.addColumn("string", "jahr", true, false);
		createStmt.addColumn("string", "periode", true, false);
		createStmt.addColumn("date", "since", true, false);
		createStmt.addColumn("date", "lastupdate", true, false);
		createStmt.addColumn("bool", "absummiert", true, false);
		createStmt.createDatabaseObject(connect);
	}

	public void addJournal
		(  Connection connect, JournalData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("jourid", writeData.getJourid());
		insert.addSetColumn("journr", writeData.getJournr());
		insert.addSetColumn("jahr", writeData.getJahr());
		insert.addSetColumn("periode", writeData.getPeriode());
		insert.addSetColumn("since", writeData.getSince());
		insert.addSetColumn("lastupdate", writeData.getLastupdate());
		insert.addSetColumn("absummiert", writeData.getAbsummiert());
		insert.execute(connect);
	}

	public void updateJournal
		(  Connection connect, 
		   JournalData writeData,
		   JournalData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("jourid", whereData.getJourid());
		update.addWhereColumn("journr", whereData.getJournr());
		update.addWhereColumn("jahr", whereData.getJahr());
		update.addWhereColumn("periode", whereData.getPeriode());
		update.addWhereColumn("since", whereData.getSince());
		update.addWhereColumn("lastupdate", whereData.getLastupdate());
		update.addWhereColumn("absummiert", whereData.getAbsummiert());
		update.addSetColumn("journr", writeData.getJournr());
		update.addSetColumn("jahr", writeData.getJahr());
		update.addSetColumn("periode", writeData.getPeriode());
		update.addSetColumn("since", writeData.getSince());
		update.addSetColumn("absummiert", writeData.getAbsummiert());
		update.execute(connect);
	}

	public void deleteJournal
	(  Connection connect
		, JournalData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("jourid", whereData.getJourid());
		delete.addWhereColumn("journr", whereData.getJournr());
		delete.addWhereColumn("jahr", whereData.getJahr());
		delete.addWhereColumn("periode", whereData.getPeriode());
		delete.addWhereColumn("since", whereData.getSince());
		delete.addWhereColumn("lastupdate", whereData.getLastupdate());
		delete.addWhereColumn("absummiert", whereData.getAbsummiert());
		delete.execute(connect);
	}

	public Vector listJournals
	(  Connection connect
		, JournalData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("jourid", whereData.getJourid());
		select.addWhereColumn("journr", whereData.getJournr());
		select.addWhereColumn("jahr", whereData.getJahr());
		select.addWhereColumn("periode", whereData.getPeriode());
		select.addWhereColumn("since", whereData.getSince());
		select.addWhereColumn("lastupdate", whereData.getLastupdate());
		select.addWhereColumn("absummiert", whereData.getAbsummiert());
		return select.select(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}
}
