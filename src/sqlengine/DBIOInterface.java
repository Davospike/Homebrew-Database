package sqlengine;

import exceptions.SqlException;

import java.io.IOException;

public interface DBIOInterface
{
    public void createDB(String dbName) throws SqlException;
    public void removeDB(String dbName) throws SqlException;
    public void createTable(String dbName, String tableName) throws SqlException, IOException;
    public void removeTable(String dbName, String tableName) throws SqlException;
    public void updateTable(String dbName, String tableName, String update) throws SqlException, IOException;
    public void readFile(DBCommandInterface db) throws SqlException;
}
