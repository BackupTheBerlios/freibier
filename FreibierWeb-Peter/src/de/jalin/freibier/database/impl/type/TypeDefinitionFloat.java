//$Id: TypeDefinitionFloat.java,v 1.3 2005/02/18 22:17:42 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import org.apache.oro.text.perl.Perl5Util;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * @author tbayen
 * 
 * Datentyp z.B. für SQL-Daten vom Typ int
 */
public class TypeDefinitionFloat extends TypeDefinitionNumber {
	
	static final Perl5Util regex = new Perl5Util();

	private NumberFormat numFormat = new DecimalFormat("0.00");
	private NumberFormat sqlNumFormat = NumberFormat.getNumberInstance(Locale.US);

	public TypeDefinitionFloat() {
		super();
		defaultValue = new Double(0.0d);
	}

	public Class getJavaType(){
		return Double.class;
	}

	public void setProperties(Map propsMap) {
		//log.trace("TypeDefinitionFloat.setProperties");
		super.setProperties(propsMap);
		String format = (String) propsMap.get("numberformat");
		if (format != null) {
			numFormat = new DecimalFormat(format);
		}
	}

	public String printText(Object d) throws SystemDatabaseException {
		if (d != null) {
			if (d instanceof Number) {
				return numFormat.format(d);
			} else {
				throw new SystemDatabaseException("Number-Objekt erwartet", log);
			}
		} else {
			return numFormat.format(0.0d);
		}
	}

	public String printSQL(Object d) throws SystemDatabaseException {
		if (d != null) {
			if (d instanceof Number) {
				return sqlNumFormat.format(d);
			} else {
				throw new SystemDatabaseException("Number-Objekt erwartet", log);
			}
		} else {
			return sqlNumFormat.format(0.0d);
		}
	}

	public ValueObject parse(String s) throws UserDatabaseException {
		Double d = null;
		ValueObject doubleValue = null;
		if (s != null && regex.match("/^(\\d+)\\.(\\d+)$/", s)) {
			s = regex.group(1) + "," + regex.group(2);
		}
		try {
			Number number = numFormat.parse(s);
			d = new Double(number.doubleValue());
			doubleValue = new ValueObject(d, this);
		} catch (NumberFormatException e) {
			throw new UserDatabaseException("Fehler im Zahlenformat: " + s, log);
		} catch (ParseException e) {
			throw new UserDatabaseException("Fehler im Zahlenformat: " + s, log);
		}
		return doubleValue;
	}

	public boolean validate(String s) {
		try {
			double d = Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
/*
 * $Log: TypeDefinitionFloat.java,v $
 * Revision 1.3  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.2  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
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