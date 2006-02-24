// $Id: MenuTreeModel.java,v 1.4 2006/02/24 22:24:22 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */

package de.jalin.fibu.gui.tree;

import java.text.DateFormat;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.forms.BuchungsForm;
import de.jalin.fibu.gui.forms.DummyForm;
import de.jalin.fibu.gui.forms.JournalTable;
import de.jalin.fibu.gui.forms.JournaleForm;
import de.jalin.fibu.gui.forms.KontenTreeForm;
import de.jalin.fibu.gui.forms.StammdatenForm;
import de.jalin.fibu.server.journal.JournalData;

public class MenuTreeModel implements TreeModel {

	private static final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
	
	private FiBuGUI gui;
	private StaticFolder rootFolder;
	private StaticFolder journaleFolder;
	private EventListenerList modelListeners;
	
	public MenuTreeModel(FiBuGUI gui) {
		modelListeners = new EventListenerList();
		rootFolder = new StaticFolder("FiBu", new StammdatenForm(gui));
		this.gui = gui;
		try {
			journaleFolder = new StaticFolder("Journale", new DummyForm("Journale"));
			journaleFolder.addFolder(new JournaleFolder(gui, "Offene Journale", true));
			journaleFolder.addFolder(new JournaleFolder(gui, "Alle Journale", false));
			rootFolder.addFolder(journaleFolder);
			StaticFolder kontenFolder = new StaticFolder("Konten-Hierarchie", new KontenTreeForm(gui));
			kontenFolder.addFolder(KontoNode.buildKontoTree(gui, kontenFolder));
			rootFolder.addFolder(kontenFolder);
			StaticFolder auswertungenFolder = new StaticFolder("Auswertungen", new DummyForm("Auswertungen"));
			rootFolder.addFolder(auswertungenFolder);
		} catch (FiBuException e) {
			gui.handleException(e);
		}
	}

	public int getChildCount(Object parent) {
		return ((TreeNode) parent).getChildCount();
	}

	public boolean isLeaf(Object node) {
		return ((TreeNode) node).isLeaf();
	}

	public Object getChild(Object parent, int index) {
		return ((TreeNode) parent).getChildAt(index);
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((TreeNode) parent).getIndex((TreeNode) child);
	}

	public void refreshJournale() {
		journaleFolder.removeFolders();
		JournaleFolder offeneJournaleFolder = new JournaleFolder(gui, "Offene Journale", true);
		journaleFolder.addFolder(offeneJournaleFolder);
		JournaleFolder alleJournaleFolder = new JournaleFolder(gui, "Alle Journale", false);
		journaleFolder.addFolder(alleJournaleFolder);
		EventListener[] listeners = modelListeners.getListeners(TreeModelListener.class);
		TreeModelListener modelListener = null;
		for (int i=0; i<listeners.length; i++) {
			modelListener = (TreeModelListener) listeners[i];
			modelListener.treeStructureChanged(
					new TreeModelEvent(
							journaleFolder, 
							new Object[] { rootFolder, journaleFolder, offeneJournaleFolder } ));
			modelListener.treeStructureChanged(
					new TreeModelEvent(
							journaleFolder, 
							new Object[] { rootFolder, journaleFolder, alleJournaleFolder } ));
		}
	}

	public Object getRoot() {
		return rootFolder;
	}

	public void addTreeModelListener(TreeModelListener l) {
		modelListeners.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		modelListeners.remove(TreeModelListener.class, l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	
	private final class JournaleFolder extends DynamicFolder {
		
		private FiBuGUI gui;
		private boolean nurOffene;
		
		private JournaleFolder(FiBuGUI gui, String title, boolean nurOffene) {
			super(title, new JournaleForm(gui, nurOffene));
			this.gui = gui;
			this.nurOffene = nurOffene;
		}

		public Vector readChildren() {
			Vector journale = new Vector();
			FiBuFacade fibu = gui.getFiBuFacade();
			try {
				if (nurOffene) {
					journale = fibu.getOffeneJournale();
				} else {
					journale = fibu.getAlleJournale();
				}
			} catch (FiBuException e) {
				gui.handleException(e);
			}
			Vector children = new Vector();
			Enumeration jourEnum = journale.elements();
			JournalData jour = null;
			Editable jourForm = null;
			while (jourEnum.hasMoreElements()) {
				jour = (JournalData) jourEnum.nextElement();
				if (nurOffene) {
					jourForm = new BuchungsForm(gui, jour);
				} else {
					jourForm = new JournalTable(gui, jour);
				}
				children.addElement(
						new LeafNode(
								jour.getPeriode() 
									+ "/" + jour.getJahr() 
									+ " ab: " + dateFormatter.format(jour.getSince()),
								jourForm
						));
			}
			return children;
		}
	}

}


//
// $Log: MenuTreeModel.java,v $
// Revision 1.4  2006/02/24 22:24:22  phormanns
// Copyright
// diverse Verbesserungen
//
// Revision 1.3  2005/11/23 23:16:49  phormanns
// Lesen Konto-Hierarchie und Buchungsliste optimiert
//
// Revision 1.2  2005/11/20 21:29:10  phormanns
// Umstellung auf XMLRPC Server
//
// Revision 1.1  2005/11/16 18:24:11  phormanns
// Exception Handling in GUI
// Refactorings, Focus-Steuerung
//
//