//$Id: TypeDefinitionString.java,v 1.5 2005/02/24 22:18:12 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.impl.TypeDefinitionImpl;
import de.jalin.freibier.database.impl.ValueObject;


/**
 * @author tbayen
 * 
 * Datentyp z.B. für SQL-Daten vom Typ char
 */
public class TypeDefinitionString extends TypeDefinitionImpl {
		
	public TypeDefinitionString() {
		super();
		defaultValue = "";
	}

	public Class getJavaType(){
		return String.class;
	}

	public String printText(Object s) throws SystemDatabaseException {
		if (s != null) {
			if (s instanceof String) {
				return ((String) s).trim();
			} else {
				throw new SystemDatabaseException("String-Objekt erwartet", log);
			}
		} else {
			return "";
		}
	}

	public String printSQL(Object s) throws SystemDatabaseException {
		if (s != null) {
			if (s instanceof String) {
				return "'" + ((String) s).trim().replaceAll("'", "''") + "'";
			} else {
				throw new SystemDatabaseException("String-Objekt erwartet", log);
			}
		} else {
			return "''";
		}
	}

	public ValueObject parse(String s) throws DatabaseException {
		s = s.trim();
		ValueObject stringValue = null;
		if (s.length() > length) {
			s = s.substring(0, length);
		}
		stringValue = new ValueObject(s, this);
		return stringValue;
	}

	public boolean validate(String s) {
		return true;
	}

	public void addColumn(UpdateQuery query, Printable printable) {
		query.addColumn(printable.getName(), ((String) printable.getValue()));
	}
}
/*
 * $Log: TypeDefinitionString.java,v $
 * Revision 1.5  2005/02/24 22:18:12  phormanns
 * Tests laufen mit HSQL und MySQL
 *
 * Revision 1.4  2005/02/24 13:52:12  phormanns
 * Mit Tests begonnen
 *
 * Revision 1.3  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.2  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.8  2004/10/18 10:58:10  tbayen
 * verbesserte Typüberprüfung bei Zuweisung an DataObject
 *
 * Revision 1.7  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.6  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.5  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.4  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingefügt
 *
 * Revision 1.3  2004/10/09 20:51:16  tbayen
 * Editorformatierung justiert und Revisionskommentare neu formatiert
 *
 * Revision 1.2  2004/10/09 20:18:54  tbayen
 * Aufteilung der TypeDefinition in Unterklassen
 * 
 * Revision 1.1 2004/10/09 20:17:31 tbayen
 * Aufteilung der TypeDefinition in Unterklassen
 *  
 */