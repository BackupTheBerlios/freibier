/* Erzeugt am 13.08.2005 von tbayen
 * $Id: Konto.java,v 1.12 2005/08/30 21:08:44 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.SysDBEx.IllegalDefaultValueDBException;
import de.bayen.database.exception.SysDBEx.ParseErrorDBException;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.SysDBEx.TypeNotSupportedDBException;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.exceptions.ImpossibleException;
import de.bayen.fibu.util.Drucktabelle;
import de.bayen.fibu.util.StringUtil;

/**
 * Klasse für ein einzelnes Konto unserer Buchhaltung. Ein Konto kann 
 * Buchunggsätze enthalten. Es hat einen Saldo.
 * 
 * @author tbayen
 */
public class Konto extends AbstractObject implements Comparable {
	private static Log log = LogFactory.getLog(Konto.class);
	private Table table;

	/**
	 * Dieser Konstruktor liest ein vorhandenes Konto aus der Tabelle
	 * 
	 * @param table
	 * @param id
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	protected Konto(Table table, Long id) throws SQL_DBException,
			RecordNotExistsDBException {
		this.table = table;
		try {
			record = table.getRecordByPrimaryKey(id.toString());
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Dieser Konstruktor liest das Konto mit der angegebenen buchhalterischen
	 * Kontonummer aus der Datenbank.
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	protected Konto(Table table, String ktonr) throws SQL_DBException,
			RecordNotExistsDBException {
		this.table = table;
		try {
			record = table.getRecordByValue("Kontonummer", ktonr);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Dieser Konstruktor erzeugt ein neues Konto
	 * 
	 * @param table
	 */
	protected Konto(Table table) {
		this.table = table;
		record = table.getEmptyRecord();
		//		record.setField("MwSt","1");
	}

