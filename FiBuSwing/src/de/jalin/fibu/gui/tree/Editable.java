// $Id: Editable.java,v 1.2 2005/11/10 12:22:27 phormanns Exp $

package de.jalin.fibu.gui.tree;

import java.awt.Component;
import de.jalin.fibu.gui.FiBuException;

public interface Editable {

	public abstract boolean validateAndSave() throws FiBuException;
	
	public abstract Component getEditor() throws FiBuException;
	
}


//
// $Log: Editable.java,v $
// Revision 1.2  2005/11/10 12:22:27  phormanns
// Erste Form tut was
//
// Revision 1.1  2005/11/09 22:28:33  phormanns
// erster Import
//
//