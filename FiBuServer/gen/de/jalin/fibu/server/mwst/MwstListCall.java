package de.jalin.fibu.server.mwst;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class MwstListCall extends AbstractCall {

	private MwstData whereData;

	public MwstListCall (
			   MwstData whereData
		)
	{
		super("mwst", "list");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("mwstid", whereData.getMwstid());
		addWhereProperty("mwstsatz", whereData.getMwstsatz());
		addWhereProperty("mwsttext", whereData.getMwsttext());
		addWhereProperty("mwstkontosoll", whereData.getMwstkontosoll());
		addWhereProperty("mwstkontohaben", whereData.getMwstkontohaben());
		addWhereProperty("mwstsatzaktiv", whereData.getMwstsatzaktiv());
	}
}
