// $Id: LeafNode.java,v 1.1 2005/11/09 22:28:33 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.tree.TreeNode;

public class LeafNode implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private String title;
	
	public LeafNode(String title) {
		this.parent = null;
		this.title = title;
	}
	
	public int getChildCount() {
		return 0;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public Enumeration children() {
		return null;
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	public int getIndex(TreeNode node) {
		return 0;
	}

	public String toString() {
		return title;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public boolean validateAndSave() {
		return true;
	}
	
	public Component getEditor() {
		// TODO Auto-generated method stub
		return new JButton(title);
	}

}


//
// $Log: LeafNode.java,v $
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//