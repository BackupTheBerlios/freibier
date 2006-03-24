// Generiert mit xmlrpcgen

package de.jalin.fibu.server.konto;

import java.sql.*;
import java.util.*;
import net.hostsharing.admin.runtime.*;

public interface KontoBackend {

	public 
		Vector 
			executeKontoListCall
		( Connection dbConnection
		, XmlRpcSession session
		, KontoData whereData
		, DisplayColumns display
		, OrderByList orderBy
	) throws KontoException;

	public 
		void
			executeKontoAddCall
		( Connection dbConnection
		, XmlRpcSession session
		, KontoData writeData
	) throws KontoException;

	public 
		void
			executeKontoUpdateCall
		( Connection dbConnection
		, XmlRpcSession session
		, KontoData writeData
		, KontoData whereData
	) throws KontoException;

	public 
		void
			executeKontoDeleteCall
		( Connection dbConnection
		, XmlRpcSession session
		, KontoData whereData
	) throws KontoException;
}
