/* Erzeugt am 20.03.2005 von tbayen
 * $Id: URIParserForFreeMarker.java,v 1.1 2005/04/05 21:34:46 tbayen Exp $
 */
package de.bayen.webframework;

import freemarker.template.TemplateHashModelEx;

/**
 * Diese Klasse stellt einen URI-Parser dar, der aus Templates heraus
 * aufgerufen wurden, die mit der FreeMarker Template Engine bearbeitet
 * werden.
 * 
 * @author tbayen
 */
public interface URIParserForFreeMarker extends URIParser {
	/**
	 * Freemarker erlaubt, Methoden innerhalb von Templates aufzurufen.
	 * Allerdings scheint es so, als ob man dabei keine Hashes übergeben kann,
	 * sondern nur die FreeMarker-internen -Objekte. Diese konvertiere ich
	 * also hier. Die Funktionsweise ist ansonsten identisch mit 
	 * {@link #createURI(Map,Map,Map)}.
	 * 
	 * @param original TemplateHashModelEx
	 * @param changes TemplateHashModelEx
	 * @param params TemplateHashModelEx
	 * @return String mit der URI
	 */
	public String createURI(TemplateHashModelEx original,
			TemplateHashModelEx changes, TemplateHashModelEx params);
}

/*
 * $Log: URIParserForFreeMarker.java,v $
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
 * Revision 1.1  2005/03/21 02:06:16  tbayen
 * Komplette Überarbeitung des Web-Frameworks
 * insbes. Modularisierung von URIParser und Actions
 *
 */