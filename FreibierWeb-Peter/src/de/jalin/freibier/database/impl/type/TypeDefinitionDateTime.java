//$Id: TypeDefinitionDateTime.java,v 1.4 2005/02/18 22:17:42 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;
import de.jalin.freibier.database.impl.TypeDefinitionImpl;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * Datentyp z.B. für SQL-Daten vom Typ DATETIME
 * 
 * Dient als Oberklasse für alle Zeit- und Datumstypen
 */
public class TypeDefinitionDateTime extends TypeDefinitionImpl {
	
	private DateFormat shortFormat
		= DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	public TypeDefinitionDateTime() {
		super();
		defaultValue = null;
	}
	
	public Class getJavaType(){
		return Date.class;
	}
	
	public Object getDefaultValue() {
		Object value = super.getDefaultValue();
		if(value==null)
			return new Date();
		else
			return value;
	}

	public String printText(Object date) throws SystemDatabaseException {
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

	public String printSQL(Object date) throws SystemDatabaseException {
		if (date != null) {
			if (date instanceof Date) {
				return "'" + shortFormat.format((Date) date) + "'";
			} else {
				throw new SystemDatabaseException("Objekt vom Type Date erwartet.", log);
			}
		} else {
			return "'0000-00-00'";
		}
	}

	public ValueObject parse(String s) throws DatabaseException {
		Date date = null;
		ValueObject dateValue = null;
		try {
			if(s != null && s.trim().length() > 0) {
				date = shortFormat.parse(s.trim());
				dateValue = new ValueObject(date, this);
			}
		} catch (ParseException e) {
			throw new UserDatabaseException("Fehler im Datumsformat", log);
		}
		return dateValue;
	}

	public boolean validate(String s) {
		try {
			Date date = shortFormat.parse(s);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	protected DateFormat getShortFormat() {
		return shortFormat;
	}
	
	protected void setShortFormat(DateFormat shortFormat) {
		this.shortFormat = shortFormat;
	}
}

/*
 * $Log: TypeDefinitionDateTime.java,v $
 * Revision 1.4  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.3  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.2  2005/02/11 15:50:35  phormanns
 * Merge
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
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