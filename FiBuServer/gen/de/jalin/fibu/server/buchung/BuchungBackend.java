// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchung;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface BuchungBackend {

	public 
		Vector 
			executeBuchungListCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws BuchungException;

	public 
		void
			executeBuchungAddCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungData writeData
	) throws BuchungException;

	public 
		void
			executeBuchungUpdateCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungData writeData
		, BuchungData whereData
	) throws BuchungException;

	public 
		void
			executeBuchungDeleteCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungData whereData
	) throws BuchungException;
}
