package de.jalin.fibu.server.buchungszeile;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungszeileDeleteCall extends AbstractCall {

	private BuchungszeileData whereData;

	public BuchungszeileDeleteCall (
			   BuchungszeileData whereData
		)
	{
		super("buchungszeile", "delete");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("buzlid", whereData.getBuzlid());
		addWhereProperty("buchid", whereData.getBuchid());
		addWhereProperty("kontoid", whereData.getKontoid());
		addWhereProperty("betrag", whereData.getBetrag());
		addWhereProperty("soll", whereData.getSoll());
		addWhereProperty("haben", whereData.getHaben());
	}
}
