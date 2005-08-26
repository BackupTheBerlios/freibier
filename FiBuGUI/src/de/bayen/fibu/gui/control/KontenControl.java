// $Id: KontenControl.java,v 1.2 2005/08/26 20:48:47 phormanns Exp $
package de.bayen.fibu.gui.control;

import de.bayen.fibu.Konto;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.data.KontoKnoten;
import de.bayen.fibu.gui.view.KontoView;
import de.bayen.fibu.gui.widget.TreePart;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class KontenControl extends AbstractControl {

	public KontenControl(AbstractView view) {
		super(view);
	}

	public TreePart getKontenBaum() throws Exception {
		Konto bilanzkonto;
		bilanzkonto = Settings.getFibuService().getBilanzkonto();
		KontoKnoten root = new KontoKnoten(bilanzkonto); 
		TreePart baum = new TreePart(root, new Action() {

			public void handleAction(Object context) throws ApplicationException {
				GUI.startView(KontoView.class, context);
			}
			
		});
		return baum;
	}
}

/*
 *  $Log: KontenControl.java,v $
 *  Revision 1.2  2005/08/26 20:48:47  phormanns
 *  Erste Buchung in der Datenbank
 *
 *  Revision 1.1  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 */
