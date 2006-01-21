/* Erzeugt am 15.01.2006 von tbayen
 * $Id: ServletDepotManager.java,v 1.1 2006/01/21 23:20:50 tbayen Exp $
 */
package de.bayen.depotmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.bayen.util.FreemarkerClassTemplateLoader;
import de.bayen.webframework.ServletDatabase;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ServletDepotManager extends ServletDatabase {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Methode schreiben
		super.doGet(req, resp);
	}

	public static void main(String args[]) {
		Class baseclass = ServletDatabase.class;
		ClassLoader cl = baseclass.getClassLoader();
		URL url;
		String classname = baseclass.getPackage().getName().replace('.', '/')+ "/templates/standard/README.txt";
//		String classname = baseclass.getPackage().getName().replace('.', '/')+ "/templates/user/tables.ftl";
		//classname=classname.replace('/', '.');
		
		InputStream strm = cl.getResourceAsStream(classname);
		System.out.println(classname + ": " + strm);

		url = cl.getResource(classname);
		System.out.println(classname + ": " + url);
		//
		Configuration cfg = new Configuration();
				TemplateLoader loaders[] = {
		// mit folgender Zeile werden auch noch abgeleitete Klassen
		// durchsucht (TO DO: aber keine mehrfach abgeleiteten).
		//				new ClassTemplateLoader(ServletDepotManager.class, "templates"),
		//				new ClassTemplateLoader(ServletDatabase.class, "templates")
				new FreemarkerClassTemplateLoader(ServletDatabase.class, "templates")
				};
				cfg.setTemplateLoader(new MultiTemplateLoader(loaders));
		//cfg.setClassForTemplateLoading(baseclass, "templates");
		// normalerweise gibts keine Euro-Zeichen, das stelle ich hier aber ein:
		//cfg.setEncoding(new Locale("de", "DE"), "ISO-8859-15");
		try {
			Template t = cfg.getTemplate("standard/*/README.txt");
			t.process(null, new PrintWriter(System.out));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
/*
 * $Log: ServletDepotManager.java,v $
 * Revision 1.1  2006/01/21 23:20:50  tbayen
 * Erste Version 1.0 des DepotManagers
 * erste FreibierWeb-Applikation im eigenen Paket
 *
 */