// $Id: KontoAuswahlDialog.java,v 1.1 2005/11/10 21:19:26 phormanns Exp $
package de.jalin.fibu.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.tree.KontoNode;

public class KontoAuswahlDialog implements ActionListener, TreeSelectionListener {
	
	private FiBuFacade fibu;
	private JDialog dialog;
	private JTextField tfKonto;

	public KontoAuswahlDialog(FiBuFacade fibu, JTextField tfKonto) {
		this.fibu = fibu;
		this.tfKonto = tfKonto;
		this.dialog = new JDialog();
		this.dialog.setSize(new Dimension(300, 200));
	}

	public void actionPerformed(ActionEvent selectKonto) {
		Konto bilanzKonto;
		try {
			bilanzKonto = fibu.getBilanzKonto();
			JTree tree = new JTree(new KontoNode(null, bilanzKonto));
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addTreeSelectionListener(this);
			dialog.getContentPane().add(tree);
			dialog.show();
		} catch (FiBuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void valueChanged(TreeSelectionEvent treeSelection) {
		KontoNode node = (KontoNode) treeSelection.getNewLeadSelectionPath().getLastPathComponent();
		Konto kto = node.getKonto();
		tfKonto.setText(kto.getKontonummer() + " - " + kto.getBezeichnung());
		dialog.hide();
	}
	
	
}

/*
 *  $Log: KontoAuswahlDialog.java,v $
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
