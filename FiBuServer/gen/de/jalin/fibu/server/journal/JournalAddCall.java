package de.jalin.fibu.server.journal;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class JournalAddCall extends AbstractCall {

	private JournalData setData;

	public JournalAddCall (
			   JournalData setData
		)
	{
		super("journal", "add");
		this.setData = setData;
	}
	
	public void prepare() {
		addSetProperty("journr", setData.getJournr());
		addSetProperty("jahr", setData.getJahr());
		addSetProperty("periode", setData.getPeriode());
		addSetProperty("since", setData.getSince());
		addSetProperty("absummiert", setData.getAbsummiert());
	}
}
