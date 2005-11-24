package de.jalin.fibu.server.buchungsmaschine;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface BuchungsmaschineBackend {

	public 
		void
			executeBuchungsmaschineAddCall
		( Connection dbConnection
		, XmlRpcSession session
		, BuchungsmaschineData writeData
	) throws BuchungsmaschineException;
}
