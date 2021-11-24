package sqlengine;

import exceptions.SqlException;
import structure.*;

import java.io.IOException;
import java.util.*;

public class DBCommand implements DBCommandInterface
{
    Database dbInUse;
    HashMap<String, Database> dbs;
    DBIOInterface dbInt;

    /* Sets up the new database if one does not already exist and names it, Database */
    public DBCommand() throws SqlException {
        dbInUse = null;
        dbs = new HashMap<>();
        dbInt = new DBIO("Database");
        dbInt.readFile(this);
    }

    @Override
    /* Creates a new database folder that holds all the tab files, if it does not already exist */
    public void createDB(String dbName, boolean append) throws SqlException
    {
        if (!dbs.containsKey(dbName)) {
            dbs.put(dbName, new Database(dbName));
            if (!append) {
                return;
            }
            dbInt.createDB(dbName);
        } else {
            throw new SqlException("[ERROR]: Database already exists");
        }
    }

    @Override
    /* Deletes or Drops the DB if one with the specified name exists */
    public void deleteDB(String dbName, boolean append) throws SqlException
    {
        if (!dbs.containsKey(dbName)) {
            throw new SqlException("[ERROR]: Database does not exist");
        }
        dbs.remove(dbName);
        if (append) {
            dbInt.removeDB(dbName);
        }
        useDB(null);
    }

    @Override
    /* Selects the specified database that follows the USE command, if one with the specified name does
    *  not exist (or the program cannot find it, however this is unlikely) the exception is called */
    public void useDB(String dbName) throws SqlException
    {
        if (dbName != null) {
            if (!dbs.containsKey(dbName)) throw new SqlException("[ERROR]: Unknown database");
        } else {
            dbInUse = null;
        }
        dbInUse = dbs.get(dbName);
    }

    @Override
    /* Creates a table file within a database, but only if a database has been selected */
    public void createTab(String table, boolean append) throws SqlException, IOException
    {
        if (dbInUse != null) {
            if (append) {
                dbInt.createTable(dbInUse.getDbName(), table);
            }
            dbInUse.addTable(table);
        } else {
            throw new SqlException("[ERROR]: Database not selected");
        }
    }

    @Override
    /* Deletes a table file within a database, but only if a database has been selected */
    public void deleteTab(String table, boolean append) throws SqlException
    {
        if (dbInUse != null) {
            if (append) {
                dbInt.removeTable(dbInUse.getDbName(), table);
            }
            dbInUse.deleteTable(table);
        } else {
            throw new SqlException("[ERROR]: Database not selected");
        }
    }

    @Override
    /* Creates an attribute / column in a table file */
    public void createAttribute(String table, String attribute, boolean append) throws SqlException, IOException
    {
        if (attribute != null && table != null) {
            getTableName(table).addAttribute(attribute);
            if (!append) {
                return;
            }
            dbInt.updateTable(dbInUse.getDbName(), table, useVal(table, new String[]{"*"}, ""));
        } else {
            throw new SqlException("[ERROR]: Invalid table || attribute name");
        }
    }

    /* Gets the name of the table contained within the database */
    private Table getTableName(String tableName) throws SqlException
    {
        if (dbInUse != null) {
            return dbInUse.getTableName(tableName);
        }
        throw new SqlException("[ERROR]: Database not selected");
    }

    @Override
    /* Deletes an attribute / column in a table file */
    public void deleteAttribute(String table, String attribute, boolean append) throws SqlException, IOException
    {
        if (attribute != null && table != null) {
            getTableName(table).removeAttribute(attribute);
            if (!append) {
                return;
            }
            dbInt.updateTable(dbInUse.getDbName(), table, useVal(table, new String[]{"*"}, ""));
        } else {
            throw new SqlException("[ERROR]: Invalid table || attribute name");
        }
    }

    @Override
    /* Adds a value into the appropriate table, under the appropriate attribute */
    public void addVal(String table, String[] value, boolean addId, boolean append) throws SqlException, IOException
    {
        if (!addId) {
            getTableName(table).addVal(value);
        } else {
            getTableName(table).addValId(value);
        }
        if (!append) {
            return;
        }
        dbInt.updateTable(dbInUse.getDbName(), table, useVal(table, new String[]{"*"}, ""));
    }

    @Override
    /* Alters a value into the appropriate table, under the appropriate attribute */
    public void changeVal(String table, String[] value, String con, boolean append) throws SqlException, IOException
    {
        getTableName(table).updateValCheck(value, con);
        if (!append) {
            return;
        }
        dbInt.updateTable(dbInUse.getDbName(), table, useVal(table, new String[]{"*"}, ""));
    }

    @Override
    /* Deletes a value into the appropriate table, under the appropriate attribute */
    public void removeVal(String table, String con, boolean append) throws SqlException, IOException
    {
        getTableName(table).removeVal(con);
        if (!append) {
            return;
        }
        dbInt.updateTable(dbInUse.getDbName(), table, useVal(table, new String[]{"*"}, ""));
    }

    @Override
    /* Selects a value from the appropriate table, under the appropriate attribute */
    public String useVal(String table, String[] value, String con) throws SqlException
    {
        return getTableName(table).getVal(value, con);
    }

    @Override
    /* Joins two or more tables together */
    public String joinComm(String origTable, String joinTable, String origCol, String joinCol) throws SqlException
    {
        String[] table = {origTable, joinTable}, column = {origCol, joinCol};
        Table joiner = new Table();
        ArrayList<Query> queries = new ArrayList<>();

        int i = 0;
        do {
            /* Only runs if the specified column exists */
            if (getTableName(table[i]).getAttributes().contains(column[i])) {
                joinTableAddCol(table[i], joiner, getTableName(table[i]));
                if (!queries.isEmpty()) {
                    joinTableIterate(origCol, joinCol, table, joiner, queries, getTableName(table[i]));
                } else {
                    queries = getTableName(table[i]).getData();
                }
                i++;
            } else {
                throw new SqlException("[ERROR]: Attribute does not exist");
            }
        } while (i < 2);
        return joiner.getVal(new String[]{"*"}, "").replace("'", "");
    }
    /* Method to add as many columns as required */
    private void joinTableAddCol(String s, Table joiner, Table temp) {
        int i = 1;
        while (i < temp.getAttributes().size()) {
            joiner.addAttribute(s + "." + temp.getAttributes().get(i));
            i++;
        }
    }
    /* Adds values by joining queries */
    private void joinTableIterate(String origCol, String joinCol, String[] table, Table joiner, ArrayList<Query> queries, Table temp) throws SqlException {
        for (int i = 0, queriesSize = queries.size(); i < queriesSize; i++) {
            ArrayList<Query> data = temp.getData();
            for (int j = 0, dataSize = data.size(); j < dataSize; j++) {
                if (!queries.get(i).attributeData(origCol).equals(data.get(j).attributeData(joinCol))) {
                    continue;
                }
                joiner.enterQuery(queries.get(i).joinQuery(data.get(j), table[0], table[1], origCol));
            }
        }
    }
}
