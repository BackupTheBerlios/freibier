package de.jalin.fibu.server.buchungsmaschine;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class BuchungsmaschineException extends XmlRpcTransactionException {

	public static final String MSG_10000 = "Fehlende Rechte für diese Funktion.";
	public static final String MSG_10010 = "Fehler beim Datenbankzugriff.";
	public static final String MSG_10020 = "Falscher Name für XMLRPC-Funktion.";
	public static final String MSG_10101 = "Cust-Fehler Unbekannt";
	public static final String MSG_10201 = "MWSt-Fehler Unbekannt";
	public static final String MSG_10301 = "Konto-Fehler Unbekannt";
	public static final String MSG_10401 = "Journal-Fehler Unbekannt";
	public static final String MSG_10501 = "Buchungs-Fehler Unbekannt";
	public static final String MSG_10601 = "Buchungszeile-Fehler Unbekannt";
	public static final String MSG_10701 = "Buchungsliste-Fehler Unbekannt";
	public static final String MSG_10801 = "Buchungsmaschine-Fehler Unbekannt";

	private static Map errorCodesMap;

	static {
		errorCodesMap = new HashMap();
		errorCodesMap.put(new Integer(10000), new ErrorCode(10000, MSG_10000));
		errorCodesMap.put(new Integer(10010), new ErrorCode(10010, MSG_10010));
		errorCodesMap.put(new Integer(10020), new ErrorCode(10020, MSG_10020));
		errorCodesMap.put(new Integer(10101), new ErrorCode(10101, MSG_10101));
		errorCodesMap.put(new Integer(10201), new ErrorCode(10201, MSG_10201));
		errorCodesMap.put(new Integer(10301), new ErrorCode(10301, MSG_10301));
		errorCodesMap.put(new Integer(10401), new ErrorCode(10401, MSG_10401));
		errorCodesMap.put(new Integer(10501), new ErrorCode(10501, MSG_10501));
		errorCodesMap.put(new Integer(10601), new ErrorCode(10601, MSG_10601));
		errorCodesMap.put(new Integer(10701), new ErrorCode(10701, MSG_10701));
		errorCodesMap.put(new Integer(10801), new ErrorCode(10801, MSG_10801));
	}
	
	public BuchungsmaschineException(int code) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)));
	}
	
	public BuchungsmaschineException(int code, Throwable ex) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)), ex);
	}
}
