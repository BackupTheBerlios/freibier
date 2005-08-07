/* Erzeugt am 09.10.2004 von tbayen
 * $Id: DataObject.java,v 1.1 2005/08/07 21:18:49 tbayen Exp $
 */
package de.bayen.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;
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

	public DataObject(Object value, TypeDefinition def)
			throws UserDatabaseException {
		super();
		this.value = value;
		this.def = def;
	}

	public String format() throws DatabaseException {
		return def.format(value);
	}
	
	public String formatNice() throws DatabaseException{
		// TODO: Peter - dies ist neu
		return NicePrinter.print(this);	
	}

	public void parse(String value) throws DatabaseException {
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

	public void setValue(Object value) throws SystemDatabaseException {
		if (!def.getJavaType().equals(value.getClass())) {
			throw new SystemDatabaseException(
					"Falscher Datentyp bei Zuweisung an DataObject: "
							+ value.getClass() + " statt " + def.getJavaType(),
					log);
		}
		this.value = value;
	}

	public TypeDefinition getTypeDefinition() {
		return def;
	}

	/**
	 * ergibt die maximale L�nge des Objektes als String. Dies ist nicht die
	 * wirkliche L�nge des konkreten Objektes.
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
		// TODO Peter: dies ist neu
		// damit fuer sowas nicht getProperty() aufgerufen werden muss
		String prop=def.getProperty("readonly");
		if(prop != null && prop.equals("1")){
			return true;
		}else{
			return false;
		}
	}

	public String getProperty(String key) {
		return def.getProperty(key);
	}
}
/*
 * $Log: DataObject.java,v $
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
 * Bin�rdaten (BLOBS) in der Datenbank und im Webinterface
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare �berarbeitet
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
 * DataObjectTextEditor als selbstst�ndige Klasse implementiert
 *
 * Revision 1.8  2004/10/18 10:58:10  tbayen
 * verbesserte Typ�berpr�fung bei Zuweisung an DataObject
 *
 * Revision 1.7  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingef�hrt,
 * neue Verwaltung von Properties f�r Datentypen
 *
 * Revision 1.6  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.5  2004/10/11 14:29:37  tbayen
 * Framework f�r Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.4  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingef�gt
 *
 * Revision 1.3  2004/10/09 20:51:16  tbayen
 * Editorformatierung justiert und Revisionskommentare neu formatiert
 *
 * Revision 1.2  2004/10/09 20:17:31  tbayen
 * Aufteilung der TypeDefinition in Unterklassen
 *
 * Revision 1.1  2004/10/09 15:09:15  tbayen
 * Einf�hrung von DataObject
 * 
 */