//$Id: TypeDefinitionForeignKey.java,v 1.1 2004/12/31 17:13:03 phormanns Exp $

package de.jalin.freibier.database.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.jalin.freibier.database.DataObject;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.ForeignKey;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

/**
 * Definition für einen Wert, der ein Fremdschlüssel ist, d.h. er referenziert
 * einen bestimmten Wert in einer anderen Tabelle. Was genau referenziert wird,
 * wird durch Properties bestimmt.
 */
public class TypeDefinitionForeignKey extends TypeDefinition {
	private TypeDefinition indexType = null;
	private TypeDefinition referenceType = null; // kann (bei Problemen) auch null sein
	// Ich muss mir die Database merken, um auf den referenzierten Wert zugreifen zu können
	private Database db;

	public TypeDefinitionForeignKey(TypeDefinition indexType, Database db)
			throws SystemDatabaseException {
		super();
		log.trace("TypeDefinitionForeignKey Constructor");
		// Die Datenbank merke ich mir, weil man die ggf. braucht, um auf die
		// referenzierte Tabelle zugreifen zu können.
		this.db = db;
		// So, wie ich mir in ForeignKey den Index und den Content merke, merke
		// ich mir hier die Typen von beiden, um diese z.B. im NicePrinter beide 
		// formatieren zu können.
		this.indexType = indexType;
		if (db != null) {
			referenceType = db.getTable(
					indexType.getProperty("foreignkey.table"))
					.getRecordDefinition().getFieldDef(
							indexType.getProperty("foreignkey.resultcolumn"));
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
				Object keyVal=((DataObject) hash
						.get(indexColumn)).getValue();
				Object contentVal=((DataObject) hash
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

	public String format(Object s) throws DatabaseException {
		return indexType.format(((ForeignKey) s).getKey());
	}

	public Object parse(String s) throws DatabaseException {
		return new ForeignKey(indexType.parse(s), null);
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
	 * Einträge unter dem Namen der Indexspalte und der Resultspalte. Diese
	 * enthalten die Werte aus der fremden Tabelle. So ergibt sich eine Liste
	 * aller möglichen Werte für den Fremdschlüssel.
	 */
	public List getPossibleValues() throws DatabaseException {
		List list = new ArrayList();
		list.add(getProperty("foreignkey.indexcolumn"));
		list.add(getProperty("foreignkey.resultcolumn"));
		list = db.getTable(getProperty("foreignkey.table")).getGivenColumns(
				list, 0);
		return list;
	}
}
/*
 * $Log: TypeDefinitionForeignKey.java,v $
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.3  2004/10/25 20:41:52  tbayen
 * Test für insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.2  2004/10/24 19:15:07  tbayen
 * ComboBox als Auswahlfeld für Foreign Keys
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.6  2004/10/24 15:42:27  tbayen
 * DataObjectTextEditor als selbstständige Klasse implementiert
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