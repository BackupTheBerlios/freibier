//$Id: TypeDefinitionDateTime.java,v 1.8 2005/03/03 22:32:45 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;
import de.jalin.freibier.database.impl.TypeDefinitionImpl;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * Datentyp z.B. f�r SQL-Daten vom Typ DATETIME
 * 
 * Dient als Oberklasse f�r alle Zeit- und Datumstypen
 */
public class TypeDefinitionDateTime extends TypeDefinitionImpl {
	
	private DateFormat shortDateTimeFormat
		= DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
	private DateFormat shortDateFormat
		= DateFormat.getDateInstance(DateFormat.MEDIUM);
	private DateFormat sqlDateFormat 
		= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public TypeDefinitionDateTime() {
		super();
		defaultValue = new Date();
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
				return shortDateTimeFormat.format((Date) date);
			} else {
				throw new SystemDatabaseException("Objekt vom Type Date erwartet.", log);
			}
		} else {
			return "";
		}
	}

	public ValueObject parse(String s) throws DatabaseException {
		Date date = null;
		ValueObject dateValue = null;
		try {
			if(s != null && s.trim().length() > 0) {
				date = shortDateTimeFormat.parse(s.trim());
				dateValue = new ValueObject(date, this);
			}
		} catch (ParseException e1) {
			try {
				if(s != null && s.trim().length() > 0) {
					date = shortDateFormat.parse(s.trim());
					dateValue = new ValueObject(date, this);
				}
			} catch (ParseException e2) {
				throw new UserDatabaseException("Fehler im Datumsformat", log);
			}
		}
		return dateValue;
	}

	public boolean validate(String s) {
		try {
			Date date = shortDateTimeFormat.parse(s);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	protected DateFormat getShortDateTimeFormat() {
		return shortDateTimeFormat;
	}
	
	protected void setShortDateTimeFormat(DateFormat shortFormat) {
		this.shortDateTimeFormat = shortFormat;
	}

	public void addColumn(UpdateQuery query, Printable printable) {
		query.addColumn(printable.getName(), (Date) printable.getValue());
	}

	public void addColumn(InsertQuery query, Printable printable) {
		query.addColumn(printable.getName(), (Date) printable.getValue());
	}
}

/*
 * $Log: TypeDefinitionDateTime.java,v $
 * Revision 1.8  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.7  2005/03/01 21:56:32  phormanns
 * Long immer als Value-Objekt zu Number-Typen
 * setRecord macht Insert, wenn PK = Default-Value
 *
 * Revision 1.6  2005/02/24 22:18:12  phormanns
 * Tests laufen mit HSQL und MySQL
 *
 * Revision 1.5  2005/02/24 13:52:12  phormanns
 * Mit Tests begonnen
 *
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
 * Erste �ffentliche Version
 *
 * Revision 1.2  2004/12/19 13:24:06  phormanns
 * Web-Version des Tabellen-Browsers mit Edit, Filter (1x)
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.6  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen f�r DATE und TIME
 *
 * Revision 1.5  2004/10/18 10:58:10  tbayen
 * verbesserte Typ�berpr�fung bei Zuweisung an DataObject
 *
 * Revision 1.4  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingef�hrt,
 * neue Verwaltung von Properties f�r Datentypen
 *
 * Revision 1.3  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.2  2004/10/15 15:11:11  tbayen
 * Ehre wem Ehre geb�hrt
 *
 * Revision 1.1  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 */