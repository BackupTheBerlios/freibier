// $Id: DynamicFolder.java,v 1.5 2006/02/24 22:24:22 phormanns Exp $
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
// Revision 1.2  2005/11/10 21:19:26  phormanns
// Buchungsdialog begonnen
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//