package com.riversoft.platform.stat;

/**
 * Created by exizhai on 3/20/2014.
 */
public class SQLParser {

    public static SingleSelectSQLStatement parseSingleSelectSQL(String select) {
        SingleSelectSQLStatement statement = new SingleSelectSQLStatement();

        int fromIndex = select.indexOf("from");
        int whereIndex = select.indexOf("where");

        String fromTables;
        if(whereIndex > -1) { //contains where
            fromTables = select.substring(fromIndex + 5, whereIndex);

            String conditions = select.substring(whereIndex + 6);
            statement.setConditions(conditions);
        } else {
            fromTables = select.substring(fromIndex + 5);
        }

        String table = fromTables.trim();
        if(fromTables.trim().contains(" ")) {
            table = fromTables.trim().split(" ")[0];
        }
        statement.setTable(table);

        return statement;
    }
}
