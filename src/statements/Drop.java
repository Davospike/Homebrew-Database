package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;

import java.util.regex.*;

/* Drop command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Drop extends Statement
{
    public Drop(String statement) {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException
    {
        String result;
        Pattern pattern = Pattern.compile("DROP\\s*(TABLE|DATABASE)\\s*(" + "[a-zA-Z0-9_.]+" + ")\\s*;",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            if (!matcher.group(1).equalsIgnoreCase("TABLE")) {
                db.deleteDB(matcher.group(2), true);
            } else {
                db.deleteTab(matcher.group(2), true);
            }
            result = "[OK]";
            return result;
        }
    }
}
