//$Id: TableImpl.java,v 1.4 2005/01/31 21:05:38 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.QueryCondition;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.Table;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;

/**
 * Repraesentiert eine einzelne Tabelle aus einer Datenbank. Zugriffe auf diese
 * Klasse werden sofort an die zugrundeliegende Datenbank weitergereicht, d.h.
 * im Normalfall als SQL-Befehle ausgefuehrt.
 * 
 * @author tbayen
 */
public abstract class TableImpl implements Table {
	
	private static Log log = LogFactory.getLog(TableImpl.class);
	
	private String name;
	private DatabaseImpl db;

	private Map columnshash = null;
	private List columnslist = null;
	private String primaryKey = null;

	public TableImpl(DatabaseImpl db, String name) {
		this.db = db;
		this.name = name;
		columnshash = new HashMap();
		columnslist = new ArrayList();
	}

	protected abstract String makeSelectStatement();
	
	protected abstract String makeWhereExpression(String columnName, Object value) throws DatabaseException;
	
	protected abstract String makeOrderByExpression(String columnName, boolean ascending) throws DatabaseException;
	
	protected abstract String makeDeleteStatement(String value) throws DatabaseException;

	protected abstract String makeReplaceStatement(Record data) throws DatabaseException;

	protected abstract String makeCountStatement() throws DatabaseException;

	protected abstract String makeSelectGivenColumns(List colNames);
	
	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void addColumn(TypeDefinitionImpl typ) {
		columnshash.put(typ.getName(), typ);
		columnslist.add(typ);
	}

	public List getFieldsList() {
		return columnslist;
	}

	public TypeDefinition getFieldDef(String name)
			throws SystemDatabaseException {
		if (!columnshash.containsKey(name)) {
			throw new SystemDatabaseException(
					"Angefordertes Feld existiert nicht: " + name, log);
		}
		return (TypeDefinition) columnshash.get(name);
	}

	public TypeDefinition getFieldDef(int column) {
		return (TypeDefinitionImpl) columnslist.get(column);
	}

	public int fieldName2Int(String name) throws SystemDatabaseException {
		String erg = "";
		for (int i = 0; i < columnslist.size(); i++) {
			if (((TypeDefinitionImpl) (columnslist.get(i))).getName().equals(name)) {
				return i;
			}
		}
		throw new SystemDatabaseException("Datensatz.get(): Feld '" + name
				+ "' nicht vorhanden.", log);
	}

	public RecordImpl getEmptyRecord() {
		Map mrbean = new HashMap();
		Iterator i = columnslist.iterator();
		TypeDefinitionImpl typdef = null;
		while (i.hasNext()) {
			typdef = (TypeDefinitionImpl) i.next();
			mrbean.put(typdef.getName(), typdef.getDefaultValue());
		}
		return new RecordImpl(this, mrbean);
	}

	/**
	 * Anzahl von Datensaetzen lesen. Es werden numberOfRecords Datensaetze ab (ausschliesslich)
	 * previousRecord zurueckgeliefert, aufsteigende oder absteigende Reihenfolge.
	 * @param numberOfRecords
	 * @param ascending
	 * @param previousRecord
	 * @return
	 * @throws DatabaseException
	 */
	public List getMultipleRecords(int startRecordNr, int numberOfRecords,
			String orderColumn, boolean ascending) throws DatabaseException {
		String selectRumpf = this.makeSelectStatement();
		String orderBy = this.makeOrderByExpression(orderColumn, ascending); 
		List dbResult = db.executeSelectMultipleRows(selectRumpf + " "
				+ orderBy, startRecordNr, numberOfRecords);
		//log.debug("dbResult ist: "+dbResult.size());
		List recordList = new ArrayList();
		Iterator resIterator = dbResult.iterator();
		Map recordMap = null;
		RecordImpl newRecord = null;
		while (resIterator.hasNext()) {
			recordMap = (Map) resIterator.next();
			newRecord = new RecordImpl(this, recordMap);
			recordList.add(newRecord);
		}
		return recordList;
	}

	/**
	 * Diese Methode erlaubt, Anfragen mit bestimmten Konditionen anzugeben.
	 * Diese Konditionen werden in eine QueryConditionImpl-Klasse verpackt.
	 */
	public List getRecordsFromQuery(QueryCondition condition, String orderColumn,
			boolean ascending) throws DatabaseException {
		return getRecords(condition,orderColumn,ascending,0,0);
	}

