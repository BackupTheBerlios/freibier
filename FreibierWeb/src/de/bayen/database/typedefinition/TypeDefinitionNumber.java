/* Erzeugt am 16.10.2004 von tbayen
 * $Id: TypeDefinitionNumber.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.database.typedefinition;


/**
 * abstrakter Datentyp für alle numerischen Daten (int und float).
 * 
 * @author tbayen
 */
public abstract class TypeDefinitionNumber extends TypeDefinition {

	public TypeDefinitionNumber() {
		super();
		setProperty("align","right");
	}
}

/*
 * $Log: TypeDefinitionNumber.java,v $
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
 * Revision 1.1  2004/10/17 21:10:58  tbayen
 * TableGUI ganz von Strings auf Objekte umgestellt, dabei
 * Ausrichtung und Formatierung in TableGUI und NicePrinter neu eingeführt,
 * neue Verwaltung von Properties für Datentypen
 *
 */