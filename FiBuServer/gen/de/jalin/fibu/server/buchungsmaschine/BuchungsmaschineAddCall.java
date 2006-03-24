// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsmaschine;

import net.hostsharing.admin.runtime.*;

public class BuchungsmaschineAddCall extends AbstractCall {

	private BuchungsmaschineData setData;

	public BuchungsmaschineAddCall (
			   BuchungsmaschineData setData
		)
	{
		super("buchungsmaschine", "add");
		this.setData = setData;
	}
	
	public void prepare() {
		addSetProperty("sollkontonr", setData.getSollkontonr());
		addSetProperty("habenkontonr", setData.getHabenkontonr());
		addSetProperty("sollmwstid", setData.getSollmwstid());
		addSetProperty("habenmwstid", setData.getHabenmwstid());
		addSetProperty("brutto", setData.getBrutto());
		addSetProperty("belegnr", setData.getBelegnr());
		addSetProperty("buchungstext", setData.getBuchungstext());
		addSetProperty("jourid", setData.getJourid());
		addSetProperty("valuta", setData.getValuta());
	}
}
