// $Id: KontoNode.java,v 1.3 2005/11/15 21:20:36 phormanns Exp $
package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.forms.KontoTable;

public class KontoNode implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private Konto kto;
	private Vector children;
	private KontoTable ktoTable;
	
	public KontoNode(TreeNode parent, Konto konto) {
		this.parent = parent;
		this.kto = konto;
		readChildren();
		this.ktoTable = new KontoTable(kto);
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
		return kto.getKontonummer() + " - " + kto.getBezeichnung();
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public boolean validateAndSave() throws FiBuException {
		return ktoTable.validateAndSave();
	}

	public Component getEditor() throws FiBuException {
		return ktoTable.getEditor();
	}
	
	public Konto getKonto() {
		return kto;
	}

	public void refresh() {
		readChildren();
	}

	private void readChildren() {
		this.children = new Vector();
		try {
			Iterator unterkonten = this.kto.getUnterkonten().iterator();
			Konto unterKto = null;
			while (unterkonten.hasNext()) {
				unterKto = (Konto) unterkonten.next();
				children.addElement(new KontoNode(this, unterKto));
			}
		} catch (SQL_DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/*
 *  $Log: KontoNode.java,v $
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
