/* Erzeugt am 24.10.2004 von tbayen
 * $Id: TypeDefinitionDate.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $
 */
package de.jalin.freibier.database.impl.type;

import java.text.DateFormat;

/**
 * Datentyp z.B. für SQL-Daten vom Typ DATE
 */
public class TypeDefinitionDate extends TypeDefinitionDateTime {

	public TypeDefinitionDate() {
		super();
	}

	protected void setDefaultShortFormat(){
		shortFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	}	
}

/*
 * $Log: TypeDefinitionDate.java,v $
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.1  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen für DATE und TIME
 *
 */