	/**
	 * Dies ist die eierlegende Wollmilchsau-Query-Methode.
	 * 
	 * Mir ist noch nicht ganz klar, ob es besser ist, die anderen 
	 * query-Methoden alle hierauf abzubilden, um alles einheitlich zu
	 * haben (ist sauberer), oder diese zu lassen (ist vielleicht ein bisschen 
	 * performanter).
	 * 
	 * Wenn bestimmte Parameter nicht angegeben werden sollen, koennen diese
	 * null bzw. 0 sein, insbesondere gilt dies fuer: 
	 * condition, orderColumn, numberOfRecords
	 */
	public List getRecords(QueryCondition condition,
			String orderColumn,
			boolean ascending, int startRecordNr, int numberOfRecords
	) throws DatabaseException{
		String selectRumpf = this.makeSelectStatement();
		String sql = selectRumpf;
		// Filtern
		if(condition!=null)
			// Das getSelectStatement endet immer mit einem WHERE, das verlaengere ich hier ggf.:
			sql += condition.expression();
		// Sortieren
		sql += " " + this.makeOrderByExpression(orderColumn, ascending);
		List dbResult = db.executeSelectMultipleRows(sql, startRecordNr, numberOfRecords);
		//log.debug("dbResult ist: "+dbResult.size());
		List recordList = new ArrayList();
		Iterator resIterator = dbResult.iterator();
		Map recordMap = null;
		RecordImpl newRecord = null;
		while (resIterator.hasNext()) {
			recordMap = (Map) resIterator.next();
			newRecord = new RecordImpl(this, recordMap);
			recordList.add(newRecord);
		}
		return recordList;
	}

	/**
	 * Diese Funktion erlaubt, Datensaetze ueber eine fortlaufende Nummer
	 * anzusprechen. Der erste Datensatz hat die Nummer 1, der letzte die
	 * Nummer getNumberOfRecords(). die Sortierung kann angegeben werden,
	 * ansonsten wird nach dem Primaerschluessel sortiert.
	 * 
	 * @param recordNr
	 * @param orderColumn
	 * @return
	 * @throws DatabaseException
	 */
	public RecordImpl getRecordByNumber(int recordNr) throws DatabaseException {
		return getRecordByNumber(recordNr, this.getPrimaryKey(), true);
	}

	public RecordImpl getRecordByNumber(int recordNr, String orderColumn,
			boolean ascending) throws DatabaseException {
		// Ich sortiere nicht nur nach der orderColumn, sondern auch nach der
		// Primärspalte, damit die Reihenfolge bei gleichen Daten immer 
		// garantiert gleich ist.
		if (orderColumn == null) {
			orderColumn = this.getPrimaryKey();
		}
		String selectRumpf = this.makeSelectStatement();
		String orderBy = this.makeOrderByExpression(orderColumn, ascending);
		Map hash = db.executeSelectSingleRow(selectRumpf + " " + orderBy, recordNr);
		return new RecordImpl(this, hash);
	}

	public int getNumberOfRecords() throws DatabaseException {
		Map hash = db.executeSelectSingleRow(makeCountStatement(), 0);
		return ((Integer) hash.get("RECORDCNT")).intValue();
	}

	/**
	 * Lies einen Datensatz mit dem angegebenen Wert in der angegebenen Spalte.
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DatabaseException
	 */
	public RecordImpl getRecordByValue(String columnName, String value)
			throws DatabaseException {
		String selectRumpf = this.makeSelectStatement();
		Map hash = db.executeSelectSingleRow(selectRumpf + " AND "
				+ makeWhereExpression(columnName, value), 0);
		return new RecordImpl(this, hash);
	}

	/**
	 * Lies den Datensatz mit dem angegebenen Primärschlüssel.
	 * @param pkValue
	 * @return
	 * @throws DatabaseException
	 */
	public Record getRecordByPrimaryKey(Object pkValue)
			throws DatabaseException {
		String selectRumpf = this.makeSelectStatement();
		Map hash = db.executeSelectSingleRow(selectRumpf
				+ " AND "
				+ makeWhereExpression(this.getPrimaryKey(), pkValue), 0);
		return new RecordImpl(this, hash);
	}

