//$Id: TypeDefinitionInteger.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import org.apache.oro.text.perl.Perl5Util;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;

/**
 * Datentyp z.B. für SQL-Daten vom Typ int
 *
 * @author tbayen
 */
public class TypeDefinitionInteger extends TypeDefinitionNumber {

	public TypeDefinitionInteger() {
		super();
		defaultValue = new Long(0l);
	}

	public Class getJavaType(){
		return Long.class;
	}

	public String format(Object i) throws SystemDatabaseException {
		if (i != null) {
			if (i instanceof Number) {
				return i.toString();
			} else {
				throw new SystemDatabaseException("Number-Objekt erwartet", log);
			}
		} else {
			return "";
		}
	}

	public Object parse(String s) throws UserDatabaseException {
		Long l = null;
		try {
			l = Long.valueOf(s);
		} catch (NumberFormatException e) {
			throw new UserDatabaseException("Fehler im Zahlenformat: " + s, log);
		}
		return l;
	}

	public boolean validate(String s) {
		s = s.trim();
		Perl5Util regex = new Perl5Util();
		return regex.match("/^-?\\d+$/", s);
	}
}
/*
 * $Log: TypeDefinitionInteger.java,v $
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.10  2004/10/24 15:42:27  tbayen
 * DataObjectTextEditor als selbstständige Klasse implementiert
 *
 * Revision 1.9  2004/10/18 10:58:10  tbayen
 * verbesserte Typüberprüfung bei Zuweisung an DataObject
 *
 * Revision 1.8  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.7  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.6  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.5  2004/10/11 09:37:46  tbayen
 * unnütze Hilfsfunktion entfernt
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