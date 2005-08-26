/**********************************************************************
 * $Source: /home/xubuntu/berlios_backup/github/tmp-cvs/freibier/Repository/FiBuGUI/src/de/bayen/fibu/gui/widget/ListSelectInput.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/26 19:19:44 $
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
import de.bayen.fibu.GenericObject;
import de.bayen.fibu.gui.data.ListIterator;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.input.ButtonInput;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Eingabe-Feld, das beim Klick auf den Button einen Dialog zur Auswahl
 * eines Datums oeffnet.
 */
public class ListSelectInput extends ButtonInput implements Listener {
	
	private Text text;
	private ListDialog dialog;
	private Object choosen;
	private ListIterator iterator;

	/**
	 * Erzeugt ein neues Eingabefeld und schreibt den uebergebenen Wert rein.
	 * @param defaultValue der initial einzufuegende Wert fuer das Eingabefeld.
	 * @param d der Dialog.
	 * @throws ApplicationException 
	 */
	public ListSelectInput(ListIterator iterator) throws ApplicationException {
		this.choosen = null;
		this.iterator = iterator;
		this.iterator.begin();
		this.value = "";
		if (iterator.hasNext()) {
			GenericObject obj = iterator.next();
			this.value = obj.toString();
		}
		this.dialog = new ListDialog(this.iterator, ListDialog.POSITION_MOUSE);
		addButtonListener(new Listener() {
			public void handleEvent(Event event) {
				Logger.debug("starting dialog");
				try {
					choosen = dialog.open();
					text.redraw();
					text.forceFocus(); // das muessen wir machen, damit die Listener ausgeloest werden
				} catch (Exception e1) {
					Logger.error("error while opening dialog", e1);
				}
			}
		});
		dialog.addCloseListener(this);
	}

	public void addColumn(String text, String propertyName) {
		dialog.addColumn(text, propertyName);
	}
	
	public void addColumn(String text, String propertyName, Formatter formatter) {
		dialog.addColumn(text, propertyName, formatter);
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
		GenericObject node = (GenericObject) this.choosen;
		this.text.setText(node.toString());
	}
}
/*********************************************************************
 * $Log: ListSelectInput.java,v $
 * Revision 1.1  2005/08/26 19:19:44  phormanns
 * Hierarchie-Auswahl für erstes Konto im Buchungsdialog
 *
 * Revision 1.1  2005/08/26 17:40:46  phormanns
 * Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 * Revision 1.1  2005/08/21 20:18:56  phormanns
 * Erste Widgets für Buchen-Dialog
 *
 **********************************************************************/
