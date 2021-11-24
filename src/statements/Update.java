package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;

import java.io.IOException;
import java.util.regex.*;

/* Update command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Update extends Statement
{
    public Update(String statement)
    {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException, IOException
    {
        String result;
        Pattern pattern = Pattern.compile("UPDATE\\s+(" + "[a-zA-Z0-9_.]+" + ")\\s+SET\\s+("+
                "(\\s*" + "[a-zA-Z0-9_.]+" + "\\s*=\\s*" + "(?:(" + "'[a-zA-Z0-9 ]*'" + ")|(" + "true|false" +
                ")|(" + "[0-9]+" + ")|(" + "[0-9]+.[0-9]+" + "))" + "\\s*,\\s*)*\\s*" + "(\\s*" + "[a-zA-Z0-9_.]+" +
                "\\s*=\\s*" + "(?:(" + "'[a-zA-Z0-9 ]*'" + ")|(" + "true|false" + ")|(" + "[0-9]+" + ")|(" +
                "[0-9]+.[0-9]+" + "))" + "\\s*)" + ")(" + "\\s+WHERE\\s+(?:[()]*" + "[a-zA-Z0-9_.]+" + "\\s*" +
                "(==|!=|<|>|<=|>=|LIKE)" + "\\s*" + "(?:(" + "'[a-zA-Z0-9 ]*'" + ")|(" + "true|false" + ")|(" +
                "[0-9]+" + ")|(" + "[0-9]+.[0-9]+" + "))" + "\\s*[()]*\\s*(AND|OR)*\\s*[()]*)+\\s*;" + "|\\s*;)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            String[] split = matcher.group(2).split("\\s*[=,]\\s*");
            db.changeVal(matcher.group(1), split, getStatement(), true);
            result = "[OK]";
            return result;
        }
    }
}
