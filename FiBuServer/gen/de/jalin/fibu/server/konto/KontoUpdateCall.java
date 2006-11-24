// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import net.hostsharing.admin.runtime.*;

public class KontoUpdateCall extends AbstractCall {

	private KontoData setData;
	private KontoData whereData;

	public KontoUpdateCall (
			   KontoData setData
			,  KontoData whereData
		)
	{
		super("konto", "update");
		this.setData = setData;
		this.whereData = whereData;
	}
	
	public void prepare() {
		addSetProperty("kontonr", setData.getKontonr());
		addSetProperty("bezeichnung", setData.getBezeichnung());
		addSetProperty("mwstid", setData.getMwstid());
		addSetProperty("oberkonto", setData.getOberkonto());
		addSetProperty("istsoll", setData.getIstsoll());
		addSetProperty("isthaben", setData.getIsthaben());
		addSetProperty("istaktiv", setData.getIstaktiv());
		addSetProperty("istpassiv", setData.getIstpassiv());
		addSetProperty("istaufwand", setData.getIstaufwand());
		addSetProperty("istertrag", setData.getIstertrag());
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
