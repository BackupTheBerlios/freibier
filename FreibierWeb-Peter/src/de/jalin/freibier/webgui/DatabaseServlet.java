// $Id: DatabaseServlet.java,v 1.10 2005/02/21 22:55:25 phormanns Exp $

package de.jalin.freibier.webgui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.DatabaseFactory;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.exception.SystemDatabaseException;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class DatabaseServlet extends HttpServlet {

    private static final int FETCH_SIZE = 15;

    private Database db;
    private Configuration freemarkerConfig;
    private ActionFactory actionFactory;

    public void init() throws ServletException {
        super.init();
        String sqlFactoryClass = getInitParameter("sqlFactoryClass");
        String jdbcDriverClass = getInitParameter("jdbcDriverClass");
        String jdbcConnectUrl = getInitParameter("jdbcConnectUrl");
        String dbUser = getInitParameter("dbUser");
        String dbPassword = getInitParameter("dbPassword");
        try {
            db = DatabaseFactory.getDatabaseInstance(sqlFactoryClass,
                    jdbcDriverClass, jdbcConnectUrl, dbUser, dbPassword);
            db.createTestData();
        } catch (DatabaseException e) {
            log("Keine Verbindung zur Datenbank", e);
        }
        
        // Freemarker Konfiguration
        freemarkerConfig = new Configuration();
        freemarkerConfig.setServletContextForTemplateLoading(
                getServletContext(), "WEB-INF/templates");
        freemarkerConfig.setTemplateUpdateDelay(0);
        freemarkerConfig.setTemplateExceptionHandler(
                TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        freemarkerConfig.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        freemarkerConfig.setDefaultEncoding("ISO-8859-1");
        freemarkerConfig.setOutputEncoding("UTF-8");
        freemarkerConfig.setLocale(Locale.GERMANY);
        
        // ActionFactory erzeugen
        try {
			actionFactory = new ActionFactory(db);
		} catch (DatabaseException e) {
			throw new ServletException(e);
		}
    }

    public void destroy() {
        try {
            db.close();
        } catch (SystemDatabaseException e) {
            log("Kann Datenbank nicht schließen", e);
        }
        db = null;
        super.destroy();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// DialogState aus der Session
		HttpSession session = request.getSession();
		DialogState dialogState = (DialogState) session.getAttribute("dialogstate");
		if (dialogState == null) {
			dialogState = new DialogState();
			dialogState.setRowsPerPage(DatabaseServlet.FETCH_SIZE);
			session.setAttribute("dialogstate", dialogState);
		}

		// Action ermitteln, Default Action ist "init"
		String actionName = null;
		actionName = request.getParameter("action");
		if (actionName == null || actionName.length() == 0) {
			actionName = "init";
		}
		
		Map templateData = new HashMap();
		String error = "";
		try {
			// Action ausfuehren
			Action action = actionFactory.getAction(actionName);
			templateData = action.performAction(dialogState);
			templateData.put("error", error);
			
		} catch (DatabaseException e) {
			error = e.getMessage();
			templateData.put("error", error);
		}
		Template template = freemarkerConfig.getTemplate("main.ftl");
		try {
			template.process(templateData, response.getWriter());
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

//	protected Template handleRequest(HttpServletRequest request,
//            HttpServletResponse response, Context context) throws Exception {
//        try {
//            List tableNamesList = db.getTableNamesList();
//            // init params
//            HttpSession session = request.getSession();
//            ViewParameter view = (ViewParameter) session.getAttribute("view");
//            if (view == null) {
//                view = new ViewParameter();
//                view.setTableName((String) tableNamesList.get(0));
//                view.setFirstRowNumber(1);
//                view.setMaxRowNumber(FETCH_SIZE);
//                view.setRowsPerPage(FETCH_SIZE);
//                view.setOrderByColumn(null);
//                session.setAttribute("view", view);
//            }
//            view.updateFromRequest(request.getParameter("tab"), 
//            		request.getParameter("order"), 
//					request.getParameter("page"),
//                    request.getParameter("edit"));
//            Filter filter = (Filter) session.getAttribute("filter");
//            if (filter == null) {
//                filter = new Filter();
//                session.setAttribute("filter", filter);
//            }
//            // get TableImpl
//            DBTable tab = db.getTable(view.getTableName());
//            String pkName = tab.getPrimaryKey();
//            TypeDefinition pkTypeDef = tab.getFieldDef(pkName);
//            IWhereClause queryFilter = null;
//            // save changed RecordImpl
//            String saveRequest = request.getParameter("save");
//            if (saveRequest != null) {
//                Record data 
//					= tab.getRecordByPrimaryKey(pkTypeDef.parse(saveRequest));
//                Enumeration requestParameterNames = request.getParameterNames();
//                String paramName = null;
//                String paramValue = null;
//                String fieldName = null;
//                while (requestParameterNames.hasMoreElements()) {
//                    paramName = (String) requestParameterNames.nextElement();
//                    if (paramName.startsWith("ed_")) {
//                        fieldName = paramName.substring(3);
//                        paramValue = request.getParameter(paramName);
//                        data.setField(fieldName, paramValue);
//                        view.setEditModeOff();
//                    }
//                }
//                tab.setRecord(data);
//            }
//            // set Filter
//            String filterRequest = request.getParameter("filter");
//            if (filterRequest != null && filterRequest.equals("set")) {
//                filter.setFilterEnabled(true);
//                filter.resetTable(tab.getName());
//                Enumeration requestParameterNames = request.getParameterNames();
//                String paramName = null;
//                String filterPattern = null;
//                String fieldName = null;
//                while (requestParameterNames.hasMoreElements()) {
//                    paramName = (String) requestParameterNames.nextElement();
//                    if (paramName.startsWith("ft_")) {
//                        fieldName = paramName.substring(3);
//                        filterPattern = request.getParameter(paramName);
//                        if (filterPattern != null
//                                && filterPattern.trim().length() > 0) {
//                            filter.addFilter(tab.getName(), fieldName,
//                                    Filter.FT_LIKE, filterPattern);
//                        }
//                    }
//                }
//            }
//            if (filter.isFilterEnabled()) {
//                try {
//                    queryFilter = filter.createQueryCondition(tab);
//                } catch (DatabaseException dbexc) {
//                    context.put("error", dbexc.getMessage());
//                }
//            }
//            // get Records
//            view.setMaxRowNumber(tab.getNumberOfRecords(queryFilter));
//            List recordsList = null;
//            recordsList = tab.getRecords(queryFilter, 
//				view.getOrderByColumn(),
//				view.isAscending(), 
//				view.getFirstRowNumber(), 
//				view.getRowsPerPage());
//            List typeDefinitions = tab.getFieldsList();
//            context.put("tablenames", tableNamesList);
//            context.put("table", tab.getName());
//            context.put("types", typeDefinitions);
//            context.put("data", recordsList);
//            context.put("editmode", new Boolean(view.isEditModeOn()));
//            context.put("filter", filter);
//            if (view.isEditModeOn())
//                context.put("editkey", view.getRecordKey());
//            return getTemplate("tables.vm");
//        } catch (UserDatabaseException e) {
//            context.put("error", e.getMessage());
//            return getTemplate("tables.vm");
//        } catch (SystemDatabaseException e) {
//            context.put("error", e.getMessage());
//            return getTemplate("fatal.vm");
//        }
//    }
}

/*
 * $Log: DatabaseServlet.java,v $
 * Revision 1.10  2005/02/21 22:55:25  phormanns
 * Hsqldb zugefuegt
 *
 * Revision 1.9  2005/02/18 22:17:42  phormanns
 * Umstellung auf Freemarker begonnen
 *
 * Revision 1.8  2005/02/16 17:24:52  phormanns
 * OrderBy und Filter funktionieren jetzt
 *
 * Revision 1.7  2005/02/14 21:24:43  phormanns
 * Kleinigkeiten
 * Revision 1.6 2005/02/13 20:27:14 phormanns
 * Funktioniert bis auf Filter
 * 
 * Revision 1.5 2005/02/11 16:46:02 phormanns MySQL geht wieder
 * 
 * Revision 1.4 2005/02/11 15:25:45 phormanns Zwischenstand, nicht
 * funktionierend
 * 
 * Revision 1.3 2005/01/29 20:21:59 phormanns RecordDefinition in TableImpl
 * integriert
 * 
 * Revision 1.2 2004/12/31 19:37:26 phormanns Database Schnittstelle
 * herausgearbeitet
 * 
 * Revision 1.1 2004/12/31 17:13:11 phormanns Erste öffentliche Version
 * 
 * Revision 1.2 2004/12/19 13:24:06 phormanns Web-Version des Tabellen-Browsers
 * mit Edit, Filter (1x)
 * 
 * Revision 1.1 2004/12/17 22:31:17 phormanns Erste Web-Version des
 * Tabellen-Browsers
 *  
 */
