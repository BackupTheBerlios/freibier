// $Id: AboutView.java,v 1.1 2005/08/17 15:04:56 phormanns Exp $

package de.bayen.fibu.gui.view;

import de.bayen.fibu.gui.FiBuPlugin;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

public class AboutView extends AbstractView {

	public AboutView() {
		super();
	}

	public void bind() throws Exception {	
		GUI.getView().setTitle("Über das FiBu Plugin");
		LabelGroup group = new LabelGroup(getParent(), "Freibier FiBu Plugin");
		AbstractPlugin plugin = Application.getPluginLoader().getPlugin(FiBuPlugin.class);
		group.addLabelPair("Lizenz", new LabelInput("GPL (http://www.gnu.org/copyleft/gpl.html)"));
		group.addLabelPair("Copyright", new LabelInput("Peter Hormanns (peter.hormanns@jalin.de)"));
		group.addLabelPair("", new LabelInput("http://www.hormanns-wenz.de"));
		group.addLabelPair("Version", new LabelInput(""+plugin.getManifest().getVersion()));
		group.addLabelPair("Arbeitsverzeichnis", new LabelInput(""+plugin.getResources().getWorkPath()));
	}

	public void unbind() throws ApplicationException {
	}

}


//
// $Log: AboutView.java,v $
// Revision 1.1  2005/08/17 15:04:56  phormanns
// Start der Integration mit FiBu-Klassen (Firmenstammdaten)
//
//