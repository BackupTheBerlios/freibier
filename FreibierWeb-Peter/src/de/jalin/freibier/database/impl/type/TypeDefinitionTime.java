/* Erzeugt am 24.10.2004 von tbayen
 * $Id: TypeDefinitionTime.java,v 1.3 2005/02/24 13:52:12 phormanns Exp $
 */
package de.jalin.freibier.database.impl.type;

import java.text.DateFormat;

/**
 * Datentyp z.B. für SQL-Daten vom Typ TIME
 */
public class TypeDefinitionTime extends TypeDefinitionDateTime {

	public TypeDefinitionTime() {
		super();
	}

	protected void setDefaultShortFormat(){
		setShortDateTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
	}	
}

/*
 * $Log: TypeDefinitionTime.java,v $
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
 * Erste öffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.1  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen für DATE und TIME
 *
 */