package de.jalin.fibu.server.konto;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class KontoDeleteCall extends AbstractCall {

	private KontoData whereData;

	public KontoDeleteCall (
			   KontoData whereData
		)
	{
		super("konto", "delete");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("kontoid", whereData.getKontoid());
		addWhereProperty("kontonr", whereData.getKontonr());
		addWhereProperty("bezeichnung", whereData.getBezeichnung());
		addWhereProperty("mwstid", whereData.getMwstid());
		addWhereProperty("oberkonto", whereData.getOberkonto());
		addWhereProperty("istsoll", whereData.getIstsoll());
		addWhereProperty("isthaben", whereData.getIsthaben());
	}
}
