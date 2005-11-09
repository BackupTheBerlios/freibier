// $Id: Editable.java,v 1.1 2005/11/09 22:28:33 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;

public interface Editable {

	public abstract boolean validateAndSave();
	
	public abstract Component getEditor();
	
}


//
// $Log: Editable.java,v $
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//