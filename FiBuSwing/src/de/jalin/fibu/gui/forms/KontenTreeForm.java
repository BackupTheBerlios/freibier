// $Id: KontenTreeForm.java,v 1.4 2006/02/24 22:24:22 phormanns Exp $
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
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.gui.tree.KontoNode;

public class KontenTreeForm implements Editable {
	
	private FiBuGUI gui;
	
	public KontenTreeForm(FiBuGUI gui) {
		this.gui = gui;
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		JPanel panel = new JPanel(new BorderLayout());
		try {
			JTree tree = new JTree(KontoNode.buildKontoTree(gui, null));
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			JScrollPane scroll = new JScrollPane(tree);
			scroll.setPreferredSize(new Dimension(585, 585));
			panel.add(scroll, BorderLayout.CENTER);
		} catch (FiBuException e) {
			gui.handleException(e);
		}
		return panel;
	}
}

/*
 *  $Log: KontenTreeForm.java,v $
 *  Revision 1.4  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.3  2005/11/23 23:16:49  phormanns
 *  Lesen Konto-Hierarchie und Buchungsliste optimiert
 *
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
