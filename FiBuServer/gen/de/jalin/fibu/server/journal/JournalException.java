// Generiert mit xmlrpcgen

package de.jalin.fibu.server.journal;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class JournalException extends XmlRpcTransactionException {

	private static final long serialVersionUID = 1143225535082L;

	public static final int ERR_MISSING_RIGHTS = 10000;
	public static final String MSG_10000 = "Fehlende Rechte für diese Funktion.";

	public static final int ERR_DATABASE_ERROR = 10010;
	public static final String MSG_10010 = "Fehler beim Datenbankzugriff.";

	public static final int ERR_XMLRPC_ERROR = 10020;
	public static final String MSG_10020 = "Falscher Name für XMLRPC-Funktion.";

	public static final int ERR_ERR_CUSTOMER = 10101;
	public static final String MSG_10101 = "Cust-Fehler Unbekannt";

	public static final int ERR_ERR_TAX = 10201;
	public static final String MSG_10201 = "MWSt-Fehler Unbekannt";

	public static final int ERR_ERR_LEDGER = 10301;
	public static final String MSG_10301 = "Konto-Fehler Unbekannt";

	public static final int ERR_ERR_JOURNAL = 10401;
	public static final String MSG_10401 = "Journal-Fehler Unbekannt";


	private static Map errorCodesMap;

	static {
		errorCodesMap = new HashMap();
		errorCodesMap.put(new Integer(ERR_MISSING_RIGHTS), new ErrorCode(ERR_MISSING_RIGHTS, "ERR_MISSING_RIGHTS", MSG_10000));
		errorCodesMap.put(new Integer(ERR_DATABASE_ERROR), new ErrorCode(ERR_DATABASE_ERROR, "ERR_DATABASE_ERROR", MSG_10010));
		errorCodesMap.put(new Integer(ERR_XMLRPC_ERROR), new ErrorCode(ERR_XMLRPC_ERROR, "ERR_XMLRPC_ERROR", MSG_10020));
		errorCodesMap.put(new Integer(ERR_ERR_CUSTOMER), new ErrorCode(ERR_ERR_CUSTOMER, "ERR_ERR_CUSTOMER", MSG_10101));
		errorCodesMap.put(new Integer(ERR_ERR_TAX), new ErrorCode(ERR_ERR_TAX, "ERR_ERR_TAX", MSG_10201));
		errorCodesMap.put(new Integer(ERR_ERR_LEDGER), new ErrorCode(ERR_ERR_LEDGER, "ERR_ERR_LEDGER", MSG_10301));
		errorCodesMap.put(new Integer(ERR_ERR_JOURNAL), new ErrorCode(ERR_ERR_JOURNAL, "ERR_ERR_JOURNAL", MSG_10401));
	}
	
	public JournalException(int code) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)));
	}
	
	public JournalException(int code, Throwable ex) {
		super((ErrorCode) errorCodesMap.get(new Integer(code)), ex);
	}
}
