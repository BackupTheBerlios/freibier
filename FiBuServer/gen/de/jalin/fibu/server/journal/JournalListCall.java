package de.jalin.fibu.server.journal;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class JournalListCall extends AbstractCall {

	private JournalData whereData;

	public JournalListCall (
			   JournalData whereData
		)
	{
		super("journal", "list");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("jourid", whereData.getJourid());
		addWhereProperty("journr", whereData.getJournr());
		addWhereProperty("jahr", whereData.getJahr());
		addWhereProperty("periode", whereData.getPeriode());
		addWhereProperty("since", whereData.getSince());
		addWhereProperty("lastupdate", whereData.getLastupdate());
		addWhereProperty("absummiert", whereData.getAbsummiert());
	}
}
