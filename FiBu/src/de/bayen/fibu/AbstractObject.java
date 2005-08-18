/* Erzeugt am 18.08.2005 von tbayen
 * $Id: AbstractObject.java,v 1.1 2005/08/18 17:04:24 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.List;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;
import de.bayen.database.exception.DatabaseException;

/**
 * Gemeinsame Basisklasse aller Datenobjekte, um Zugriffe generalisieren zu 
 * können.
 * 
 * @author tbayen
 */
public abstract class AbstractObject implements GenericObject{
	protected Record record;

	public Long getID() throws DatabaseException {
		return (Long) record.getPrimaryKey().getValue();
	}

	public boolean equals(GenericObject arg) throws DatabaseException {
		try {
			if (!getClass().equals(arg.getClass()))
				return false;
			return getID().equals(((AbstractObject)arg).getID());
		} catch (ClassCastException e) {
			return false;
		}
	}

	public Object getAttribute(String feldname) throws DatabaseException {
		DataObject field = record.getField(feldname);
		if (field.getTypeDefinition().getJavaType().equals(ForeignKey.class)) {
			return ((ForeignKey) field.getValue()).getKey();
		} else {
			return field.getValue();
		}
	}

	public String[] getAttributeNames() {
		List liste = record.getRecordDefinition().getFieldsList();
		String[] namen = new String[liste.size()];
		for (int i = 0; i < liste.size(); i++) {
			namen[i] = (String) liste.get(i);
		}
		return namen;
	}

	public String getGOID() throws DatabaseException {
		return getID().toString();
	}

	public String getPrimaryAttribute() {
		return ("id");
	}
	
}

/*
 * $Log: AbstractObject.java,v $
 * Revision 1.1  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 */