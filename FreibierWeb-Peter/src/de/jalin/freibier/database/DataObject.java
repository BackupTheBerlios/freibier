//$Id: DataObject.java,v 1.1 2004/12/31 17:12:42 phormanns Exp $

package de.jalin.freibier.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;
import de.jalin.freibier.database.type.TypeDefinition;

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
 * $Log: DataObject.java,v $
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste öffentliche Version
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