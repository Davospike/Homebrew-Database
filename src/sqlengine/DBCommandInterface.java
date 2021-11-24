package sqlengine;

import exceptions.SqlException;

import java.io.IOException;

public interface DBCommandInterface
{
    public void deleteDB(String dbName, boolean append) throws SqlException;
    public void useDB(String dbName) throws SqlException;
    public void deleteTab(String table, boolean append) throws SqlException;
    public void deleteAttribute(String table, String attribute, boolean append) throws SqlException, IOException;
    public void addVal(String table, String[] value, boolean addId, boolean append) throws SqlException, IOException;
    public void createAttribute(String table, String attribute, boolean append) throws SqlException, IOException;
    public void changeVal(String table, String[] value, String con, boolean append) throws SqlException, IOException;
    public void createTab(String table, boolean append) throws SqlException, IOException;
    public void createDB(String dbName, boolean append) throws SqlException;
    public String useVal(String table, String[] value, String con) throws SqlException;
    public void removeVal(String table, String con, boolean append) throws SqlException, IOException;
    public String joinComm(String origTable, String joinTable, String origCol, String joinCol) throws SqlException;
}
