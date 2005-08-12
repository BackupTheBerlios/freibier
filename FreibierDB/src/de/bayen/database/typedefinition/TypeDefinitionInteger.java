/* Erzeugt am 09.10.2004 von tbayen
 * $Id: TypeDefinitionInteger.java,v 1.2 2005/08/12 19:37:18 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import org.apache.oro.text.perl.Perl5Util;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;

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
		if (i != null && !i.equals("")) {
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
		if(s==null || s.equals("") || s.equals("null"))
			return null;
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
 * Revision 1.2  2005/08/12 19:37:18  tbayen
 * unnötige TODO-Kommentare entfernt
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
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.3  2005/02/28 11:18:28  tbayen
 * Layout verfeinert
 *
 * Revision 1.2  2005/02/24 11:48:33  tbayen
 * automatische Aktivierung des ersten Eingabefeldes
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
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