/* Erzeugt am 27.03.2005 von tbayen
 * $Id: HttpMultipartRequest.java,v 1.1 2005/04/05 21:34:48 tbayen Exp $
 */
package de.bayen.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

/**
 * Ein ServletRequest, der auch Multipart-Formulare auswerten kann. Nur
 * so können File-Uploads ausgewertet werden.
 * <p>
 * Normale Formulare werden (wenn im <form...>-Tag nichts angegeben wird) 
 * mit dem MIME-Type "application/x-www-form-urlencoded" übertragen. Man
 * kann dort z.B. "text/plain" angeben. Das verschönert das Ergebnis,
 * wenn man eine "mailto:..."-Action benutzt. Man kann allerdings auch
 * enctype="multipart/form-data" angeben. In diesem Falle wird das Formular
 * als Multipart übertragen, was es erlaubt, beliebig grosse und uncodierte
 * Daten (wie z.B. Dateien) zu übertragen. Nur dann ist es möglich, ein
 * Input-Feld vom Typ "file" einzubauen.</p>
 * <p>
 * Diese Klasse wrappt den normalen, übergebenen Request und sorgt dafür,
 * daß sie sich benimmt wie das ürsprüngliche Objekt. Handelt es sich
 * um keinen Multipart-Request, so ändert sich auch gar nichts. Ist es
 * jedoch ein Multipart-Request, so ergibt ein Aufruf von getUploadedFile()
 * für den Namen eines HTML <input...>-Feldes mit dem Typ "file" ein
 * Objekt der eingebetteten Klasse "UploadedFile".</p>
 * <p>
 * Übrigens gibt die zugrundeliegende Bibliothek commons-fileupload auch
 * intelligentere Verfahren, um größere Dateien im Filesystem statt im
 * Speicher zu halten und dann ggf. nur an einen Zielort umzubenennen.
 * Wer diese Klasse also dahingehend verbessern möchte: Nur zu!</p>
 * @author tbayen
 */
public class HttpMultipartRequest extends HttpServletRequestWrapper {
	static Logger logger = Logger.getLogger(HttpMultipartRequest.class
			.getName());
	boolean isMultipart;
	Hashtable multipartParams = null;

	/**
	 * Eine per Http-Multipart Formular hochgeladene Datei.
	 * 
	 * @author tbayen
	 */
	public class UploadedFile {
		public String name;
		public byte[] data;

		public UploadedFile(String name, byte[] data) {
			this.name = name;
			this.data = data;
		}
	}

	/**
	 * Konstruktor, dem ein zu wrappender Request übergeben wird
	 * 
	 * @param request
	 */
	public HttpMultipartRequest(HttpServletRequest request) {
		super(request);
		isMultipart = FileUpload.isMultipartContent(request);
		if (isMultipart) {
			// Create a new file upload handler
			DiskFileUpload upload = new DiskFileUpload();
			try {
				// Parse the request
				List items = upload.parseRequest(request);
				multipartParams = new Hashtable();
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					if (item.isFormField()) {
						if (item.isFormField()) {
							multipartParams.put(item.getFieldName(), item
									.getString());
						}
					} else {
						if (item.getSize() != 0 || item.getName().length()!=0) {
							// nur setzen, wenn auch eine Datei upgeloaded wurde.
							// hierdurch kann ActionEdit dies erkennen und den
							// alten Wert erhalten.
							UploadedFile file = new UploadedFile(
									item.getName(), item.get());
							multipartParams.put(item.getFieldName(), file);
						}
					}
				}
			} catch (FileUploadException e) {
				logger.error("Kann Multipart-Upload nicht parsen");
				isMultipart = false;
			}
		}
	}

	/**
	 * Diese Methode ergibt die hochgeladene Datei oder null, falls es sich
	 * bei diesem Feld nicht um einen Datei-Upload handelt.
	 * 
	 * @param name
	 * @return UploadedFile-Objekt
	 */
	public UploadedFile getUploadedFile(String name) {
		if (isMultipart) {
			Object param = multipartParams.get(name);
			if (param.getClass().equals(UploadedFile.class)) {
				return (UploadedFile) param;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// überladene Methoden:
	public String getParameter(String name) {
		if (isMultipart) {
			Object param = multipartParams.get(name);
			if (param == null) {
				return null;
			} else if (param.getClass().equals(String.class)) {
				return (String) param;
			} else {
				return ((UploadedFile) param).name;
			}
		} else {
			return super.getParameter(name);
		}
	}

	public Map getParameterMap() {
		if (isMultipart) {
			return multipartParams;
		} else {
			return super.getParameterMap();
		}
	}

	public Enumeration getParameterNames() {
		if (isMultipart) {
			return multipartParams.keys();
		} else {
			return super.getParameterNames();
		}
	}

	public String[] getParameterValues(String arg0) {
		if (isMultipart) {
			String[] values = new String[multipartParams.size()];
			int i = 0;
			for (Enumeration e = multipartParams.keys(); e.hasMoreElements();) {
				values[i++] = (String) e.nextElement();
			}
			return values;
		} else {
			return super.getParameterValues(arg0);
		}
	}
}
/*
 * $Log: HttpMultipartRequest.java,v $
 * Revision 1.1  2005/04/05 21:34:48  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.2  2005/03/28 17:08:16  tbayen
 * BLOBs in Records nicht immer löschen
 *
 * Revision 1.1  2005/03/28 03:09:45  tbayen
 * Binärdaten (BLOBS) in der Datenbank und im Webinterface
 *
 */