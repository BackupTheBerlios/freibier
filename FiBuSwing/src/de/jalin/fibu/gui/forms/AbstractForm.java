// $Id: AbstractForm.java,v 1.1 2005/11/16 18:24:11 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import de.jalin.fibu.gui.tree.Editable;

public abstract class AbstractForm implements Editable {

	public boolean validateAndSave() {
		return true;
	}

	public abstract Component getEditor();
	
	public JTextField createTextField(String text, boolean editable) {
		JTextField tf = new JTextField(text);
		tf.setEditable(editable);
		tf.setFocusable(editable);
		tf.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				JTextField field = (JTextField) e.getComponent();
				field.setSelectionStart(0);
				field.setSelectionEnd(field.getText().length());
			}

			public void focusLost(FocusEvent e) {
			}
		});
		return tf;
	}
}

/*
 *  $Log: AbstractForm.java,v $
 *  Revision 1.1  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
