package statements;

import sqlengine.DBCommandInterface;
import exceptions.SqlException;

import java.io.IOException;

public abstract class Statement
{
    final ThreadLocal<String> statement = new ThreadLocal<String>();

    public Statement(String statement)
    {
        this.statement.set(statement);
    }

    public String getStatement()
    {
        int location = 0;

        if (statement.get().lastIndexOf("WHERE") == -1) {
            location = statement.get().lastIndexOf("where");
        }

        return location == -1 ? "" : statement.get().substring(location + 5);
    }

    public abstract String runStatement(DBCommandInterface db) throws SqlException, IOException;
}
