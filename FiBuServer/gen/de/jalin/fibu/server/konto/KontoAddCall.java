package de.jalin.fibu.server.konto;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class KontoAddCall extends AbstractCall {

	private KontoData setData;

	public KontoAddCall (
			   KontoData setData
		)
	{
		super("konto", "add");
		this.setData = setData;
	}
	
	public void prepare() {
		addSetProperty("kontonr", setData.getKontonr());
		addSetProperty("bezeichnung", setData.getBezeichnung());
		addSetProperty("mwstid", setData.getMwstid());
		addSetProperty("oberkonto", setData.getOberkonto());
		addSetProperty("istsoll", setData.getIstsoll());
		addSetProperty("isthaben", setData.getIsthaben());
	}
}
