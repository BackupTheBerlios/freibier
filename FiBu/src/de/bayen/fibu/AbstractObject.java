/* Erzeugt am 18.08.2005 von tbayen
 * $Id: AbstractObject.java,v 1.3 2005/09/08 06:27:44 tbayen Exp $
 */
package de.bayen.fibu;

import java.util.List;
import de.bayen.database.DataObject;
import de.bayen.database.ForeignKey;
import de.bayen.database.Record;

/**
 * Gemeinsame Basisklasse aller Datenobjekte, um Zugriffe generalisieren zu 
 * können.
 * 
 * @author tbayen
 */
public abstract class AbstractObject implements GenericObject{
	protected Record record;

	public Long getID(){
		return (Long) record.getPrimaryKey().getValue();
	}

	public boolean equals(GenericObject arg){
		try {
			if (!getClass().equals(arg.getClass()))
				return false;
			return getID().equals(((AbstractObject)arg).getID());
		} catch (ClassCastException e) {
			return false;
		}
	}

	public boolean equals(Object arg){
		try {
			if (!getClass().equals(arg.getClass()))
				return false;
			return getID().equals(((AbstractObject)arg).getID());
		} catch (ClassCastException e) {
			return false;
		}
	}

	public Object getAttribute(String feldname){
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

	public String getGOID(){
		return getID().toString();
	}

	public String getPrimaryAttribute() {
		return ("id");
	}
	
}

/*
 * $Log: AbstractObject.java,v $
 * Revision 1.3  2005/09/08 06:27:44  tbayen
 * Buchhaltung.getBilanzkonto() überarbeitet
 *
 * Revision 1.2  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.1  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 */