package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;

import java.util.regex.*;

/* Use command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Use extends Statement
{
    public Use(String statement)
    {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException
    {
        String result;
        Pattern pattern = Pattern.compile("^USE\\s*+(" + "[a-zA-Z0-9_.]+" + ")\\s*;$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            db.useDB(matcher.group(1));
            result = "[OK]";
            return result;
        }
    }
}
