// $Id: StaticFolder.java,v 1.1 2005/11/09 22:28:33 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.tree.TreeNode;

public class StaticFolder implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private Vector nodeList;
	private String title;
	
	public StaticFolder(String title) {
		this.parent = null;
		this.title = title;
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

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		// TODO Auto-generated method stub
		return new JLabel(title);
	}

}


//
// $Log: StaticFolder.java,v $
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//