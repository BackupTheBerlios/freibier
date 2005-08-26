// $Id: TreeDialog.java,v 1.1 2005/08/26 17:40:46 phormanns Exp $
package de.bayen.fibu.gui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import de.bayen.fibu.gui.data.GenericObjectNode;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.util.ApplicationException;

public class TreeDialog extends AbstractDialog {
	
	GenericObjectNode root;
	Object selection;
	
	public TreeDialog(GenericObjectNode root, int position) {
		super(position);
		setSize(SWT.DEFAULT, 250);
		this.root = root;
	}

	protected void paint(Composite parent) throws Exception {
		TreePart tree = new TreePart(root, new Action() {

			public void handleAction(Object context) throws ApplicationException {
				selection = context;
				close();
			}
			
		});
		tree.paint(parent);
	}

	protected Object getData() throws Exception {
		return selection;
	}
}

/*
 *  $Log: TreeDialog.java,v $
 *  Revision 1.1  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 */
