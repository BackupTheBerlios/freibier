// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchung;

import net.hostsharing.admin.runtime.*;

public class BuchungUpdateCall extends AbstractCall {

	private BuchungData setData;
	private BuchungData whereData;

	public BuchungUpdateCall (
			   BuchungData setData
			,  BuchungData whereData
		)
	{
		super("buchung", "update");
		this.setData = setData;
		this.whereData = whereData;
	}
	
	public void prepare() {
		addSetProperty("belegnr", setData.getBelegnr());
		addSetProperty("buchungstext", setData.getBuchungstext());
		addSetProperty("jourid", setData.getJourid());
		addSetProperty("valuta", setData.getValuta());
		addWhereProperty("buchid", whereData.getBuchid());
		addWhereProperty("belegnr", whereData.getBelegnr());
		addWhereProperty("buchungstext", whereData.getBuchungstext());
		addWhereProperty("jourid", whereData.getJourid());
		addWhereProperty("valuta", whereData.getValuta());
		addWhereProperty("erfassung", whereData.getErfassung());
	}
}
