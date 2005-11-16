// $Id: Editable.java,v 1.3 2005/11/16 18:24:11 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;

public interface Editable {

	public abstract boolean validateAndSave();
	
	public abstract Component getEditor();
	
}


//
// $Log: Editable.java,v $
// Revision 1.3  2005/11/16 18:24:11  phormanns
// Exception Handling in GUI
// Refactorings, Focus-Steuerung
//
// Revision 1.2  2005/11/10 12:22:27  phormanns
// Erste Form tut was
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//