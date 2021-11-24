package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;
import sqlengine.Display;

import java.util.regex.*;

/* Join command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Join extends Statement
{
    public Join(String statement) {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException
    {
        String result;
        Pattern pattern = Pattern.compile("JOIN\\s+("+ "[a-zA-Z0-9_.]+" + ")\\s+AND\\s+(" +
                "[a-zA-Z0-9_.]+" + ")\\s+" + "ON\\s+(" + "[a-zA-Z0-9_.]+" + ")\\s+AND\\s+(" +
                "[a-zA-Z0-9_.]+" + ")\\s*;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            String combined = db.joinComm(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            result = Display.results(combined);
            return result;
        }
    }
}
