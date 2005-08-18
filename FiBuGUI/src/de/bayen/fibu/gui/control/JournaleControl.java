// $Id: JournaleControl.java,v 1.2 2005/08/18 14:08:13 phormanns Exp $
package de.bayen.fibu.gui.control;

import java.rmi.RemoteException;
import java.util.List;
import de.bayen.database.exception.DatabaseException;
import de.bayen.fibu.Buchhaltung;
import de.bayen.fibu.FibuService;
import de.bayen.fibu.Journal;
import de.bayen.fibu.gui.FiBuPlugin;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.action.JournalBuchenAction;
import de.bayen.fibu.gui.menu.AlleJournalMenu;
import de.bayen.fibu.gui.menu.OffenesJournalMenu;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
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
			offeneJournale.addItem(new JournalWrapper(journal));
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
		TablePart journaleTable = new TablePart(new JournaleIterator(service.getFiBu(), alleJournale), new JournalBuchenAction());
		journaleTable.addColumn("Journalnummer", "Journalnummer");
		journaleTable.addColumn("Startdatum", "Startdatum", new DateFormatter(Settings.getDateFormat()));
		journaleTable.addColumn("Buchungsjahr", "Buchungsjahr");
		journaleTable.addColumn("Buchungsperiode", "Buchungsperiode");
		journaleTable.addColumn("absummiert", "absummiert");
		return  journaleTable;
	}
	
	private class JournaleIterator implements GenericIterator {

		private Buchhaltung fibu;
		private boolean alleJournale;
		private List journaleList;
		private int idx;
		
		public JournaleIterator(Buchhaltung fibu, boolean alleJournale) {
			this.fibu = fibu;
			this.alleJournale = alleJournale;
			this.journaleList = null;
			this.idx = 0;
		}
		
		public boolean hasNext() throws RemoteException {
			return idx < size();
		}

		public GenericObject next() throws RemoteException {
			GenericObject jrnl = new JournalWrapper((Journal) journaleList.get(idx));
			idx++;
			return jrnl;
		}

		public GenericObject previous() throws RemoteException {
			if (idx > 0) idx--;
			return new JournalWrapper((Journal) journaleList.get(idx));
		}

		public void begin() throws RemoteException {
			try {
				if (alleJournale) {
					journaleList = fibu.getAlleJournale();
				} else {
					journaleList = fibu.getOffeneJournale();
				}
				idx = 0;
			} catch (DatabaseException e) {
				throw new RemoteException("Fehler beim Lesen der Journale", e);
			}
		}

		public int size() throws RemoteException {
			return journaleList != null ? journaleList.size() : 0;
		}

		public GenericObject contains(GenericObject arg0) throws RemoteException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class JournalWrapper implements GenericObject {

		private Journal jrnl;
		
		public JournalWrapper(Journal j) {
			this.jrnl = j;
		}
		
		public Object getAttribute(String property) throws RemoteException {
			try {
				if ("ID".equals(property)) return jrnl.getID().toString();
				if ("Journalnummer".equals(property)) return jrnl.getJournalnummer();
				if ("Startdatum".equals(property)) return jrnl.getStartdatum();
				if ("Buchungsjahr".equals(property)) return jrnl.getBuchungsjahr();
				if ("Buchungsperiode".equals(property)) return jrnl.getBuchungsperiode();
				if ("absummiert".equals(property)) return new Boolean(jrnl.isAbsummiert());
			} catch (DatabaseException e) {
				throw new RemoteException("Fehler beim Lesen eines Journals", e);
			}
			return null;
		}

		public String[] getAttributeNames() throws RemoteException {
			return new String[] {
									"ID", 
									"Journalnummer", 
									"Startdatum",
									"Buchungsjahr",
									"Buchungsperiode",
									"absummiert"
								};
		}

		public String getID() throws RemoteException {
			try {
				return jrnl.getID().toString();
			} catch (DatabaseException e) {
				throw new RemoteException("Fehler beim Lesen eines Journals", e);
			}
		}

		public String getPrimaryAttribute() throws RemoteException {
			return "ID";
		}

		public boolean equals(GenericObject obj) throws RemoteException {
			return obj.getID().equals(getID());
		}
		
	}
}

/*
 *  $Log: JournaleControl.java,v $
 *  Revision 1.2  2005/08/18 14:08:13  phormanns
 *  Buchungsdialog begonnen
 *
 *  Revision 1.1  2005/08/18 11:24:12  phormanns
 *  Neue FiBu Version von Thomas
 *  Anzeige Journal-Listen
 *
 */
