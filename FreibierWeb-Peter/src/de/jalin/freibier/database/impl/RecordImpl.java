//$Id: RecordImpl.java,v 1.2 2005/01/29 20:21:59 phormanns Exp $

package de.jalin.freibier.database.impl;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import de.jalin.freibier.database.Printable;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.Table;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import de.jalin.freibier.database.impl.type.TypeDefinitionForeignKey;

/**
 * @author tbayen
 * 
 * Diese Klasse repräsentiert einen Datensatz bzw. eine Zeile innerhalb einer
 * Datenbank-Tabelle. Ein RecordImpl-Objekt ist ein eigenständiges Objekt im
 * Hauptspeicher, d.h. um Werte in der Datenbank zu ändern, muss es explizit
 * wieder in diese zurückgeschrieben werden.
 */
public class RecordImpl implements Record {
	
	private static Log log = LogFactory.getLog(RecordImpl.class);
	private static Perl5Util regex = new Perl5Util();
	
	private TableImpl tab;
	private Map daten;

	public RecordImpl(TableImpl tab, Map bean) {
		log.trace("RecordImpl Constructor(" + bean.keySet() + ")");
		this.tab = tab;
		this.daten = bean;
		// Ein Foreign Key z.B. im Feld "kdnr" bedeutet, daß ein Schlüssel 
		// "kdnr_foreign" angelegt ist, in dem der Wert aus der anderen Tabelle
		// enthalten ist. Diese beiden müssen zusammengefasst werden in ein 
		// Objekt:
		Iterator i = tab.getFieldsList().iterator();
		while (i.hasNext()) {
			TypeDefinitionImpl typ = (TypeDefinitionImpl) i.next();
			if (typ instanceof TypeDefinitionForeignKey) {
				//log.debug("Foreign Key gefunden: " + key);
				String keyname = typ.getName();
				// Wenn schon ein Foreign Key übergeben wurde (z.B. von 
				// getEmptyRecord()), brauche ich nichts mehr zu machen.
				if(!(daten.get(keyname) instanceof ForeignKey)){
					ForeignKey fk = new ForeignKey(daten.get(keyname), daten
							.get(keyname + "_foreign"));
					daten.put(keyname, fk);
					daten.remove(keyname + "_foreign");
				}
			}
		}
	}

	public Printable getField(String name) throws DatabaseException {
		return new DataObject(daten.get(name), tab.getFieldDef(name));
	}
	
	public String getFormatted(String name) throws DatabaseException {
		return getField(name).format();
	}

	public Printable getField(int col) throws DatabaseException {
		TypeDefinition typdef = tab.getFieldDef(col);
		return new DataObject(daten.get(typdef.getName()), typdef);
	}

	public void setField(String name, DataObject value)
			throws DatabaseException {
		daten.put(name, value.getValue());
	}

	public void setField(int col, DataObject value) throws DatabaseException {
		TypeDefinition typdef = tab.getFieldDef(col);
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
		daten.put(name, tab.getFieldDef(name).parse(value));
	}

	public void setField(int col, String value) throws DatabaseException {
		TypeDefinition typdef = tab.getFieldDef(col);
		daten.put(typdef.getName(), typdef.parse(value));
	}
	
	public Table getTable() {
		return tab;
	}
}
/*
 * $Log: RecordImpl.java,v $
 * Revision 1.2  2005/01/29 20:21:59  phormanns
 * RecordDefinition in TableImpl integriert
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:12:42  phormanns
 * Erste öffentliche Version
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
 * Datenbankklassen bis auf TableImpl fertig für weitere Tests
 *  
 */