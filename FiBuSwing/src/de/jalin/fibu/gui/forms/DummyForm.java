// $Id: DummyForm.java,v 1.2 2005/11/16 18:24:11 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class DummyForm extends AbstractForm {

	private String label;
	
	public DummyForm(String text) {
		label = text;
	}

	public Component getEditor() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(label), BorderLayout.NORTH);
		return panel;
	}
}

/*
 *  $Log: DummyForm.java,v $
 *  Revision 1.2  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
