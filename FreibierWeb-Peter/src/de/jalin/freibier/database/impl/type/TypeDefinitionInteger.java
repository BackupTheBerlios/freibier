//$Id: TypeDefinitionInteger.java,v 1.7 2005/03/03 22:32:45 phormanns Exp $

package de.jalin.freibier.database.impl.type;

import java.text.NumberFormat;
import java.text.ParseException;
import org.apache.oro.text.perl.Perl5Util;
import com.crossdb.sql.InsertQuery;
import com.crossdb.sql.UpdateQuery;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.exception.UserDatabaseException;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * Datentyp z.B. fuer SQL-Daten vom Typ int
 *
 * @author tbayen
 */
public class TypeDefinitionInteger extends TypeDefinitionNumber {

	private static final NumberFormat numFo = NumberFormat.getNumberInstance();
	
	public TypeDefinitionInteger() {
		super();
		defaultValue = new Long(-1L);
	}

	public Class getJavaType(){
		return Long.class;
	}

	public String printText(Object i) throws SystemDatabaseException {
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

	public ValueObject parse(String s) throws UserDatabaseException {
		ValueObject longValue = null;
		try {
			longValue = new ValueObject(new Long(numFo.parse(s).longValue()), this);
		} catch (ParseException e) {
			throw new UserDatabaseException("Fehler im Zahlenformat: " + s, log);
		}
		return longValue;
	}

	public boolean validate(String s) {
		s = s.trim();
		Perl5Util regex = new Perl5Util();
		return regex.match("/^-?\\d+$/", s);
	}

	public void addColumn(InsertQuery query, Printable printable) {
		query.addColumn(printable.getName(), ((Long) printable.getValue()).intValue());
	}

	public void addColumn(UpdateQuery query, Printable printable) {
		query.addColumn(printable.getName(), ((Long) printable.getValue()).intValue());
	}
}
/*
 * $Log: TypeDefinitionInteger.java,v $
 * Revision 1.7  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.6  2005/03/01 21:56:32  phormanns
 * Long immer als Value-Objekt zu Number-Typen
 * setRecord macht Insert, wenn PK = Default-Value
 *
 * Revision 1.5  2005/02/28 21:52:38  phormanns
 * SaveAction begonnen
 *
 * Revision 1.4  2005/02/24 22:18:12  phormanns
 * Tests laufen mit HSQL und MySQL
 *
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
 * Erste �ffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.10  2004/10/24 15:42:27  tbayen
 * DataObjectTextEditor als selbstst�ndige Klasse implementiert
 *
 * Revision 1.9  2004/10/18 10:58:10  tbayen
 * verbesserte Typ�berpr�fung bei Zuweisung an DataObject
 *
 * Revision 1.8  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingef�hrt,
 * neue Verwaltung von Properties f�r Datentypen
 *
 * Revision 1.7  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.6  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.5  2004/10/11 09:37:46  tbayen
 * unn�tze Hilfsfunktion entfernt
 *
 * Revision 1.4  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingef�gt
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