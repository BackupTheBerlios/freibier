// $Id: KontoAuswahlDialog.java,v 1.3 2005/11/11 21:40:35 phormanns Exp $
package de.jalin.fibu.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.gui.tree.KontoNode;

public class KontoAuswahlDialog implements ActionListener, TreeSelectionListener {
	
	private FiBuFacade fibu;
	private JDialog dialog;
	private JTextField tfKontoNr;
	private JTextField tfKontoText;
	private JTextField tfKontoMWSt;

	public KontoAuswahlDialog(FiBuFacade fibu, JTextField tfKontoNr, JTextField tfKontoText, JTextField tfKontoMWSt) {
		this.fibu = fibu;
		this.tfKontoNr = tfKontoNr;
		this.tfKontoText = tfKontoText;
		this.tfKontoMWSt = tfKontoMWSt;
		this.dialog = new JDialog();
		this.dialog.setSize(new Dimension(300, 200));
	}

	public void actionPerformed(ActionEvent selectKonto) {
		Konto bilanzKonto;
		try {
			bilanzKonto = fibu.getBilanzKonto();
			JTree tree = new JTree(new KontoNode(null, bilanzKonto));
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.addTreeSelectionListener(this);
			dialog.getContentPane().add(tree);
			dialog.show();
		} catch (FiBuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void valueChanged(TreeSelectionEvent treeSelection) {
		KontoNode node = (KontoNode) treeSelection.getNewLeadSelectionPath().getLastPathComponent();
		Konto kto = node.getKonto();
		try {
			tfKontoNr.setText(kto.getKontonummer());
			if (tfKontoText != null) {
				tfKontoText.setText(kto.getBezeichnung());
			}
			if (tfKontoMWSt != null) {
				tfKontoMWSt.setText(kto.getMwSt());
			}
		} catch (SQL_DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordNotExistsDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialog.hide();
	}
	
}

/*
 *  $Log: KontoAuswahlDialog.java,v $
 *  Revision 1.3  2005/11/11 21:40:35  phormanns
 *  Einstiegskonten im Stammdaten-Form
 *
 *  Revision 1.2  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 *  Revision 1.1  2005/11/10 21:19:26  phormanns
 *  Buchungsdialog begonnen
 *
 */
