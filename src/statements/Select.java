package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;
import sqlengine.Display;

import java.util.regex.*;

/* Select command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Select extends Statement
{
    public Select(String statement) {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException
    {
        String result;
        Pattern pattern = Pattern.compile("SELECT\\s+(\\*|" + "((\\s*" + "[a-zA-Z0-9_.]+" + "\\s*,)*(\\s*" +
                "[a-zA-Z0-9_.]+" + "\\s*))" + ")\\s+FROM\\s+" + "(" + "[a-zA-Z0-9_.]+" + ")(?:\\s*;|" +
                "\\s+WHERE\\s+(?:[()]*" + "[a-zA-Z0-9_.]+" + "\\s*" + "(==|!=|<|>|<=|>=|LIKE)" + "\\s*" + "(?:(" +
                "'[a-zA-Z0-9 ]*'" + ")|(" + "true|false" + ")|(" + "[0-9]+" + ")|(" + "[0-9]+.[0-9]+" + "))" +
                "\\s*[()]*\\s*(AND|OR)*\\s*[()]*)+\\s*;" + ")", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            String selectedVals = db.useVal(matcher.group(5),  matcher.group(1).split("\\s*,\\s*"), getStatement());
            result = "[OK]\n" + Display.results(selectedVals);
            return result;
        }
    }
}
