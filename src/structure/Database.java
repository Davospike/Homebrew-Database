package structure;

import exceptions.SqlException;

import java.util.*;

public class Database
{
    String dbName;
    HashMap<String, Table> tabFiles;

    public Database(String dbName)
    {
        this.dbName = dbName;
        tabFiles = new HashMap<>();
    }
    /* Checks if a table exists, if so, returns its name */
    public Table getTableName(String tableName) throws SqlException
    {
        if (!tabFiles.containsKey(tableName)) {
            throw new SqlException("[ERROR]: Table does not exist");
        } else {
            return tabFiles.get(tableName);
        }
    }
    /* Adds a table to the database provided it does not already exist */
    public void addTable(String tableName) throws SqlException
    {
        if (!tabFiles.containsKey(tableName)) {
            tabFiles.put(tableName, new Table());
        } else {
            throw new SqlException("[ERROR]: Table already exists");
        }
    }
    /* Deletes a table provided it exists  */
    public void deleteTable(String tableName) throws SqlException
    {
        if (!tabFiles.containsKey(tableName)) {
            throw new SqlException("[ERROR]: Table does not exist");
        } else {
            tabFiles.remove(tableName);
        }
    }
    /* Returns the name of the current database */
    public String getDbName()
    {
        return dbName;
    }
}
