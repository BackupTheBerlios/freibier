// $Id: KontoNrListener.java,v 1.7 2006/02/24 22:24:22 phormanns Exp $
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import de.jalin.fibu.gui.FiBuException;
import de.jalin.fibu.gui.FiBuFacade;
import de.jalin.fibu.server.konto.KontoData;

public class KontoNrListener implements FocusListener {
	
	private FiBuFacade fibu;
	private JTextField tfKontoNr;
	private JTextField tfKontoText;
	private JTextField tfMWStSatz;
	private BetragListener betragListener;

	public KontoNrListener(FiBuFacade fibu, JTextField tfKontoNr,
			JTextField tfKontoText, JTextField tfMWStSatz, BetragListener betragListener) {
		this.fibu = fibu;
		this.tfKontoNr = tfKontoNr;
		this.tfKontoText = tfKontoText;
		this.tfMWStSatz = tfMWStSatz;
		this.betragListener = betragListener;
	}

	public void focusGained(FocusEvent gotFocus) {
		writeKontoText("");
		writeMWStSatz("0");
	}

	public void focusLost(FocusEvent lostFocus) {
		try {
			KontoData kto = fibu.getKonto(tfKontoNr.getText().trim());
			if (kto != null) {
				tfKontoNr.setText(kto.getKontonr());
				writeKontoText(kto.getBezeichnung());
				writeMWStSatz(fibu.getMWSt(kto));
				if (betragListener != null) betragListener.berechneMWSt();
			} else {
				tfKontoNr.setText("");
				writeKontoText("");
				writeMWStSatz("0");
			}
		} catch (FiBuException e) {
			tfKontoNr.setText("");
			writeKontoText("");
			writeMWStSatz("0");
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
 *  Revision 1.7  2006/02/24 22:24:22  phormanns
 *  Copyright
 *  diverse Verbesserungen
 *
 *  Revision 1.6  2005/11/24 17:43:05  phormanns
 *  Buchen als eine Transaktion in der "Buchungsmaschine"
 *
 *  Revision 1.5  2005/11/20 21:29:10  phormanns
 *  Umstellung auf XMLRPC Server
 *
 *  Revision 1.4  2005/11/16 18:24:11  phormanns
 *  Exception Handling in GUI
 *  Refactorings, Focus-Steuerung
 *
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
