// $Id: DynamicFolder.java,v 1.1 2005/11/09 22:28:33 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.tree.TreeNode;

public abstract class DynamicFolder implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private String title;
	private Vector nodeList;
	private long nodeListCreationTime;
	
	public DynamicFolder(String title) {
		this.parent = null;
		this.title = title;
		this.nodeListCreationTime = 0L;
		this.nodeList = null;
	}
	
	public abstract Vector readChildren();
	
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public int getChildCount() {
		updateNodeList();
		return nodeList.size();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return false;
	}

	public Enumeration children() {
		updateNodeList();
		return nodeList.elements();
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode getChildAt(int childIndex) {
		updateNodeList();
		return (TreeNode) nodeList.elementAt(childIndex);
	}

	public int getIndex(TreeNode node) {
		updateNodeList();
		return nodeList.indexOf(node);
	}

	public String toString() {
		return title;
	}

	private void updateNodeList() {
		long now = (new Date()).getTime();
		if (now - nodeListCreationTime > 2000L) {
			nodeList = readChildren();
			nodeListCreationTime = now;
		}
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
// $Log: DynamicFolder.java,v $
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//