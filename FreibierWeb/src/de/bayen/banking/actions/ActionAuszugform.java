/* Erzeugt am 26.03.2005 von tbayen
 * $Id: ActionAuszugform.java,v 1.2 2005/04/18 10:57:55 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Legt Vorgabewerte in das Model, die im Formular zur Anforderung eines
 * neuen Auszugs benutzt werden können.
 * 
 * @author tbayen
 */
public class ActionAuszugform implements Action {
	/*
	 * @see de.bayen.webframework.Action#executeAction(de.bayen.webframework.ActionDispatcher, javax.servlet.http.HttpServletRequest, java.util.Map, de.bayen.webframework.WebDBDatabase)
	 */
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet) throws DatabaseException,
			ServletException {
		root.put("_passportpassword", "banking");  // kein richtiges Geheimnis
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
				Locale.GERMANY);
		Calendar jetzt = new GregorianCalendar();
		Calendar datum = new GregorianCalendar(jetzt.get(Calendar.YEAR), jetzt
				.get(Calendar.MONTH), jetzt.get(Calendar.DAY_OF_MONTH));
		datum.add(Calendar.DATE, -1); // -1 heisst gestern
		root.put("_startdate", df.format(datum.getTime()));
		root.put("_enddate", df.format(datum.getTime()));
		List liste = new ArrayList();
		root.put("list", liste);
		Table konten=db.getTable("Konten");
		for(int i=0; i<konten.getNumberOfRecords(); i++){
			Record record=konten.getRecordByNumber(i);
			Map map=new HashMap();
			liste.add(map);
			map.put("name",record.getFormatted("Bezeichnung"));
			map.put("value",record.getFormatted("id"));
		}
	}
}

/*
 * $Log: ActionAuszugform.java,v $
 * Revision 1.2  2005/04/18 10:57:55  tbayen
 * Urlaubsarbeit:
 * Eigenes View, um Exceptions abzufangen
 * System von verteilten Properties-Dateien
 *
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.3  2005/04/05 21:14:12  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.2  2005/03/26 23:07:29  tbayen
 * Auswahl mehrerer Konten für Auszug
 *
 * Revision 1.1  2005/03/26 03:10:44  tbayen
 * Banking-Applikation kann per Chipkarte
 * Auszüge abholen und anzeigen
 *
 */