//$Id: Table.java,v 1.1 2004/12/31 17:12:42 phormanns Exp $

package de.jalin.freibier.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;
import de.jalin.freibier.database.type.TypeDefinition;

/**
 * Repr�sentiert eine einzelne Tabelle aus einer Datenbank. Zugriffe auf diese
 * Klasse werden sofort an die zugrundeliegende Datenbank weitergereicht, d.h.
 * im Normalfall als SQL-Befehle ausgef�hrt.
 * 
 * @author tbayen
 */
public class Table {
	private static Log log = LogFactory.getLog(Table.class);
	protected RecordDefinition def;
	protected String name;
	protected Database db;

	public Table(Database db, String name, RecordDefinition def) {
		this.db = db;
		this.name = name;
		this.def = def;
	}

	/**
	 * Anzahl von Datens�tzen lesen. Es werden numberOfRecords Datens�tze ab (ausschlie�lich)
	 * previousRecord zur�ckgeliefert, aufsteigende oder absteigende Reihenfolge.
	 * @param numberOfRecords
	 * @param ascending
	 * @param previousRecord
	 * @return
	 * @throws DatabaseException
	 */
	public List getMultipleRecords(int startRecordNr, int numberOfRecords,
			String orderColumn, boolean ascending) throws DatabaseException {
		String selectRumpf = def.getSelectStatement(name);
		List dbResult = db.executeSelectMultipleRows(selectRumpf + " ORDER BY "
				+ name + "." + orderColumn + (ascending ? " ASC" : " DESC")
				+ ", " + name + "." + def.getPrimaryKey()
				+ (ascending ? " ASC" : " DESC") + " LIMIT " + startRecordNr
				+ ", " + numberOfRecords);
		//log.debug("dbResult ist: "+dbResult.size());
		List recordList = new ArrayList();
		Iterator resIterator = dbResult.iterator();
		Map recordMap = null;
		Record newRecord = null;
		while (resIterator.hasNext()) {
			recordMap = (Map) resIterator.next();
			newRecord = new Record(def, recordMap);
			recordList.add(newRecord);
		}
		return recordList;
	}

	/**
	 * Diese Hilfsklasse wird benutzt, um WHERE-Konditionen an 
	 * getRecordsFromQuery() zu �bergeben.
	 */
	public class QueryCondition {
		String column;
		int operator;
		Object value;
		QueryCondition next = null;
		// Konstanten f�r den Operator:
		public static final int EQUAL = 0;
		public static final int GREATER_OR_EQUAL = 1;
		public static final int GREATER = 2;
		public static final int LESS = 3;
		public static final int LESS_OR_EQUAL = 4;
		public static final int LIKE = 5;

		public QueryCondition(String column, int operator, Object value) {
			super();
			this.column = column;
			this.operator = operator;
			this.value = value;
		}

		public void and(QueryCondition cond) {
			next = cond;
		}

		public String expression() throws DatabaseException {
			String erg = column;
			switch (operator) {
			case EQUAL:				erg += " = ";	break;
			case GREATER:			erg += " > ";	break;
			case GREATER_OR_EQUAL:	erg += " >= ";	break;
			case LESS:				erg += " < ";	break;
			case LESS_OR_EQUAL:		erg += " <= ";	break;
			case LIKE:				erg += " LIKE ";break;
			default:
				throw new SystemDatabaseException("falsche QueryCondition: ("
						+ column + "," + operator + "," + "value" + ")", log);
			}
			DataObject val=new DataObject(value,def.getFieldDef(column));
			erg += SQLPrinter.print(val);
			if (next != null)
				erg += " AND " + next.expression();
			return erg;
		}
	}

