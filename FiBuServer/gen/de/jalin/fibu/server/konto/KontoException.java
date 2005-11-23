package de.jalin.fibu.server.konto;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class KontoException extends XmlRpcTransactionException {

	public static final String MSG_10000 = "Fehlende Rechte f�r diese Funktion.";
	public static final String MSG_10010 = "Fehler beim Datenbankzugriff.";
	public static final String MSG_10020 = "Falscher Name f�r XMLRPC-Funktion.";
	public static final String MSG_10101 = "Cust-Fehler Unbekannt";
	public static final String MSG_10201 = "MWSt-Fehler Unbekannt";
	public static final String MSG_10301 = "Konto-Fehler Unbekannt";

	private static Map errorCodesMap;

	static {
		errorCodesMap = new HashMap();
		errorCodesMap.put(new Integer(10000), new ErrorCode(10000, MSG_10000));
		errorCodesMap.put(new Integer(10010), new ErrorCode(10010, MSG_10010));
		errorCodesMap.put(new Integer(10020), new ErrorCode(10020, MSG_10020));
		errorCodesMap.put(new Integer(10101), new ErrorCode(10101, MSG_10101));
		errorCodesMap.put(new Integer(10201), new ErrorCode(10201, MSG_10201));
		errorCodesMap.put(new Integer(10301), new ErrorCode(10301, MSG_10301));
	}
	
	public KontoException(int code) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)));
	}
	
	public KontoException(int code, Throwable ex) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)), ex);
	}
}