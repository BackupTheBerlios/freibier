package de.jalin.fibu.server.buchung;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungListCall extends AbstractCall {

	private BuchungData whereData;

	public BuchungListCall (
			   BuchungData whereData
		)
	{
		super("buchung", "list");
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
