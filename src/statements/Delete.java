package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;

import java.io.IOException;
import java.util.regex.*;

/* Delete command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Delete extends Statement
{
    public Delete(String statement) {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException, IOException
    {
        String result;
        Pattern pattern = Pattern.compile("DELETE\\s+FROM\\s+(" + "[a-zA-Z0-9_.]+" + ")" +
                "\\s+WHERE\\s+(?:[()]*" + "[a-zA-Z0-9_.]+" + "\\s*" + "(==|!=|<|>|<=|>=|LIKE)" + "\\s*" + "(?:(" +
                "'[a-zA-Z0-9 ]*'" + ")|(" + "true|false" + ")|(" + "[0-9]+" + ")|(" + "[0-9]+.[0-9]+" + "))" +
                "\\s*[()]*\\s*(AND|OR)*\\s*[()]*)+\\s*;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            db.removeVal(matcher.group(1), getStatement(), true);
            result = "[OK]";
            return result;
        }
    }
}
