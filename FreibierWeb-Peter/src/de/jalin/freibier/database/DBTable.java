// $Id: DBTable.java,v 1.4 2005/03/01 21:56:32 phormanns Exp $
package de.jalin.freibier.database;

import java.util.List;
import com.crossdb.sql.IWhereClause;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

/**
 * Schnittstelle zu einer Datenbank-Tabelle.
 * @author peter
 */
public interface DBTable {
    
	public static final int MAX_FETCH_SIZE = 2000;

	/**
	 * Anzahl von Datensaetzen lesen. Es werden numberOfRecords Datensätze ab (ausschliesslich)
	 * previousRecord zurueckgeliefert, aufsteigende oder absteigende Reihenfolge.
	 * @param numberOfRecords
	 * @param ascending
	 * @param previousRecord
	 * @return List of Records
	 * @throws DatabaseException
	 */
	public abstract List getMultipleRecords(int startRecordNr,
			int numberOfRecords, String orderColumn, boolean ascending)
			throws DatabaseException;

	/**
	 * Diese Methode erlaubt, Anfragen mit bestimmten Konditionen anzugeben.
	 * Diese Konditionen werden in eine QueryConditionImpl-Klasse verpackt.
	 */
	public abstract List getRecordsFromQuery(IWhereClause condition,
			String orderColumn, boolean ascending) throws DatabaseException;

	/**
	 * Dies ist die eierlegende Wollmilchsau-Query-Methode.
	 * Mir ist noch nicht ganz klar, ob es besser ist, die anderen 
	 * query-Methoden alle hierauf abzubilden, um alles einheitlich zu
	 * haben (ist sauberer), oder diese zu lassen (ist vielleicht ein bisschen 
	 * performanter).
	 * Wenn bestimmte Parameter nicht angegeben werden sollen, koennen diese
	 * null bzw. 0 sein, insbesondere gilt dies fuer: 
	 * condition, orderColumn, numberOfRecords
	 */
	public abstract List getRecords(IWhereClause condition,
			String orderColumn, boolean ascending, int startRecordNr,
			int numberOfRecords) throws DatabaseException;

	/**
	 * Liefert die Anzahl der Datensaetze der Tabelle.
	 * @return Anzahl
	 * @throws DatabaseException
	 */
	public abstract int getNumberOfRecords(IWhereClause clause) throws DatabaseException;

	/**
	 * Lies den Datensatz mit dem angegebenen Primaerschluessel.
	 * @param pkValue
	 * @return Record
	 * @throws DatabaseException
	 */
	public abstract Record getRecordByPrimaryKey(Object pkValue)
			throws DatabaseException;

	public Record getEmptyRecord() throws DatabaseException;
	
	/**
	 * Diese Methode ergibt eine Liste von DataObjects, die alle Werte in
	 * den angegebenen Spalten enthaelt.
	 * Die Werte sind nach dem Wert der Primaerspalte sortiert, so dass eine
	 * eindeutige und wiederholbare Reihenfolge vorliegt.
	 * Wird als Limit 0 angegeben, werden alle Eintraege ausgegeben.
	 */
	public abstract List getGivenColumns(List colNames, int limit)
			throws DatabaseException;

	/**
	 * Speichert den Datensatz. INSERT, falls der Primaerschluessel
	 * undefiniert ist, sonst UPDATE.
	 * @param data
	 */
	public abstract void setRecord(Record data) throws DatabaseException;

	/**
	 * Loescht den Datensatz.
	 * @param data
	 * @throws DatabaseException
	 */
	public abstract void deleteRecord(Record data) throws DatabaseException;

	public abstract String getName();
	
	public abstract TypeDefinition getFieldDef(String fieldName) throws SystemDatabaseException ;

	public abstract String getPrimaryKey();

	public abstract List getFieldsList();
}
/*
 *  $Log: DBTable.java,v $
 *  Revision 1.4  2005/03/01 21:56:32  phormanns
 *  Long immer als Value-Objekt zu Number-Typen
 *  setRecord macht Insert, wenn PK = Default-Value
 *
 *  Revision 1.3  2005/02/18 22:17:42  phormanns
 *  Umstellung auf Freemarker begonnen
 *
 *  Revision 1.2  2005/02/16 17:24:52  phormanns
 *  OrderBy und Filter funktionieren jetzt
 *
 *  Revision 1.1  2005/02/13 20:27:14  phormanns
 *  Funktioniert bis auf Filter
 *
 *  Revision 1.4  2005/01/29 20:21:59  phormanns
 *  RecordDefinition in TableImpl integriert
 *
 *  Revision 1.3  2005/01/28 17:13:25  phormanns
 *  Schnittstelle dokumentiert.
 *
 *  Revision 1.2  2004/12/31 19:37:26  phormanns
 *  Database Schnittstelle herausgearbeitet
 *
 */
