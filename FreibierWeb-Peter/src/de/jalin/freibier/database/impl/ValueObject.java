//$Id: ValueObject.java,v 1.2 2005/03/03 22:32:45 phormanns Exp $

package de.jalin.freibier.database.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;

/**
 * @author tbayen
 * 
 * Ein Objekt dieser Klasse stellt einen einzelnen Wert aus unserer Datenbank
 * dar. Der Wert kennt seinen genauen Datentyp, kann also z.B. auch
 * typspezifisch richtig formatiert werden.
 */
public class ValueObject implements Printable {
	
	private static Log log = LogFactory.getLog(ValueObject.class);
	
	private Object value = null;
	private TypeDefinition def = null;

	public ValueObject(Object value, TypeDefinition def)
			throws UserDatabaseException {
		this.value = value;
		this.def = def;
	}

	public String getText() throws DatabaseException {
		return def.printText(value);
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

	public int getLength() {
		return def.getLength();
	}

	public String getName() {
		return def.getName();
	}

	public Class getType() {
		return def.getClass();
	}

	public String getProperty(String key) {
		return def.getProperty(key);
	}
}
/*
 * $Log: ValueObject.java,v $
 * Revision 1.2  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.1  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.4  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.3  2005/02/14 21:24:43  phormanns
 * Kleinigkeiten
 *
 * Revision 1.2  2005/01/29 20:21:59  phormanns
 * RecordDefinition in TableImpl integriert
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste �ffentliche Version
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