	/**
	 * Diese Methode erlaubt, Anfragen mit bestimmten Konditionen anzugeben.
	 * Diese Konditionen werden in eine QueryCondition-Klasse verpackt.
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
	 * Wenn Bestimmte PArameter nicht angegeben werden sollen, k�nnen diese
	 * null bzw. 0 sein, insbesondere gilt dies f�r: 
	 * condition, orderColumn, numberOfRecords
	 */
	public List getRecords(QueryCondition condition,
			String orderColumn,
			boolean ascending, int startRecordNr, int numberOfRecords
	) throws DatabaseException{
		String selectRumpf = def.getSelectStatement(name);
		String sql = selectRumpf;
		// Filtern
		if(condition!=null)
			// Das getSelectStatement endet imm er mit einem WHERE, das verl�ngere ich hier ggf.:
			sql += " AND "+condition.expression();
		// Sortieren
		sql += " ORDER BY ";
		// Wenn angegeben, nach einer bestimmten Spalte sortieren
		if (orderColumn != null && !orderColumn.equals(""))
			sql += name + "." + orderColumn + (ascending ? " ASC" : " DESC")
					+ ", ";
		// auf jeden Fall nach der Prim�rspalte sortieren, um immer eine eindeutige Reihenfolge zu haben
		sql += name + "." + def.getPrimaryKey()
				+ (ascending ? " ASC" : " DESC");
		if (numberOfRecords != 0)
			sql += " LIMIT " + startRecordNr + ", " + numberOfRecords;
		List dbResult = db.executeSelectMultipleRows(sql);
		//log.debug("dbResult ist: "+dbResult.size());
		List recordList = new ArrayList();
		Iterator resIterator = dbResult.iterator();
		Map recordMap = null;
		Record newRecord = null;
		while (resIterator.hasNext()) {
			recordMap = (Map) resIterator.next();
			newRecord = new Record(def, recordMap);
			recordList.add(newRecord);
		}
		return recordList;
	}

	/**
	 * Diese Funktion erlaubt, Datens�tze �ber eine fortlaufende Nummer
	 * anzusprechen. Der erste Datensatz hat die Nummer 1, der letzte die
	 * Nummer getNumberOfRecords(). die Sortierung kann angegeben werden,
	 * ansonsten wird nach dem Prim�rschl�ssel sortiert.
	 * 
	 * @param recordNr
	 * @param orderColumn
	 * @return
	 * @throws DatabaseException
	 */
	public Record getRecordByNumber(int recordNr) throws DatabaseException {
		return getRecordByNumber(recordNr, def.getPrimaryKey(), 1);
	}

	public Record getRecordByNumber(int recordNr, String orderColumn,
			int direction) throws DatabaseException {
		// Ich sortiere nicht nur nach der orderColumn, sondern auch nach der
		// Prim�rspalte, damit die Reihenfolge bei gleichen Daten immer 
		// garantiert gleich ist.
		if (orderColumn == null) {
			orderColumn = def.getPrimaryKey();
		}
		String selectRumpf = def.getSelectStatement(name);
		Map hash = db.executeSelectSingleRow(selectRumpf + " ORDER BY `"
				+ orderColumn + "` " + (direction < 0 ? "DESC" : "ASC") + ", `"
				+ def.getPrimaryKey() + "` " + (direction < 0 ? "DESC" : "ASC")
				+ " LIMIT " + recordNr + ",1");
		return new Record(def, hash);
	}

	public int getNumberOfRecords() throws DatabaseException {
		Map hash = db.executeSelectSingleRow("SELECT COUNT(*) AS COUNT FROM "
				+ name);
		return ((Integer) hash.get("COUNT")).intValue();
	}

	/**
	 * Lies einen Datensatz mit dem angegebenen Wert in der angegebenen Spalte.
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DatabaseException
	 */
	public Record getRecordByValue(String columnName, String value)
			throws DatabaseException {
		String selectRumpf = def.getSelectStatement(name);
		Map hash = db.executeSelectSingleRow(selectRumpf + " AND "
				+ makeWhereExpression(def.getFieldDef(columnName), value));
		return new Record(def, hash);
	}

	/**
	 * Lies den Datensatz mit dem angegebenen Prim�rschl�ssel.
	 * @param pkValue
	 * @return
	 * @throws DatabaseException
	 */
	public Record getRecordByPrimaryKey(Object pkValue)
			throws DatabaseException {
		String selectRumpf = def.getSelectStatement(name);
		Map hash = db.executeSelectSingleRow(selectRumpf
				+ " AND "
				+ makeWhereExpression(def.getFieldDef(def.getPrimaryKey()),
						pkValue));
		return new Record(def, hash);
	}

