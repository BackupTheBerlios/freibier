/* Erzeugt am 18.08.2005 von tbayen
 * $Id: GenericObject.java,v 1.2 2005/08/21 17:08:55 tbayen Exp $
 */
package de.bayen.fibu;


/**
 * Diese Schnittstelle entspricht der GenericObject-Schnittstelle aus dem
 * Jameica-Framework. Man kann so generisch auf die FiBu-Klassen zugreifen.
 * 
 * @author tbayen
 */
public interface GenericObject {
	public abstract Object getAttribute(String arg);

	public abstract String[] getAttributeNames();

	public abstract String getGOID();

	public abstract String getPrimaryAttribute();

	public abstract boolean equals(GenericObject arg);
}
/*
 * $Log: GenericObject.java,v $
 * Revision 1.2  2005/08/21 17:08:55  tbayen
 * Exception-Klassenhierarchie komplett neu geschrieben und überall eingeführt
 *
 * Revision 1.1  2005/08/18 17:04:24  tbayen
 * Interface GenericObject für alle Business-Objekte eingeführt
 * durch Ableitung von AbstractObject
 * Fehler in Buchhaltung (Journal statt Konto)
 *
 */