// $Id: SaveAction.java,v 1.1 2005/02/28 21:52:38 phormanns Exp $
package de.jalin.freibier.webgui;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.jalin.freibier.database.DBTable;
import de.jalin.freibier.database.Database;
import de.jalin.freibier.database.Record;
import de.jalin.freibier.database.TypeDefinition;
import de.jalin.freibier.database.exception.DatabaseException;
import de.jalin.freibier.database.impl.ValueObject;

/**
 * @author peter
 */
public class SaveAction extends AbstractAction {

    public SaveAction(Database db) throws DatabaseException {
        super(db);
    }

    public Map performAction(HttpServletRequest request, DialogState state)
            throws DatabaseException {
        state.setEditModeOff();
        DBTable tab = this.getDatabase().getTable(state.getTableName());
        TypeDefinition pkType = tab.getFieldDef(tab.getPrimaryKey());
        ValueObject object = pkType.parse(request.getParameter("row"));
        Record rec = tab.getRecordByPrimaryKey(object.getValue());
        Enumeration parameterNames = request.getParameterNames();
        String paramName = null;
        while (parameterNames.hasMoreElements()) {
            paramName = (String) parameterNames.nextElement();
            if (paramName.startsWith("ed_")) {
                rec.setField(paramName.substring(3), request.getParameter(paramName));
            }
        }
        tab.setRecord(rec);
        return readData(state);
    }

}

/*
 *  $Log: SaveAction.java,v $
 *  Revision 1.1  2005/02/28 21:52:38  phormanns
 *  SaveAction begonnen
 *
 */
