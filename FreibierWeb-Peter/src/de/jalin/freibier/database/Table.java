// $Id: Table.java,v 1.2 2004/12/31 19:37:26 phormanns Exp $
package de.jalin.freibier.database;

import java.util.List;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

public interface Table {
	/**
	 * Anzahl von Datensätzen lesen. Es werden numberOfRecords Datensätze ab (ausschließlich)
	 * previousRecord zurückgeliefert, aufsteigende oder absteigende Reihenfolge.
	 * @param numberOfRecords
	 * @param ascending
	 * @param previousRecord
	 * @return
	 * @throws DatabaseException
	 */
	public abstract List getMultipleRecords(int startRecordNr,
			int numberOfRecords, String orderColumn, boolean ascending)
			throws DatabaseException;

	/**
	 * Diese Methode erlaubt, Anfragen mit bestimmten Konditionen anzugeben.
	 * Diese Konditionen werden in eine QueryConditionImpl-Klasse verpackt.
	 */
	public abstract List getRecordsFromQuery(QueryCondition condition,
			String orderColumn, boolean ascending) throws DatabaseException;

	/**
	 * Dies ist die eierlegende Wollmilchsau-Query-Methode.
	 * 
	 * Mir ist noch nicht ganz klar, ob es besser ist, die anderen 
	 * query-Methoden alle hierauf abzubilden, um alles einheitlich zu
	 * haben (ist sauberer), oder diese zu lassen (ist vielleicht ein bisschen 
	 * performanter).
	 * 
	 * Wenn Bestimmte PArameter nicht angegeben werden sollen, können diese
	 * null bzw. 0 sein, insbesondere gilt dies für: 
	 * condition, orderColumn, numberOfRecords
	 */
	public abstract List getRecords(QueryCondition condition,
			String orderColumn, boolean ascending, int startRecordNr,
			int numberOfRecords) throws DatabaseException;

	/**
	 * Diese Funktion erlaubt, Datensätze über eine fortlaufende Nummer
	 * anzusprechen. Der erste Datensatz hat die Nummer 1, der letzte die
	 * Nummer getNumberOfRecords(). die Sortierung kann angegeben werden,
	 * ansonsten wird nach dem Primärschlüssel sortiert.
	 * 
	 * @param recordNr
	 * @param orderColumn
	 * @return
	 * @throws DatabaseException
	 */
//	public abstract RecordImpl getRecordByNumber(int recordNr)
//			throws DatabaseException;
//
//	public abstract RecordImpl getRecordByNumber(int recordNr, String orderColumn,
//			int direction) throws DatabaseException;
//
	public abstract int getNumberOfRecords() throws DatabaseException;

	/**
	 * Lies einen Datensatz mit dem angegebenen Wert in der angegebenen Spalte.
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DatabaseException
	 */
//	public abstract RecordImpl getRecordByValue(String columnName, String value)
//			throws DatabaseException;

	/**
	 * Lies den Datensatz mit dem angegebenen Primärschlüssel.
	 * @param pkValue
	 * @return
	 * @throws DatabaseException
	 */
	public abstract Record getRecordByPrimaryKey(Object pkValue)
			throws DatabaseException;

	/**
	 * Diese Methode ergibt eine Liste von DataObjects, die alle Werte in
	 * den angegebenen Spalten enthält.
	 * 
	 * Die Werte sind nach dem Wert der Primärspalte sortiert, so daß eine
	 * eindeutige und wiederholbare Reihenfolge vorliegt.
	 * 
	 * Wird als Limit 0 angegeben, werden alle Einträge ausgegeben.
	 */
	public abstract List getGivenColumns(List colNames, int limit)
			throws DatabaseException;

	/**
	 * Speichert den Datensatz. INSERT, falls der Primärschlüssel
	 * undefiniert ist, sonst UPDATE.
	 * @param data
	 */
	public abstract void setRecord(Record data) throws DatabaseException;

	/**
	 * Löscht den Datensatz.
	 * @param data
	 * @throws DatabaseException
	 */
	public abstract void deleteRecord(Record data) throws DatabaseException;

//	public abstract RecordImpl getEmptyRecord();

	public abstract String getName();
	
	public QueryCondition createQueryCondition(String column, int operator, Object value);

	public abstract TypeDefinition getFieldDef(String fieldName) throws SystemDatabaseException ;

	public abstract String getPrimaryKey();

	public abstract List getFieldsList();
}
/*
 *  $Log: Table.java,v $
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
