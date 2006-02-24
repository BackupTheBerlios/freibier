// $Id: Adoptable.java,v 1.3 2006/02/24 22:24:22 phormanns Exp $
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

import javax.swing.tree.TreeNode;



public interface Adoptable {

	public abstract void setParent(TreeNode parent);

	public abstract void refresh();
}

//
// $Log: Adoptable.java,v $
// Revision 1.3  2006/02/24 22:24:22  phormanns
// Copyright
// diverse Verbesserungen
//
// Revision 1.2  2005/11/15 21:20:36  phormanns
// Refactorings in FiBuGUI
// Focus und Shortcuts in BuchungsForm und StammdatenForm
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//