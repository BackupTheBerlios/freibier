/* Erzeugt am 21.10.2004 von tbayen
 * $Id: TypeDefinitionForeignKey.java,v 1.6 2005/11/24 11:47:45 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import java.util.ArrayList;
import java.util.List;
import de.bayen.database.Database;
import de.bayen.database.ForeignKey;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.SQL_getTableDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;

/**
 * Definition f�r einen Wert, der ein Fremdschl�ssel ist, d.h. er referenziert
 * einen bestimmten Wert in einer anderen Tabelle. Was genau referenziert wird,
 * wird durch Properties bestimmt.
 */
public class TypeDefinitionForeignKey extends TypeDefinition {
	private TypeDefinition indexType = null;
	private TypeDefinition referenceType = null; // kann (bei Problemen) auch null sein
	// Ich muss mir die Database merken, um auf den referenzierten Wert zugreifen zu k�nnen
	private Database db;

	public TypeDefinitionForeignKey(TypeDefinition indexType, Database db)
			throws ParseErrorDBException {
		super();
		log.trace("TypeDefinitionForeignKey Constructor");
		// Die Datenbank merke ich mir, weil man die ggf. braucht, um auf die
		// referenzierte Tabelle zugreifen zu k�nnen.
		this.db = db;
		// So, wie ich mir in ForeignKey den Index und den Content merke, merke
		// ich mir hier die Typen von beiden, um diese z.B. im NicePrinter beide 
		// formatieren zu k�nnen.
		this.indexType = indexType;
		// Default Value setzen:
		String defaultProp = getProperty("default");
		if (defaultProp != null) {
			//			try {
			defaultValue = new ForeignKey(indexType.parse(defaultProp), null);
			//			} catch (DatabaseException e1) {
			//				log.error("Default Foreign Key kann nicht gelesen werden", e1);
			//				defaultValue = null;
			//			}
		} else {
			List list = new ArrayList();
			String indexColumn = indexType
					.getProperty("foreignkey.indexcolumn");
			String resultColumn = indexType
					.getProperty("foreignkey.resultcolumn");
			list.add(indexColumn);
			list.add(resultColumn);
			//			try {
			//				List ersterRecord = db.getTable(
			//						indexType.getProperty("foreignkey.table"))
			//						.getGivenColumns(list, 1);
			//				if (ersterRecord.size() == 0) {
			//					throw new SysDBEx(
			//							"Fremdschl�sseltabelle leer", log);
			//				}
			//				Map hash = (Map) ersterRecord.get(0);
			//				log.debug("Wert: " + hash.get(indexColumn));
			//				Object keyVal = ((DataObject) hash.get(indexColumn)).getValue();
			//				Object contentVal = ((DataObject) hash.get(resultColumn))
			//						.getValue();
			//				defaultValue = new ForeignKey(keyVal, contentVal);
			//			} catch (DatabaseException e) {
			//				log.error("Kann keinen default Foreign Key festlegen");
			defaultValue = null;
			//			}
		}
	}

	public Class getJavaType() {
		return ForeignKey.class;
	}

	public String format(Object s) throws WrongTypeDBException {
		return indexType.format(((ForeignKey) s).getKey());
	}

	public Object parse(String s) throws ParseErrorDBException {
		return new ForeignKey(indexType.parse(s), null);
	}

	public boolean validate(String s) {
		return indexType.validate(s);
	}

	public TypeDefinition getIndexType() {
		return indexType;
	}

	public TypeDefinition getReferenceType() throws SQL_getTableDBException,
			IllegalDefaultValueDBException, ParseErrorDBException {
		if (db != null && referenceType == null) {
			// Ich hole den Referenztyp erst, wenn er benutzt wird. 
			// Am Anfang hatte ich den bei der Initialisierung geholt, was
			// aber zu Endlosschleifen f�hrt, wenn z.B. ein Konto auf ein 
			// anderes Konto verweist.
			referenceType = db.getTable(
					indexType.getProperty("foreignkey.table"))
					.getRecordDefinition().getFieldDef(
							indexType.getProperty("foreignkey.resultcolumn"));
		}
		return referenceType;
	}

	/**
	 * Diese Methode ergibt eine Liste von Hashes. In jedem Hash sind zwei
	 * Eintr�ge unter dem Namen der Indexspalte und der Resultspalte. Diese
	 * enthalten die Werte aus der fremden Tabelle. So ergibt sich eine Liste
	 * aller m�glichen Werte f�r den Fremdschl�ssel.
	 * @throws SQL_DBException 
	 * @throws IllegalDefaultValueDBException 
	 * @throws SQL_getTableDBException 
	 * @throws ParseErrorDBException 
	 */
	public List getPossibleValues() throws SQL_getTableDBException,
			IllegalDefaultValueDBException, SQL_DBException,
			ParseErrorDBException {
		List list = new ArrayList();
		list.add(getIndexcolumn());
		list.add(getResultcolumn());
		list = db.getTable(getProperty("foreignkey.table")).getGivenColumns(
				list, 0);
		return list;
	}

	/**
	 * Ergibt den Namen der Spalte in der fremden Tabelle, die den
	 * endg�ltigen Wert enth�lt (also den, der normalerweise ausgegeben 
	 * werden soll, wenn es darum geht, eine Ausgabe f�r den Benutzer
	 * aufzubereiten.
	 * 
	 * @return Name der Spalte in der fremden Tabelle
	 */
	private String getResultcolumn() {
		return getProperty("foreignkey.resultcolumn");
	}

	/**
	 * Ergibt den Namen der Indexspalte (normalerweise die 
	 * Prim�rschl�sselspalte) der fremden Tabelle.
	 * 
	 * @return Name der Indexspalte
	 */
	public String getIndexcolumn() {
		return getProperty("foreignkey.indexcolumn");
	}
}
/*
 * $Log: TypeDefinitionForeignKey.java,v $
 * Revision 1.6  2005/11/24 11:47:45  tbayen
 * getSelectStatement(), das auch bei null-Fremdschl�ssel funktioniert
 * sowie einige Verbesserungen in der JavaDoc
 *
 * Revision 1.5  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und �berall eingef�hrt
 *
 * Revision 1.4  2005/08/14 20:06:21  tbayen
 * Verbesserungen an den ForeignKeys, die sich aus der FiBu ergeben haben
 *
 * Revision 1.3  2005/08/12 19:39:47  tbayen
 * kleine Nachbesserung...
 *
 * Revision 1.2  2005/08/12 19:37:18  tbayen
 * unn�tige TO DO-Kommentare entfernt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
 *
 * Revision 1.2  2005/03/02 18:02:56  tbayen
 * Probleme mit leeren Tabellen behoben
 * Einige �nderungen wie mit G�nther besprochen
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.3  2004/10/25 20:41:52  tbayen
 * Test f�r insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.2  2004/10/24 19:15:07  tbayen
 * ComboBox als Auswahlfeld f�r Foreign Keys
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.6  2004/10/24 15:42:27  tbayen
 * DataObjectTextEditor als selbstst�ndige Klasse implementiert
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