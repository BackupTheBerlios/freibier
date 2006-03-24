// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungszeile;

import net.hostsharing.admin.runtime.*;

public class BuchungszeileAddCall extends AbstractCall {

	private BuchungszeileData setData;

	public BuchungszeileAddCall (
			   BuchungszeileData setData
		)
	{
		super("buchungszeile", "add");
		this.setData = setData;
	}
	
	public void prepare() {
		addSetProperty("buchid", setData.getBuchid());
		addSetProperty("kontoid", setData.getKontoid());
		addSetProperty("betrag", setData.getBetrag());
		addSetProperty("soll", setData.getSoll());
		addSetProperty("haben", setData.getHaben());
	}
}
