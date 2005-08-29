/**********************************************************************
 * $Source: /home/xubuntu/berlios_backup/github/tmp-cvs/freibier/Repository/FiBuGUI/src/de/bayen/fibu/gui/widget/TreeSelectInput.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/08/29 16:23:20 $
 * $Author: phormanns $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.bayen.fibu.gui.widget;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import de.bayen.fibu.gui.data.GenericObjectNode;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.ButtonInput;

/**
 * Eingabe-Feld, das beim Klick auf den Button einen Dialog zur Auswahl
 * eines Datums oeffnet.
 */
public class TreeSelectInput extends ButtonInput implements Listener {
	
	private Text text;
	private TreeDialog dialog;
	private Object choosen;
	private GenericObjectNode rootNode;

	/**
	 * Erzeugt ein neues Eingabefeld und schreibt den uebergebenen Wert rein.
	 * @param defaultValue der initial einzufuegende Wert fuer das Eingabefeld.
	 * @param d der Dialog.
	 */
	public TreeSelectInput(GenericObjectNode rootNode) {
		this.choosen = rootNode;
		this.rootNode = rootNode;
		this.value = (String) this.rootNode.getAttribute(this.rootNode.getPrimaryAttribute());
		this.dialog = new TreeDialog(this.rootNode, TreeDialog.POSITION_MOUSE);
		addButtonListener(new Listener() {
			public void handleEvent(Event event) {
				try {
					choosen = dialog.open();
					text.redraw();
					text.forceFocus(); // das muessen wir machen, damit die Listener ausgeloest werden
				} catch (Exception e1) {
				}
			}
		});
		dialog.addCloseListener(this);
	}

	/**
	 * Liefert das Objekt, welches in dem Dialog ausgewaehlt wurde.
	 * Fuer gewoehnlich ist das ein Fach-Objekt.
	 * @see de.willuhn.jameica.gui.input.Input#getValue()
	 */
	public Object getValue() {
		return choosen;
	}

	/**
	 * Liefert den derzeit angezeigten Text.
	 * @return angezeigter Text.
	 */
	public String getText() {
		if (text != null && !text.isDisposed())
			return text.getText();
		return value;
	}

	/**
	 * Speichert den anzuzeigenden Text.
	 * @param text anzuzeigender Text.
	 */
	public void setText(String text) {
		if (text == null)
			return;
		if (this.text != null && !this.text.isDisposed())
			this.text.setText(text);
	}

	/**
	 * @see de.willuhn.jameica.gui.input.Input#setValue(java.lang.Object)
	 * Speichert jedoch nicht den anzuzeigenden Text sondern das FachObjekt.
	 * Sprich: Das Objekt, welches auch geliefert wird, wenn der Dialog
	 * zur Auswahl des Objektes verwendet werden wuerde.
	 * Soll der anzuzeigende Text geaendert werden, dann bitte die
	 * Funktion <code>setText(String)</code> verwenden.
	 */
	public void setValue(Object value) {
		this.choosen = value;
	}

	/**
	 * @see de.willuhn.jameica.gui.input.ButtonInput#getClientControl(org.eclipse.swt.widgets.Composite)
	 */
	public Control getClientControl(Composite parent) {
		text = GUI.getStyleFactory().createText(parent);
		if (value != null)
			text.setText(value);
		return text;
	}

	public void handleEvent(Event event) {
		this.choosen = event.data;
		GenericObjectNode node = (GenericObjectNode) this.choosen;
		this.text.setText((String) node.getAttribute(node.getPrimaryAttribute()));
	}
}
/*********************************************************************
 * $Log: TreeSelectInput.java,v $
 * Revision 1.3  2005/08/29 16:23:20  phormanns
 * Jameica JAR-Dateien eingecheckt
 *
 * Revision 1.2  2005/08/26 19:19:44  phormanns
 * Hierarchie-Auswahl für erstes Konto im Buchungsdialog
 *
 * Revision 1.1  2005/08/26 17:40:46  phormanns
 * Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 * Revision 1.1  2005/08/21 20:18:56  phormanns
 * Erste Widgets für Buchen-Dialog
 *
 **********************************************************************/
