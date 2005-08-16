/* Erzeugt am 07.10.2004 von tbayen
 * $Id: Record.java,v 1.9 2005/08/16 12:21:06 tbayen Exp $
 */
package de.bayen.database;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.database.typedefinition.TypeDefinition;
import de.bayen.database.typedefinition.TypeDefinitionBLOB;
import de.bayen.database.typedefinition.TypeDefinitionBool;
import de.bayen.database.typedefinition.TypeDefinitionForeignKey;

/**
 * @author tbayen
 * 
 * Diese Klasse repräsentiert einen Datensatz bzw. eine Zeile innerhalb einer
 * Datenbank-Tabelle. Ein Record-Objekt ist ein eigenständiges Objekt im
 * Hauptspeicher, d.h. um Werte in der Datenbank zu ändern, muss es explizit
 * wieder in diese zurückgeschrieben werden.
 */
public class Record {
	private static Log log = LogFactory.getLog(Record.class);
	protected RecordDefinition def;
	public Map daten;

	public Record(RecordDefinition def, Map bean) {
		log.trace("Record Constructor(" + bean.keySet() + ")");
		this.def = def;
		this.daten = bean;
		// Ein Foreign Key z.B. im Feld "kdnr" bedeutet, daß ein Schlüssel 
		// "kdnr_foreign" angelegt ist, in dem der Wert aus der anderen Tabelle
		// enthalten ist. Diese beiden müssen zusammengefasst werden in ein 
		// Objekt:
		Iterator i = def.getFieldsList().iterator();
		while (i.hasNext()) {
			TypeDefinition typ = (TypeDefinition) i.next();
			if (typ instanceof TypeDefinitionForeignKey) {
				//log.debug("Foreign Key gefunden: " + key);
				String keyname = typ.getName();
				// Wenn schon ein Foreign Key übergeben wurde (z.B. von 
				// getEmptyRecord()), brauche ich nichts mehr zu machen.
				if (!(daten.get(keyname) instanceof ForeignKey)) {
					ForeignKey fk = new ForeignKey(daten.get(keyname), daten
							.get(keyname + "_foreign"));
					daten.put(keyname, fk);
					daten.remove(keyname + "_foreign");
				}
			} else if (typ instanceof TypeDefinitionBLOB) {
				// Die JDBC-Klassen ergeben bei BLOBs einen Wert vom Typ byte[]
				// dies korrigiere ich hier, da ich intern mit BLOB arbeite
				String keyname = typ.getName();
				if (!(daten.get(keyname) instanceof BLOB)) {
					daten.put(keyname, new BLOB((byte[]) daten.get(keyname)));
				}
			} else if (typ instanceof TypeDefinitionBool) {
				// Die JDBC-Klassen ergeben bei Bools ein Byte
				// dies korrigiere ich hier, da ich intern mit Boolean arbeite
				String keyname = typ.getName();
				if (!(daten.get(keyname) instanceof Boolean)) {
					daten.put(keyname, new Boolean(((Byte) daten.get(keyname))
							.intValue() == 1));
				}
			}
		}
	}

	public DataObject getField(String name) throws DatabaseException {
		Object data = daten.get(name);
		if (data == null) {
			Perl5Util re = new Perl5Util();
			if (re.match("/^[[:upper:]]+$/", name)) {
				// Falls in der Datenbank ein Feldname nur aus Grossbuchstaben
				// besteht, wird das vom Java-Treiber in Kleinbuchstaben 
				// konvertiert. Damit ich nun mit dem "richtigen" Namen darauf
				// zugreifen kann, mache ich den ggf. hier klein.
				name = name.toLowerCase();
				data = daten.get(name);
			}
		}
		return new DataObject(data, def.getFieldDef(name));
	}

	public String getFormatted(String name) throws DatabaseException {
		return getField(name).format();
	}

	public DataObject getField(int col) throws DatabaseException {
		TypeDefinition typdef = def.getFieldDef(col);
		return new DataObject(daten.get(typdef.getName()), typdef);
	}

	/**
	 * Besorgt den Wert des Primärschlüssels dieses Records.
	 * 
	 * @return schlüsselwert
	 * @throws DatabaseException
	 */
	public DataObject getPrimaryKey() throws DatabaseException {
		return getField(def.getPrimaryKey());
	}

	/**
	 * setzt den Wert eines einzelnen Datenfeldes in dem Record. Falls es sich
	 * um einen ForeignKey handelt, kann als Wert sowohl ein echter ForeignKey
	 * als auch der gewünschte Schlüsselwert übergeben werden (Natürlich immer
	 * innerhalb des DataObjects).
	 * 
	 * @param name
	 * @param value
	 * @throws DatabaseException
	 */
	public void setField(String name, DataObject value)
			throws DatabaseException {
		if (def.getFieldDef(name).getJavaType().equals(ForeignKey.class)
				&& (!value.getValue().getClass().equals(ForeignKey.class))) {
			daten.put(name, new ForeignKey(value.getValue(), null));
		} else {
			daten.put(name, value.getValue());
		}
	}

