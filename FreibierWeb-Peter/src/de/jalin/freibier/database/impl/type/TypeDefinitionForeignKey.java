//$Id: TypeDefinitionForeignKey.java,v 1.8 2005/02/24 22:18:12 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.impl.DatabaseImpl;
import de.jalin.freibier.database.impl.ForeignKey;
import de.jalin.freibier.database.impl.TypeDefinitionImpl;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * Definition fuer einen Wert, der ein Fremdschluessel ist, d.h. er referenziert
 * einen bestimmten Wert in einer anderen Tabelle. Was genau referenziert wird,
 * wird durch Properties bestimmt.
 */
public class TypeDefinitionForeignKey extends TypeDefinitionImpl {
	
	private TypeDefinition indexType = null;
	private TypeDefinition referenceType = null; // kann (bei Problemen) auch null sein
	// Ich muss mir die DatabaseImpl merken, um auf den referenzierten Wert zugreifen zu koennen
	private DatabaseImpl db;

	public TypeDefinitionForeignKey(TypeDefinition indexType, DatabaseImpl db)
			throws DatabaseException {
		super();
		log.trace("TypeDefinitionForeignKey Constructor");
		// Die Datenbank merke ich mir, weil man die ggf. braucht, um auf die
		// referenzierte Tabelle zugreifen zu koennen.
		this.db = db;
		// So, wie ich mir in ForeignKey den Index und den Content merke, merke
		// ich mir hier die Typen von beiden, um diese z.B. im NicePrinter beide 
		// formatieren zu koennen.
		this.indexType = indexType;
		if (db != null) {
			referenceType = db.getTable(
					indexType.getProperty("foreignkey.table"))
						.getFieldDef(indexType.getProperty("foreignkey.resultcolumn"));
		}
		// Default Value setzen:
		String defaultProp = getProperty("default");
		if (defaultProp != null) {
			try {
				defaultValue = new ForeignKey(indexType.parse(defaultProp),
						null);
			} catch (DatabaseException e1) {
				log.error("Default Foreign Key kann nicht gelesen werden", e1);
				defaultValue = null;
			}
		} else {
			List list = new ArrayList();
			String indexColumn = indexType
					.getProperty("foreignkey.indexcolumn");
			String resultColumn = indexType
					.getProperty("foreignkey.resultcolumn");
			list.add(indexColumn);
			list.add(resultColumn);
			try {
				Map hash = ((Map) db.getTable(
						indexType.getProperty("foreignkey.table"))
									.getGivenColumns(list, 1).get(0));
				log.debug("Wert: " + hash.get(indexColumn));
				Object keyVal=((ValueObject) hash
						.get(indexColumn)).getValue();
				Object contentVal=((ValueObject) hash
						.get(resultColumn)).getValue();
				defaultValue = new ForeignKey(keyVal, contentVal);
			} catch (DatabaseException e) {
				log.error("Kann keinen default Foreign Key festlegen");
				defaultValue = null;
			}
		}
	}

	public Class getJavaType() {
		return ForeignKey.class;
	}

	public String printText(Object s) throws DatabaseException {
		return indexType.printText(((ForeignKey) s).getKey());
	}


	public String printSQL(Object s) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
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
	 * Diese Methode ergibt eine Liste von Hashes. In jedem Hash sind zwei
	 * Eintraege unter dem Namen der Indexspalte und der Resultspalte. Diese
	 * enthalten die Werte aus der fremden Tabelle. So ergibt sich eine Liste
	 * aller moeglichen Werte fuer den Fremdschluessel.
	 */
	public List getPossibleValues() throws DatabaseException {
		List list = new ArrayList();
		list.add(getProperty("foreignkey.indexcolumn"));
		list.add(getProperty("foreignkey.resultcolumn"));
		list = db.getTable(getProperty("foreignkey.table")).getGivenColumns(
				list, 0);
		return list;
	}

	public void addColumn(UpdateQuery query, Printable printable) {
		// TODO Auto-generated method stub
		
	}
}
/*
 * $Log: TypeDefinitionForeignKey.java,v $
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