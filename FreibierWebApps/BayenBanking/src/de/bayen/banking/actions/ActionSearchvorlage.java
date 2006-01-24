/* Erzeugt am 02.06.2005 von tbayen
 * $Id: ActionSearchvorlage.java,v 1.1 2006/01/24 00:26:01 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import de.bayen.database.Database;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.Table.QueryCondition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;

/**
 * Diese Action sucht in der Liste der Vorlagen zum Suchstring passende
 * Einträge und stellt sie in eine Liste.
 * 
 * @author tbayen
 */
public class ActionSearchvorlage implements Action {
	static Logger logger = Logger
			.getLogger(ActionSearchvorlage.class.getName());

	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, Database db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		Map uri = (Map) root.get("uri");
		String name = (String) uri.get("table");
		if (!name.equals("Transaktionen"))
			throw new ServletException(
					"Vorlagensuche nur für Transaktionen möglich");
		List fields = (List) root.get("fields");
		Table tab = db.getTable(name);
		// QueryCondition zusammenbauen:
		String parameter = "_Empfaenger";
		String value = req.getParameter(parameter);
		value=value.replace('*','%');   // regex-Syntax für SQL erzeugen
		int operator = Table.QueryCondition.LIKE;
		QueryCondition condi = tab.new QueryCondition(parameter.substring(1),
				operator, value);
		// zusätzliche Kondition: nur Datensätze aus dem Vorlagenkorb
		String vorlagenkorb = db.getTable("Ausgangskoerbe").getRecordByValue(
				"Bezeichnung", "Vorlagen").getField("id").format();
		QueryCondition nextcond = tab.new QueryCondition("Ausgangskorb",
				Table.QueryCondition.EQUAL, vorlagenkorb);
		condi.and(nextcond);
		List records = tab.getRecordsFromQuery(condi, tab.getRecordDefinition()
				.getPrimaryKey(), true);
		// Fertigmachen für ein List-View
		root.put("list", records);
		if (records.size() != 0) {
			// Vorlagen-Record gefunden!
			Record vorlage = (Record) records.get(0);
			String recordid = (String) ((Map) root.get("uri")).get("id");
			Record record = tab.getRecordByPrimaryKey(tab.getRecordDefinition()
					.getFieldDef("id").parse(recordid));
			// Felder einzeln kopieren:
			Iterator i = fields.iterator();
			while (i.hasNext()) {
				String feld = (String) i.next();
				if ((!feld.equals("id")) && (!feld.equals("Ausgangskorb"))) {
					record.setField(feld, vorlage.getField(feld));
				}
			}
			// und in die Datenbank schreiben:
			tab.setRecord(record);
		}
		// Umleitung auf andere Action und anderes view
		uri.put("view","editform");
		ad.executeAction("show", req, root, db, servlet);
	}
}
/*
 * $Log: ActionSearchvorlage.java,v $
 * Revision 1.1  2006/01/24 00:26:01  tbayen
 * Erste eigenständige Version (1.6beta)
 * sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
 *
 * Revision 1.1  2005/08/07 16:56:15  tbayen
 * Produktionsversion 1.5
 *
 */