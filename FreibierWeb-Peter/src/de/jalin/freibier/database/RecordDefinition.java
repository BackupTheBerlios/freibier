//$Id: RecordDefinition.java,v 1.1 2004/12/31 17:12:42 phormanns Exp $

package de.jalin.freibier.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.type.TypeDefinition;
import de.jalin.freibier.database.type.TypeDefinitionForeignKey;

/**
 * @author tbayen
 *
 * Definition der Felder eines Datensatzes bzw. der Spalten einer Tabelle 
 * (die ja aus Datensätzen besteht)
 */
public class RecordDefinition {
	protected static Log log = LogFactory.getLog(RecordDefinition.class);
	private Map columnshash = null;
	private List columnslist = null;
	private String primaryKey = null;

	public RecordDefinition() {
		columnshash = new HashMap();
		columnslist = new ArrayList();
	}

	public void addColumn(TypeDefinition typ) {
		columnshash.put(typ.getName(), typ);
		columnslist.add(typ);
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
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
		return (TypeDefinition) columnslist.get(column);
	}

	public int fieldName2Int(String name) throws SystemDatabaseException {
		String erg = "";
		for (int i = 0; i < columnslist.size(); i++) {
			if (((TypeDefinition) (columnslist.get(i))).getName().equals(name)) {
				return i;
			}
		}
		throw new SystemDatabaseException("Datensatz.get(): Feld '" + name
				+ "' nicht vorhanden.", log);
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
	 * Ergibt ein SQL-Select-Statement, das geeignet ist, alle Daten für diesen
	 * Record einzulesen. Dabei werden auch durch Fremdschlüssel referenzierte
	 * Werte miteingelesen. 
	 * 
	 * Das Statement enthält immer eine WHERE-Klausel am Ende, so daß mit 
	 * "AND ..." weitere WHERE-Bedingungen angehängt werden können.
	 */
	public String getSelectStatement(String myTable) {
		String felder = "";
		String tabellen = "`" + myTable + "`";
		String where = null;
		Iterator i = columnslist.iterator();
		TypeDefinition feldtyp;
		while (i.hasNext()) {
			feldtyp = (TypeDefinition) i.next();
			if (!felder.equals("")) {
				felder += ", ";
			}
			felder += myTable + "." + feldtyp.getName();
			if (feldtyp instanceof TypeDefinitionForeignKey) {
				String tabelle = feldtyp.getProperty("foreignkey.table");
				String spalte = feldtyp.getProperty("foreignkey.resultcolumn");
				felder += ", " + tabelle + "." + spalte + " AS " + feldtyp.getName() +"_foreign";
				tabellen += ", `" + tabelle + "`";
				if (where == null) {
					where = " WHERE ";
				} else {
					where += " AND ";
				}
				where += myTable + "." + feldtyp.getName() + "=" + tabelle
						+ "." + feldtyp.getProperty("foreignkey.indexcolumn");
			}
		}
		if (where == null)
			where = " WHERE 1";
		return "SELECT " + felder + " FROM " + tabellen + where;
		// Beispiel:
		// SELECT adressen.vorname, adressen.nachname, adressen.lebensalter, adressen.sprache, programmiersprachen.name, 
		// adressen.id FROM `adressen`,Programmiersprachen where programmiersprachen.id=adressen.sprache
	}
}
/*
 * $Log: RecordDefinition.java,v $
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste öffentliche Version
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
 * Vorbereitung auf Bearbeitung von Fremdschlüsseln
 * Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 * Revision 1.4  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu Überarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.3  2004/10/09 15:09:15  tbayen
 * Einführung von DataObject
 *
 * Revision 1.2  2004/10/08 12:36:31  phormanns
 * Schnittstelle für Tabelle vollständig (ohne Implementierung)
 *
 * Revision 1.1  2004/10/07 17:15:33  tbayen
 * Datenbankklassen bis auf Table fertig für weitere Tests
 *
 */