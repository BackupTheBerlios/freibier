// Generiert mit xmlrpcgen

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
		// bool istsoll
		// bool isthaben
		// bool istaktiv
		// bool istpassiv
		// bool istaufwand
		// bool istertrag

	private DatabaseTable table;
	private DisplayColumns display;

	public BuchungslisteDAO() throws XmlRpcTransactionException {
		table = new DatabaseTable("buchungsliste");
		display = new DisplayColumns();
		display.addColumnDefinition("buzlid", 1);
		display.addColumnDefinition("betrag", 1);
		display.addColumnDefinition("soll", 1);
		display.addColumnDefinition("haben", 1);
		display.addColumnDefinition("buchid", 1);
		display.addColumnDefinition("belegnr", 1);
		display.addColumnDefinition("buchungstext", 1);
		display.addColumnDefinition("jourid", 1);
		display.addColumnDefinition("journr", 1);
		display.addColumnDefinition("jahr", 1);
		display.addColumnDefinition("periode", 1);
		display.addColumnDefinition("since", 1);
		display.addColumnDefinition("absummiert", 1);
		display.addColumnDefinition("valuta", 1);
		display.addColumnDefinition("erfassung", 1);
		display.addColumnDefinition("kontoid", 1);
		display.addColumnDefinition("kontonr", 1);
		display.addColumnDefinition("bezeichnung", 1);
		display.addColumnDefinition("istsoll", 1);
		display.addColumnDefinition("isthaben", 1);
		display.addColumnDefinition("istaktiv", 1);
		display.addColumnDefinition("istpassiv", 1);
		display.addColumnDefinition("istaufwand", 1);
		display.addColumnDefinition("istertrag", 1);
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
		createStmt.addColumn("bool", "istsoll", false, false);
		createStmt.addColumn("bool", "isthaben", false, false);
		createStmt.addColumn("bool", "istaktiv", false, false);
		createStmt.addColumn("bool", "istpassiv", false, false);
		createStmt.addColumn("bool", "istaufwand", false, false);
		createStmt.addColumn("bool", "istertrag", false, false);
		createStmt.createDatabaseObject(connect);
	}
	
	public void dropDatabaseObject(Connection connect) throws XmlRpcTransactionException {
		CreateTableStatement dropStmt = new CreateTableStatement(table);
		dropStmt.dropDatabaseObject(connect);
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
		insert.addSetColumn("istsoll", writeData.getIstsoll());
		insert.addSetColumn("isthaben", writeData.getIsthaben());
		insert.addSetColumn("istaktiv", writeData.getIstaktiv());
		insert.addSetColumn("istpassiv", writeData.getIstpassiv());
		insert.addSetColumn("istaufwand", writeData.getIstaufwand());
		insert.addSetColumn("istertrag", writeData.getIstertrag());
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
		update.addWhereColumn("istsoll", whereData.getIstsoll());
		update.addWhereColumn("isthaben", whereData.getIsthaben());
		update.addWhereColumn("istaktiv", whereData.getIstaktiv());
		update.addWhereColumn("istpassiv", whereData.getIstpassiv());
		update.addWhereColumn("istaufwand", whereData.getIstaufwand());
		update.addWhereColumn("istertrag", whereData.getIstertrag());
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
		delete.addWhereColumn("istsoll", whereData.getIstsoll());
		delete.addWhereColumn("isthaben", whereData.getIsthaben());
		delete.addWhereColumn("istaktiv", whereData.getIstaktiv());
		delete.addWhereColumn("istpassiv", whereData.getIstpassiv());
		delete.addWhereColumn("istaufwand", whereData.getIstaufwand());
		delete.addWhereColumn("istertrag", whereData.getIstertrag());
		delete.execute(connect);
	}

	public QueryResult listBuchungslistes
	(  Connection connect
		, BuchungslisteData whereData 
		, DisplayColumns display
		, OrderByList orderBy )
					throws XmlRpcTransactionException
	{
	    if (display == null) display = this.display;
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
		return "buchungsliste";
	}
	
	public DisplayColumns getDisplayColumns() {
		return display;
	}
	
}
