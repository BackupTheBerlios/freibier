/* Erzeugt am 24.10.2004 von tbayen
 * $Id: TypeDefinitionDate.java,v 1.1 2005/08/07 21:18:49 tbayen Exp $
 */
package de.bayen.database.typedefinition;

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
 * Revision 1.1  2005/08/07 21:18:49  tbayen
 * Version 1.0 der Freibier-Datenbankklassen,
 * extrahiert aus dem Projekt WebDatabase V1.5
 *
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/02/21 16:11:53  tbayen
 * Erste Version mit Tabellen- und Datensatzansicht
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.1  2004/10/24 13:54:21  tbayen
 * Eigene Datentypen für DATE und TIME
 *
 */