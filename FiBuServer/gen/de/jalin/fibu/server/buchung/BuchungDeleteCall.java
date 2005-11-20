package de.jalin.fibu.server.buchung;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungDeleteCall extends AbstractCall {

	private BuchungData whereData;

	public BuchungDeleteCall (
			   BuchungData whereData
		)
	{
		super("buchung", "delete");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("buchid", whereData.getBuchid());
		addWhereProperty("belegnr", whereData.getBelegnr());
		addWhereProperty("buchungstext", whereData.getBuchungstext());
		addWhereProperty("jourid", whereData.getJourid());
		addWhereProperty("valuta", whereData.getValuta());
		addWhereProperty("erfassung", whereData.getErfassung());
	}
}
