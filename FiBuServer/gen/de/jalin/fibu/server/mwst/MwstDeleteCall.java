package de.jalin.fibu.server.mwst;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class MwstDeleteCall extends AbstractCall {

	private MwstData whereData;

	public MwstDeleteCall (
			   MwstData whereData
		)
	{
		super("mwst", "delete");
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
