/* Erzeugt am 14.10.2004 von phormanns
 * $Id: TypeDefinitionDecimal.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.oro.text.perl.Perl5Util;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;

/**
 * @author tbayen
 * 
 * Datentyp z.B. für SQL-Daten vom Typ decimal oder double
 */
public class TypeDefinitionDecimal extends TypeDefinitionNumber {
	//private NumberFormat numFormat = new DecimalFormat("0.00");

	public TypeDefinitionDecimal() {
		super();
		defaultValue = BigDecimal.valueOf(0);
	}

	public Class getJavaType() {
		return Double.class;
	}

	public void setProperties(Map propsMap) {
		//log.trace("TypeDefinitionDecimal.setProperties");
		super.setProperties(propsMap);
		//String format = (String) propsMap.get("numberformat");
		//if (format != null) {
		//	numFormat = new DecimalFormat(format);
		//}
	}

	public String format(Object d) throws SystemDatabaseException {
		if (d != null) {
			if (d instanceof Number) {
				return d.toString();
			} else {
				throw new SystemDatabaseException("Number-Objekt erwartet", log);
			}
		} else {
			return "0";
		}
	}

	static final Perl5Util regex = new Perl5Util();

	public Object parse(String s) throws UserDatabaseException {
		if (s != null && regex.match("/^(\\d+)(\\.|,)(\\d+)$/", s)) {
			s = regex.group(1) + "." + regex.group(3);
		}
		try {
			return new BigDecimal(s);
		} catch (NumberFormatException e) {
			throw new UserDatabaseException("Fehler im Zahlenformat: " + s, log);
		}
	}

	public boolean validate(String s) {
		if (s != null && regex.match("/^(\\d+)(\\.|,)(\\d+)$/", s)) {
			s = regex.group(1) + "." + regex.group(3);
		}
		try {
			BigDecimal dec = new BigDecimal(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
/*
 * $Log: TypeDefinitionDecimal.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:11  tbayen
 * HBCI-Banking-Applikation fertiggestellt
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
 * Revision 1.7  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.6  2004/10/18 10:58:10  tbayen
 * verbesserte Typüberprüfung bei Zuweisung an DataObject
 *
 * Revision 1.5  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.4  2004/10/15 19:27:08  tbayen
 * Properties für Datentypen
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