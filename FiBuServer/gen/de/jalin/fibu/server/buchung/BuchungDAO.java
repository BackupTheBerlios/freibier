package de.jalin.fibu.server.buchung;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class BuchungDAO implements Createable {

		// int buchid
		// string belegnr
		// string buchungstext
		// int jourid
		// date valuta
		// date erfassung

	private DatabaseTable table;

	public BuchungDAO() {
		table = new DatabaseTable("buchung");
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "buchid", true, true);
		createStmt.addColumn("string", "belegnr", true, false);
		createStmt.addColumn("string", "buchungstext", true, false);
		createStmt.addColumn("int", "jourid", true, false);
		createStmt.addColumn("date", "valuta", true, false);
		createStmt.addColumn("date", "erfassung", true, false);
		createStmt.createDatabaseObject(connect);
	}

	public void addBuchung
		(  Connection connect, BuchungData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("buchid", writeData.getBuchid());
		insert.addSetColumn("belegnr", writeData.getBelegnr());
		insert.addSetColumn("buchungstext", writeData.getBuchungstext());
		insert.addSetColumn("jourid", writeData.getJourid());
		insert.addSetColumn("valuta", writeData.getValuta());
		insert.addSetColumn("erfassung", writeData.getErfassung());
		insert.execute(connect);
	}

	public void updateBuchung
		(  Connection connect, 
		   BuchungData writeData,
		   BuchungData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("buchid", whereData.getBuchid());
		update.addWhereColumn("belegnr", whereData.getBelegnr());
		update.addWhereColumn("buchungstext", whereData.getBuchungstext());
		update.addWhereColumn("jourid", whereData.getJourid());
		update.addWhereColumn("valuta", whereData.getValuta());
		update.addWhereColumn("erfassung", whereData.getErfassung());
		update.addSetColumn("belegnr", writeData.getBelegnr());
		update.addSetColumn("buchungstext", writeData.getBuchungstext());
		update.addSetColumn("jourid", writeData.getJourid());
		update.addSetColumn("valuta", writeData.getValuta());
		update.execute(connect);
	}

	public void deleteBuchung
	(  Connection connect
		, BuchungData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("buchid", whereData.getBuchid());
		delete.addWhereColumn("belegnr", whereData.getBelegnr());
		delete.addWhereColumn("buchungstext", whereData.getBuchungstext());
		delete.addWhereColumn("jourid", whereData.getJourid());
		delete.addWhereColumn("valuta", whereData.getValuta());
		delete.addWhereColumn("erfassung", whereData.getErfassung());
		delete.execute(connect);
	}

	public Vector listBuchungs
	(  Connection connect
		, BuchungData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("buchid", whereData.getBuchid());
		select.addWhereColumn("belegnr", whereData.getBelegnr());
		select.addWhereColumn("buchungstext", whereData.getBuchungstext());
		select.addWhereColumn("jourid", whereData.getJourid());
		select.addWhereColumn("valuta", whereData.getValuta());
		select.addWhereColumn("erfassung", whereData.getErfassung());
		return select.select(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "buchung";
	}
}
