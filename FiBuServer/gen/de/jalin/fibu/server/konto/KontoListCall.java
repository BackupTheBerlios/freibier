// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import net.hostsharing.admin.runtime.*;

public class KontoListCall extends AbstractCall {

	private KontoData whereData;

	public KontoListCall (
			   KontoData whereData
		)
	{
		super("konto", "list");
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
		addWhereProperty("istaktiv", whereData.getIstaktiv());
		addWhereProperty("istpassiv", whereData.getIstpassiv());
		addWhereProperty("istaufwand", whereData.getIstaufwand());
		addWhereProperty("istertrag", whereData.getIstertrag());
	}
}
