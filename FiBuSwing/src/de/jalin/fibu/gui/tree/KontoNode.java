// $Id: KontoNode.java,v 1.1 2005/11/10 21:19:26 phormanns Exp $
package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.tree.TreeNode;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;

public class KontoNode implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private Konto kto;
	private Vector children;
	
	public KontoNode(TreeNode parent, Konto konto) {
		this.parent = parent;
		this.kto = konto;
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
		// TODO Auto-generated method stub
		return true;
	}

	public Component getEditor() throws FiBuException {
		// TODO Auto-generated method stub
		return new JLabel("Konto Dummy");
	}
	
	public Konto getKonto() {
		return kto;
	}
}

/*
 *  $Log: KontoNode.java,v $
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
