/* Erzeugt am 19.03.2005 von tbayen
 * $Id: URIParserImpl.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.webframework;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/**
 * Umsetzung der URI-Parser-Klasse für das von mir bevorzugte URI-Format
 * 
 * Die URL hat die Bestandteile
 * 
 *     /context/servlet/[theme-[view-]]action[/table[/id]]
 * 
 * Es wird ein root-Objekt erzeugt, dass Werte enthalten kann. Dann
 * wird die sich aus der URL ergebende Action aufgerufen, die ggf. 
 * Aktionen ausführt und weitere Daten in das root-Objekt schreibt.
 * Dann wird das entsprechende Template aufgerufen.
 */
public class URIParserImpl implements URIParser, URIParserForFreeMarker {
	public Map parseURI(HttpServletRequest req) {
		Map map = new HashMap();
		map.put("parser", this); // hiermit können Templates createURI() aufrufen
		map.put("baseurl", req.getContextPath());
		String url[] = req.getRequestURI().split("/");
		map.put("context", url[1]);
		map.put("servlet", url[2]);
		if (url.length > 3) {
			String parts[] = url[3].split("-");
			switch (parts.length) {
			case 1:
				map.put("theme", "standard");
				map.put("view", parts[0]);
				map.put("action", parts[0]);
				break;
			case 2:
				map.put("theme", parts[0]);
				map.put("view", parts[1]);
				map.put("action", parts[1]);
				break;
			case 3:
				map.put("theme", parts[0]);
				map.put("view", parts[1]);
				map.put("action", parts[2]);
				break;
			default:
				map.put("theme", "standard");
				map.put("view", "tables");
				map.put("action", "tables");
				break;
			}
		}
		if (url.length > 4) {
			map.put("table", url[4]);
		}
		if (url.length > 5) {
			map.put("id", url[5]);
		}
		return map;
	}

	/**
	 * Konvertierung von FreeMarker-Hash-Objekten
	 */
	private Map fmhash2map(TemplateHashModelEx shash) {
		if (shash == null) {
			return null;
		}
		Map map = new HashMap();
		try {
			TemplateModelIterator i = shash.keys().iterator();
			while (i.hasNext()) {
				String key = i.next().toString();
				map.put(key, shash.get(key).toString());
			}
		} catch (TemplateModelException e) {}
		return map;
	}

	public String createURI(TemplateHashModelEx original,
			TemplateHashModelEx changes, TemplateHashModelEx params) {
		return createURI(fmhash2map(original), fmhash2map(changes),
				fmhash2map(params));
	}

	public String createURI(Map original, Map changes, Map params) {
		Map map = new HashMap();
		map.putAll(original);
		// den Inhalt von "changes" kann ich nicht mit putAll() übernehmen,
		// weil es leere Strings enthalten kann, die nicht übernommen werden
		// sollen.
		Iterator it = changes.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			String value = (String) changes.get(key);
			if (value.length() > 0) {
				map.put(key, value);
			}
		}
		if ((changes.get("action") != null && ((String) changes.get("action"))
				.length() > 0)
				&& (changes.get("view") == null || ((String) changes
						.get("view")).length() == 0)) {
			// keiner ändert nur die Action und will dabei das view des letzten
			// Aufrufs stehenlassen. Deshalb wird das view in so einem Fall 
			// automagisch angepasst.
			map.put("view", changes.get("action"));
		}
		String baseurl = (String) map.get("baseurl");
		String servlet = (String) map.get("servlet");
		String theme = (String) map.get("theme");
		String view = (String) map.get("view");
		String action = (String) map.get("action");
		String table = (String) map.get("table");
		String id = (String) map.get("id");
		String uri = baseurl + "/" + servlet + "/";
		if (theme != null && theme.length() > 0 && !theme.equals("standard")
				|| ((view != null) && !(view.length() == 0)
				&& !view.equals(action))) {
			uri += theme + "-";
		}
		if ((view != null) && !(view.length() == 0) && !view.equals(action)) {
			uri += view + "-";
		}
		uri += action;
		if (table != null && (table.length() > 0) && !table.equals("-")) {
			uri += "/" + table;
			if (id != null && id.length() > 0 && !id.equals("-")) {
				uri += "/" + id;
			}
		}
		if (params != null && params.keySet().size() > 0) {
			uri += "?";
			List keys = new ArrayList(params.keySet());
			for (int i = 0; i < keys.size(); i++) {
				try {
					uri += ((String) keys.get(i))
							+ "="
							+ URLEncoder.encode((String) params
									.get(keys.get(i)), "UTF-8");
					if (i != keys.size() - 1) { // immer ausser ganz am Ende:
						uri += "&";
					}
				} catch (UnsupportedEncodingException e) {
					// Exception kann nicht aufreten, solange da "UTF-8" steht
				}
			}
		}
		return uri;
	}
}
/*
 * $Log: URIParserImpl.java,v $
 * Revision 1.1  2005/04/05 21:34:46  tbayen
 * WebDatabase 1.4 - freigegeben auf Berlios
 *
 * Revision 1.1  2005/04/05 21:14:08  tbayen
 * HBCI-Banking-Applikation fertiggestellt
 *
 * Revision 1.1  2005/03/23 18:03:10  tbayen
 * Sourcecodebaum reorganisiert und
 * Javadoc-Kommentare überarbeitet
 *
 * Revision 1.1  2005/03/22 19:28:27  tbayen
 * Telefonbuch als zweite Applikation
 * Behebung einiger Bugs; jetzt Version 1.2
 *
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */