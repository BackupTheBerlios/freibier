// $Id: AbstractForm.java,v 1.2 2006/02/24 22:24:22 phormanns Exp $
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
 *  Revision 1.2  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.1  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
 *  Revision 1.1  2005/11/10 12:22:27  phormanns
 *  Erste Form tut was
 *
 */
