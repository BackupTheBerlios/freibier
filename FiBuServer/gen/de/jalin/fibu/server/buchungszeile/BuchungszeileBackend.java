// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungszeile;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface BuchungszeileBackend {

	public 
		Vector 
			executeBuchungszeileListCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungszeileData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws BuchungszeileException;

	public 
		void
			executeBuchungszeileAddCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungszeileData writeData
	) throws BuchungszeileException;

	public 
		void
			executeBuchungszeileUpdateCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungszeileData writeData
		, BuchungszeileData whereData
	) throws BuchungszeileException;

	public 
		void
			executeBuchungszeileDeleteCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungszeileData whereData
	) throws BuchungszeileException;
}
