package de.jalin.fibu.server.buchungsliste;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;
import net.hostsharing.admin.runtime.sql.*;

abstract public class BuchungslisteDAO implements Createable {

		// int buzlid
		// int betrag
		// bool soll
		// bool haben
		// int buchid
		// string belegnr
		// string buchungstext
		// int jourid
		// string journr
		// string jahr
		// string periode
		// date since
		// bool absummiert
		// date valuta
		// date erfassung
		// int kontoid
		// string kontonr
		// string bezeichnung

	private DatabaseTable table;

	public BuchungslisteDAO() {
		table = new DatabaseTable("buchungsliste");
	}

	public void createDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement createStmt = new CreateTableStatement(table);
		createStmt.addColumn("int", "buzlid", false, true);
		createStmt.addColumn("int", "betrag", false, false);
		createStmt.addColumn("bool", "soll", false, false);
		createStmt.addColumn("bool", "haben", false, false);
		createStmt.addColumn("int", "buchid", false, false);
		createStmt.addColumn("string", "belegnr", false, false);
		createStmt.addColumn("string", "buchungstext", false, false);
		createStmt.addColumn("int", "jourid", false, false);
		createStmt.addColumn("string", "journr", false, false);
		createStmt.addColumn("string", "jahr", false, false);
		createStmt.addColumn("string", "periode", false, false);
		createStmt.addColumn("date", "since", false, false);
		createStmt.addColumn("bool", "absummiert", false, false);
		createStmt.addColumn("date", "valuta", false, false);
		createStmt.addColumn("date", "erfassung", false, false);
		createStmt.addColumn("int", "kontoid", false, false);
		createStmt.addColumn("string", "kontonr", false, false);
		createStmt.addColumn("string", "bezeichnung", false, false);
		createStmt.createDatabaseObject(connect);
	}

	public void addBuchungsliste
		(  Connection connect, BuchungslisteData writeData )
					throws XmlRpcTransactionException
	{
		InsertStatement insert = new InsertStatement(table);
		insert.addSetColumn("buzlid", writeData.getBuzlid());
		insert.addSetColumn("betrag", writeData.getBetrag());
		insert.addSetColumn("soll", writeData.getSoll());
		insert.addSetColumn("haben", writeData.getHaben());
		insert.addSetColumn("buchid", writeData.getBuchid());
		insert.addSetColumn("belegnr", writeData.getBelegnr());
		insert.addSetColumn("buchungstext", writeData.getBuchungstext());
		insert.addSetColumn("jourid", writeData.getJourid());
		insert.addSetColumn("journr", writeData.getJournr());
		insert.addSetColumn("jahr", writeData.getJahr());
		insert.addSetColumn("periode", writeData.getPeriode());
		insert.addSetColumn("since", writeData.getSince());
		insert.addSetColumn("absummiert", writeData.getAbsummiert());
		insert.addSetColumn("valuta", writeData.getValuta());
		insert.addSetColumn("erfassung", writeData.getErfassung());
		insert.addSetColumn("kontoid", writeData.getKontoid());
		insert.addSetColumn("kontonr", writeData.getKontonr());
		insert.addSetColumn("bezeichnung", writeData.getBezeichnung());
		insert.execute(connect);
	}

	public void updateBuchungsliste
		(  Connection connect, 
		   BuchungslisteData writeData,
		   BuchungslisteData whereData )
					throws XmlRpcTransactionException
	{
		UpdateStatement update = new UpdateStatement(table);
		update.addWhereColumn("buzlid", whereData.getBuzlid());
		update.addWhereColumn("soll", whereData.getSoll());
		update.addWhereColumn("haben", whereData.getHaben());
		update.addWhereColumn("buchid", whereData.getBuchid());
		update.addWhereColumn("belegnr", whereData.getBelegnr());
		update.addWhereColumn("buchungstext", whereData.getBuchungstext());
		update.addWhereColumn("jourid", whereData.getJourid());
		update.addWhereColumn("journr", whereData.getJournr());
		update.addWhereColumn("jahr", whereData.getJahr());
		update.addWhereColumn("periode", whereData.getPeriode());
		update.addWhereColumn("absummiert", whereData.getAbsummiert());
		update.addWhereColumn("kontoid", whereData.getKontoid());
		update.addWhereColumn("kontonr", whereData.getKontonr());
		update.execute(connect);
	}

	public void deleteBuchungsliste
	(  Connection connect
		, BuchungslisteData whereData )
					throws XmlRpcTransactionException
	{
		DeleteStatement delete = new DeleteStatement(table);
		delete.addWhereColumn("buzlid", whereData.getBuzlid());
		delete.addWhereColumn("soll", whereData.getSoll());
		delete.addWhereColumn("haben", whereData.getHaben());
		delete.addWhereColumn("buchid", whereData.getBuchid());
		delete.addWhereColumn("belegnr", whereData.getBelegnr());
		delete.addWhereColumn("buchungstext", whereData.getBuchungstext());
		delete.addWhereColumn("jourid", whereData.getJourid());
		delete.addWhereColumn("journr", whereData.getJournr());
		delete.addWhereColumn("jahr", whereData.getJahr());
		delete.addWhereColumn("periode", whereData.getPeriode());
		delete.addWhereColumn("absummiert", whereData.getAbsummiert());
		delete.addWhereColumn("kontoid", whereData.getKontoid());
		delete.addWhereColumn("kontonr", whereData.getKontonr());
		delete.execute(connect);
	}

	public Vector listBuchungslistes
	(  Connection connect
		, BuchungslisteData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
		SelectStatement select = new SelectStatement(table, display, orderBy);
		select.addWhereColumn("buzlid", whereData.getBuzlid());
		select.addWhereColumn("soll", whereData.getSoll());
		select.addWhereColumn("haben", whereData.getHaben());
		select.addWhereColumn("buchid", whereData.getBuchid());
		select.addWhereColumn("belegnr", whereData.getBelegnr());
		select.addWhereColumn("buchungstext", whereData.getBuchungstext());
		select.addWhereColumn("jourid", whereData.getJourid());
		select.addWhereColumn("journr", whereData.getJournr());
		select.addWhereColumn("jahr", whereData.getJahr());
		select.addWhereColumn("periode", whereData.getPeriode());
		select.addWhereColumn("absummiert", whereData.getAbsummiert());
		select.addWhereColumn("kontoid", whereData.getKontoid());
		select.addWhereColumn("kontonr", whereData.getKontonr());
		return select.select(connect);
	}

	public DatabaseTable getTable() {
		return table;
	}

	public String getModuleName() {
		return "buchungsliste";
	}
}