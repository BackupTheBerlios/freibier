/* Erzeugt am 09.10.2004 von tbayen
 * $Id: DataObject.java,v 1.8 2006/01/28 17:36:40 tbayen Exp $
 */
package de.bayen.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.exception.SysDBEx;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.SQL_getTableDBException;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.database.typedefinition.TypeDefinition;

/**
 * @author tbayen
 * 
 * Ein Objekt dieser Klasse stellt einen einzelnen Wert aus unserer Datenbank
 * dar. Der Wert kennt seinen genauen Datentyp, kann also z.B. auch
 * typspezifisch richtig formatiert werden.
 */
public class DataObject implements Printable {
	private static Log log = LogFactory.getLog(DataObject.class);
	private Object value;
	private TypeDefinition def = null;

	public DataObject(Object value, TypeDefinition def) {
		super();
		this.value = value;
		this.def = def;
	}

	public String format() throws WrongTypeDBException {
		return def.format(value);
	}

	public String formatNice() throws SQL_getTableDBException,
			TypeNotSupportedDBException, WrongTypeDBException {
		return NicePrinter.print(this);
	}

	public void parse(String value) throws ParseErrorDBException {
		this.value = def.parse(value);
	}

	public boolean validate(String value) {
		return def.validate(value);
	}

	public boolean validatePart(String value) {
		return def.validatePart(value);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) throws WrongTypeDBException {
		if (!def.getJavaType().equals(value.getClass())) {
			throw new SysDBEx.WrongTypeDBException(
					"Falscher Datentyp bei Zuweisung an DataObject: "
							+ value.getClass() + " statt " + def.getJavaType(),
					log);
		}
		this.value = value;
	}
	
	/**
	 * Ergibt den formatNice()-Wert des DataObject, also eine (ggf. hübsch
	 * formatierte) String-Darstellung des Wertes des Objekts.
	 * 
	 * @see java.lang.Object#toString()
	 */
	
	public String toString() {
		try {
			return formatNice();
		} catch (Exception e) {
			// bei irgendeinem Fehler nehme ich die Standard-Implementation
			return super.toString()+":E("+e.getMessage()+")";
		}
	}

	/**
	 * Diese Methode kann aufgerufen werden, wenn es sich bei diesem Feld um
	 * einen Foreign Key handelt. Sie ergibt den Record, auf den dieser 
	 * Foreign Key verweist.
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 * @throws ParseErrorDBException 
	 * @throws TypeNotSupportedDBException 
	 *
	 */
	public Record getForeignRecord(Database db) throws SQL_DBException,
			RecordNotExistsDBException, ParseErrorDBException,
			TypeNotSupportedDBException {
		if (!(getValue() instanceof ForeignKey)) {
			throw new SysDBEx.NotAForeignKeyDBException(
					"Dies ist kein Foreign Key", log);
		}
		ForeignKey fkey = ((ForeignKey) getValue());
		if (fkey.getKey() == null)
			return null;
		String foreigntable = def.getProperty("foreignkey.table");
		Table ftable = db.getTable(foreigntable);
		return ftable.getRecordByPrimaryKey(fkey.getKey());
	}

	/**
	 * Diese Methode kann aufgerufen werden, wenn es sich bei diesem Feld um
	 * einen Foreign Key handelt. Sie ergibt den Wert in der Resultcolumn der
	 * Zieltabelle.
	 * 
	 * @param db
	 * @return Zielwert
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 * @throws ParseErrorDBException 
	 * @throws TypeNotSupportedDBException 
	 */
	public String getForeignResultColumn(Database db) throws SQL_DBException,
			RecordNotExistsDBException, ParseErrorDBException,
			TypeNotSupportedDBException {
		if (!(getValue() instanceof ForeignKey)) {
			throw new SysDBEx.NotAForeignKeyDBException(
					"Dies ist kein Foreign Key", log);
		}
		ForeignKey fkey = ((ForeignKey) getValue());
		if (fkey.getKey() == null)
			return null;
		String foreigntable = def.getProperty("foreignkey.table");
		String resultcolumn = def.getProperty("foreignkey.resultcolumn");
		Table ftable = db.getTable(foreigntable);
		Record rec = ftable.getRecordByPrimaryKey(fkey.getKey());
		return rec.getFormatted(resultcolumn);
	}

	public TypeDefinition getTypeDefinition() {
		return def;
	}

	/**
	 * ergibt die maximale Länge des Objektes als String. Dies ist nicht die
	 * wirkliche Länge des konkreten Objektes.
	 */
	public int getLength() {
		return def.getLength();
	}

	public String getName() {
		return def.getName();
	}

	public Class getType() {
		return def.getClass();
	}

	public boolean getReadonly() {
		// damit fuer sowas nicht getProperty() aufgerufen werden muss
		String prop = def.getProperty("readonly");
		if (prop != null && prop.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	public String getProperty(String key) {
		return def.getProperty(key);
	}
}
/*
 * $Log: DataObject.java,v $
 * Revision 1.8  2006/01/28 17:36:40  tbayen
 * Extraktion von BLOBS optimiert (altes Todo abgearbeitet)
 *
 * Revision 1.7  2006/01/17 21:06:35  tbayen
 * DataObject.toString() eingefügt
 *
 * Revision 1.6  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.5  2005/08/15 16:17:12  tbayen
 * Javadoc-Warnungen beseitigt
 *
 * Revision 1.4  2005/08/14 20:06:21  tbayen
 * Verbesserungen an den ForeignKeys, die sich aus der FiBu ergeben haben
 *
 * Revision 1.3  2005/08/12 19:39:47  tbayen
 * kleine Nachbesserung...
 *
 * Revision 1.2  2005/08/12 19:37:18  tbayen
 * unnötige TO DO-Kommentare entfernt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.3  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.2  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.2  2005/03/19 12:55:04  tbayen
 * Zwischenmodell entfernt, stattdessen
 * direkter Zugriff auf Freibier-Klassen
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.10  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.9  2004/10/24 15:42:27  tbayen
 * DataObjectTextEditor als selbstständige Klasse implementiert
 *
 * Revision 1.8  2004/10/18 10:58:10  tbayen
 * verbesserte Typüberprüfung bei Zuweisung an DataObject
 *
 * Revision 1.7  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.6  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.5  2004/10/11 14:29:37  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.4  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingefügt
 *
 * Revision 1.3  2004/10/09 20:51:16  tbayen
 * Editorformatierung justiert und Revisionskommentare neu formatiert
 *
 * Revision 1.2  2004/10/09 20:17:31  tbayen
 * Aufteilung der TypeDefinition in Unterklassen
 *
 * Revision 1.1  2004/10/09 15:09:15  tbayen
 * Einführung von DataObject
 * 
 */