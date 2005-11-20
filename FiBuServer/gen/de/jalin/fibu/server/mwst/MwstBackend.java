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
}
