// $Id: RecordObject.java,v 1.1 2005/08/23 19:56:05 phormanns Exp $

package de.bayen.fibu.gui.data;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import de.bayen.database.DataObject;
import de.bayen.database.Record;
import de.bayen.database.RecordDefinition;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.typedefinition.TypeDefinition;
import de.willuhn.datasource.GenericObject;

public class RecordObject implements GenericObject {

	private Record data;
	
	public RecordObject(Record data) {
		this.data = data;
	}

	public Object getAttribute(String property) throws RemoteException {
		DataObject field = data.getField(property);
		return field.getValue();
	}
	
	public void setAttribute(String property, String value) throws RemoteException {
		try {
			data.setField(property, value);
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim Datenzugriff", e);
		}
	}

	public String[] getAttributeNames() throws RemoteException {
		List typeDefsList = data.getRecordDefinition().getFieldsList();
		Iterator typeDefsIterator = typeDefsList.iterator();
		TypeDefinition typeDef = null;
		int idx = 0;
		String[] propertyNames = new String[typeDefsList.size()];
		while (typeDefsIterator.hasNext()) {
			typeDef = (TypeDefinition) typeDefsIterator.next();
			propertyNames[idx] = typeDef.getName();
			idx++;
		}
		return propertyNames;
	}

	public String getID() throws RemoteException {
		RecordDefinition recDef = data.getRecordDefinition();
		try {
			return data.getField(recDef.getPrimaryKey()).format();
		} catch (DatabaseException e) {
			throw new RemoteException("Fehler beim Datenzugriff", e);
		}
	}

	public String getPrimaryAttribute() throws RemoteException {
		RecordDefinition recDef = data.getRecordDefinition();
		return recDef.getPrimaryKey();
	}

	public boolean equals(GenericObject obj) throws RemoteException {
		return getID().equals(obj.getID());
	}

	public Record getRecord() {
		return data;
	}

}


//
// $Log: RecordObject.java,v $
// Revision 1.1  2005/08/23 19:56:05  phormanns
// Neues Paket data f�r Datenobjekte
//
// Revision 1.3  2005/08/21 20:18:09  phormanns
// Erste Widgets f�r Buchen-Dialog
//
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//