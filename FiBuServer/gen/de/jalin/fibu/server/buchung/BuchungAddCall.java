// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchung;

import net.hostsharing.admin.runtime.*;

public class BuchungAddCall extends AbstractCall {

	private BuchungData setData;

	public BuchungAddCall (
			   BuchungData setData
		)
	{
		super("buchung", "add");
		this.setData = setData;
	}
	
	public void prepare() {
		addSetProperty("belegnr", setData.getBelegnr());
		addSetProperty("buchungstext", setData.getBuchungstext());
		addSetProperty("jourid", setData.getJourid());
		addSetProperty("valuta", setData.getValuta());
	}
}
