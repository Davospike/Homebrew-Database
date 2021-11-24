package statements;

import exceptions.*;
import sqlengine.DBCommandInterface;

import java.io.IOException;
import java.util.regex.*;

/* Create command : ensures that the entered query matches the exact pattern that is accepted by the BNF */
public class Create extends Statement
{
    public Create(String statement) {
        super(statement);
    }

    @Override
    public String runStatement(DBCommandInterface db) throws SqlException, IOException
    {
        String result;
        Pattern pattern = Pattern.compile("CREATE\\s*(TABLE|DATABASE)\\s*([a-zA-Z0-9]+)\\s*;" +
                "|CREATE\\s*TABLE\\s*(" + "[a-zA-Z0-9_.]+" + ")\\s*\\" + "(" + "((\\s*" + "[a-zA-Z0-9_.]+" +
                "\\s*,)*(\\s*" + "[a-zA-Z0-9_.]+" + "\\s*))" + "\\)\\s*;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(statement.get());
        boolean matches = matcher.find();

        if (!matches) {
            throw new SqlException(MiscErrors.mError(statement.get()));
        } else {
            if (matcher.group(1) == null) {
                addTableAttributes(db, matcher.group(3), matcher.group(4));
            } else {
                DbOrTable(db, matcher);
            }
            result = "[OK]";
            return result;
        }
    }

    private void DbOrTable(DBCommandInterface db, Matcher matcher) throws SqlException, IOException
    {
        if (!matcher.group(1).equalsIgnoreCase("DATABASE")) {
            db.createTab(matcher.group(2), true);
        } else {
            db.createDB(matcher.group(2), true);
        }
    }

    private void addTableAttributes(DBCommandInterface db, String itemName, String attribs) throws SqlException, IOException
    {
        String[] columns = attribs.split("\\s*,\\s*");
        int i = 0;

        db.createTab(itemName, true);

        while (i < columns.length - 1) {
            db.createAttribute(itemName, columns[i], false);
            i++;
        }
        db.createAttribute(itemName, columns[columns.length - 1], true);
    }
}
