// $Id: KontoAuswahlDialog.java,v 1.5 2005/11/20 21:29:10 phormanns Exp $
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
import de.jalin.fibu.gui.tree.KontoNode;
import de.jalin.fibu.server.konto.KontoData;

public class KontoAuswahlDialog implements ActionListener, TreeSelectionListener {
	
	private FiBuGUI gui;
	private JDialog dialog;
	private JTextField tfKontoNr;
	private JTextField tfKontoText;
	private JTextField tfKontoMWSt;

	public KontoAuswahlDialog(FiBuGUI gui, 
			JTextField tfKontoNr, JTextField tfKontoText, JTextField tfKontoMWSt) {
		this.gui = gui;
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
		KontoData bilanzKonto;
		try {
			bilanzKonto = gui.getFiBuFacade().getBilanzKonto();
			JTree tree = new JTree(new KontoNode(gui, null, bilanzKonto));
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
		dialog.hide();
	}
	
}

/*
 *  $Log: KontoAuswahlDialog.java,v $
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
