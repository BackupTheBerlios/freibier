package de.jalin.fibu.server.mwst;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class MwstUpdateCall extends AbstractCall {

	private MwstData setData;
	private MwstData whereData;

	public MwstUpdateCall (
			   MwstData setData
			,  MwstData whereData
		)
	{
		super("mwst", "update");
		this.setData = setData;
		this.whereData = whereData;
	}
	
	public void prepare() {
		addSetProperty("mwsttext", setData.getMwsttext());
		addSetProperty("mwstsatzaktiv", setData.getMwstsatzaktiv());
		addWhereProperty("mwstid", whereData.getMwstid());
		addWhereProperty("mwstsatz", whereData.getMwstsatz());
		addWhereProperty("mwsttext", whereData.getMwsttext());
		addWhereProperty("mwstkontosoll", whereData.getMwstkontosoll());
		addWhereProperty("mwstkontohaben", whereData.getMwstkontohaben());
		addWhereProperty("mwstsatzaktiv", whereData.getMwstsatzaktiv());
	}
}
