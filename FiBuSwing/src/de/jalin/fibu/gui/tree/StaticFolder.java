// $Id: StaticFolder.java,v 1.5 2006/02/24 22:24:22 phormanns Exp $
/* 
 * HSAdmin - hostsharing.net Paketadministration
 * Copyright (C) 2005, 2006 Peter Hormanns                               
 *                                                                
 * This program is free software; you can redistribute it and/or  
 * modify it under the terms of the GNU General Public License    
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.         
 *                                                                 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.                   
 *                                                                 
 * You should have received a copy of the GNU General Public      
 * License along with this program; if not, write to the Free      
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.                                                                                        
 */

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

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
	
	public void removeFolders() {
		this.nodeList = new Vector();
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
		return editor.validateAndSave();
	}

	public Component getEditor() {
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
// Revision 1.5  2006/02/24 22:24:22  phormanns
// Copyright
// diverse Verbesserungen
//
// Revision 1.4  2005/11/16 18:24:11  phormanns
// Exception Handling in GUI
// Refactorings, Focus-Steuerung
//
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