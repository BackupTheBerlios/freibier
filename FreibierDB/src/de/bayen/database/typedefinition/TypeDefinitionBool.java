/* Erzeugt am 28.03.2005 von tbayen
 * $Id: TypeDefinitionBool.java,v 1.2 2005/08/21 17:06:59 tbayen Exp $
 */
package de.bayen.database.typedefinition;

import de.bayen.database.exception.SysDBEx;
import de.bayen.database.exception.SysDBEx.WrongTypeDBException;

/**
 * Datentyp z.B. für SQL-Daten vom Typ bool (bzw. tinyint)
 * 
 * @author tbayen
 */
public class TypeDefinitionBool extends TypeDefinition {
	public TypeDefinitionBool() {
		super();
		defaultValue = new Boolean(false);
	}

	public Class getJavaType() {
		return Boolean.class;
	}

	public String format(Object s) throws WrongTypeDBException {
		if (s != null) {
			if (s instanceof Boolean) {
				return ((Boolean) s).booleanValue() ? "true" : "false";
			} else {
				throw new SysDBEx.WrongTypeDBException(
						"Bool-Objekt erwartet, '" + s.getClass().getName()
								+ "' bekommen", log);
			}
		} else {
			return "";
		}
	}

	public Object parse(String s) {
		s = s.trim();
		// SQL benutzt 0 und 1, ich benutze lieber true und false, 
		// deshalb erlaube ich hier beides:
		return new Boolean(s.equals("true") || s.equals("1"));
	}

	public boolean validate(String s) {
		s = s.trim();
		return (s.equals("true") || s.equals("false") || s.equals("0") || s
				.equals("1"));
	}
}
/*
 * $Log: TypeDefinitionBool.java,v $
 * Revision 1.2  2005/08/21 17:06:59  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/28 15:53:03  tbayen
 * Boolean-Typen eingeführt
 *
 */