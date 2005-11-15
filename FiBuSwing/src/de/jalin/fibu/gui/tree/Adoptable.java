// $Id: Adoptable.java,v 1.2 2005/11/15 21:20:36 phormanns Exp $

package de.jalin.fibu.gui.tree;

import javax.swing.tree.TreeNode;



public interface Adoptable {

	public abstract void setParent(TreeNode parent);

	public abstract void refresh();
}

//
// $Log: Adoptable.java,v $
// Revision 1.2  2005/11/15 21:20:36  phormanns
// Refactorings in FiBuGUI
// Focus und Shortcuts in BuchungsForm und StammdatenForm
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//