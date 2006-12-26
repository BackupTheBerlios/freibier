/* Erzeugt am 16.01.2006 von tbayen
 * $Id: ActionShow.java,v 1.4 2006/12/26 14:27:52 tbayen Exp $
 */
package de.bayen.depotmanager.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.database.DataObject;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.util.StringHelper;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

public class ActionShow extends de.bayen.webframework.actions.ActionShow {
	static Logger logger = Logger.getLogger(ActionShow.class.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		super.executeAction(ad, req, root, db, servlet);
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		String recordid = (String) uri.get("id");
		// für die Ausgabe eines Portfolios gibt es eine Extrawurst:
		if (name.equals("Portfolios")) {
			Map papiere = new HashMap();
			root.put("bewegungen", papiere);
			Table bew = db.getTable("Bewegungen");
			QueryCondition cond = bew.new QueryCondition("Portfolio",
					QueryCondition.EQUAL, recordid);
			List bewegungen = bew.getRecordsFromQuery(cond, "Datum", true);
			for (int i = 0; i < bewegungen.size(); i++) {
				Record rec = (Record) bewegungen.get(i);
				// Wertpapier feststellen, ggf. Hash dazu erzeugen und in "papier" bereitstellen
				DataObject papierId = rec.getField("Wertpapier");
				Map papier;
				if (!papiere.containsKey(papierId.format())) {
					// Dieses Papier gibt es noch nicht, es wird neu angelegt:
					papier = new HashMap();
					Record wertpapier = db.getTable("Wertpapiere")
							.getRecordByPrimaryKey(papierId.getValue());
					papier.put("Name", wertpapier.getFormatted("Name"));
					papier.put("ISIN", wertpapier.getFormatted("ISIN"));
					papier.put("einzelbewegungen", new ArrayList());
					papier.put("Anzahl", new BigDecimal(0));
					papier.put("Summe", new BigDecimal(0));
					// aktuellen Kurs feststellen
					Table kursdatenTabelle = db.getTable("Kursdaten");
					Table.QueryCondition condition = kursdatenTabelle.new QueryCondition(
							"Wertpapier", QueryCondition.EQUAL, papierId
									.getValue());
					List aktuellerkursList = kursdatenTabelle.getRecords(
							condition, "Datum", false, 0, 1);
					if (aktuellerkursList.size() > 0) {
						Record aktuellerkurs = (Record) aktuellerkursList
								.get(0);
						BigDecimal kursaktuell = (BigDecimal) aktuellerkurs
								.getField("Schlusskurs").getValue();
						papier.put("KursAktuell", kursaktuell);
						papier.put("KursAktuellNice",
								StringHelper.BigDecimal2String(
										(BigDecimal) (aktuellerkurs
												.getField("Schlusskurs")
												.getValue()), 2));
						papier
								.put("KursDatum", aktuellerkurs
										.getField("Datum"));
					} else {
						papier.put("KursAktuell", new BigDecimal(0));
						papier.put("KursAktuellNice", "0,00");
						papier.put("KursDatum", "00.00.0000");
					}
					logger.debug("Neues Papier: " + papier);
					papiere.put(papierId.format(), papier);
				} else {
					papier = (Map) papiere.get(papierId.format());
				}
				// Jetzt die eigentliche Bewegung in den "papier"-Hash schreiben
				List papierBewegungen = (List) papier.get("einzelbewegungen");
				Map einzelbewegung = new HashMap();
				einzelbewegung.put("Datum", rec.getField("Datum"));
				einzelbewegung.put("Anzahl", StringHelper.BigDecimal2String(
						(BigDecimal) (rec.getField("Anzahl").getValue()), 2));
				einzelbewegung.put("Kurs", StringHelper.BigDecimal2String(
						(BigDecimal) (rec.getField("Kurs").getValue()), 2));
				BigDecimal betrag = ((BigDecimal) (rec.getField("Anzahl")
						.getValue())).multiply((BigDecimal) rec
						.getField("Kurs").getValue());
				einzelbewegung.put("Betrag", StringHelper.BigDecimal2String(
						betrag, 2));
				logger.debug("Einzelbewegung: " + einzelbewegung);
				papierBewegungen.add(einzelbewegung);
				// Papier-Daten aktualisieren
				papier.put("Anzahl", ((BigDecimal) papier.get("Anzahl")).add(
						(BigDecimal) rec.getField("Anzahl").getValue())
						.setScale(2));
				papier.put("Summe", ((BigDecimal) papier.get("Summe")).add(
						betrag).setScale(2));
				papier.put("AnzahlNice", StringHelper.BigDecimal2String(
						(BigDecimal) papier.get("Anzahl"), 2));
				papier.put("SummeNice", StringHelper.BigDecimal2String(
						(BigDecimal) papier.get("Summe"), 2));
				BigDecimal summeAktuell = ((BigDecimal) papier.get("Anzahl"))
						.multiply((BigDecimal) papier.get("KursAktuell"));
				papier.put("SummeAktuell", summeAktuell);
				papier.put("SummeAktuellNice", StringHelper.BigDecimal2String(
						summeAktuell, 2));
				BigDecimal gewinn = summeAktuell.subtract((BigDecimal) papier
						.get("Summe"));
				papier.put("Gewinn", gewinn);
				papier.put("GewinnNice", StringHelper.BigDecimal2String(gewinn,
						2));
				papier.put("GV", gewinn.compareTo(new BigDecimal(0)) < 0 ? "V"
						: "G");
			}
			// Jetzt die Summen berechnen:
			BigDecimal gesamtsumme = new BigDecimal(0);
			BigDecimal gesamtgewinn = new BigDecimal(0);
			for (Iterator iter = papiere.keySet().iterator(); iter.hasNext();) {
				Map papier = (Map) papiere.get(iter.next());
				gesamtsumme = gesamtsumme.add((BigDecimal)papier.get("SummeAktuell"));
				gesamtgewinn = gesamtgewinn.add((BigDecimal)papier.get("Gewinn"));
			}
			root.put("Gewinn", gesamtgewinn);
			root.put("GewinnNice", StringHelper.BigDecimal2String(gesamtgewinn,
					2));
			root.put("GV", gesamtgewinn.compareTo(new BigDecimal(0)) < 0 ? "V"
					: "G");
			root.put("Summe", gesamtsumme);
			root.put("SummeNice", StringHelper
					.BigDecimal2String(gesamtsumme, 2));
		}
	}
}
/*
 * $Log: ActionShow.java,v $
 * Revision 1.4  2006/12/26 14:27:52  tbayen
 * Depotmanager hat sich in der Summe verrechnet (wie peinlich...)
 *
 * Revision 1.3  2006/01/24 21:59:28  tbayen
 * Prozentangabe bei Gewinn/Verlust
 *
 * Revision 1.2  2006/01/22 20:07:34  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.1  2006/01/21 23:20:50  tbayen
 * Erste Version 1.0 des DepotManagers
 * erste FreibierWeb-Applikation im eigenen Paket
 *
 */