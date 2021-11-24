package statements;

import sqlengine.DBCommandInterface;
import exceptions.*;

import java.io.IOException;
import java.util.regex.*;

/* Alter command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Alter extends Statement
{
    public Alter(String statement)
    {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException, IOException
    {
        String result;
        Pattern pattern = Pattern.compile("ALTER\\s*TABLE\\s*(" + "[a-zA-Z0-9_.]+"+")\\s*(ADD|DROP)\\s*" +
                "("+"[a-zA-Z0-9_.]+"+")\\s*;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            if (!matcher.group(2).equalsIgnoreCase("ADD")) {
                db.deleteAttribute(matcher.group(1), matcher.group(3), true);
            } else {
                db.createAttribute(matcher.group(1), matcher.group(3), true);
            }
        }
        result = "[OK]";
        return result;
    }
}

