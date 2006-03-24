// Generiert mit xmlrpcgen

package de.jalin.fibu.server.journal;

import net.hostsharing.admin.runtime.*;

public class JournalDeleteCall extends AbstractCall {

	private JournalData whereData;

	public JournalDeleteCall (
			   JournalData whereData
		)
	{
		super("journal", "delete");
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
