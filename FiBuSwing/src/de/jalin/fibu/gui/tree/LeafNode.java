// $Id: LeafNode.java,v 1.5 2006/02/24 22:24:22 phormanns Exp $
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

import javax.swing.tree.TreeNode;

public class LeafNode implements TreeNode, Adoptable, Editable {

	private TreeNode parent;
	private String title;
	private Editable editor;
	
	public LeafNode(String title, Editable editor) {
		this.parent = null;
		this.title = title;
		this.editor = editor;
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
		return editor.validateAndSave();
	}
	
	public Component getEditor() {
		return editor.getEditor();
	}

	public void refresh() {
	}

}


//
// $Log: LeafNode.java,v $
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