//$Id: TypeDefinitionForeignKey.java,v 1.12 2005/03/22 20:58:27 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import java.util.ArrayList;
import java.util.List;
import com.crossdb.sql.Column;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.impl.ForeignKey;
import de.jalin.freibier.database.impl.TypeDefinitionImpl;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * Definition fuer einen Wert, der ein Fremdschluessel ist, d.h. er referenziert
 * einen bestimmten Wert in einer anderen Tabelle. Was genau referenziert wird,
 * wird durch Properties bestimmt.
 */
public class TypeDefinitionForeignKey extends TypeDefinitionImpl {
	
	// Ich muss mir die DatabaseImpl merken, um auf den referenzierten Wert zugreifen zu koennen
	private Database db;
	private String foreignPrimaryKey;
	private String foreignTable;
	private TypeDefinition indexType = null;
	private TypeDefinition referenceType = null; 

	public TypeDefinitionForeignKey(TypeDefinition typeDef, Column col, Database db) throws DatabaseException {
		this.db = db;
		this.foreignTable = col.getForeignTable();
		this.foreignPrimaryKey = col.getForeignPrimaryKey();
		this.indexType = typeDef;
		this.indexType.setName(col.getForeignPrimaryKey());
		DBTable tab = db.getTable(this.foreignTable);
		this.referenceType = tab.getFieldDef((String) tab.getFieldsList().get(1));
	}

	public Class getJavaType() {
		return ForeignKey.class;
	}

	public String printText(Object s) throws DatabaseException {
		return indexType.printText(((ForeignKey) s).getKey());
	}

	public ValueObject parse(String s) throws DatabaseException {
		return new ValueObject(new ForeignKey(indexType.parse(s), null), this);
	}

	public boolean validate(String s) {
		return indexType.validate(s);
	}

	public TypeDefinition getIndexType() {
		return indexType;
	}

	public TypeDefinition getReferenceType() {
		return referenceType;
	}

	/**
	 * Diese Methode ergibt eine Liste von Records mit nur zwei Spalten.  
	 * So ergibt sich eine Liste aller moeglichen Werte fuer den Fremdschluessel.
	 */
	public List getPossibleValues() throws DatabaseException {
		List list = new ArrayList();
		list.add(foreignPrimaryKey);
		list.add(referenceType.getName());
		return db.getTable(foreignTable).getGivenColumns(list, 10);
	}

	public void addColumn(InsertQuery query, Printable printable) throws DatabaseException {
		// TODO geht nur fuer ganzzahlige Fremdschluessel
		Object fkObj = printable.getValue();
		if (fkObj instanceof ForeignKey) {
			ForeignKey fk = (ForeignKey) printable.getValue();
			Printable valObj = (Printable) fk.getKey();
			query.addColumn(printable.getName(), ((Long) valObj.getValue()).intValue());
		} else {
			throw new SystemDatabaseException("ForeignKey Objekt erwartet.", log);
		}
	}

	public void addColumn(UpdateQuery query, Printable printable) throws DatabaseException {
		// TODO geht nur fuer ganzzahlige Fremdschluessel
		Object fkObj = printable.getValue();
		if (fkObj instanceof ForeignKey) {
			ForeignKey fk = (ForeignKey) printable.getValue();
			Printable valObj = (Printable) fk.getKey();
			query.addColumn(printable.getName(), ((Long) valObj.getValue()).intValue());
		} else {
			throw new SystemDatabaseException("ForeignKey Objekt erwartet.", log);
		}
	}
}
/*
 * $Log: TypeDefinitionForeignKey.java,v $
 * Revision 1.12  2005/03/22 20:58:27  phormanns
 * ForeignKey Objekt wird beim DB-Select angelegt, falls noetig
 *
 * Revision 1.11  2005/03/21 21:41:11  tbayen
 * Probleme mit Fremdschluessel gefixt
 *
 * Revision 1.10  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.9  2005/03/01 21:56:32  phormanns
 * Long immer als Value-Objekt zu Number-Typen
 * setRecord macht Insert, wenn PK = Default-Value
 *
 * Revision 1.8  2005/02/24 22:18:12  phormanns
 * Tests laufen mit HSQL und MySQL
 *
 * Revision 1.7  2005/02/24 13:52:12  phormanns
 * Mit Tests begonnen
 *
 * Revision 1.6  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.5  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.4  2005/02/11 16:46:02  phormanns
 * MySQL geht wieder
 *
 * Revision 1.3  2005/02/11 15:50:35  phormanns
 * Merge
 *
 * Revision 1.2  2005/01/31 21:05:38  phormanns
 * PgSqlTableImpl angelegt
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste oeffentliche Version
 *
 * Revision 1.3  2004/10/25 20:41:52  tbayen
 * Test fuer insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.2  2004/10/24 19:15:07  tbayen
 * ComboBox als Auswahlfeld fuer Foreign Keys
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.6  2004/10/24 15:42:27  tbayen
 * DataObjectTextEditor als selbststaendige Klasse implementiert
 *
 * Revision 1.5  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.4  2004/10/23 12:22:20  tbayen
 * Zugriff auf ForeignKey-Felder per getter/setter
 *
 * Revision 1.3  2004/10/23 12:08:28  tbayen
 * NicePrinter gibt Foreign Keys aus
 *
 * Revision 1.2  2004/10/23 11:35:46  tbayen
 * Verwaltung von Foreign Keys in eigenem Datentyp
 *
 * Revision 1.1  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 */