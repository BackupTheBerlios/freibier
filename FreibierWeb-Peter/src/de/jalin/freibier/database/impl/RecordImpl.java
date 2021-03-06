//$Id: RecordImpl.java,v 1.8 2005/03/21 21:41:11 tbayen Exp $

package de.jalin.freibier.database.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;

/**
 * @author tbayen
 * 
 * Diese Klasse repr�sentiert einen Datensatz bzw. eine Zeile innerhalb einer
 * Datenbank-Tabelle. Ein RecordImpl-Objekt ist ein eigenst�ndiges Objekt im
 * Hauptspeicher, d.h. um Werte in der Datenbank zu �ndern, muss es explizit
 * wieder in diese zur�ckgeschrieben werden.
 */
public class RecordImpl implements Record {
	
	private static Log log = LogFactory.getLog(RecordImpl.class);
	private static Perl5Util regex = new Perl5Util();
	
	private DBTableImpl tab;
	private Map daten;

	public RecordImpl(DBTableImpl tab, Map bean) throws DatabaseException {
		log.trace("RecordImpl Constructor(" + bean.keySet() + ")");
		this.tab = tab;
		this.daten = new HashMap();
		Iterator colIterator = this.tab.getFieldsList().iterator();
		String colName = null;
		while (colIterator.hasNext()) {
			colName = (String) colIterator.next();
			this.daten.put(colName, 
					new ValueObject(bean.get(colName), tab.getFieldDef(colName)));
		}
		// TODO Foreign Key Referenz
		//		Iterator i = tab.getFieldsList().iterator();
		//		while (i.hasNext()) {
		//			Column col = (Column) i.next();
		//			if (col.isForeignKey()) {
		//			}
		//		}
	}

	public Printable getPrintable(String name) {
		return (Printable) daten.get(name);
	}
	
	public String printText(String name) throws DatabaseException {
		return getPrintable(name).getText();
	}

//	public Printable getField(int col) throws DatabaseException {
//		TypeDefinition typdef = tab.getFieldDef(col);
//		return new DataObject(daten.get(typdef.getName()), typdef);
//	}

	public void setField(String name, ValueObject value)
			throws DatabaseException {
		daten.put(name, value);
	}

	/**
	 * Mit dieser Methode kann man direkt einen String in ein Feld setzen, ohne
	 * daf�r ein DataObject erzeugen zu m�ssen.
	 * 
	 * @param name
	 * @param value
	 * @throws SystemDatabaseException
	 */
	public void setField(String name, String value) throws DatabaseException {
		daten.put(name, tab.getFieldDef(name).parse(value));
	}

	public DBTable getTable() {
		return tab;
	}

	/**
	 * Liefert den eigentlichen Wert des Datenbank-Feldes, also ein
	 * String-, Integer-, Double- oder Date-Objekt
	 */
	public Object get(String name) {
		Object datenObject = daten.get(name);
		Object value = null;
		if (datenObject != null) {
			value = ((ValueObject) daten.get(name)).getValue();
		} 
		if (value == null) {
			return "";
		}
		return value;
	}
}
/*
 * $Log: RecordImpl.java,v $
 * Revision 1.8  2005/03/21 21:41:11  tbayen
 * Probleme mit Fremdschluessel gefixt
 *
 * Revision 1.7  2005/03/03 22:32:45  phormanns
 * Arbeit an ForeignKeys
 *
 * Revision 1.6  2005/02/24 13:52:12  phormanns
 * Mit Tests begonnen
 *
 * Revision 1.5  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.4  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.3  2005/02/13 20:27:14  phormanns
 * Funktioniert bis auf Filter
 *
 * Revision 1.2  2005/01/29 20:21:59  phormanns
 * RecordDefinition in TableImpl integriert
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste �ffentliche Version
 *
 * Revision 1.13  2004/12/17 22:31:17  phormanns
 * Erste Web-Version des Tabellen-Browsers
 *
 * Revision 1.12  2004/10/25 20:41:52  tbayen
 * Test f�r insert-Kommando und Fehler bei insert behoben
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
 * Vorbereitung auf Bearbeitung von Fremdschl�sseln
 * Verbesserung der Fehlerabfrage bei falschen Feldnamen
 *
 * Revision 1.7  2004/10/14 21:36:12  phormanns
 * Weitere Datentypen
 *
 * Revision 1.6  2004/10/10 14:06:14  tbayen
 * Definitionstypen verbessert, validate mit RegExen; Tests eingef�gt
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
 * Einf�hrung von DataObject 
 * 
 * Revision 1.1 2004/10/07 17:15:33 tbayen 
 * Datenbankklassen bis auf TableImpl fertig f�r weitere Tests
 *  
 */