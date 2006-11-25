// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class KontoDAO implements Createable {

		// int kontoid
		// string kontonr
		// string bezeichnung
		// int mwstid
		// int oberkonto
		// bool istsoll
		// bool isthaben
		// bool istaktiv
		// bool istpassiv
		// bool istaufwand
		// bool istertrag

	private DatabaseTable table;
	private DisplayColumns display;

	public KontoDAO() throws XmlRpcTransactionException {
		table = new DatabaseTable("konto");
		display = new DisplayColumns();
		display.addColumnDefinition("kontoid", 1);
		display.addColumnDefinition("kontonr", 1);
		display.addColumnDefinition("bezeichnung", 1);
		display.addColumnDefinition("mwstid", 1);
		display.addColumnDefinition("oberkonto", 1);
		display.addColumnDefinition("istsoll", 1);
		display.addColumnDefinition("isthaben", 1);
		display.addColumnDefinition("istaktiv", 1);
		display.addColumnDefinition("istpassiv", 1);
		display.addColumnDefinition("istaufwand", 1);
		display.addColumnDefinition("istertrag", 1);
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "kontoid", true, true);
		createStmt.addColumn("string", "kontonr", true, false);
		createStmt.addColumn("string", "bezeichnung", true, false);
		createStmt.addColumn("int", "mwstid", true, false);
		createStmt.addColumn("int", "oberkonto", false, false);
		createStmt.addColumn("bool", "istsoll", true, false);
		createStmt.addColumn("bool", "isthaben", true, false);
		createStmt.addColumn("bool", "istaktiv", true, false);
		createStmt.addColumn("bool", "istpassiv", true, false);
		createStmt.addColumn("bool", "istaufwand", true, false);
		createStmt.addColumn("bool", "istertrag", true, false);
		createStmt.createDatabaseObject(connect);
	}
	
	public void dropDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement dropStmt = new CreateTableStatement(table);
		dropStmt.dropDatabaseObject(connect);
	}
	
	public void addKonto
		(  Connection connect, KontoData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("kontoid", writeData.getKontoid());
		insert.addSetColumn("kontonr", writeData.getKontonr());
		insert.addSetColumn("bezeichnung", writeData.getBezeichnung());
		insert.addSetColumn("mwstid", writeData.getMwstid());
		insert.addSetColumn("oberkonto", writeData.getOberkonto());
		insert.addSetColumn("istsoll", writeData.getIstsoll());
		insert.addSetColumn("isthaben", writeData.getIsthaben());
		insert.addSetColumn("istaktiv", writeData.getIstaktiv());
		insert.addSetColumn("istpassiv", writeData.getIstpassiv());
		insert.addSetColumn("istaufwand", writeData.getIstaufwand());
		insert.addSetColumn("istertrag", writeData.getIstertrag());
		insert.execute(connect);
	}

	public void updateKonto
		(  Connection connect, 
		   KontoData writeData,
		   KontoData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("kontoid", whereData.getKontoid());
		update.addWhereColumn("kontonr", whereData.getKontonr());
		update.addWhereColumn("bezeichnung", whereData.getBezeichnung());
		update.addWhereColumn("mwstid", whereData.getMwstid());
		update.addWhereColumn("oberkonto", whereData.getOberkonto());
		update.addWhereColumn("istsoll", whereData.getIstsoll());
		update.addWhereColumn("isthaben", whereData.getIsthaben());
		update.addWhereColumn("istaktiv", whereData.getIstaktiv());
		update.addWhereColumn("istpassiv", whereData.getIstpassiv());
		update.addWhereColumn("istaufwand", whereData.getIstaufwand());
		update.addWhereColumn("istertrag", whereData.getIstertrag());
		update.addSetColumn("kontonr", writeData.getKontonr());
		update.addSetColumn("bezeichnung", writeData.getBezeichnung());
		update.addSetColumn("mwstid", writeData.getMwstid());
		update.addSetColumn("oberkonto", writeData.getOberkonto());
		update.addSetColumn("istsoll", writeData.getIstsoll());
		update.addSetColumn("isthaben", writeData.getIsthaben());
		update.addSetColumn("istaktiv", writeData.getIstaktiv());
		update.addSetColumn("istpassiv", writeData.getIstpassiv());
		update.addSetColumn("istaufwand", writeData.getIstaufwand());
		update.addSetColumn("istertrag", writeData.getIstertrag());
		update.execute(connect);
	}

	public void deleteKonto
	(  Connection connect
		, KontoData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("kontoid", whereData.getKontoid());
		delete.addWhereColumn("kontonr", whereData.getKontonr());
		delete.addWhereColumn("bezeichnung", whereData.getBezeichnung());
		delete.addWhereColumn("mwstid", whereData.getMwstid());
		delete.addWhereColumn("oberkonto", whereData.getOberkonto());
		delete.addWhereColumn("istsoll", whereData.getIstsoll());
		delete.addWhereColumn("isthaben", whereData.getIsthaben());
		delete.addWhereColumn("istaktiv", whereData.getIstaktiv());
		delete.addWhereColumn("istpassiv", whereData.getIstpassiv());
		delete.addWhereColumn("istaufwand", whereData.getIstaufwand());
		delete.addWhereColumn("istertrag", whereData.getIstertrag());
		delete.execute(connect);
	}

	public QueryResult listKontos
	(  Connection connect
		, KontoData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
	    if (display == null) display = this.display;
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("kontoid", whereData.getKontoid());
		select.addWhereColumn("kontonr", whereData.getKontonr());
		select.addWhereColumn("bezeichnung", whereData.getBezeichnung());
		select.addWhereColumn("mwstid", whereData.getMwstid());
		select.addWhereColumn("oberkonto", whereData.getOberkonto());
		select.addWhereColumn("istsoll", whereData.getIstsoll());
		select.addWhereColumn("isthaben", whereData.getIsthaben());
		select.addWhereColumn("istaktiv", whereData.getIstaktiv());
		select.addWhereColumn("istpassiv", whereData.getIstpassiv());
		select.addWhereColumn("istaufwand", whereData.getIstaufwand());
		select.addWhereColumn("istertrag", whereData.getIstertrag());
		return select.executeQuery(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "konto";
	}
	
	public DisplayColumns getDisplayColumns() {
		return display;
	}
	
}
