// $Id: KontoKnoten.java,v 1.2 2005/09/08 06:27:46 tbayen Exp $
package de.bayen.fibu.gui.data;

import java.util.List;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.Konto;
import de.bayen.fibu.gui.Settings;
import de.willuhn.util.ApplicationException;

public class KontoKnoten implements GenericObjectNode {
	
	private Konto konto;

	public KontoKnoten(Konto kto) {
		this.konto = kto;
	}
	
	public Konto getKonto() {
		return konto;
	}

	public GenericIterator getChildren() throws ApplicationException {
		
		GenericIterator res;
		try {
			res = new GenericIterator() {

				private List unterkonten = konto.getUnterkonten();
				private int index = 0;
				
				public void begin() throws ApplicationException {
					try {
						unterkonten = konto.getUnterkonten();
						index = 0;
					} catch (SQL_DBException e) {
						throw new ApplicationException("Fehler beim Lesen der Unterkonten", e);
					}
					
				}

				public boolean hasNext() {
					return unterkonten != null && index < unterkonten.size();
				}

				public GenericObject next() {
					GenericObject obj = new KontoKnoten((Konto) unterkonten.get(index));
					index++;
					return obj;
				}
				
			};
			return res;
		} catch (SQL_DBException e) {
			throw new ApplicationException("Fehler beim Aufbau der Kontohierarchie", e);
		}
	}

	public Object getAttribute(String attr) {
		if ("Beschriftung".equals(attr)) {
			return konto.getAttribute("Kontonummer");
			// TODO sollte man das trotzdem formatieren?
//			return Settings.getKontoFormat().format(
//					Integer.parseInt((String) konto.getAttribute("Kontonummer"))
//				) 
//				+ " " + konto.getAttribute("Bezeichnung");
		}
		return konto.getAttribute(attr);
	}

	public String[] getAttributeNames() {
		String[] attribs = konto.getAttributeNames();
		String[] ret = new String[attribs.length + 1];
		ret[0] = "Beschriftung";
		for (int i=0; i < attribs.length; i++) {
			ret[i+1] = attribs[i];
		}
		return ret;
	}

	public String getGOID() {
		return konto.getGOID();
	}

	public String getPrimaryAttribute() {
		return "Beschriftung";
	}

	public boolean equals(GenericObject go) {
		return konto.equals(go);
	}
}

/*
 *  $Log: KontoKnoten.java,v $
 *  Revision 1.2  2005/09/08 06:27:46  tbayen
 *  Buchhaltung.getBilanzkonto() überarbeitet
 *
 *  Revision 1.1  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 */
