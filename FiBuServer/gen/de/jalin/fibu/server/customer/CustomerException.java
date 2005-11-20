package de.jalin.fibu.server.customer;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class CustomerException extends XmlRpcTransactionException {

	public static final String MSG_10000 = "Fehlende Rechte für diese Funktion.";
	public static final String MSG_10010 = "Fehler beim Datenbankzugriff.";
	public static final String MSG_10020 = "Falscher Name für XMLRPC-Funktion.";
	public static final String MSG_10101 = "Cust-Fehler Unbekannt";

	private static Map errorCodesMap;

	static {
		errorCodesMap = new HashMap();
		errorCodesMap.put(new Integer(10000), new ErrorCode(10000, MSG_10000));
		errorCodesMap.put(new Integer(10010), new ErrorCode(10010, MSG_10010));
		errorCodesMap.put(new Integer(10020), new ErrorCode(10020, MSG_10020));
		errorCodesMap.put(new Integer(10101), new ErrorCode(10101, MSG_10101));
	}
	
	public CustomerException(int code) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)));
	}
	
	public CustomerException(int code, Throwable ex) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)), ex);
	}
}
