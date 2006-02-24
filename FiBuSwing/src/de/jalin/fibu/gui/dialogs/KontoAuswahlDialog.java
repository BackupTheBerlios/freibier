// $Id: KontoAuswahlDialog.java,v 1.8 2006/02/24 22:24:22 phormanns Exp $
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
package de.jalin.fibu.gui.dialogs;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.forms.BetragListener;
import de.jalin.fibu.gui.tree.KontoNode;
import de.jalin.fibu.server.konto.KontoData;

public class KontoAuswahlDialog implements ActionListener, TreeSelectionListener {
	
	private FiBuGUI gui;
	private JDialog dialog;
	private JTextField tfKontoNr;
	private JTextField tfKontoText;
	private JTextField tfKontoMWSt;
	private BetragListener betragListener;

	public KontoAuswahlDialog(FiBuGUI gui, 
			JTextField tfKontoNr, JTextField tfKontoText, JTextField tfKontoMWSt, BetragListener betragListener) {
		this.gui = gui;
		this.betragListener = betragListener;
		this.tfKontoNr = tfKontoNr;
		this.tfKontoText = tfKontoText;
		this.tfKontoMWSt = tfKontoMWSt;
		JFrame mainWindow = gui.getFrame();
		Point locationOnScreen = mainWindow.getLocation();
		this.dialog = new JDialog(mainWindow, "Kontoauswahl Dialog", true);
		locationOnScreen.move(200, 150);
		this.dialog.setLocation(locationOnScreen);
		this.dialog.setSize(new Dimension(400, 300));
	}

	public void actionPerformed(ActionEvent selectKonto) {
		try {
			JTree tree = new JTree(KontoNode.buildKontoTree(gui, null));
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addTreeSelectionListener(this);
			dialog.getContentPane().add(tree);
			dialog.show();
		} catch (FiBuException e) {
			gui.handleException(e);
		}
	}

	public void valueChanged(TreeSelectionEvent treeSelection) {
		KontoNode node = (KontoNode) treeSelection.getNewLeadSelectionPath().getLastPathComponent();
		KontoData kto = node.getKonto();
		tfKontoNr.setText(kto.getKontonr());
		if (tfKontoText != null) {
			tfKontoText.setText(kto.getBezeichnung());
		}
		if (tfKontoMWSt != null) {
			try {
				tfKontoMWSt.setText(gui.getFiBuFacade().getMWSt(kto));
			} catch (FiBuException e) {
				gui.handleException(e);
			}
		}
		if (betragListener != null) {
			betragListener.berechneMWSt();
		}
		dialog.hide();
	}
	
}

/*
 *  $Log: KontoAuswahlDialog.java,v $
 *  Revision 1.8  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.7  2005/11/24 17:43:06  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.6  2005/11/23 23:16:49  phormanns
 *  Lesen Konto-Hierarchie und Buchungsliste optimiert
 *
 *  Revision 1.5  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.4  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.3  2005/11/11 21:40:35  phormanns
 *  Einstiegskonten im Stammdaten-Form
 *
 *  Revision 1.2  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
