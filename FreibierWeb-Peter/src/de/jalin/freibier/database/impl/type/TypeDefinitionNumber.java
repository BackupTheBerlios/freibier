/* Erzeugt am 16.10.2004 von tbayen
 * $Id: TypeDefinitionNumber.java,v 1.1 2004/12/31 19:37:26 phormanns Exp $
 */
package de.jalin.freibier.database.impl.type;

import de.jalin.freibier.database.impl.TypeDefinitionImpl;


/**
 * abstrakter Datentyp für alle numerischen Daten (int und float).
 * 
 * @author tbayen
 */
public abstract class TypeDefinitionNumber extends TypeDefinitionImpl {

	public TypeDefinitionNumber() {
		super();
		setProperty("align","right");
	}
}

/*
 * $Log: TypeDefinitionNumber.java,v $
 * Revision 1.1  2004/12/31 19:37:26  phormanns
 * Database Schnittstelle herausgearbeitet
 *
 * Revision 1.1  2004/12/31 17:13:03  phormanns
 * Erste öffentliche Version
 *
 * Revision 1.1  2004/10/24 15:46:42  tbayen
 * Typdefinitionen in eigenes Package ausgelagert
 *
 * Revision 1.1  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 */