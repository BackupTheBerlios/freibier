// $Id: KontoControl.java,v 1.2 2005/08/30 21:11:14 tbayen Exp $
package de.bayen.fibu.gui.control;

import de.bayen.database.exception.SysDBEx.SQL_DBException;
import de.bayen.fibu.Konto;
import de.bayen.fibu.gui.Settings;
import de.bayen.fibu.gui.data.KontoKnoten;
import de.bayen.fibu.gui.view.KontenView;
import de.bayen.fibu.gui.view.KontoView;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.IntegerInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.util.ApplicationException;

public class KontoControl extends AbstractControl {
	private KontoView view;
	private TextInput kontonummer;
	private TextInput bezeichnung;
	private IntegerInput mwst;
	private IntegerInput gewicht;

	public KontoControl(AbstractView view) {
		super(view);
		this.view = (KontoView) view;
	}

	public Input getKontonummer() {
		kontonummer = initTextField(
				kontonummer, 
				(String) ((KontoKnoten) view.getCurrentObject()).getAttribute("Kontonummer"), 
				4);
		return kontonummer;
	}

	public Input getBezeichnung() {
		bezeichnung = initTextField(
				bezeichnung, 
				(String) ((KontoKnoten) view.getCurrentObject()).getAttribute("Bezeichnung"), 
				20);
		return bezeichnung;
	}

	public IntegerInput getMWSt() {
		Object mwstObj = ((KontoKnoten) view.getCurrentObject())
				.getAttribute("MwSt");
		mwst = initIntegerField(mwst,
				mwstObj instanceof Long ? ((Long) mwstObj).intValue() : 0);
		return mwst;
	}

	public Input getGewicht() {
		Object gewichtObj;
		gewichtObj = ((KontoKnoten) view.getCurrentObject())
				.getAttribute("Gewicht");
		gewicht = initIntegerField(gewicht,
				gewichtObj instanceof Long ? ((Long) gewichtObj).intValue() : 0);
		return gewicht;
	}

	private TextInput initTextField(TextInput textField, String initValue,
			int maxLen) {
		if (textField == null) {
			textField = new TextInput(initValue, maxLen);
		}
		return textField;
	}

	private IntegerInput initIntegerField(IntegerInput intField, int initValue) {
		if (intField == null) {
			intField = new IntegerInput(initValue);
		}
		return intField;
	}

	public void saveKonto() throws ApplicationException {
		KontoKnoten kto = (KontoKnoten) view.getCurrentObject();
		Konto konto = kto.getKonto();
		try {
			konto.setKontonummer((String) kontonummer.getValue());
			konto.setBezeichnung((String) bezeichnung.getValue());
			konto.setMwSt(new Long(((Integer) mwst.getValue()).longValue()));
			konto.setGewicht(((Integer) gewicht.getValue()).intValue());
			konto.write();
			GUI.startView(KontenView.class, kto);
		} catch (SQL_DBException e) {
			throw new ApplicationException("Fehler beim Speichern des Kontos",e );
		}
	}

	public void createKonto() {
		KontoKnoten kto = (KontoKnoten) view.getCurrentObject();
		Konto konto = kto.getKonto();
		try {
			Konto neuesKonto = Settings.getFibuService().getFiBu().createKonto();
			neuesKonto.setOberkonto(konto);
			neuesKonto.write();
			KontoKnoten neuerKnoten = new KontoKnoten(neuesKonto);
			GUI.startView(KontoView.class, neuerKnoten);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/*
 *  $Log: KontoControl.java,v $
 *  Revision 1.2  2005/08/30 21:11:14  tbayen
 *  Neue Version der FiBu-Bibliothek mit kleiner API-Änderung eingespielt
 *
 *  Revision 1.1  2005/08/26 17:40:46  phormanns
 *  Anzeige der Kontenhierarchie, Anlegen von Unterkonten
 *
 */
