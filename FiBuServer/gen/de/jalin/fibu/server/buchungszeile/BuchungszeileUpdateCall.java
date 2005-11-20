package de.jalin.fibu.server.buchungszeile;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungszeileUpdateCall extends AbstractCall {

	private BuchungszeileData setData;
	private BuchungszeileData whereData;

	public BuchungszeileUpdateCall (
			   BuchungszeileData setData
			,  BuchungszeileData whereData
		)
	{
		super("buchungszeile", "update");
		this.setData = setData;
		this.whereData = whereData;
	}
	
	public void prepare() {
		addSetProperty("betrag", setData.getBetrag());
		addSetProperty("soll", setData.getSoll());
		addSetProperty("haben", setData.getHaben());
		addWhereProperty("buzlid", whereData.getBuzlid());
		addWhereProperty("buchid", whereData.getBuchid());
		addWhereProperty("kontoid", whereData.getKontoid());
		addWhereProperty("betrag", whereData.getBetrag());
		addWhereProperty("soll", whereData.getSoll());
		addWhereProperty("haben", whereData.getHaben());
	}
}
