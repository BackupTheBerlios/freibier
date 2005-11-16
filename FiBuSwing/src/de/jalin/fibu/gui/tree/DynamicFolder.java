// $Id: DynamicFolder.java,v 1.4 2005/11/16 18:24:11 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

public abstract class DynamicFolder implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private String title;
	private Editable editor;
	private Vector nodeList;
	private long nodeListCreationTime;
	
	public DynamicFolder(String title, Editable editor) {
		this.parent = null;
		this.title = title;
		this.editor = editor;
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
	
	public void refresh() {
		updateNodeList();
	}

	public boolean validateAndSave() {
		return editor.validateAndSave();
	}
	
	public Component getEditor() {
		return editor.getEditor();
	}

}


//
// $Log: DynamicFolder.java,v $
// Revision 1.4  2005/11/16 18:24:11  phormanns
// Exception Handling in GUI
// Refactorings, Focus-Steuerung
//
// Revision 1.3  2005/11/15 21:20:36  phormanns
// Refactorings in FiBuGUI
// Focus und Shortcuts in BuchungsForm und StammdatenForm
//
// Revision 1.2  2005/11/10 21:19:26  phormanns
// Buchungsdialog begonnen
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//