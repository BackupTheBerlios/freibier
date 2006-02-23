package de.jalin.fibu.server.mwst;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface MwstBackend {

	public 
		Vector 
			executeMwstListCall
		( Connection dbConnection
		, XmlRpcSession session
		, MwstData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws MwstException;

	public 
		void
			executeMwstAddCall
		( Connection dbConnection
		, XmlRpcSession session
		, MwstData writeData
	) throws MwstException;

	public 
		void
			executeMwstUpdateCall
		( Connection dbConnection
		, XmlRpcSession session
		, MwstData writeData
		, MwstData whereData
	) throws MwstException;

	public 
		void
			executeMwstDeleteCall
		( Connection dbConnection
		, XmlRpcSession session
		, MwstData whereData
	) throws MwstException;
}
