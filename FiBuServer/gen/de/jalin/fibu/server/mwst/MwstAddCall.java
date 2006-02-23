package de.jalin.fibu.server.mwst;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class MwstAddCall extends AbstractCall {

	private MwstData setData;

	public MwstAddCall (
			   MwstData setData
		)
	{
		super("mwst", "add");
		this.setData = setData;
	}
	
	public void prepare() {
		addSetProperty("mwstsatz", setData.getMwstsatz());
		addSetProperty("mwsttext", setData.getMwsttext());
		addSetProperty("mwstkontosoll", setData.getMwstkontosoll());
		addSetProperty("mwstkontohaben", setData.getMwstkontohaben());
		addSetProperty("mwstsatzaktiv", setData.getMwstsatzaktiv());
	}
}
