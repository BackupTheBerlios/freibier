package de.jalin.fibu.server.buchungszeile;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungszeileListCall extends AbstractCall {

	private BuchungszeileData whereData;

	public BuchungszeileListCall (
			   BuchungszeileData whereData
		)
	{
		super("buchungszeile", "list");
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
