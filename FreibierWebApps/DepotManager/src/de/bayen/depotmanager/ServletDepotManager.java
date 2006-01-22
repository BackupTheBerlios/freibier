/* Erzeugt am 15.01.2006 von tbayen
 * $Id: ServletDepotManager.java,v 1.2 2006/01/22 20:07:35 tbayen Exp $
 */
package de.bayen.depotmanager;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.bayen.webframework.ServletDatabase;

public class ServletDepotManager extends ServletDatabase {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}
}
/*
 * $Log: ServletDepotManager.java,v $
 * Revision 1.2  2006/01/22 20:07:35  tbayen
 * Datenbank-Zugriff korrigiert: Man konnte nicht in mehreren Fenstern arbeiten.
 * Klasse WebDBDatabase unnötig, wurde gelöscht
 *
 * Revision 1.1  2006/01/21 23:20:50  tbayen
 * Erste Version 1.0 des DepotManagers
 * erste FreibierWeb-Applikation im eigenen Paket
 *
 */