	public void setField(int col, DataObject value) throws DatabaseException {
		TypeDefinition typdef = def.getFieldDef(col);
		daten.put(typdef.getName(), value.getValue());
	}

	/**
	 * Mit dieser Methode kann man direkt einen String in ein Feld setzen, ohne
	 * dafür ein DataObject erzeugen zu müssen.
	 * 
	 * @param name
	 * @param value
	 * @throws SystemDatabaseException
	 */
	public void setField(String name, String value) throws DatabaseException {
		daten.put(name, def.getFieldDef(name).parse(value));
	}

	public void setField(int col, String value) throws DatabaseException {
		TypeDefinition typdef = def.getFieldDef(col);
		daten.put(typdef.getName(), typdef.parse(value));
	}

	/**
	 * Hiermit ist es möglich, in ein ForeignKey-Feld direkt einen Record
	 * als Wert einzusetzen.
	 */
	public void setField(String name, Record value) throws DatabaseException {
		daten.put(name, value.getPrimaryKey().getValue());
	}

	public void setField(int col, Record value) throws DatabaseException {
		setField(def.getFieldDef(col).getName(), value);
	}

	/**
	 * Hiermit ist es möglich, direkt ein Datenobjekt in ein Feld eines
	 * Records zu setzen.
	 */
	public void setField(String name, Object value) throws DatabaseException {
		if (def.getFieldDef(name).getClass().equals(
				TypeDefinitionForeignKey.class)) {
			// Wer ein Objekt übergibt (also in dieser Methode hier landet),
			// übergibt garantiert keinen sauberen ForeignKey, also erzeuge ich
			// den ggf. hier:
			value = new ForeignKey(value, null);
		}
		if (!def.getFieldDef(name).getJavaType().isInstance(value)) {
			throw new SystemDatabaseException("Objekt '" + value
					+ "' ist nicht vom richtigen Typ '"
					+ def.getFieldDef(name).getJavaType() + "'", log);
		}
		daten.put(name, value);
	}

	public void setField(int col, Object value) throws DatabaseException {
		setField(def.getFieldDef(col).getName(), value);
	}

	public RecordDefinition getRecordDefinition() {
		return def;
	}

	public TypeDefinition getFieldDef(String feldname)
			throws SystemDatabaseException {
		return def.getFieldDef(feldname);
	}
}
/*
 * $Log: Record.java,v $
 * Revision 1.9  2005/08/16 12:21:06  tbayen
 * kleinere Ergänzungen und Bugfixes bei der Arbeit an der FiBu
 *
 * Revision 1.8  2005/08/16 08:52:01  tbayen
 * kleinere Ergänzungen und Bugfixes bei der Arbeit an der FiBu
 *
 * Revision 1.7  2005/08/16 07:44:44  tbayen
 * Record.getFieldDef() als Verkürzung
 *
 * Revision 1.6  2005/08/16 07:37:14  tbayen
 * setField auch für Records
 *
 * Revision 1.5  2005/08/15 16:17:12  tbayen
 * Javadoc-Warnungen beseitigt
 *
 * Revision 1.4  2005/08/15 11:47:09  tbayen
 * Record.setField() nimmt bei ForeignKey-Feldern als Wert auch einen einfachen Wert
 *
 * Revision 1.3  2005/08/14 20:05:11  tbayen
 * neue Methode getPrimaryKey()
 *
 * Revision 1.2  2005/08/08 06:35:29  tbayen
 * Compiler-Warnings bereinigt
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
 * Revision 1.2  2005/02/24 00:35:28  tbayen
 * Listen funktionieren, Datensätze anlegen funktioniert
 *
 * Revision 1.1  2005/02/21 16:11:54  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.13  2004/12/17 22:31:17  phormanns
 * Erste Web-Version des Tabellen-Browsers
 *
 * Revision 1.12  2004/10/25 20:41:52  tbayen
 * Test für insert-Kommando und Fehler bei insert behoben
 *
 * Revision 1.11  2004/10/24 15:46:43  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.10  2004/10/23 11:35:46  tbayen
 * Verwaltung von Foreign Keys in eigenem Datentyp
 *
 * Revision 1.9  2004/10/21 20:41:16  tbayen
 * Foreign Keys werden aus der Datenbank gelesen (aber noch nicht richtig verarbeitet)
 *
 * Revision 1.8  2004/10/18 11:45:01  tbayen
 * Vorbereitung auf Bearbeitung von Fremdschlüsseln
 * Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 * Revision 1.7  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.6  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingefügt
 *
 * Revision 1.5  2004/10/09 20:51:16  tbayen
 * Editorformatierung justiert und Revisionskommentare neu formatiert
 *
 * Revision 1.4  2004/10/09 20:17:31  tbayen
 * Aufteilung der TypeDefinition in Unterklassen
 *
 * Revision 1.3  2004/10/09 15:12:23  tbayen 
 * dokumentiert
 * 
 * Revision 1.2 2004/10/09 15:09:15 tbayen 
 * Einführung von DataObject 
 * 
 * Revision 1.1 2004/10/07 17:15:33 tbayen 
 * Datenbankklassen bis auf Table fertig für weitere Tests
 *  
 */