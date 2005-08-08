/* Erzeugt am 09.10.2004 von phormanns
 * $Id: TypeDefinitionDateTime.java,v 1.2 2005/08/08 06:35:29 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;

/**
 * Datentyp z.B. für SQL-Daten vom Typ DATETIME
 * 
 * Dient als Oberklasse für alle Zeit- und Datumstypen
 */
public class TypeDefinitionDateTime extends TypeDefinition {
	protected DateFormat shortFormat;

	protected TypeDefinitionDateTime() {
		super();
		defaultValue = null;
		setDefaultShortFormat();
	}
	
	protected void setDefaultShortFormat(){
		shortFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
	}

	public Class getJavaType(){
		return Date.class;
	}
	
	public Object getDefaultValue() {
		Object value = super.getDefaultValue();
		//if(value==null)
		//	return new Date();
		//else
			return value;
	}

	public String format(Object date) throws SystemDatabaseException {
		if (date != null) {
			if (date instanceof Date) {
				return shortFormat.format((Date) date);
			} else {
				throw new SystemDatabaseException("Objekt vom Type Date erwartet.", log);
			}
		} else {
			return "";
		}
	}

	public Object parse(String s) throws DatabaseException {
		Date date = null;
		try {
			if(s != null && s.trim().length() > 0)
				date = shortFormat.parse(s.trim());
		} catch (ParseException e) {
			throw new UserDatabaseException("Fehler im Datumsformat", log);
		}
		return date;
	}

	public boolean validate(String s) {
		try {
			shortFormat.parse(s);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
}

/*
 * $Log: TypeDefinitionDateTime.java,v $
 * Revision 1.2  2005/08/08 06:35:29  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.2  2005/03/28 16:43:57  tbayen
 * Daten können jetzt auch null enthalten
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.2  2004/12/19 13:24:06  phormanns
 * Web-Version des Tabellen-Browsers mit Edit, Filter (1x)
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.6  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen für DATE und TIME
 *
 * Revision 1.5  2004/10/18 10:58:10  tbayen
 * verbesserte Typüberprüfung bei Zuweisung an DataObject
 *
 * Revision 1.4  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.3  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.2  2004/10/15 15:11:11  tbayen
 * Ehre wem Ehre gebührt
 *
 * Revision 1.1  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 */