	/**
	 * Diese Methode ergibt eine Liste von DataObjects, die alle Werte in
	 * den angegebenen Spalten enth�lt.
	 * 
	 * Die Werte sind nach dem Wert der Prim�rspalte sortiert, so da� eine
	 * eindeutige und wiederholbare Reihenfolge vorliegt.
	 * 
	 * Wird als Limit 0 angegeben, werden alle Eintr�ge ausgegeben.
	 */
	public List getGivenColumns(List colNames, int limit)
			throws DatabaseException {
		List list = new ArrayList();
		String statement = "";
		Iterator i = colNames.iterator();
		while (i.hasNext()) {
			if (!statement.equals(""))
				statement += ", ";
			statement += (String) i.next();
		}
		String limitstring = (limit == 0 ? "" : " LIMIT " + limit);
		List rows = db.executeSelectMultipleRows("SELECT " + statement
				+ " FROM " + name + " ORDER BY " + def.getPrimaryKey()
				+ limitstring);
		i = rows.iterator();
		while (i.hasNext()) {
			Map hash = (Map) i.next();
			Map newhash = new HashMap();
			Iterator j = hash.keySet().iterator();
			while (j.hasNext()) {
				String key = (String) j.next();
				newhash.put(key, new DataObject(hash.get(key), def
						.getFieldDef(key)));
			}
			list.add(newhash);
		}
		return list;
	}

	/**
	 * Speichert den Datensatz. INSERT, falls der Prim�rschl�ssel
	 * undefiniert ist, sonst UPDATE.
	 * @param data
	 */
	public void setRecord(Record data) throws DatabaseException {
		try {
			String sql = "REPLACE INTO `" + name + "` SET ";
			for (int i = 0; i < def.getFieldsList().size(); i++) {
				if (i != 0) {
					sql += ", ";
				}
				String feldname = def.getFieldDef(i).getName();
				sql += "`" + feldname + "` = "
						+ SQLPrinter.print(data.getField(feldname));
			}
			db.executeUpdate(sql);
		} catch (DatabaseException e) {
			throw new UserDatabaseException(
					"Record kann nicht gespeichert werden", e, log);
		}
	}

	/**
	 * L�scht den Datensatz.
	 * @param data
	 * @throws DatabaseException
	 */
	public void deleteRecord(Record data) throws DatabaseException {
		db.executeUpdate("DELETE FROM "
				+ name
				+ " WHERE "
				+ makeWhereExpression(def.getFieldDef(def.getPrimaryKey()),
						data.getField(def.getPrimaryKey()).getValue()));
	}

	public Record getEmptyRecord() {
		return def.getEmptyRecord();
	}

	public RecordDefinition getRecordDefinition() {
		return def;
	}

	public String getName() {
		return name;
	}

	private String makeWhereExpression(TypeDefinition def, Object value)
			throws DatabaseException {
		return (name + "." + def.getName() + "=" 
				+ SQLPrinter.print(new DataObject(value, def)));
	}
}
/*
 * $Log: Table.java,v $
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste �ffentliche Version
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
 * Test f�r insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.18  2004/10/24 19:15:07  tbayen
 * ComboBox als Auswahlfeld f�r Foreign Keys
 *
 * Revision 1.17  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.16  2004/10/24 14:07:43  tbayen
 * In WHERE-Expr. Spaltenbezeichner mit Tabellennamen qualifiziert,
 * weil mehrere Tabellen in einem Select sein k�nnen
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
 * dazu �berarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.7  2004/10/11 14:31:11  tbayen
 * Framework f�r Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.6  2004/10/11 14:29:37  tbayen
 * Framework f�r Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.5  2004/10/11 12:55:11  tbayen
 * Table.setRecord implementiert
 *
 * Revision 1.4  2004/10/09 15:09:15  tbayen
 * Einf�hrung von DataObject
 *
 * Revision 1.3  2004/10/09 11:28:24  phormanns
 * Implementierung der Select- und Delete-Operationen
 *
 * Revision 1.2  2004/10/08 12:36:31  phormanns
 * Schnittstelle f�r Tabelle vollst�ndig (ohne Implementierung)
 *
 * Revision 1.1  2004/10/07 17:15:33  tbayen
 * Datenbankklassen bis auf Table fertig f�r weitere Tests
 *
 */