/* Erzeugt am 16.01.2006 von tbayen
 * $Id: ActionShow.java,v 1.1 2006/01/21 23:20:50 tbayen Exp $
 */
package de.bayen.depotmanager.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.util.StringHelper;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

public class ActionShow extends de.bayen.webframework.actions.ActionShow {
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		super.executeAction(ad, req, root, db, servlet);
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		String recordid = (String) uri.get("id");
		// f�r die Ausgabe eines Portfolios gibt es eine Extrawurst:
		if (name.equals("Portfolios")) {
			BigDecimal gesamtgewinn = new BigDecimal(0);
			BigDecimal gesamtsumme = new BigDecimal(0);
			Map papiere = new HashMap();
			root.put("bewegungen", papiere);
			Table bew = db.getTable("Bewegungen");
			QueryCondition cond = bew.new QueryCondition("Portfolio", 0,
					recordid);
			List bewegungen = bew.getRecordsFromQuery(cond, "Datum", true);
			for (int i = 0; i < bewegungen.size(); i++) {
				Record rec = (Record) bewegungen.get(i);
				// Wertpapier feststellen, ggf. Hash dazu erzeugen und in "papier" bereitstellen
				DataObject papierId = rec.getField("Wertpapier");
				Map papier;
				if (!papiere.containsValue(papierId.format())) {
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
						Record aktuellerkurs = (Record) aktuellerkursList.get(0);
						BigDecimal kursaktuell = (BigDecimal) aktuellerkurs.getField(
								"Schlusskurs").getValue();
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
						.multiply((BigDecimal)papier.get("KursAktuell"));
				papier.put("SummeAktuellNice", StringHelper.BigDecimal2String(
						summeAktuell, 2));
				gesamtsumme = gesamtsumme.add(summeAktuell);
				BigDecimal gewinn = summeAktuell.subtract((BigDecimal) papier
						.get("Summe"));
				papier.put("GewinnNice", StringHelper.BigDecimal2String(gewinn,
						2));
				papier.put("GV", gewinn.compareTo(new BigDecimal(0)) < 0 ? "V"
						: "G");
				gesamtgewinn = gesamtgewinn.add(gewinn);
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
 * Revision 1.1  2006/01/21 23:20:50  tbayen
 * Erste Version 1.0 des DepotManagers
 * erste FreibierWeb-Applikation im eigenen Paket
 *
 */