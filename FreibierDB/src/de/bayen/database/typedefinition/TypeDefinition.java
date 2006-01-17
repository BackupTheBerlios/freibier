/* Erzeugt am 07.10.2004 von tbayen
 * $Id: TypeDefinition.java,v 1.6 2006/01/17 21:06:01 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import java.sql.Types;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.bayen.database.Database;
import de.bayen.database.exception.DBRuntimeException;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;

/**
 * @author tbayen
 * 
 * Diese Klasse beschreibt einen Datentyp, der für ein einzelnes DataObject oder
 * aber auch z.B. für eine gesamte Tabellenspalte gilt. Der Datentyp hat einen
 * Namen und bezeichnet einen Grunddatentyp wie char, int, etc.. Darüber hinaus
 * erfasst er alle Eigenschaften des speziellen Datentyps wie z.B. Länge,
 * Formatierungsanweisungen, etc.
 * 
 * Die eigentlichen Datentypen sind Unterklassen dieser Klasse. Sie werden
 * jedoch normalerweise alle zentral über die Factoryfunktion
 * TypeDefinition.create() erzeugt.
 */
abstract public class TypeDefinition {
	protected static Log log = LogFactory.getLog(TypeDefinition.class);
	protected static Map typesMap = null;
	protected String name;
	protected int type;
	protected int length;
	protected Object defaultValue = null;
	public Map properties = new HashMap();
	static {
		typesMap = new HashMap();
		typesMap.put(new Integer(Types.BIGINT), TypeDefinitionInteger.class);
		typesMap.put(new Integer(Types.CHAR), TypeDefinitionString.class);
		typesMap.put(new Integer(Types.DATE), TypeDefinitionDate.class);
		typesMap.put(new Integer(Types.DECIMAL), TypeDefinitionDecimal.class);
		typesMap.put(new Integer(Types.DOUBLE), TypeDefinitionDecimal.class);
		typesMap.put(new Integer(Types.FLOAT), TypeDefinitionDecimal.class);
		typesMap.put(new Integer(Types.INTEGER), TypeDefinitionInteger.class);
		typesMap
				.put(new Integer(Types.LONGVARCHAR), TypeDefinitionString.class);
		typesMap.put(new Integer(Types.NUMERIC), TypeDefinitionDecimal.class);
		typesMap.put(new Integer(Types.REAL), TypeDefinitionDecimal.class);
		typesMap.put(new Integer(Types.SMALLINT), TypeDefinitionInteger.class);
		typesMap.put(new Integer(Types.TIME), TypeDefinitionTime.class);
		typesMap
				.put(new Integer(Types.TIMESTAMP), TypeDefinitionDateTime.class);
		// Types.DATETIME gibts nicht, weil das gleich Types.TIMESTAMP ist
		typesMap.put(new Integer(Types.TINYINT), TypeDefinitionBool.class);
		typesMap.put(new Integer(Types.VARCHAR), TypeDefinitionString.class);
		// mediumblob wird in JDBC nach VARBINARY gemappt
		typesMap.put(new Integer(Types.VARBINARY), TypeDefinitionBLOB.class);
	}

	/**
	 * virtueller Konstruktor / Factory-Methode
	 * @param db
	 * @throws IllegalDefaultValueDBException 
	 * @throws ParseErrorDBException 
	 */
	public static TypeDefinition create(String name, int type, int length,
			ResourceBundle resource, Database db)
			throws IllegalDefaultValueDBException, ParseErrorDBException {
		//log.trace("create: "+name+", "+type);
		TypeDefinition typeDef = null;
		Map propsMap = null;
		try {
			if (resource != null) {
				propsMap = TypeDefinition.parseProperties(resource, name);
			} else {
				propsMap = new HashMap();
			}
			if (propsMap.containsKey("length")) {
				length = Integer.parseInt((String) propsMap.get("length"));
			}
			String foreignKeyTable = (String) propsMap.get("foreignkey.table");
			typeDef = (TypeDefinition) ((Class) typesMap.get(new Integer(type)))
					.newInstance();
			if (foreignKeyTable != null) {
				typeDef.setProperties(propsMap);
				typeDef.setSQLType(type);
				typeDef.setName(name);
				typeDef.setLength(length);
				typeDef = new TypeDefinitionForeignKey(typeDef, db);
			}
			typeDef.setProperties(propsMap);
			typeDef.setSQLType(type);
			typeDef.setName(name);
			typeDef.setLength(length);
			String def = typeDef.getProperty("default");
			if (def != null)
				typeDef.setDefaultValue(typeDef.parse(def));
		} catch (InstantiationException e) {
			throw new DBRuntimeException.TypeNotFoundException(
					"Spezialisierung von TypeDefinition fehlt (1)", e, log);
		} catch (IllegalAccessException e) {
			throw new DBRuntimeException.TypeNotFoundException(
					"Spezialisierung von TypeDefinition fehlt (2)", e, log);
		} catch (NullPointerException e) {
			throw new DBRuntimeException.TypeNotFoundException(
					"unbekannter Datentyp " + type, e, log);
			//		} catch (DatabaseException e) {
			//			throw new SysDBEx.IllegalDefaultValueDBException(
			//					"Fehler im Defaultwert des Datentyps " + type, e, log);
		}
		return typeDef;
	}

