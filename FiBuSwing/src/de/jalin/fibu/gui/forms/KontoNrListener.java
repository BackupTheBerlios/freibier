// $Id: KontoNrListener.java,v 1.3 2005/11/11 21:40:35 phormanns Exp $
package de.jalin.fibu.gui.forms;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.database.exception.UserDBEx.RecordNotExistsDBException;
import de.bayen.fibu.Konto;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;

public class KontoNrListener implements FocusListener {
	private FiBuFacade fibu;
	private JTextField tfKontoNr;
	private JTextField tfKontoText;
	private JTextField tfMWStSatz;

	public KontoNrListener(FiBuFacade fibu, JTextField tfKontoNr,
			JTextField tfKontoText, JTextField tfMWStSatz) {
		this.fibu = fibu;
		this.tfKontoNr = tfKontoNr;
		this.tfKontoText = tfKontoText;
		this.tfMWStSatz = tfMWStSatz;
	}

	public void focusGained(FocusEvent gotFocus) {
		writeKontoText("");
		writeMWStSatz("0");
	}

	public void focusLost(FocusEvent lostFocus) {
		try {
			Konto kto = fibu.getKonto(tfKontoNr.getText().trim());
			if (kto != null) {
				tfKontoNr.setText(kto.getKontonummer());
				writeKontoText(kto.getBezeichnung());
				writeMWStSatz(kto.getMwSt());
			} else {
				tfKontoNr.setText("");
				writeKontoText("");
				writeMWStSatz("0");
			}
		} catch (FiBuException e) {
			tfKontoNr.setText("");
			writeKontoText("");
			writeMWStSatz("0");
		} catch (SQL_DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordNotExistsDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeMWStSatz(String text) {
		if (tfMWStSatz != null) {
			tfMWStSatz.setText(text);
		}
	}

	private void writeKontoText(String text) {
		if (tfKontoText != null) {
			tfKontoText.setText(text);
		}
	}

}
/*
 *  $Log: KontoNrListener.java,v $
 *  Revision 1.3  2005/11/11 21:40:35  phormanns
 *  Einstiegskonten im Stammdaten-Form
 *
 *  Revision 1.2  2005/11/11 19:46:26  phormanns
 *  MWSt-Berechnung im Buchungsdialog
 *
 *  Revision 1.1  2005/11/11 13:25:55  phormanns
 *  Kontoauswahl im Buchungsdialog
 *
 */
