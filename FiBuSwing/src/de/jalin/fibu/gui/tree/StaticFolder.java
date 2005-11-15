// $Id: StaticFolder.java,v 1.3 2005/11/15 21:20:36 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import de.jalin.fibu.gui.FiBuException;

public class StaticFolder implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private Vector nodeList;
	private String title;
	private Editable editor;
	
	public StaticFolder(String title, Editable editor) {
		this.parent = null;
		this.title = title;
		this.editor = editor;
		this.nodeList = new Vector();
	}
	
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public void addFolder(Adoptable folder) {
		folder.setParent(this);
		nodeList.addElement(folder);
	}
	
	public int getChildCount() {
		return nodeList.size();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return false;
	}

	public Enumeration children() {
		return nodeList.elements();
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) nodeList.elementAt(childIndex);
	}

	public int getIndex(TreeNode node) {
		return nodeList.indexOf(node);
	}

	public String toString() {
		return title;
	}

	public boolean validateAndSave() throws FiBuException {
		return editor.validateAndSave();
	}

	public Component getEditor() throws FiBuException {
		return editor.getEditor();
	}

	public void refresh() {
		Enumeration iterate = nodeList.elements();
		Adoptable node = null;
		while (iterate.hasMoreElements()) {
			node = (Adoptable) iterate.nextElement();
			node.refresh();
		}
	}

}


//
// $Log: StaticFolder.java,v $
// Revision 1.3  2005/11/15 21:20:36  phormanns
// Refactorings in FiBuGUI
// Focus und Shortcuts in BuchungsForm und StammdatenForm
//
// Revision 1.2  2005/11/10 12:22:27  phormanns
// Erste Form tut was
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//