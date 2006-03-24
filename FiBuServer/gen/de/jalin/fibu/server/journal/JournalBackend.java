// Generiert mit xmlrpcgen

package de.jalin.fibu.server.journal;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface JournalBackend {

	public 
		Vector 
			executeJournalListCall
		( Connection dbConnection
		, XmlRpcSession session
		, JournalData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws JournalException;

	public 
		Vector 
			executeJournalAddCall
		( Connection dbConnection
		, XmlRpcSession session
		, JournalData writeData
	) throws JournalException;

	public 
		void
			executeJournalUpdateCall
		( Connection dbConnection
		, XmlRpcSession session
		, JournalData writeData
		, JournalData whereData
	) throws JournalException;

	public 
		void
			executeJournalDeleteCall
		( Connection dbConnection
		, XmlRpcSession session
		, JournalData whereData
	) throws JournalException;
}
