/* Erzeugt am 24.10.2004 von tbayen
 * $Id: TypeDefinitionDate.java,v 1.4 2005/03/01 21:56:32 phormanns Exp $
 */
package de.jalin.freibier.database.impl.type;

import java.text.DateFormat;
import java.util.Date;

/**
 * Datentyp z.B. f�r SQL-Daten vom Typ DATE
 */
public class TypeDefinitionDate extends TypeDefinitionDateTime {

	public TypeDefinitionDate() {
		super();
		defaultValue = new Date();
	}

	private void setDefaultShortFormat(){
		setShortDateTimeFormat(DateFormat.getDateInstance(DateFormat.MEDIUM));
	}	
}

/*
 * $Log: TypeDefinitionDate.java,v $
 * Revision 1.4  2005/03/01 21:56:32  phormanns
 * Long immer als Value-Objekt zu Number-Typen
 * setRecord macht Insert, wenn PK = Default-Value
 *
 * Revision 1.3  2005/02/24 13:52:12  phormanns
 * Mit Tests begonnen
 *
 * Revision 1.2  2005/02/11 15:50:35  phormanns
 * Merge
 *
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste �ffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.1  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen f�r DATE und TIME
 *
 */