	protected TypeDefinition() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * gibt einen SQL-Typ zurück, wie er in java.sql.Types beschrieben ist. Dies
	 * ist der Typ, mit dem der Wert in der eigentlichen Datenbank abgespeichert
	 * ist.
	 */
	public int getSQLType() {
		return type;
	}

	public void setSQLType(int type) {
		this.type = type;
	}

	/**
	 * Ergibt den Typ als Stringwert. Die Angabe entspricht einem SQL-Typ,
	 * wobei der sich hier ergebende Typ nicht unbedingt identisch mit dem
	 * in der Datenbank verwandten ist.
	 * 
	 * @return String - SQL-Typ
	 */
	public String getStringType() {
		int sqltype = getSQLType();
		String type = String.valueOf(sqltype);
		if (sqltype == Types.VARCHAR) {
			type = "char";
		} else if (sqltype == Types.CHAR) {
			type = "char";
		} else if (sqltype == Types.LONGVARCHAR) {
			type = "text";
		} else if (sqltype == Types.INTEGER) {
			type = "int";
		} else if (sqltype == Types.DECIMAL) {
			type = "decimal";
		} else if (sqltype == Types.TINYINT) {
			type = "bool"; // MySQL wandelt das in "tinyint(1)"
		} else if (sqltype == Types.VARBINARY) {
			type = "blob"; // in MySQL benutze ich hierfür mediumblob
		} else if (sqltype == Types.DATE) {
			type = "date";  // TODO: welcher Types...-Typ für "datetime" steht, weiss ich gerade nicht
		}
		return type;
	}

	/**
	 * Diese Klasse ergibt die Java-Klasse, die den eigentlichen Wert intern
	 * verwaltet. Dies ist z.B. der Datentyp, der sich bei DataObject.getValue()
	 * ergibt.
	 */
	public abstract Class getJavaType();

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	static public Map parseProperties(ResourceBundle resource, String columnName) {
		//log.trace("parseProperties");
		Map propsMap = new HashMap();
		try {
			Perl5Util regex = new Perl5Util();
			Enumeration keysenum = resource.getKeys();
			while (keysenum.hasMoreElements()) {
				String key = (String) keysenum.nextElement();
				if (regex.match("/^" + columnName + "\\.(.*)$/", key)) {
					//log.debug("properties.put(" + columnName + ","
					//		+ regex.group(1) + "," + resource.getString(key)
					//		+ ")");
					propsMap.put(regex.group(1), resource.getString(key));
				}
			}
		} catch (Exception e) {
			log.error("Fehler beim parsen der Properties", e);
		}
		return propsMap;
	}

	protected void setProperties(Map propsMap) {
		//properties = propsMap;
		properties.putAll(propsMap);
	}

