//$Id: TypeDefinitionImpl.java,v 1.2 2005/02/11 15:50:35 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.sql.Types;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.impl.type.TypeDefinitionDate;
import de.jalin.freibier.database.impl.type.TypeDefinitionDateTime;
import de.jalin.freibier.database.impl.type.TypeDefinitionFloat;
import de.jalin.freibier.database.impl.type.TypeDefinitionForeignKey;
import de.jalin.freibier.database.impl.type.TypeDefinitionInteger;
import de.jalin.freibier.database.impl.type.TypeDefinitionString;
import de.jalin.freibier.database.impl.type.TypeDefinitionTime;

/**
 * @author tbayen
 * 
 * Diese Klasse beschreibt einen Datentyp, der f�r ein einzelnes DataObject oder
 * aber auch z.B. f�r eine gesamte Tabellenspalte gilt. Der Datentyp hat einen
 * Namen und bezeichnet einen Grunddatentyp wie char, int, etc.. Dar�ber hinaus
 * erfasst er alle Eigenschaften des speziellen Datentyps wie z.B. L�nge,
 * Formatierungsanweisungen, etc.
 * 
 * Die eigentlichen Datentypen sind Unterklassen dieser Klasse. Sie werden
 * jedoch normalerweise alle zentral �ber die Klassenfunktion
 * TypeDefinition.create() erzeugt.
 */