	/**
	 * Diese Methode ergibt eine Liste von DataObjects, die alle Werte in
	 * den angegebenen Spalten enthaelt.
	 * Die Werte sind nach dem Wert der Primaerspalte sortiert, so dass eine
	 * eindeutige und wiederholbare Reihenfolge vorliegt.
	 * Wird als Limit 0 angegeben, werden alle Eintraege ausgegeben.
	 */
	public List getGivenColumns(List colNames, int limit)
			throws DatabaseException {
		List list = new ArrayList();
		String select = makeSelectGivenColumns(colNames);
		List rows = db.executeSelectMultipleRows(select, 1, limit);
		Iterator i = rows.iterator();
		while (i.hasNext()) {
			Map hash = (Map) i.next();
			Map newhash = new HashMap();
			Iterator j = hash.keySet().iterator();
			while (j.hasNext()) {
				String key = (String) j.next();
				newhash.put(key, new DataObject(hash.get(key), 
						this.getFieldDef(key)));
			}
			list.add(newhash);
		}
		return list;
	}

	/**
	 * Speichert den Datensatz. INSERT, falls der Primaerschluessel
	 * undefiniert ist, sonst UPDATE.
	 * @param data
	 */
	public void setRecord(Record data) throws DatabaseException {
		try {
			String sql = makeReplaceStatement(data);
			db.executeUpdate(sql);
		} catch (DatabaseException e) {
			throw new UserDatabaseException(
					"RecordImpl kann nicht gespeichert werden", e, log);
		}
	}

	/**
	 * Loescht den Datensatz.
	 * @param data
	 * @throws DatabaseException
	 */
	public void deleteRecord(Record data) throws DatabaseException {
		db.executeUpdate(this.makeDeleteStatement(data.getFormatted(this.getPrimaryKey())));
	}

	public String getName() {
		return name;
	}

	public QueryCondition createQueryCondition(String column, int operator, Object value) {
		QueryCondition cond = new QueryConditionImpl(this, column, operator, value);
		return cond;
	}

	protected List getColumnsList() {
		return columnslist;
	}
}
/*
 * $Log: TableImpl.java,v $
 * Revision 1.4  2005/01/31 21:05:38  phormanns
 * PgSqlTableImpl angelegt
 *
 * Revision 1.3  2005/01/29 22:10:02  phormanns
 * SQL zum Teil nach MysqlTableImpl verschoben
 * MySQL LIMIT-Statement durch JDBC 3.0 ersetzt
 *
 * Revision 1.2  2005/01/29 20:21:59  phormanns
 * RecordDefinition in TableImpl integriert
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.22  2004/12/19 13:24:06  phormanns
 * Web-Version des Tabellen-Browsers mit Edit, Filter (1x)
 *
 * Revision 1.21  2004/11/01 12:06:46  tbayen
 * Erste Kalenderversion mit Datenbank-Zugriff
 *
 * Revision 1.20  2004/10/28 16:16:45  phormanns
 * Ich wusste nicht genau was "nr" ist
 *
 * Revision 1.19  2004/10/25 20:41:52  tbayen
 * Test für insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.18  2004/10/24 19:15:07  tbayen
 * ComboBox als Auswahlfeld für Foreign Keys
 *
 * Revision 1.17  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.16  2004/10/24 14:07:43  tbayen
 * In WHERE-Expr. Spaltenbezeichner mit Tabellennamen qualifiziert,
 * weil mehrere Tabellen in einem Select sein können
 *
 * Revision 1.15  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.14  2004/10/23 17:24:20  tbayen
 * ForeignKeys werden bei allen Selects mitgeholt
 *
 * Revision 1.13  2004/10/23 12:13:54  tbayen
 * Debugausgaben etwas bereinigt
 *
 * Revision 1.12  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.11  2004/10/15 16:14:00  phormanns
 * Optimierung TableCache liest 20 Records
 *
 * Revision 1.10  2004/10/14 21:37:36  phormanns
 * Weitere Datentypen
 *
 * Revision 1.9  2004/10/14 21:03:23  tbayen
 * TableGUI kann nun richtig sortieren (d.h. direkt in SQL)
 *
 * Revision 1.8  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu Überarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.7  2004/10/11 14:31:11  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.6  2004/10/11 14:29:37  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.5  2004/10/11 12:55:11  tbayen
 * TableImpl.setRecord implementiert
 *
 * Revision 1.4  2004/10/09 15:09:15  tbayen
 * Einführung von DataObject
 *
 * Revision 1.3  2004/10/09 11:28:24  phormanns
 * Implementierung der Select- und Delete-Operationen
 *
 * Revision 1.2  2004/10/08 12:36:31  phormanns
 * Schnittstelle für Tabelle vollständig (ohne Implementierung)
 *
 * Revision 1.1  2004/10/07 17:15:33  tbayen
 * Datenbankklassen bis auf TableImpl fertig für weitere Tests
 *
 */
