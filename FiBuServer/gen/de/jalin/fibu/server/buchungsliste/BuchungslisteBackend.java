// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungsliste;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface BuchungslisteBackend {

	public 
		Vector 
			executeBuchungslisteListCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungslisteData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws BuchungslisteException;
}
