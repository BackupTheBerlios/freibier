//$Id: SQLPrinter.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.impl.type.TypeDefinitionDate;
import de.jalin.freibier.database.impl.type.TypeDefinitionDateTime;
import de.jalin.freibier.database.impl.type.TypeDefinitionFloat;
import de.jalin.freibier.database.impl.type.TypeDefinitionForeignKey;
import de.jalin.freibier.database.impl.type.TypeDefinitionInteger;
import de.jalin.freibier.database.impl.type.TypeDefinitionString;

/**
 * Diese Klasse gibt Printable-Objekte in SQL-Syntax aus.
 * 
 * @author tbayen
 */
public class SQLPrinter {
	private static final Log log = LogFactory.getLog(SQLPrinter.class);
	private static Perl5Util regex = new Perl5Util();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final NumberFormat floatFormat 
		= new DecimalFormat("0.0###", new DecimalFormatSymbols(Locale.US)); 
	
	public static String print(Printable obj) throws DatabaseException {
		if (obj.getValue() == null)
			return "NULL";
		String val;
		if (TypeDefinitionString.class.equals(obj.getType())) {
			val = obj.format();
			val = regex.substitute("s/\\\\/\\\\\\\\/g", val); // \
			val = regex.substitute("s/\"/\\\\\"/g", val); // "
			val = regex.substitute("s/'/\\\\'/g", val); // '
			// theoretisch sind noch mehr Sachen zu quoten (siehe MySQL-Doku,
			// Kapitel 10.1.1). Aber hier kommen keine Nullbytes, CR, Tab, etc.
			// vor und das Ganze wird ja auch nicht schneller, wenn ich nach
			// noch mehr Sachen suche...
			val = "'" + val + "'";
		} else if (TypeDefinitionDate.class.equals(obj.getType())) {
			val = "'" + dateFormat.format((Date) obj.getValue()) + "'";
		} else if (TypeDefinitionDateTime.class.equals(obj.getType())) {
			val = "'" + dateFormat.format((Date) obj.getValue()) + "'";
		} else if (TypeDefinitionFloat.class.equals(obj.getType())) {
			val = floatFormat.format(obj.getValue());
		} else if (TypeDefinitionInteger.class.equals(obj.getType())) {
			val = obj.format();
		} else if (TypeDefinitionForeignKey.class.equals(obj.getType())) {
			val = obj.format();
			if(val.equals(""))
				val="null";
		} else {
			throw new SystemDatabaseException("Typ wird vom SQL-Printer nicht unterstützt.", log);
		}
		// damit könnte man auf Ableitungsbeziehungen testen: 
		//if(TypeDefinitionString.class.isAssignableFrom(obj.getType())){...}
		return val;
	}
}
/*
 * $Log: SQLPrinter.java,v $
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.9  2004/12/19 13:24:06  phormanns
 * Web-Version des Tabellen-Browsers mit Edit, Filter (1x)
 *
 * Revision 1.8  2004/10/25 20:41:52  tbayen
 * Test für insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.7  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.6  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.5  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.4  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.3  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.2  2004/10/13 19:11:30  tbayen
 * Erstellung von TableGUI und TestWindow,
 * dazu Überarbeitung und Debugging vieler anderer Klassen
 *
 * Revision 1.1  2004/10/11 14:29:37  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 */