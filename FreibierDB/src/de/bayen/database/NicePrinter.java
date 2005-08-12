/* Erzeugt am 16.10.2004 von tbayen
 * $Id: NicePrinter.java,v 1.3 2005/08/12 19:39:47 tbayen Exp $
 */
package de.bayen.database;

import java.text.DateFormat;
import java.text.DecimalFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.database.typedefinition.TypeDefinition;
import de.bayen.database.typedefinition.TypeDefinitionBLOB;
import de.bayen.database.typedefinition.TypeDefinitionBool;
import de.bayen.database.typedefinition.TypeDefinitionDate;
import de.bayen.database.typedefinition.TypeDefinitionDateTime;
import de.bayen.database.typedefinition.TypeDefinitionDecimal;
import de.bayen.database.typedefinition.TypeDefinitionForeignKey;
import de.bayen.database.typedefinition.TypeDefinitionInteger;
import de.bayen.database.typedefinition.TypeDefinitionString;
import de.bayen.database.typedefinition.TypeDefinitionTime;

/**
 * Diese Klasse versucht, einen DataObject-Wert möglichst "schön", d.h.
 * bestmöglich formatiert, darzustellen.
 * 
 * @author tbayen
 */
public class NicePrinter {
	private static final Log log = LogFactory.getLog(SQLPrinter.class);

	/**
	 * Schön formatierte Ausgabe des Printable-Objektes als String.
	 * 
	 * @param obj
	 * @return ausgabe
	 * @throws DatabaseException
	 */
	public static String print(Printable obj) throws DatabaseException {
		if (obj.getValue() == null)
			return "";
		String val;
		if (TypeDefinitionString.class.equals(obj.getType())) {
			val = obj.format();
		} else if (TypeDefinitionDateTime.class.equals(obj.getType())) {
			val = DateFormat.getDateTimeInstance(DateFormat.LONG,
					DateFormat.MEDIUM).format(obj.getValue());
		} else if (TypeDefinitionDate.class.equals(obj.getType())) {
			val = DateFormat.getDateInstance(DateFormat.LONG).format(
					obj.getValue());
		} else if (TypeDefinitionTime.class.equals(obj.getType())) {
			val = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(
					obj.getValue());
		} else if (TypeDefinitionDecimal.class.equals(obj.getType())) {
			String numberFormat = ((DataObject) obj)
					.getProperty("numberformat");
			if (numberFormat == null)
				numberFormat = "#,##0.00";
			val = (new DecimalFormat(numberFormat)).format(obj.getValue());
		} else if (TypeDefinitionInteger.class.equals(obj.getType())) {
			val = obj.format();
		} else if (TypeDefinitionBool.class.equals(obj.getType())) {
			val = ((Boolean)obj.getValue()).booleanValue()?"Ja":"Nein";
		} else if (TypeDefinitionBLOB.class.equals(obj.getType())) {
			val = "Datei mit "
					+ String.valueOf(((BLOB) obj.getValue()).length())
					+ " Bytes";
		} else if (TypeDefinitionForeignKey.class.equals(obj.getType())) {
			TypeDefinition referenceType = ((TypeDefinitionForeignKey) ((DataObject) obj)
					.getTypeDefinition()).getReferenceType();
			val =
			//	"(" + obj.format() + ") " + // besser weg damit!?
			referenceType.format(((ForeignKey) obj.getValue()).getContent());
		} else {
			throw new SystemDatabaseException(
					"Typ wird vom Nice-Printer nicht unterstützt: "
							+ obj.getType(), log);
		}
		// damit könnte man auf Ableitungsbeziehungen testen: 
		//if(TypeDefinitionString.class.isAssignableFrom(obj.getType())){...}
		return val;
	}
}
/*
 * $Log: NicePrinter.java,v $
 * Revision 1.3  2005/08/12 19:39:47  tbayen
 * kleine Nachbesserung...
 *
 * Revision 1.2  2005/08/12 19:37:18  tbayen
 * unnötige TO DO-Kommentare entfernt
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
 * Revision 1.3  2005/02/24 13:24:59  tbayen
 * Referenzen und Listen funktionieren jetzt!
 *
 * Revision 1.2  2005/02/24 12:15:46  tbayen
 * Anzeigen von Referenzen (Editieren noch nicht)
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.7  2004/10/29 13:30:33  tbayen
 * NicePrinter an neue Date und Time-Klassen angepasst
 *
 * Revision 1.6  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.5  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.4  2004/10/23 12:22:20  tbayen
 * Zugriff auf ForeignKey-Felder per getter/setter
 *
 * Revision 1.3  2004/10/23 12:08:28  tbayen
 * NicePrinter gibt Foreign Keys aus
 *
 * Revision 1.2  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.1  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 */