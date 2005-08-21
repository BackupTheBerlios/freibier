/* Erzeugt am 11.10.2004 von tbayen
 * $Id: SQLPrinter.java,v 1.2 2005/08/21 17:06:59 tbayen Exp $
 */
package de.bayen.database;

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
import de.bayen.database.exception.SysDBEx;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.database.typedefinition.TypeDefinitionBLOB;
import de.bayen.database.typedefinition.TypeDefinitionBool;
import de.bayen.database.typedefinition.TypeDefinitionDate;
import de.bayen.database.typedefinition.TypeDefinitionDateTime;
import de.bayen.database.typedefinition.TypeDefinitionDecimal;
import de.bayen.database.typedefinition.TypeDefinitionForeignKey;
import de.bayen.database.typedefinition.TypeDefinitionInteger;
import de.bayen.database.typedefinition.TypeDefinitionString;

/**
 * Diese Klasse gibt Printable-Objekte in SQL-Syntax aus.
 * 
 * @author tbayen
 */
public class SQLPrinter {
	private static final Log log = LogFactory.getLog(SQLPrinter.class);
	private static Perl5Util regex = new Perl5Util();
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final NumberFormat floatFormat = new DecimalFormat("0.0###",
			new DecimalFormatSymbols(Locale.US));

	public static String print(Printable obj)
			throws TypeNotSupportedDBException, WrongTypeDBException {
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
		} else if (TypeDefinitionDecimal.class.equals(obj.getType())) {
			val = floatFormat.format(obj.getValue());
		} else if (TypeDefinitionInteger.class.equals(obj.getType())) {
			val = obj.format();
		} else if (TypeDefinitionBool.class.equals(obj.getType())) {
			val = ((Boolean) obj.getValue()).booleanValue() ? "1" : "0";
		} else if (TypeDefinitionBLOB.class.equals(obj.getType())) {
			log.debug(obj.getValue().getClass().getName());
			val = ((BLOB) obj.getValue()).toSQL();
		} else if (TypeDefinitionForeignKey.class.equals(obj.getType())) {
			val = obj.format();
			if (val.equals(""))
				val = "null";
		} else {
			throw new SysDBEx.TypeNotSupportedDBException(
					"Typ wird vom SQL-Printer nicht unterstützt.", log);
		}
		// damit könnte man auf Ableitungsbeziehungen testen: 
		//if(TypeDefinitionString.class.isAssignableFrom(obj.getType())){...}
		return val;
	}
}
/*
 * $Log: SQLPrinter.java,v $
 * Revision 1.2  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:47  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.4  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.3  2005/03/28 15:53:03  tbayen
 * Boolean-Typen eingeführt
 *
 * Revision 1.2  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
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