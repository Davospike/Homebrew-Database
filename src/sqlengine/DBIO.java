package sqlengine;

import exceptions.SqlException;

import java.io.*;
import java.util.Arrays;

public class DBIO implements DBIOInterface
{
    File dbFolder;

    public DBIO(String dbFolderName) throws SqlException
    {
        dbFolder = new File(dbFolderName + File.separator);

        if (!dbFolder.exists() && !dbFolder.mkdir()) {
            throw new SqlException("[ERROR]: Could not create database");
        }
    }

    @Override
    /* Creates a database with the specified name */
    public void createDB(String dbName) throws SqlException
    {
        File directory = new File(dbFolder.getPath() + File.separator + dbName + File.separator);

        if (directory.exists() || directory.mkdir()) {
            return;
        }
        throw new SqlException("[ERROR]: Database could not be created");
    }

    @Override
    /* Removes a database with the specified name */
    public void removeDB(String dbName) throws SqlException
    {
        File directory = new File(dbFolder.getPath() + File.separator + dbName + File.separator);
        String[] tableList = directory.list();

        rmDbIterator(dbName, tableList);
        if (directory.delete()) {
            return;
        }
        throw new SqlException("[ERROR]: Database could not be deleted");
    }
    /* Removes the tables from a database primed for deletion */
    private void rmDbIterator(String dbName, String[] tableList) throws SqlException {
        if (tableList != null) {
            for (int i = 0; i < tableList.length; i++) {
                removeTable(dbName, tableList[i]);
            }
        }
    }
    /* Reads in the data from the tab files */
    void getTableData(DBCommandInterface db, File dbFolder)
    {
        File[] dbTables = dbFolder.listFiles();

        if (dbTables != null) {
            Arrays.stream(dbTables).forEach(table -> {
                try {
                    db.createTab(table.getName().replace(".tab", ""), false);
                    getTableEntries(db, table);
                } catch (IOException | SqlException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    /* Creates a table within the specified database, with the specified name. Ensures that the table file
    *  ends with the .tab extension */
    public void createTable(String dbName, String tableName) throws SqlException, IOException
    {
        String directory = dbFolder.getPath() + File.separator + dbName + File.separator + tableName;

        if (!directory.endsWith(".tab")) {
            directory += ".tab";
        }

        File table = new File(directory);

        if (table.exists() || table.createNewFile()) {
            return;
        }
        throw new SqlException("[ERROR]: Table could not be created");

    }

    @Override
    /* Removes a table within the specified database, with the specified name. Ensures that the table file
     * ends with the .tab extension */
    public void removeTable(String dbName, String tableName) throws SqlException
    {
        String directory = dbFolder.getPath() + File.separator + dbName + File.separator + tableName;

        if(!directory.endsWith(".tab")) {
            directory += ".tab";
        }

        File table = new File(directory);

        if (table.delete()) {
            return;
        }
        throw new SqlException("[ERROR]: Table could not be deleted");

    }

    @Override
    /* Updates table data within the specified database, with the specified name. */
    public void updateTable(String dbName, String tableName, String update) throws IOException
    {
        FileWriter fw = new FileWriter(dbFolder.getPath() + File.separator + dbName + File.separator + tableName + ".tab");
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(update);
        bw.flush();
        bw.close();
        fw.close();
    }

    /* Companion method to getTableData, reads in entries from the .tab file */
    public void getTableEntries(DBCommandInterface db, File tableName) throws SqlException, IOException
    {
        FileReader fr = new FileReader(tableName);
        BufferedReader br = new BufferedReader(fr);
        String table = tableName.getName().replace(".tab", ""), col = br.readLine();

        if (col != null) {
            String tab = getString(db, br, table, col);

            if (tab != null) {
                entryIterator(db, br, table, tab);
            }
            br.close();
            fr.close();
        }
    }

    /* Companion method to getTableEntries - iterates through splitting each entry with a /t (tab) */
    private void entryIterator(DBCommandInterface db, BufferedReader br, String table, String tab) throws SqlException, IOException {
        do {
            db.addVal(table, tab.split("\t"), true, false);
            tab = br.readLine();
        } while (tab != null);
    }

    /* Companion method to getTableEntries */
    private String getString(DBCommandInterface db, BufferedReader br, String table, String col) throws IOException, SqlException {
        String[] split = col.split("\t");
        String tab = br.readLine();
        int i = 0, splitLength = split.length;

        while (i < splitLength) {
            String column = split[i];
            db.createAttribute(table, column, false);
            i++;
        }
        return tab;
    }

    @Override
    /* Reads in the .tab files */
    public void readFile(DBCommandInterface db) throws SqlException {
        File[] dbs = dbFolder.listFiles();

        if (dbs != null) {
            for (int i = 0, dbsLength = dbs.length; i < dbsLength; i++) {
                db.createDB(dbs[i].getName(), false);
                db.useDB(dbs[i].getName());
                getTableData(db, dbs[i]);
            }
            db.useDB(null);
        }
    }
}
