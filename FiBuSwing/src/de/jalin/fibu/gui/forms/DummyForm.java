// $Id: DummyForm.java,v 1.1 2005/11/10 12:22:27 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import javax.swing.JLabel;
import de.jalin.fibu.gui.tree.Editable;

public class DummyForm implements Editable {

	private String label;
	
	public DummyForm(String text) {
		label = text;
	}

	public boolean validateAndSave() {
		return true;
	}

	public Component getEditor() {
		return new JLabel(label);
	}
}

/*
 *  $Log: DummyForm.java,v $
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