abstract public class TypeDefinitionImpl implements TypeDefinition {
	protected static Log log = LogFactory.getLog(TypeDefinitionImpl.class);
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
		typesMap.put(new Integer(Types.DECIMAL), TypeDefinitionFloat.class);
		typesMap.put(new Integer(Types.DOUBLE), TypeDefinitionFloat.class);
		typesMap.put(new Integer(Types.FLOAT), TypeDefinitionFloat.class);
		typesMap.put(new Integer(Types.INTEGER), TypeDefinitionInteger.class);
		typesMap.put(new Integer(Types.LONGVARCHAR), TypeDefinitionString.class);
		typesMap.put(new Integer(Types.NUMERIC), TypeDefinitionFloat.class);
		typesMap.put(new Integer(Types.REAL), TypeDefinitionFloat.class);
		typesMap.put(new Integer(Types.SMALLINT), TypeDefinitionInteger.class);
		typesMap.put(new Integer(Types.TIME), TypeDefinitionTime.class);
		typesMap.put(new Integer(Types.TIMESTAMP), TypeDefinitionDateTime.class);
		// Types.DATETIME gibts nicht, weil das gleich Types.TIMESTAMP ist
		typesMap.put(new Integer(Types.TINYINT), TypeDefinitionInteger.class);
		typesMap.put(new Integer(Types.VARCHAR), TypeDefinitionString.class);
	}

	/**
	 * virtueller Konstruktor / Factory-Methode
	 * @param db
	 * 
	 * @throws SystemDatabaseException
	 *  
	 */
	public static TypeDefinitionImpl create(String name, int type, int length,
			ResourceBundle resource, AbstractDatabaseImpl db)
			throws SystemDatabaseException {
		//log.trace("create: "+name+", "+type);
		TypeDefinitionImpl typeDef = null;
		Map propsMap = null;
		try {
			if (resource != null) {
				propsMap = TypeDefinitionImpl.parseProperties(resource, name);
			} else {
				propsMap = new HashMap();
			}
			if (propsMap.containsKey("length")) {
				length = Integer.parseInt((String) propsMap.get("length"));
			}
			String foreignKeyTable = (String) propsMap.get("foreignkey.table");
			typeDef = (TypeDefinitionImpl) ((Class) typesMap.get(new Integer(type)))
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
		} catch (InstantiationException e) {
			throw new SystemDatabaseException(
					"Spezialisierung von TypeDefinition fehlt (1)", e, log);
		} catch (IllegalAccessException e) {
			throw new SystemDatabaseException(
					"Spezialisierung von TypeDefinition fehlt (2)", e, log);
		} catch (NullPointerException e) {
			typeDef = new TypeDefinitionString();
			typeDef.setProperties(propsMap);
			typeDef.setSQLType(type);
			typeDef.setName(name);
			typeDef.setLength(length);
//			throw new SystemDatabaseException("unbekannter Datentyp " + type,
//					log);
		}
		return typeDef;
	}

	protected TypeDefinitionImpl() {}

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
	 * gibt einen SQL-Typ zur�ck, wie er in java.sql.Types beschrieben ist. Dies
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
	 * Diese Operation liefert die Java-Klasse, die den eigentlichen Wert intern
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
			Enumeration enum = resource.getKeys();
			while (enum.hasMoreElements()) {
				String key = (String) enum.nextElement();
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
		properties = propsMap;
	}

	/*
	 * Um Properties benutzen zu k�nnen, m�ssen diese in eine Datei
	 * "<tabellenname>.properties" geschrieben werden. Der Pfad zu dieser Datei
	 * sollte dann mit db.setPropertyPath(...) gesetzt werden. Der Name der 
	 * Property beginnt dann mit dem Namen der Spalte, auf die sich die
	 * Property bezieht und einem Punkt. Danach kommt die eigentliche Bezeichnung
	 * der Property. Folgende Werte k�nnen eingestellt werden:
	 * 
	 * length
	 *   L�nge des Wertes in der Ausgabe (sonst gilt Default-Wert aus der SQL-Definition)
	 * align
	 *   Ausrichtung: left, right, center
	 * foreignkey.table
	 *   Diese Spalte ist ein Fremdschl�ssel auf die angegebene Tabelle
	 * foreignkey.indexcolumn
	 *   In dieser Spalte der o.a. Tabelle steht der Schl�ssel
	 * foreignkey.resultcolumn
	 *   Diese Spalte enth�lt eine Beschreibung des Wertes, die ggf. mit ausgegeben wird
	 */
	public String getProperty(String key) {
		return (String) properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Diese Methode ergibt eine m�glichst f�r Menschen lesbare String-Version
	 * des Wertes. Er kann z.B. in einem Text-Eingabefeld benutzt werden. Es ist
	 * garantiert, da� dieser Wert mit der parse()-Methode wieder eingelesen und
	 * zur�ckgewandelt werden kann.
	 * 
	 * @param s
	 * @return
	 * @throws SystemDatabaseException
	 */
	public abstract String format(Object s) throws DatabaseException;

	/**
	 * Diese Methode parst den angegebenen String,der z.B. aus einem 
	 * Texteingabefeld stammen kann und wandelt ihn in die interne Repr�sentation
	 * des Datentyps.
	 * 
	 * @param s
	 * @return
	 * @throws DatabaseException
	 */
	public abstract Object parse(String s) throws DatabaseException;

	/**
	 * Diese Methode �berpr�ft, ob der angegebene String ein g�ltiger Wert ist,
	 * den man der parse()-Funktion �bergeben kann.
	 * 
	 * @param s
	 * @return
	 */
	public abstract boolean validate(String s);

	/**
	 * Nur einen Teilstring validieren, also Kl�rung der Frage:
	 * Der Benutzer hat gerade eine Taste gedr�ckt. Wenn ich die jetzt an der
	 * Cursorposition einf�ge, kann dann jemals ein g�ltiger String daraus
	 * werden?
	 * Beispiel "11." ist kein g�ltiges Datum, kann aber eins werden.
	 * 
	 * @param s
	 * @return
	 */
	public boolean validatePart(String s) {
		return s.length() == 0 ? true : validate(s);
	}
}
/*
 * $Log: TypeDefinitionImpl.java,v $
 * Revision 1.2  2005/02/11 15:50:35  phormanns
 * Merge
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste �ffentliche Version
 *
 * Revision 1.3  2004/12/17 22:31:17  phormanns
 * Erste Web-Version des Tabellen-Browsers
 *
 * Revision 1.2  2004/10/24 19:47:08  tbayen
 * st�rende Debugmeldung entfernt
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.21  2004/10/24 14:01:58  tbayen
 * Properties an einheitlicher Stelle dokumentiert
 *
 * Revision 1.20  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen f�r DATE und TIME
 *
 * Revision 1.19  2004/10/24 13:10:20  tbayen
 * Merken des Typs des Zielwertes eines Foreign Keys
 * formatierte Ausgabe von Foreign Keys und Test hierzu
 *
 * Revision 1.18  2004/10/23 12:13:54  tbayen
 * Debugausgaben etwas bereinigt
 *
 * Revision 1.17  2004/10/23 08:31:39  tbayen
 * Jetzt kann man die L�nge wieder per Property einstellen
 *
 * Revision 1.16  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.15  2004/10/18 12:10:14  tbayen
 * Test f�r den Zugriff auf Properties
 *
 * Revision 1.14  2004/10/18 10:58:10  tbayen
 * verbesserte Typ�berpr�fung bei Zuweisung an DataObject
 *
 * Revision 1.13  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingef�hrt,
 * neue Verwaltung von Properties f�r Datentypen
 *
 * Revision 1.12  2004/10/15 19:27:08  tbayen
 * Properties f�r Datentypen
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
 * Typ VARCHAR eingef�gt
 *
 * Revision 1.7  2004/10/11 14:29:37  tbayen
 * Framework f�r Printer und Editoren
 * sowie SQLPrinter als erste Anwendung desselben
 *
 * Revision 1.6  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingef�gt
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
 * Datenbankklassen bis auf TableImpl fertig f�r weitere Tests
 * 
 */