	/*
	 * Um Properties benutzen zu können, müssen diese in eine Datei
	 * "<tabellenname>.properties" geschrieben werden. Der Pfad zu dieser Datei
	 * sollte dann mit db.setPropertyPath(...) gesetzt werden. Der Name der 
	 * Property beginnt dann mit dem Namen der Spalte, auf die sich die
	 * Property bezieht und einem Punkt. Danach kommt die eigentliche Bezeichnung
	 * der Property. Folgende Werte können eingestellt werden:
	 * 
	 * length
	 *   Länge des Wertes in der Ausgabe (sonst gilt Default-Wert aus der SQL-Definition)
	 * align
	 *   Ausrichtung: left, right, center
	 * foreignkey.table
	 *   Diese Spalte ist ein Fremdschlüssel auf die angegebene Tabelle
	 * foreignkey.indexcolumn
	 *   In dieser Spalte der o.a. Tabelle steht der Schlüssel
	 * foreignkey.resultcolumn
	 *   Diese Spalte enthält eine Beschreibung des Wertes, die ggf. mit ausgegeben wird
	 */
	public String getProperty(String key) {
		return (String) properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Diese Methode ergibt eine möglichst für Menschen lesbare String-Version
	 * des Wertes. Er kann z.B. in einem Text-Eingabefeld benutzt werden. Es ist
	 * garantiert, daß dieser Wert mit der parse()-Methode wieder eingelesen und
	 * zurückgewandelt werden kann.
	 * 
	 * @param s
	 * @return für Menschen formatierter String
	 * @throws WrongTypeDBException 
	 */
	public abstract String format(Object s) throws WrongTypeDBException;

	/**
	 * Diese Methode parst den angegebenen String,der z.B. aus einem 
	 * Texteingabefeld stammen kann und wandelt ihn in die interne Repräsentation
	 * des Datentyps.
	 * 
	 * @param s
	 * @return Objekt in der internen Repräsentation
	 * @throws ParseErrorDBException 
	 */
	public abstract Object parse(String s) throws ParseErrorDBException;

	/**
	 * Diese Methode überprüft, ob der angegebene String ein gültiger Wert ist,
	 * den man der parse()-Funktion übergeben kann.
	 * 
	 * @param s
	 * @return boolean
	 */
	public abstract boolean validate(String s);

	/**
	 * Nur einen Teilstring validieren, also Klärung der Frage:
	 * Der Benutzer hat gerade eine Taste gedrückt. Wenn ich die jetzt an der
	 * Cursorposition einfüge, kann dann jemals ein gültiger String daraus
	 * werden?
	 * Beispiel "11." ist kein gültiges Datum, kann aber eins werden.
	 * 
	 * @param s
	 * @return boolean
	 */
	public boolean validatePart(String s) {
		return s.length() == 0 ? true : validate(s);
	}
}
/*
 * $Log: TypeDefinition.java,v $
 * Revision 1.6  2006/01/17 21:06:01  tbayen
 * Anpassung für date-Objekte
 *
 * Revision 1.5  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.4  2005/08/12 19:39:47  tbayen
 * kleine Nachbesserung...
 *
 * Revision 1.3  2005/08/12 19:37:18  tbayen
 * unnötige TO DO-Kommentare entfernt
 *
 * Revision 1.2  2005/08/08 06:35:29  tbayen
 * Compiler-Warnings bereinigt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.3  2005/08/07 16:56:14  tbayen
 * Produktionsversion 1.5
 *
 * Revision 1.2  2005/04/07 20:32:49  tbayen
 * Ausrichtung von Zahlenfeldern korrigiert
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.4  2005/04/05 21:14:11  tbayen
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
 * Revision 1.2  2005/03/19 12:55:04  tbayen
 * Zwischenmodell entfernt, stattdessen
 * direkter Zugriff auf Freibier-Klassen
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.3  2004/12/17 22:31:17  phormanns
 * Erste Web-Version des Tabellen-Browsers
 *
 * Revision 1.2  2004/10/24 19:47:08  tbayen
 * störende Debugmeldung entfernt
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.21  2004/10/24 14:01:58  tbayen
 * Properties an einheitlicher Stelle dokumentiert
 *
 * Revision 1.20  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen für DATE und TIME
 *
 * Revision 1.19  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.18  2004/10/23 12:13:54  tbayen
 * Debugausgaben etwas bereinigt
 *
 * Revision 1.17  2004/10/23 08:31:39  tbayen
 * Jetzt kann man die Länge wieder per Property einstellen
 *
 * Revision 1.16  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.15  2004/10/18 12:10:14  tbayen
 * Test für den Zugriff auf Properties
 *
 * Revision 1.14  2004/10/18 10:58:10  tbayen
 * verbesserte Typüberprüfung bei Zuweisung an DataObject
 *
 * Revision 1.13  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 * Revision 1.12  2004/10/15 19:27:08  tbayen
 * Properties für Datentypen
 *
 * Revision 1.11  2004/10/15 17:52:59  phormanns
 * Formate fuer SQL und Anzeige
 *
 * Revision 1.10  2004/10/14 21:37:36  phormanns
 * Weitere Datentypen
 *
 * Revision 1.9  2004/10/14 13:13:14  tbayen
 * Weitere int-Datentypen angegeben, um Tabellen vom Freibier-Server zu testen
 *
 * Revision 1.8  2004/10/13 21:40:50  phormanns
 * Typ VARCHAR eingefügt
 *
 * Revision 1.7  2004/10/11 14:29:37  tbayen
 * Framework für Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.6  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingefügt
 *
 * Revision 1.5  2004/10/09 20:51:16  tbayen
 * Editorformatierung justiert und Revisionskommentare neu formatiert
 *
 * Revision 1.4  2004/10/09 20:18:54  tbayen
 * Aufteilung der TypeDefinition in Unterklassen
 * 
 * Revision 1.3 2004/10/09 20:17:31 tbayen
 * Aufteilung der TypeDefinition in Unterklassen 
 * 
 * Revision 1.2 2004/10/09 15:05:38 tbayen 
 * dokumentiert und formatiert 
 * 
 * Revision 1.1 2004/10/07 17:15:33 tbayen 
 * Datenbankklassen bis auf Table fertig für weitere Tests
 * 
 */