	/**
	 * Änderungen am Konto werden erst hiermit endgültig in die Datenbank
	 * geschrieben.
	 * <p>
	 * (Dies gilt nicht für Buchungen, die ja in eigenen Tabellen verwaltet 
	 * werden.)
	 * </p>
	 * @throws SQL_DBException 
	 */
	public void write() throws SQL_DBException {
		try {
			record = table.setRecordAndGetRecord(record);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Long getID() {
		return (Long) record.getField("id").getValue();
	}

	public String getKontonummer() {
		try {
			return record.getFormatted("Kontonummer");
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public String getBezeichnung() {
		try {
			return record.getFormatted("Bezeichnung");
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * ergibt den Mehrwertsteuersatz als String, also z.B. "16.0"
	 * @return MwSt-Satz
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	public String getMwSt() throws SQL_DBException, RecordNotExistsDBException {
		DataObject field = record.getField("MwSt");
		try {
			return field.getForeignResultColumn(table.getDatabase());
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * ergibt die buchhalterische Kontonummer des Oberkontos (nicht die ID).
	 * @return String, der die buchhalterische Kontonummer enthält
	 * @throws SQL_DBException 
	 */
	public String getOberkontoNummer() throws SQL_DBException {
		DataObject field = record.getField("Oberkonto");
		try {
			return field.getForeignResultColumn(table.getDatabase());
		} catch (RecordNotExistsDBException e) {
			return null;
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public Konto getOberkonto() throws SQL_DBException {
		Object field = record.getField("Oberkonto").getValue();
		Long ktoid = ((Long) ((ForeignKey) field).getKey());
		if (ktoid == null)
			return null;
		try {
			return new Konto(table, ktoid);
		} catch (RecordNotExistsDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public int getGewicht() {
		try {
			return Integer.parseInt(record.getFormatted("Gewicht"));
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		} catch (NumberFormatException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * ergibt, ob es sich bei diesem Konto um ein Soll-Konto oder ein Haben-Konto
	 * handelt. Je nach Oberkonto heisst Soll soviel wie Aktiv, Gewinn oder 
	 * Forderung und Haben soviel wie Passiv, Verlust oder Verbindlichkeit.
	 * <p>
	 * Dieser Wert kann bei der Ausgabe von Konten bzw. von Kontenlisten zur
	 * besseren Übersicht genutzt werden. Echte Unterschiede zwischen den
	 * Kontenarten gibt es nicht.
	 * </p>
	 * @return bool-Wert
	 */
	public boolean getSoll() {
		try {
			if (record.getFormatted("Soll").equals("true")) {
				return true;
			} else {
				return false;
			}
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setKontonummer(String ktonr) {
		try {
			record.setField("Kontonummer", ktonr);
		} catch (ParseErrorDBException e) {
			// Kontonummer ist ein String, also kann es keinen Parserfehler geben
			throw new ImpossibleException(e, log);
		}
	}

	public void setBezeichnung(String bezeichnung) {
		try {
			record.setField("Bezeichnung", bezeichnung);
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Hier wird ein ID-Wert als Parameter angegeben (kein MwSt-Satz oder sowas).
	 * @param mwst
	 */
	public void setMwSt(Long mwst) {
		try {
			record.setField("MwSt", mwst);
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Hier wird ein ID-Wert als Parameter angegeben (keine Kontonummer).
	 */
	public void setOberkonto(Long kto) {
		try {
			record.setField("Oberkonto", kto);
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Hier wird ein Konto-Objekt angegeben, um das Oberkonto festzulegen.
	 */
	public void setOberkonto(Konto kto) {
		try {
			record.setField("Oberkonto", kto.getID());
		} catch (WrongTypeDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Das Oberkonto wird anhand der buchhalterischen Kontonummer gesetzt.
	 * @throws ParseErrorDBException 
	 * @throws RecordNotExistsDBException 
	 * @throws SQL_DBException 
	 */
	public void setOberkontoNummer(String ktonr) throws SQL_DBException,
			RecordNotExistsDBException, ParseErrorDBException {
		Record rec;
		try {
			rec = table.getRecordByValue("Kontonummer", ktonr);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		}
		record.setField("Oberkonto", rec.getPrimaryKey());
	}

	public void setGewicht(int gew) {
		try {
			record.setField("Gewicht", String.valueOf(gew));
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	public void setSoll(boolean soll) {
		try {
			record.setField("Soll", soll ? "1" : "0");
		} catch (ParseErrorDBException e) {
			throw new ImpossibleException(e, log);
		}
	}

	/**
	 * Ergibt eine Liste mit Konto-Objekten. Die aufgelisteten Konten sind die,
	 * die dieses Konto als Oberkonto angegeben haben.
	 * @throws SQL_DBException 
	 */
	public List getUnterkonten() throws SQL_DBException {
		List konten = new ArrayList();
		List records;
		try {
			records = table.getRecordsFromQuery(table.new QueryCondition(
					"Oberkonto", QueryCondition.EQUAL, getID()), null, true);
		} catch (TypeNotSupportedDBException e1) {
			throw new ImpossibleException(e1, log);
		}
		for (Iterator iter = records.iterator(); iter.hasNext();) {
			Record rec = (Record) iter.next();
			try {
				konten.add(new Konto(table, (Long) rec.getPrimaryKey()
						.getValue()));
			} catch (RecordNotExistsDBException e) {
				throw new ImpossibleException(e, log);
			}
		}
		return konten;
	}

	/**
	 * Ergibt alle Buchungszeilen auf diesem Konto.
	 * @throws SQL_DBException 
	 */
	public List getBuchungszeilen() throws SQL_DBException {
		List zeilen = new ArrayList();
		Table table;
		try {
			table = this.table.getDatabase().getTable("Buchungszeilen");
		} catch (IllegalDefaultValueDBException e1) {
			throw new ImpossibleException(e1, log);
		} catch (ParseErrorDBException e1) {
			throw new ImpossibleException(e1, log);
		}
		List records;
		try {
			QueryCondition query = table.new QueryCondition("Konto",
					QueryCondition.EQUAL, getID());
			// TODO Auswahl nach Periode, absummiert, etc.
			query.and(table.new QueryCondition("Buchungen, Journale",
					QueryCondition.SQL,
					"Buchungszeilen.Buchung=Buchungen.id AND "
							+ "Buchungen.Journal=Journale.id AND "
							+ "Journale.absummiert=0"));
			records = table.getRecordsFromQuery(query, null, true);
		} catch (TypeNotSupportedDBException e) {
			throw new ImpossibleException(e, log);
		} catch (ParseErrorDBException e) {
			// QueryCondition.SQL-Daten werden gar nicht geparst
			throw new ImpossibleException(e, log);
		}
		for (Iterator iter = records.iterator(); iter.hasNext();) {
			Record zeile = (Record) iter.next();
			try {
				zeilen.add(new Buchungszeile(table, (Long) zeile
						.getPrimaryKey().getValue()));
			} catch (RecordNotExistsDBException e) {
				throw new ImpossibleException(e, log);
			}
		}
		// Besser wäre, die Sortierung dem SQL-Server zu überlassen, aber bei
		// dieser Datenstruktur ist das mit FreibierDB nicht so einfach. 
		// Dies hier ist aufwendiger, aber einstweilen funktioniert es:
		Collections.sort(zeilen);
		return zeilen;
	}

	/**
	 * ergibt den Saldo dieses Kontos.
	 * 
	 * @return Saldo
	 * @throws SQL_DBException 
	 */
	public Betrag getSaldo() throws SQL_DBException {
		Betrag erg = new Betrag();
		// Saldo von Buchungen, die direkt auf dieses Konto erfolgt sind:
		for (Iterator iter = getBuchungszeilen().iterator(); iter.hasNext();) {
			Buchungszeile zeile = (Buchungszeile) iter.next();
			erg = erg.add(zeile.getBetrag());
		}
		// und dazu die Salden der Unterkonten dieses Kontos
		for (Iterator iter = getUnterkonten().iterator(); iter.hasNext();) {
			Konto konto = (Konto) iter.next();
			erg = erg.add(konto.getSaldo());
		}
		if (erg.equals(new Betrag())) {
			// Wenn der Wert gleich Null ist, setze ich Soll/Haben richtig
			erg.setSoll(getSoll());
		}
		return erg;
	}

	/**
	 * vergleicht zwei Objekte miteinander. Diese Methode implementiert
	 * das Comparable-Interface. Sie erlaubt, Listen dieser Klasse zu 
	 * sortieren.
	 * 
	 * @param o
	 * @return -1: this<o; 1: this>o; 0:this=o
	 * @throws Exception 
	 */
	public int compareTo(Object o) {
		try {
			Konto konto = (Konto) o;
			int cmp = getKontonummer().compareTo(konto.getKontonummer());
			return cmp;
		} catch (Exception e) {
			// in compareTo() darf keine "fangbare" Exception geworfen werden
			throw new RuntimeException("Fehler beim Vergleich von Objekten", e);
		}
	}

	/**
	 * Ausgabe des Kontos in Textform
	 */
	public String toString() {
		String erg;
		try {
			erg = "Konto <" + getKontonummer() + ">:";
			erg += " '" + getBezeichnung() + "'";
			erg += "\n";
			erg += "(";
			erg += "Oberkonto <" + getOberkontoNummer() + ">";
			erg += "; Gewicht: " + getGewicht();
			erg += ")";
			for (Iterator iter = getBuchungszeilen().iterator(); iter.hasNext();) {
				Buchungszeile zeile = (Buchungszeile) iter.next();
				erg += "\n" + zeile;
			}
		} catch (Exception e) {
			log.error("Fehler in toString()", e);
			erg = "EXCEPTION: " + e.getMessage();
		}
		return erg;
	}

	/**
	 * Gibt ein Konto als Tabelle aus. Das maxgewicht gibt an, wie tief
	 * dabei in die Kontenhierarchie hinabgestiegen wird. nursalden gibt an,
	 * ob auch Buchungszeilen ausgegeben werden. keinnullsaldo gibt an, ob
	 * Unterkonten, deren Saldo Null ist, weggelassen werden sollen.
	 * 
	 * @param maxgewicht
	 * @param nursalden
	 * @return Textstring, der eine Tabelle enthält
	 * @throws SQL_DBException
	 */
	public String ausgabe(int maxgewicht, boolean nursalden,
			boolean keinnullsaldo) throws SQL_DBException {
		String spalten[] = {
				"Nr", "Bezeichnung", "Soll", "Haben"
		};
		int breiten[] = {
				6, 40, 14, 14
		};
		int ausrichtung[] = {
				Drucktabelle.RECHTSBUENDIG, Drucktabelle.LINKSBUENDIG,
				Drucktabelle.RECHTSBUENDIG, Drucktabelle.RECHTSBUENDIG
		};
		Drucktabelle tab = new Drucktabelle(spalten, breiten, ausrichtung);
		return tab.printUeberschrift(true) + "\n"
				+ ausgabe(tab, 0, maxgewicht, nursalden, keinnullsaldo);
	}

	private String ausgabe(Drucktabelle tab, int ebene, int maxgewicht,
			boolean nursalden, boolean keinnullsaldo) throws SQL_DBException {
		String erg = printSaldenzeile(tab, ebene) + "\n";
		for (Iterator iter = getUnterkonten().iterator(); iter.hasNext();) {
			Konto konto = (Konto) iter.next();
			int gewicht = konto.getGewicht();
			if (maxgewicht >= gewicht) {
				if ((!keinnullsaldo)
						|| (!konto.getSaldo().equals(new Betrag()))) {
					erg += konto.ausgabe(tab, ebene + 1, maxgewicht - gewicht,
							nursalden, keinnullsaldo);
				}
			}
		}
		if (maxgewicht > 0 && (!nursalden)) {
			for (Iterator iter = getBuchungszeilen().iterator(); iter.hasNext();) {
				Buchungszeile zeile = (Buchungszeile) iter.next();
				erg += zeile.ausgabe(tab, ebene + 1) + "\n";
			}
		}
		return erg;
	}

	/**
	 * Gibt das Konto als eine Zeile in einen String aus (wie z.B. in einer 
	 * Saldenliste). Es können die Spaltenbreiten angegeben werden sowie eine
	 * Einrückungsebene.
	 * 
	 * @param tab
	 * @param ebene
	 * @return Textstring, der eine Tabellenzeile enthält
	 * @throws SQL_DBException
	 */
	public String printSaldenzeile(Drucktabelle tab, int ebene)
			throws SQL_DBException {
		Map hash = new HashMap();
		hash.put("Nr", getKontonummer());
		String bezeichnung = "";
		for (int i = 0; i < ebene; i++)
			bezeichnung += "  ";
		bezeichnung += getBezeichnung();
		hash.put("Bezeichnung", bezeichnung);
		Betrag betrag = getSaldo();
		if (betrag.isSoll()) {
			hash.put("Soll", StringUtil.formatNumber(betrag.getWert()));
		} else {
			hash.put("Haben", StringUtil.formatNumber(betrag.getWert()));
		}
		return tab.printZeile(hash);
	}
}
/*
 * $Log: Konto.java,v $
 * Revision 1.12  2005/08/30 21:08:44  tbayen
 * kleinere Warnung im Javadoc beseitigt
 *
 * Revision 1.11  2005/08/30 21:05:53  tbayen
 * Kontenplanimport aus GNUCash
 * Ausgabe von Auswertungen, Kontenübersicht, Bilanz, GuV, etc. als Tabelle
 * Nutzung von Transaktionen
 *
 * Revision 1.10  2005/08/21 17:26:12  tbayen
 * doppelte Variable in fast allen von AbstractObject abgeleiteten Klassen
 *
 * Revision 1.9  2005/08/21 17:13:38  tbayen
 * Konstruktor weniger umständlich
 *
 * Revision 1.8  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.7  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 * Revision 1.6  2005/08/18 14:14:04  tbayen
 * diverse Erweiterungen, Konto kennt jetzt auch Buchungen
 *
 * Revision 1.5  2005/08/17 20:28:04  tbayen
 * zwei Methoden zum Auflisten von Objekten und alles, was dazu sonst noch nötig war
 *
 * Revision 1.4  2005/08/17 18:54:32  tbayen
 * An vielen Stellen int durch Long ersetzt. Das macht vieles klarer und kürzer
 *
 * Revision 1.3  2005/08/16 12:22:09  tbayen
 * rudimentäres Arbeiten mit Buchungszeilen möglich
 *
 * Revision 1.2  2005/08/16 08:52:32  tbayen
 * Grundgerüst der Klasse Buchung (mit Test) steht
 *
 * Revision 1.1  2005/08/15 19:13:09  tbayen
 * Erste Version von heute.
 *
 */