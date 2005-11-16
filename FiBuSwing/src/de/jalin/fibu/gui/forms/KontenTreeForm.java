// $Id: KontenTreeForm.java,v 1.2 2005/11/16 18:24:11 phormanns Exp $
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
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.tree.Editable;
import de.jalin.fibu.gui.tree.KontoNode;

public class KontenTreeForm implements Editable {
	
	private FiBuFacade fibu;
	private FiBuGUI gui;
	
	public KontenTreeForm(FiBuGUI gui) {
		this.gui = gui;
		this.fibu = gui.getFiBuFacade();
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		JPanel panel = new JPanel(new BorderLayout());
		try {
			JTree tree = new JTree(new KontoNode(gui, null, fibu.getBilanzKonto()));
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
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
