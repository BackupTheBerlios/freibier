// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsliste;

import net.hostsharing.admin.runtime.*;

public class BuchungslisteListCall extends AbstractCall {

	private BuchungslisteData whereData;

	public BuchungslisteListCall (
			   BuchungslisteData whereData
		)
	{
		super("buchungsliste", "list");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("buzlid", whereData.getBuzlid());
		addWhereProperty("soll", whereData.getSoll());
		addWhereProperty("haben", whereData.getHaben());
		addWhereProperty("buchid", whereData.getBuchid());
		addWhereProperty("belegnr", whereData.getBelegnr());
		addWhereProperty("buchungstext", whereData.getBuchungstext());
		addWhereProperty("jourid", whereData.getJourid());
		addWhereProperty("journr", whereData.getJournr());
		addWhereProperty("jahr", whereData.getJahr());
		addWhereProperty("periode", whereData.getPeriode());
		addWhereProperty("absummiert", whereData.getAbsummiert());
		addWhereProperty("kontoid", whereData.getKontoid());
		addWhereProperty("kontonr", whereData.getKontonr());
		addWhereProperty("istsoll", whereData.getIstsoll());
		addWhereProperty("isthaben", whereData.getIsthaben());
		addWhereProperty("istaktiv", whereData.getIstaktiv());
		addWhereProperty("istpassiv", whereData.getIstpassiv());
		addWhereProperty("istaufwand", whereData.getIstaufwand());
		addWhereProperty("istertrag", whereData.getIstertrag());
	}
}
