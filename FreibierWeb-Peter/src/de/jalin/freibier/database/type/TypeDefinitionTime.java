/* Erzeugt am 24.10.2004 von tbayen
 * $Id: TypeDefinitionTime.java,v 1.1 2004/12/31 17:13:03 phormanns Exp $
 */
package de.jalin.freibier.database.type;

import java.text.DateFormat;

/**
 * Datentyp z.B. für SQL-Daten vom Typ TIME
 */
public class TypeDefinitionTime extends TypeDefinitionDateTime {

	public TypeDefinitionTime() {
		super();
	}

	protected void setDefaultShortFormat(){
		shortFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	}	
}

/*
 * $Log: TypeDefinitionTime.java,v $
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