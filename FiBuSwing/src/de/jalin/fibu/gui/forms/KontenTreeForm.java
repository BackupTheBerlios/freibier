// $Id: KontenTreeForm.java,v 1.1 2005/11/10 21:19:26 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.gui.tree.KontoNode;

public class KontenTreeForm implements Editable {
	
	private FiBuFacade fibu;
	
	public KontenTreeForm(FiBuFacade fibu) {
		this.fibu = fibu;
	}

	public boolean validateAndSave() throws FiBuException {
		return true;
	}

	public Component getEditor() throws FiBuException {
		JPanel panel = new JPanel(new BorderLayout());
		JTree tree = new JTree(new KontoNode(null, fibu.getBilanzKonto()));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setPreferredSize(new Dimension(585, 585));
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}
}

/*
 *  $Log: KontenTreeForm.java,v $
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
