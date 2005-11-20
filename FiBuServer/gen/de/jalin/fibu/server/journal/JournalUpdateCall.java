package de.jalin.fibu.server.journal;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class JournalUpdateCall extends AbstractCall {

	private JournalData setData;
	private JournalData whereData;

	public JournalUpdateCall (
			   JournalData setData
			,  JournalData whereData
		)
	{
		super("journal", "update");
		this.setData = setData;
		this.whereData = whereData;
	}
	
	public void prepare() {
		addSetProperty("journr", setData.getJournr());
		addSetProperty("jahr", setData.getJahr());
		addSetProperty("periode", setData.getPeriode());
		addSetProperty("since", setData.getSince());
		addSetProperty("absummiert", setData.getAbsummiert());
		addWhereProperty("jourid", whereData.getJourid());
		addWhereProperty("journr", whereData.getJournr());
		addWhereProperty("jahr", whereData.getJahr());
		addWhereProperty("periode", whereData.getPeriode());
		addWhereProperty("since", whereData.getSince());
		addWhereProperty("lastupdate", whereData.getLastupdate());
		addWhereProperty("absummiert", whereData.getAbsummiert());
	}
}
