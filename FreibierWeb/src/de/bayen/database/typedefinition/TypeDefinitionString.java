/* Erzeugt am 09.10.2004 von tbayen
 * $Id: TypeDefinitionString.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;

/**
 * Datentyp z.B. für SQL-Daten vom Typ char
 * 
 * @author tbayen
 */
public class TypeDefinitionString extends TypeDefinition {
		
	public TypeDefinitionString() {
		super();
		defaultValue = "";
	}

	public Class getJavaType(){
		return String.class;
	}

	public String format(Object s) throws SystemDatabaseException {
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

	public Object parse(String s) throws DatabaseException {
		s = s.trim();
		if (s.length() > length) {
			s = s.substring(0, length);
		}
		return s;
	}

	public boolean validate(String s) {
		return true;
	}
}
/*
 * $Log: TypeDefinitionString.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.2  2005/03/28 15:53:03  tbayen
 * Boolean-Typen eingeführt
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
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