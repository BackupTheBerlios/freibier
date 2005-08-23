// $Id: JournaleControl.java,v 1.5 2005/08/23 19:40:15 phormanns Exp $
package de.bayen.fibu.gui.control;

import java.util.List;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.FibuService;
import de.bayen.fibu.Journal;
import de.bayen.fibu.ObjectWrapper;
import de.bayen.fibu.gui.FiBuPlugin;
import de.bayen.fibu.gui.ListIterator;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.action.JournalBuchenAction;
import de.bayen.fibu.gui.menu.AlleJournalMenu;
import de.bayen.fibu.gui.menu.OffenesJournalMenu;
import de.bayen.fibu.gui.widget.TablePart;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

public class JournaleControl extends AbstractControl {

	private TablePart alleJournale, offeneJournale;
	
	public JournaleControl(AbstractView view) {
		super(view);
	}


	public void createJournal() throws ApplicationException {
		try {
			Journal journal = Settings.getFibuService().getFiBu().createJournal();
			offeneJournale.addItem(new ObjectWrapper(journal));
		} catch (Exception e) {
			throw new ApplicationException("Fehler beim Journal-Anlegen", e);
		}
	}
	public TablePart getOffeneJournale() throws ApplicationException {
		if (offeneJournale == null) {
			offeneJournale = readJournale(false);
			offeneJournale.setContextMenu(new OffenesJournalMenu());
		}
		return offeneJournale;
	}

	public TablePart getAlleJournale() throws ApplicationException {
		if (alleJournale == null) {
			alleJournale = readJournale(true);
			alleJournale.setContextMenu(new AlleJournalMenu());
		}
		return alleJournale;
	}

	private TablePart readJournale(boolean alleJournale) throws ApplicationException {
		FibuService service;
		try {
			service = (FibuService) Application.getServiceFactory().lookup(FiBuPlugin.class, "buchhaltung");
		} catch (Exception e) {
			throw new ApplicationException("FiBu-Service nicht gefunden", e);
		}
		TablePart journaleTable = new TablePart(new JournaleList(service.getFiBu(), alleJournale), new JournalBuchenAction());
		journaleTable.addColumn("Journalnummer", "Journalnummer");
		journaleTable.addColumn("Startdatum", "Startdatum", new DateFormatter(Settings.getDateFormat()));
		journaleTable.addColumn("Buchungsjahr", "Buchungsjahr");
		journaleTable.addColumn("Buchungsperiode", "Buchungsperiode");
		journaleTable.addColumn("absummiert", "absummiert");
		return  journaleTable;
	}

	private class JournaleList extends ListIterator {

		private Buchhaltung fibu;
		private boolean alleJournale;
		
		public JournaleList(Buchhaltung fibu, boolean alleJournale) {
			super();
			this.alleJournale = alleJournale;
			this.fibu = fibu;
		}
		
		public List reloadList() throws ApplicationException {
			try {
				if (alleJournale) {
					return fibu.getAlleJournale();
				} else {
					return fibu.getOffeneJournale();
				}
			} catch (DatabaseException e) {
				throw new ApplicationException("Fehler beim Lesen der Journale", e);
			}
		}
		
	}
}

/*
 *  $Log: JournaleControl.java,v $
 *  Revision 1.5  2005/08/23 19:40:15  phormanns
 *  Abhängigkeiten vom Willuhn-Persistenzframework  durch Kopieren und Anpassen einiger Widgets entfernt
 *
 *  Revision 1.4  2005/08/21 20:18:56  phormanns
 *  Erste Widgets für Buchen-Dialog
 *
 *  Revision 1.3  2005/08/18 17:45:48  tbayen
 *  generischer Wrapper für FiBu-Objekte
 *
 *  Revision 1.2  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/08/18 11:24:12  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
