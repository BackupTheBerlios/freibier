// $Id: KontoNode.java,v 1.5 2005/11/20 21:29:10 phormanns Exp $
package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuGUI;
import de.jalin.fibu.gui.forms.KontoTable;
import de.jalin.fibu.server.konto.KontoData;

public class KontoNode implements TreeNode, Adoptable, Editable {

	private FiBuGUI gui;
	private TreeNode parent;
	private KontoData kto;
	private Vector children;
	private KontoTable ktoTable;
	
	public KontoNode(FiBuGUI gui, TreeNode parent, KontoData konto) {
		this.gui = gui;
		this.parent = parent;
		this.kto = konto;
		readChildren();
		this.ktoTable = new KontoTable(gui, kto);
	}

	public int getChildCount() {
		return children.size();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return (getChildCount() == 0);
	}

	public Enumeration children() {
		return children.elements();
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode getChildAt(int index) {
		return (TreeNode) children.get(index);
	}

	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}
	
	public String toString() {
		return kto.getKontonr() + " - " + kto.getBezeichnung();
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public boolean validateAndSave() {
		return ktoTable.validateAndSave();
	}

	public Component getEditor() {
		return ktoTable.getEditor();
	}
	
	public KontoData getKonto() {
		return kto;
	}

	public void refresh() {
		readChildren();
	}

	private void readChildren() {
		children = new Vector();
		try {
			Iterator unterkonten = gui.getFiBuFacade().getUnterkonten(kto).iterator();
			KontoData unterKto = null;
			while (unterkonten.hasNext()) {
				unterKto = (KontoData) unterkonten.next();
				children.addElement(new KontoNode(gui, this, unterKto));
			}
		} catch (FiBuException e) {
			gui.handleException(e);
		}
	}

}

/*
 *  $Log: KontoNode.java,v $
 *  Revision 1.5  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.4  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.3  2005/11/15 21:20:36  phormanns
 *  Refactorings in FiBuGUI
 *  Focus und Shortcuts in BuchungsForm und StammdatenForm
 *
 *  Revision 1.2  2005/11/12 11:44:46  phormanns
 *  KontoTabelle zeigt Buchungen zum Konto
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
