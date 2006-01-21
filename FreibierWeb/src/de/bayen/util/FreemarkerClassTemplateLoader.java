/* Erzeugt am 21.01.2006 von tbayen
 * $Id: FreemarkerClassTemplateLoader.java,v 1.1 2006/01/21 23:10:10 tbayen Exp $
 */
package de.bayen.util;

import java.net.URL;
import freemarker.cache.ClassTemplateLoader;

/**
 * Entspricht dem ClassTemplateLoader aus freemarker, umgeht jedoch einen
 * Freemarker-Bug.
 * 
 * @author tbayen
 */

//Freemarker Bug:
//
//Wenn ich "acquisition" benutze, kann ich keine Dateien aus eingebundenen
//Bibliotheken benutzen, sondern nur aus meinem eigenen Quellcode.
//
//Der Fehler liegt in freemarker.cache.TemplateCache.concatPath(). Hier wird
//an den erzeugten Pfad ein "/" angehängt (hinter den Filenamen). Dies scheint
//andere Loader nicht zu verwirren, wenn der ClassLoader jedoch das File aus
//einem Jar-File holen soll, klappts dann nicht.
//
//folgender Befehl:
//
//  Template t = cfg.getTemplate("standard/*/README.txt");
//
//ergibt diese Logeinträge:
//
//2006-01-21 19:30:48,704 DEBUG [freemarker.cache] - <Could not find template in cache, creating new one; id=[standard/*/README.txt[de_DE,ISO-8859-15,parsed] ]>
//2006-01-21 19:30:48,710 DEBUG [freemarker.cache] - <Trying to find template source standard/README_de_DE.txt/>
//2006-01-21 19:30:48,715 DEBUG [freemarker.cache] - <Trying to find template source README_de_DE.txt/>
//2006-01-21 19:30:48,717 DEBUG [freemarker.cache] - <Trying to find template source standard/README_de.txt/>
//2006-01-21 19:30:48,719 DEBUG [freemarker.cache] - <Trying to find template source README_de.txt/>
//2006-01-21 19:30:48,721 DEBUG [freemarker.cache] - <Trying to find template source standard/README.txt/>
//2006-01-21 19:30:48,723 DEBUG [freemarker.cache] - <Trying to find template source README.txt/>


public class FreemarkerClassTemplateLoader extends ClassTemplateLoader {
	public FreemarkerClassTemplateLoader() {
		super();
	}

	public FreemarkerClassTemplateLoader(Class loaderClass) {
		super(loaderClass);
	}

	public FreemarkerClassTemplateLoader(Class loaderClass, String path) {
		super(loaderClass, path);
	}

	protected URL getURL(String name) {
		if(name.endsWith("/"))
			name=name.substring(0, name.length()-1);
		return super.getURL(name);
	}
	
	
}

/*
 * $Log: FreemarkerClassTemplateLoader.java,v $
 * Revision 1.1  2006/01/21 23:10:10  tbayen
 * Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
 *
 */