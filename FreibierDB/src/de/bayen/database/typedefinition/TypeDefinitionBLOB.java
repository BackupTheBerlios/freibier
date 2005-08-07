/* Erzeugt am 09.10.2004 von tbayen
 * $Id: TypeDefinitionBLOB.java,v 1.1 2005/08/07 21:18:49 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import java.io.IOException;
import de.bayen.database.exception.DatabaseException;
import de.bayen.database.exception.SystemDatabaseException;
import de.bayen.database.exception.UserDatabaseException;

/**
 * @author tbayen
 * 
 * Datentyp z.B. für SQL-Daten vom Typ mediumblob (Binärobjekte, z.B. Bilder)
 */
public class TypeDefinitionBLOB extends TypeDefinition {
	public TypeDefinitionBLOB() {
		super();
		defaultValue = new BLOB();
	}

	public Class getJavaType() {
		return BLOB.class;
	}

	public String format(Object s) throws SystemDatabaseException {
		if (s != null) {
			if (s instanceof BLOB) {
				return s.toString();
			} else {
				throw new SystemDatabaseException("BLOB-Objekt erwartet", log);
			}
		} else {
			return "";
		}
	}

	public Object parse(String s) throws DatabaseException {
		BLOB blob = new BLOB();
		log.debug("BLOB parsen: '"+s+"'");
		if (s != null && s.length() > 0) {
			try {
				blob.parseString(s);
			} catch (IOException e) {
				throw new UserDatabaseException(
						"BLOB kann nicht geparst werden", e, log);
			}
		}
		return blob;
	}

	public boolean validate(String s) {
		BLOB blob = new BLOB();
		try {
			blob.parseString(s);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
/*
 * $Log: TypeDefinitionBLOB.java,v $
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 */