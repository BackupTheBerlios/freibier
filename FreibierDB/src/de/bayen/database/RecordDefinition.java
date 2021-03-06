/* Erzeugt am 07.10.2004 von tbayen
 * $Id: RecordDefinition.java,v 1.11 2005/11/24 11:47:45 tbayen Exp $
 */
package de.bayen.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.bayen.database.exception.DBRuntimeException;
import de.bayen.database.typedefinition.TypeDefinition;
import de.bayen.database.typedefinition.TypeDefinitionForeignKey;

/**
 * @author tbayen
 *
 * Definition der Felder eines Datensatzes bzw. der Spalten einer Tabelle 
 * (die ja aus Datens�tzen besteht)
 */
public class RecordDefinition {
	protected static Log log = LogFactory.getLog(RecordDefinition.class);
	private String name=null;
	private Map columnshash = null;
	private List columnslist = null;
	private String primaryKey = null;
	private List sublists = null;
	private Table table;  // wird nirgendwo ben�tigt, ist nur f�r Anwender interessant

	public RecordDefinition() {
		columnshash = new HashMap();
		columnslist = new ArrayList();
	}

	public void addColumn(TypeDefinition typ) {
		columnshash.put(typ.getName(), typ);
		columnslist.add(typ);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List getSublists() {
		return sublists;
	}

	public void setSublists(List sublists) {
		this.sublists = sublists;
	}

	public List getFieldsList() {
		return columnslist;
	}

	public TypeDefinition getFieldDef(String name) {
		if (!columnshash.containsKey(name)) {
			Perl5Util re = new Perl5Util();
			if (re.match("/^[[:upper:]]+$/", name)) {
				// Falls in der Datenbank ein Feldname nur aus Grossbuchstaben
				// besteht, wird das vom Java-Treiber in Kleinbuchstaben 
				// konvertiert. Damit ich nun mit dem "richtigen" Namen darauf
				// zugreifen kann, mache ich den ggf. hier klein.
				name = name.toLowerCase();
			}
		}
		if (!columnshash.containsKey(name)) {
			throw new DBRuntimeException.IllegalFieldNameException(
					"Angefordertes Feld existiert nicht: " + name + "("
							+ columnshash.keySet().toString() + ")", log);
		}
		return (TypeDefinition) columnshash.get(name);
	}

	public TypeDefinition getFieldDef(int column) {
		return (TypeDefinition) columnslist.get(column);
	}

	public int fieldName2Int(String name) {
		for (int i = 0; i < columnslist.size(); i++) {
			if (((TypeDefinition) (columnslist.get(i))).getName().equals(name)) {
				return i;
			}
		}
		throw new DBRuntimeException.IllegalFieldNameException(
				"Datensatz.get(): Feld '" + name + "' nicht vorhanden.", log);
	}

	public Record getEmptyRecord() {
		Map mrbean = new HashMap();
		Iterator i = columnslist.iterator();
		TypeDefinition typdef = null;
		while (i.hasNext()) {
			typdef = (TypeDefinition) i.next();
			mrbean.put(typdef.getName(), typdef.getDefaultValue());
		}
		return new Record(this, mrbean);
	}

	/**
	 * Ermittelt das zugeh�rige Table-Objekt. Ist diese Definition
	 * beim �ffnen einer Tabelle benutzt worden, ist dieser Wert 
	 * hier abgelegt worden. Der Wert wird intern nicht ben�tigt 
	 * und kann deshalb ggf. auch null sein.
	 * 
	 * @return Tabelle, in die dieser Record geh�rt
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Eine Applikation kann hier eine zugeh�rige Tabelle 
	 * speichern. Wird der Record aus der Table-Klasse heraus 
	 * erzeugt, geschieht das automatisch. Der Wert wird intern 
	 * nicht ben�tigt, muss also nicht gesetzt sein.
	 * 
	 * @param table
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * Ergibt ein SQL-Select-Statement, das geeignet ist, alle Daten f�r diesen
	 * Record einzulesen. Dabei werden auch durch Fremdschl�ssel referenzierte
	 * Werte miteingelesen.
	 * <p>
	 * Der fromtabellen-String kann angegeben werden, damit weitere Tabellen in
	 * die FROM-Clause aufgenommen werden. Dies kann n�tzlich sein, wenn man
	 * davon abh�ngig besondere Querys (mit dem SQL-operator) erstellt.
	 * </p><p>
	 * Das Statement enth�lt immer eine WHERE-Klausel am Ende, so da� mit 
	 * "AND ..." weitere WHERE-Bedingungen angeh�ngt werden k�nnen.
	 * </p><p>
	 * Falls ein Fremdschl�ssel NULL ist, ist der Wert, der dazu gelesen wird,
	 * garantiert falsch.
	 * </p><p>
	 * Dieses SELECT-Statement funktioniert �brigens nicht, wenn ich einen
	 * Fremdschl�ssel auf eine Tabelle habe, die leer ist. Dann gibt es keinen
	 * Ergebnis-Datensatz. In diesem Fall kann man 
	 * <code>getSelectStatementWithoutFrom()</code> aufrufen.
	 * </p>
	 */
	protected String getSelectStatement(String myTable, String fromtabellen) {
		String felder = "";
		String tabellen = "`" + myTable + "`";
		if(fromtabellen!=null && fromtabellen.length()>0)
			tabellen+=","+fromtabellen;
		String where = null;
		Iterator i = columnslist.iterator();
		TypeDefinition feldtyp;
		String foreigntablename = "_foreign_x";
		while (i.hasNext()) {
			feldtyp = (TypeDefinition) i.next();
			if (!felder.equals("")) {
				felder += ", ";
			}
			felder += myTable + "." + feldtyp.getName();
			if (feldtyp instanceof TypeDefinitionForeignKey) {
				String tabelle = feldtyp.getProperty("foreignkey.table");
				String spalte = feldtyp.getProperty("foreignkey.resultcolumn");
				felder += ",  " + foreigntablename + "." + spalte + " AS "
						+ feldtyp.getName() + "_foreign";
				tabellen += ", `" + tabelle + "` AS " + foreigntablename;
				if (where == null) {
					where = " WHERE ";
				} else {
					where += " AND ";
				}
				where += "(" + myTable + "." + feldtyp.getName() + "="
						+ foreigntablename + "."
						+ feldtyp.getProperty("foreignkey.indexcolumn")
						+ " OR ISNULL(" + myTable + "." + feldtyp.getName()
						+ "))";
				foreigntablename = foreigntablename + "x";
			}
		}
		if (where == null)
			where = " WHERE 1";
		return "SELECT " + felder + " FROM " + tabellen + where;
		// Beispiel:
		// SELECT adressen.vorname, adressen.nachname, adressen.lebensalter, adressen.sprache, programmiersprachen.name, 
		// adressen.id FROM `adressen`,Programmiersprachen where programmiersprachen.id=adressen.sprache
	}

	/**
	 * verk�rzte Version der Methode ohne die Angabe von fromtabellen.
	 * 
	 * @param myTable
	 * @return SQL-Statement
	 */
	protected String getSelectStatement(String myTable) {
		return getSelectStatement(myTable,null);
	}

	/**
	 * Version von <code>getSelectStatement()</code>, die ganz ohne weitere
	 * FROM-Tabellen arbeitet und daher keine Fremdschl�sseldaten liefert.
	 * Der Vorteil ist, das diese Methode auch funktioniert, wenn eine
	 * Fremdschl�sseltabelle leer ist.
	 * 
	 * @param myTable
	 * @return Select-Statement als String
	 */
	protected String getSelectStatementWithoutFrom(String myTable) {
		String felder = "";
		Iterator i = columnslist.iterator();
		TypeDefinition feldtyp;
		String foreigntablename = "_foreign_x";
		while (i.hasNext()) {
			feldtyp = (TypeDefinition) i.next();
			if (!felder.equals("")) {
				felder += ", ";
			}
			felder += myTable + "." + feldtyp.getName();
			if (feldtyp instanceof TypeDefinitionForeignKey) {
				felder += ",  NULL AS "
						+ feldtyp.getName() + "_foreign";
				foreigntablename = foreigntablename + "x";
			}
		}
		return "SELECT " + felder + " FROM " + "`" + myTable + "` WHERE 1";
	}
}
/*
 * $Log: RecordDefinition.java,v $
 * Revision 1.11  2005/11/24 11:47:45  tbayen
 * getSelectStatement(), das auch bei null-Fremdschl�ssel funktioniert
 * sowie einige Verbesserungen in der JavaDoc
 *
 * Revision 1.10  2005/09/11 16:39:56  tbayen
 * RecordDefinition enth�lt auch name und ggf. table
 *
 * Revision 1.9  2005/08/30 20:31:03  tbayen
 * erweiterte Querys mit direkter SQL-Syntax m�glich
 *
 * Revision 1.8  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und �berall eingef�hrt
 *
 * Revision 1.7  2005/08/14 22:34:19  tbayen
 * Foreign Keys k�nnen jetzt auch NULL sein
 *
 * Revision 1.6  2005/08/14 20:06:21  tbayen
 * Verbesserungen an den ForeignKeys, die sich aus der FiBu ergeben haben
 *
 * Revision 1.5  2005/08/12 19:39:47  tbayen
 * kleine Nachbesserung...
 *
 * Revision 1.4  2005/08/12 19:37:18  tbayen
 * unn�tige TO DO-Kommentare entfernt
 *
 * Revision 1.3  2005/08/12 19:27:45  tbayen
 * Tests laufen wieder alle
 *
 * Revision 1.2  2005/08/08 06:35:29  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.2  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.3  2005/03/21 02:06:16  tbayen
 * Komplette �berarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 * Revision 1.2  2005/02/24 15:47:43  tbayen
 * Probleme mit der Neuanlage bei ForeignKeys behoben
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.10  2004/11/01 12:06:46  tbayen
 * Erste Kalenderversion mit Datenbank-Zugriff
 *
 * Revision 1.9  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.8  2004/10/23 17:24:20  tbayen
 * ForeignKeys werden bei allen Selects mitgeholt
 *
 * Revision 1.7  2004/10/23 11:37:09  tbayen
 * Property textcolumn umbenannt in resultcolumn (Muss ja keinen Text enthalten)
 *
 * Revision 1.6  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.5  2004/10/18 11:45:01  tbayen
 * Vorbereitung auf Bearbeitung von Fremdschl�sseln
 * Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 * Revision 1.4  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu �berarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.3  2004/10/09 15:09:15  tbayen
 * Einf�hrung von DataObject
 *
 * Revision 1.2  2004/10/08 12:36:31  phormanns
 * Schnittstelle f�r Tabelle vollst�ndig (ohne Implementierung)
 *
 * Revision 1.1  2004/10/07 17:15:33  tbayen
 * Datenbankklassen bis auf Table fertig f�r weitere Tests
 *
 */