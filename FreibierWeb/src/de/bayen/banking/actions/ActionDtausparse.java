/* Erzeugt am 18.04.2005 von tbayen
 * $Id: ActionDtausparse.java,v 1.1 2005/04/19 17:17:04 tbayen Exp $
 */
package de.bayen.banking.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import de.bayen.banking.hbci.DTAUSReader;
import de.bayen.database.Record;
import de.bayen.database.Table;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.BLOB;
import de.bayen.webframework.Action;
import de.bayen.webframework.ActionDispatcher;
import de.bayen.webframework.ServletDatabase;
import de.bayen.webframework.WebDBDatabase;

/**
 * Diese Action nimmt einen Datensatz in der Pool-Tabelle, der eine
 * DTAUS-Datei enthält und erzeugt daraus wieder Transaktionen, die dann
 * in einen (durch einen Webformular-Parameter) angegebenen Ausgangskorb
 * gelegt werden.
 * 
 * @author tbayen
 */
public class ActionDtausparse implements Action {
	/*
	 * @see de.bayen.webframework.Action#executeAction(de.bayen.webframework.ActionDispatcher, javax.servlet.http.HttpServletRequest, java.util.Map, de.bayen.webframework.WebDBDatabase, de.bayen.webframework.ServletDatabase)
	 */
	public void executeAction(ActionDispatcher ad, HttpServletRequest req,
			Map root, WebDBDatabase db, ServletDatabase servlet)
			throws DatabaseException, ServletException {
		// erstmal die Show-Action alles einlesen lassen:
		new ActionShow().executeAction(ad,req,root,db,servlet);
		Record record = (Record) root.get("record");
		BLOB dtausblob = ((BLOB) record.getField("DTAUS").getValue());
		Table ausgangskoerbe=db.getTable("Ausgangskoerbe");
		Table transaktionen=db.getTable("Transaktionen");
		try {
			DTAUSReader dtaus = new DTAUSReader(dtausblob.toByteArray());
			for(int i=0; i<dtaus.getC_count(); i++){
				Record ta=transaktionen.getEmptyRecord();
				ta.setField("Empfaenger",dtaus.getC_Empfaenger(i));
				ta.setField("blz",dtaus.getC_BLZ(i));
				ta.setField("Kontonummer",dtaus.getC_Kontonummer(i));
				ta.setField("Ausgangskorb",req.getParameter("_konto"));
				ta.setField("Betrag",dtaus.getC_Betrag(i));
				List vwz=dtaus.getC_Verwendungszweck(i);
				ta.setField("Vwz1",(String)vwz.get(0));
				if(vwz.size()>1)
					ta.setField("Vwz2",(String)vwz.get(1));
				if(vwz.size()>2)
					ta.setField("Vwz3",(String)vwz.get(2));
				if(vwz.size()>3)
					ta.setField("Vwz4",(String)vwz.get(3));
				transaktionen.setRecord(ta);
			}
		} catch (Exception e) {
			throw new ServletException("Fehler beim Lesen der DTAUS-Datei",e);
		}
		
	}
}

/*
 * $Log: ActionDtausparse.java,v $
 * Revision 1.1  2005/04/19 17:17:04  tbayen
 * DTAUS-Dateien wieder einlesen in die Datenbank
 *
 */